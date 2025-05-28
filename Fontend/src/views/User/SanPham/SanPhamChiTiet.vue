<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const sanPhamChiTiet = ref(null);
const loading = ref(false);
const error = ref(null);
const selectedMauSac = ref(null);
const selectedSize = ref(null);
const soLuongMua = ref(1);

const fetchSanPhamChiTiet = async () => {
    try {
        loading.value = true;
        const { id } = route.params;
        const spctRes = await axios.get(`http://localhost:8080/san-pham-chi-tiet/san-pham/${id}`);

        sanPhamChiTiet.value = {
            id: spctRes.data.id,
            tenSanPham: spctRes.data.sanPham.tenSanPham,
            giaBan: spctRes.data.giaBan,
            // giaGiam: spctRes.data.giaGiam,
            danhMuc: spctRes.data.sanPham.danhMuc.tenDanhMuc,
            thuongHieu: spctRes.data.sanPham.thuongHieu.tenThuongHieu,
            soLuong: spctRes.data.soLuong,
            danhSachMauSac: spctRes.data.danhSachMauSac,
            danhSachSize: spctRes.data.danhSachSize,
            moTa: spctRes.data.sanPham.moTa,
            danhSachHinhAnh: spctRes.data.danhSachHinhAnh || [],
            bienThe: spctRes.data.bienThe,
            // Thêm các dòng dưới đây
            danhSachMauSac: Array.from(new Map(spctRes.data.bienThe.map(b => [b.mauSac.id, b.mauSac])).values()),
            danhSachSize: Array.from(new Map(spctRes.data.bienThe.map(b => [b.size.id, b.size])).values())
        };
    } catch (err) {
        error.value = "Không tìm thấy sản phẩm!";
    } finally {
        loading.value = false;
    }
};
const danhSachSizeTheoMau = computed(() => {
    if (!selectedMauSac.value) return [];
    const bienTheTheoMau = sanPhamChiTiet.value.bienThe.filter(
        b => b.mauSac.id === selectedMauSac.value && b.soLuong > 0 // Only show sizes with stock > 0
    );
    return Array.from(new Map(bienTheTheoMau.map(b => [b.size.id, b.size])).values());
});

const soLuongTheoBienThe = computed(() => {
    if (!selectedMauSac.value || !selectedSize.value) return null;
    const bienThe = sanPhamChiTiet.value.bienThe.find(
        b => b.mauSac.id === selectedMauSac.value && b.size.id === selectedSize.value
    );
    return bienThe ? bienThe.soLuong : 0;
});
const giaTheoBienThe = computed(() => {
    if (!selectedMauSac.value || !selectedSize.value) return null;
    const bienThe = sanPhamChiTiet.value.bienThe.find(
        b => b.mauSac.id === selectedMauSac.value && b.size.id === selectedSize.value
    );
    return bienThe ? {
        giaBan: bienThe.giaBan,
    } : null;
});


const themVaoGioHang = () => {

    if (!selectedMauSac.value || !selectedSize.value) {
        alert("Vui lòng chọn màu sắc và kích thước!");
        return;
    }

    const gioHang = JSON.parse(localStorage.getItem("gioHang")) || [];

    const bienThe = sanPhamChiTiet.value.bienThe.find(
        b => b.mauSac.id === selectedMauSac.value && b.size.id === selectedSize.value
    );

    if (!bienThe) {
        alert("Không tìm thấy biến thể sản phẩm!");
        return;
    }
    if (soLuongMua.value > bienThe.soLuong) {
        alert(`Số lượng trong kho chỉ còn ${bienThe.soLuong} sản phẩm!`);
        return;
    }

    const mauSac = sanPhamChiTiet.value.danhSachMauSac.find(m => m.id === selectedMauSac.value);
    const size = sanPhamChiTiet.value.danhSachSize.find(s => s.id === selectedSize.value);

    const sanPham = {
        id: bienThe.id, // mã SPCT dùng để xử lý
        maSPCT: bienThe.maSPCT, // 👈 thêm dòng này để lưu mã SPCT
        tenSanPham: sanPhamChiTiet.value.tenSanPham,
        giaBan: bienThe.giaBan,
        // giaGiam: bienThe.giaGiam,
        // giamPhanTram: Math.round(100 - (bienThe.giaGiam / bienThe.giaBan) * 100),
        soLuong: soLuongMua.value,
        mauSac: { id: mauSac.id, tenMau: mauSac.tenMau },
        size: { id: size.id, tenSize: size.tenSize },
        hinhAnh: sanPhamChiTiet.value.danhSachHinhAnh[0] || "",
    };

    // Kiểm tra trùng sản phẩm
    const index = gioHang.findIndex(item =>
        item.id === sanPham.id &&
        item.mauSac.id === sanPham.mauSac.id &&
        item.size.id === sanPham.size.id
    );

    if (index !== -1) {
        gioHang[index].soLuong += soLuongMua.value;
    } else {
        gioHang.push(sanPham);
    }

    localStorage.setItem("gioHang", JSON.stringify(gioHang));
    alert("Đã thêm sản phẩm vào giỏ hàng!");

    // ✅ Điều hướng sang trang Cart.vue
    router.push({ path: '/giohang' });
};

const muaNgay = () => {
    if (!selectedMauSac.value || !selectedSize.value) {
        alert("Vui lòng chọn màu sắc và kích thước!");
        return;
    }
    router.push({
        path: "/thanh-toan",

    });
};

const tangSoLuong = () => {
    if (soLuongTheoBienThe.value !== null && soLuongMua.value < soLuongTheoBienThe.value) {
        soLuongMua.value++;
    } else if (soLuongTheoBienThe.value !== null) {
        alert(`Số lượng trong kho chỉ còn ${soLuongTheoBienThe.value} sản phẩm!`);
    }
};


const giamSoLuong = () => {
    if (soLuongMua.value > 1) {
        soLuongMua.value--;
    } else {
        alert("Số lượng tối thiểu là 1.");
    }
};


const handleInputSoLuong = (event) => {
    let value = parseInt(event.target.value);

    if (isNaN(value) || value < 1) {
        soLuongMua.value = 1;
        alert("Số lượng tối thiểu là 1.");
    } else if (soLuongTheoBienThe.value !== null && value > soLuongTheoBienThe.value) {
        soLuongMua.value = soLuongTheoBienThe.value;
        alert(`Số lượng trong kho chỉ còn ${soLuongTheoBienThe.value} sản phẩm!`);
    } else {
        soLuongMua.value = value;
    }
};


onMounted(fetchSanPhamChiTiet);
</script>

<template>
    <div class="container mt-5">
        <div v-if="loading" class="text-center">
            <span class="spinner-border text-primary"></span> Đang tải...
        </div>
        <div v-else-if="error" class="alert alert-danger text-center">{{ error }}</div>
        <div v-else-if="sanPhamChiTiet" class="row product-container shadow-lg p-4 rounded">
            <!-- Hình ảnh sản phẩm -->
            <!-- Hình ảnh sản phẩm -->
            <div class="col-md-6">
                <div id="productCarousel" class="carousel slide shadow-lg rounded" data-bs-ride="carousel">
                    <div class="carousel-inner">
                        <div v-for="(img, index) in sanPhamChiTiet.danhSachHinhAnh" :key="index" class="carousel-item"
                            :class="{ active: index === 0 }">
                            <img :src="img" class="d-block w-100 rounded product-img" alt="Hình ảnh sản phẩm">
                        </div>
                    </div>

                    <!-- Nút điều hướng -->
                    <button class="carousel-control-prev" type="button" data-bs-target="#productCarousel"
                        data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#productCarousel"
                        data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Next</span>
                    </button>
                </div>
            </div>



            <!-- Thông tin sản phẩm -->
            <div class="col-md-6">
                <h2 class="fw-bold text-primary">{{ sanPhamChiTiet.tenSanPham }}</h2>
                <p>
                    <strong>Thương hiệu:</strong> <span class="text-secondary">{{ sanPhamChiTiet.thuongHieu }}</span> |
                    <strong>Loại:</strong> <span class="text-secondary">{{ sanPhamChiTiet.danhMuc }}</span>
                </p>

                <p class="text-danger fs-2 fw-bold" v-if="giaTheoBienThe">
                    {{ new Intl.NumberFormat("vi-VN").format(giaTheoBienThe.giaBan) }} đ
                </p>




                <!-- Chọn màu sắc -->
                <div class="mb-3">
                    <strong>Chọn màu sắc:</strong>
                    <div class="d-flex mt-2">
                        <button v-for="mau in sanPhamChiTiet.danhSachMauSac" :key="mau.id"
                            class="btn color-btn me-2 px-3"
                            :class="{ 'btn-dark text-white': selectedMauSac === mau.id, 'btn-outline-dark': selectedMauSac !== mau.id }"
                            @click="selectedMauSac = mau.id">
                            {{ mau.tenMau }}
                        </button>
                    </div>
                </div>

                <!-- Chọn kích thước -->
                <div class="mb-3">
                    <strong>Chọn kích thước:</strong>
                    <div class="d-flex mt-2">
                        <button v-for="size in sanPhamChiTiet.danhSachSize" :key="size.id"
                            class="btn size-btn me-2 px-3" :class="{
                                'btn-dark text-white': selectedSize === size.id,
                                'btn-outline-dark': selectedSize !== size.id
                            }" :disabled="!danhSachSizeTheoMau.some(s => s.id === size.id)"
                            @click="selectedSize = size.id">
                            {{ size.tenSize }}
                        </button>
                    </div>
                </div>

                <!-- Số lượng -->
                <div class="mb-3">
                    <strong v-if="selectedMauSac && selectedSize">Tồn kho: {{ soLuongTheoBienThe }}</strong>
                    <div class="d-flex align-items-center mt-2">
                        <button class="btn btn-outline-dark px-3" @click="giamSoLuong">-</button>
                        <input type="number" class="form-control mx-2 text-center fw-bold quantity-input"
                            :value="soLuongMua" @input="handleInputSoLuong" min="1" />

                        <button class="btn btn-outline-dark px-3" @click="tangSoLuong">+</button>
                    </div>
                </div>

                <!-- Nút mua hàng -->
                <div class="d-flex mt-4">
                    <button class="btn btn-dark me-3 px-4 py-2 fs-5 shadow" @click="themVaoGioHang">🛒 Thêm vào giỏ
                        hàng</button>
                    <!-- Nút mua ngay -->
                    <button class="btn btn-danger px-4 py-2 fs-5 shadow" @click="muaNgay">⚡ Mua ngay</button>
                </div>

                <!-- Mô tả sản phẩm -->
                <div class="mt-4">
                    <h4 class="fw-bold">Mô tả sản phẩm</h4>
                    <p class="text-secondary">{{ sanPhamChiTiet.moTa }}</p>
                </div>

                <div class="mt-4">
                    <router-link to="/trang-chu" class="text-decoration-none text-dark fw-bold">⬅ Quay lại</router-link>
                </div>
            </div>
        </div>
    </div>
</template>


<style scoped>
.product-container {
    background: #fff;
    border-radius: 15px;
}

.product-img {
    width: 80%;
    max-height: 400px;
    object-fit: contain;
}

.color-btn {
    border-radius: 20px;
    padding: 8px 20px;
    transition: 0.3s;
}

.size-btn {
    border-radius: 10px;
    padding: 8px 16px;
    transition: 0.3s;
}

.color-btn:hover,
.size-btn:hover {
    transform: scale(1.1);
}

.quantity-input {
    width: 60px;
    text-align: center;
    font-size: 18px;
}

.shadow-lg {
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
}

.btn:hover {
    opacity: 0.9;
}

.carousel {
    max-width: 100%;
    border-radius: 10px;
    overflow: hidden;
}

.carousel-item img {
    max-height: 400px;
    object-fit: contain;
    border-radius: 10px;
}

.carousel-control-prev-icon,
.carousel-control-next-icon {
    filter: invert(1);
}
</style>
