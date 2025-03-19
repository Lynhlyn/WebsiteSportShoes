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
            tenSanPham: spctRes.data.sanPham.tenSanPham,
            giaBan: spctRes.data.giaBan,
            giaGiam: spctRes.data.giaGiam,
            danhMuc: spctRes.data.sanPham.danhMuc.tenDanhMuc,
            thuongHieu: spctRes.data.sanPham.thuongHieu.tenThuongHieu,
            soLuong: spctRes.data.soLuong,
            danhSachMauSac: spctRes.data.danhSachMauSac,
            danhSachSize: spctRes.data.danhSachSize,
            moTa: spctRes.data.sanPham.moTa,
            hinhAnh: spctRes.data.sanPham.hinhAnh || 'https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg'
        };
    } catch (err) {
        error.value = "Không tìm thấy sản phẩm!";
    } finally {
        loading.value = false;
    }
};

const themVaoGioHang = () => {
    if (!selectedMauSac.value || !selectedSize.value) {
        alert("Vui lòng chọn màu sắc và kích thước!");
        return;
    }

    const gioHang = JSON.parse(localStorage.getItem("gioHang")) || [];
    const sanPham = {
        id: sanPhamChiTiet.value.id,
        tenSanPham: sanPhamChiTiet.value.tenSanPham,
        giaGiam: sanPhamChiTiet.value.giaGiam,
        soLuong: soLuongMua.value,
        mauSac: selectedMauSac.value,
        size: selectedSize.value,
        thuongHieu: sanPhamChiTiet.value.thuongHieu,
        danhMuc: sanPhamChiTiet.value.danhMuc,
        hinhAnh: sanPhamChiTiet.value.hinhAnh
    };

    // Kiểm tra sản phẩm đã tồn tại trong giỏ hàng chưa
    const index = gioHang.findIndex(item =>
        item.tenSanPham === sanPham.tenSanPham &&
        item.mauSac === sanPham.mauSac &&
        item.size === sanPham.size &&
        item.thuongHieu === sanPham.thuongHieu &&
        item.danhMuc === sanPham.danhMuc
    );

    if (index !== -1) {
        // Nếu sản phẩm đã có, tăng số lượng
        gioHang[index].soLuong += sanPham.soLuong;
        console.log(`Cập nhật số lượng sản phẩm: ${gioHang[index].soLuong}`);
    } else {
        // Nếu sản phẩm chưa có, thêm mới vào giỏ hàng
        gioHang.push(sanPham);
        console.log(`Thêm sản phẩm mới vào giỏ hàng: ${sanPham.tenSanPham}`);
    }

    // Lưu giỏ hàng vào localStorage
    localStorage.setItem("gioHang", JSON.stringify(gioHang));

    // Chuyển hướng đến trang giỏ hàng
    router.push("/giohang");
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
    soLuongMua.value++;
};

const giamSoLuong = () => {
    if (soLuongMua.value > 1) {
        soLuongMua.value--;
    }
};

const giaBanFormatted = computed(() => {
    if (!sanPhamChiTiet.value || !sanPhamChiTiet.value.giaBan) return "0 đ";
    return new Intl.NumberFormat("vi-VN").format(sanPhamChiTiet.value.giaBan) + " đ";
});

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
            <div class="col-md-6 text-center">
                <img :src="sanPhamChiTiet.hinhAnh" class="img-fluid rounded shadow-lg product-img"
                    alt="Hình ảnh sản phẩm">
            </div>

            <!-- Thông tin sản phẩm -->
            <div class="col-md-6">
                <h2 class="fw-bold text-primary">{{ sanPhamChiTiet.tenSanPham }}</h2>
                <p>
                    <strong>Thương hiệu:</strong> <span class="text-secondary">{{ sanPhamChiTiet.thuongHieu }}</span> |
                    <strong>Loại:</strong> <span class="text-secondary">{{ sanPhamChiTiet.danhMuc }}</span>
                </p>

                <!-- Giá sản phẩm -->
                <p class="text-danger fs-2 fw-bold">
                    <span v-if="sanPhamChiTiet.giaBan !== sanPhamChiTiet.giaGiam"
                        class="text-muted text-decoration-line-through">
                        {{ new Intl.NumberFormat("vi-VN").format(sanPhamChiTiet.giaBan) }} đ
                    </span>
                    <span> {{ new Intl.NumberFormat("vi-VN").format(sanPhamChiTiet.giaGiam) }} đ</span>
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
                            class="btn size-btn me-2 px-3"
                            :class="{ 'btn-dark text-white': selectedSize === size.id, 'btn-outline-dark': selectedSize !== size.id }"
                            @click="selectedSize = size.id">
                            {{ size.tenSize }}
                        </button>
                    </div>
                </div>

                <!-- Số lượng -->
                <div class="mb-3">
                    <strong>Tồn kho: {{ sanPhamChiTiet.soLuong }}</strong>
                    <div class="d-flex align-items-center mt-2">
                        <button class="btn btn-outline-dark px-3" @click="giamSoLuong">-</button>
                        <input type="text" class="form-control mx-2 text-center fw-bold quantity-input"
                            v-model="soLuongMua">
                        <button class="btn btn-outline-dark px-3" @click="tangSoLuong">+</button>
                    </div>
                </div>

                <!-- Nút mua hàng -->
                <div class="d-flex mt-4">
                    <button class="btn btn-dark me-3 px-4 py-2 fs-5 shadow" @click="themVaoGioHang">🛒 Thêm vào giỏ hàng</button>
                    <!-- Nút mua ngay -->
                    <button class="btn btn-danger px-4 py-2 fs-5 shadow" @click="muaNgay">⚡ Mua ngay</button>
                </div>

                <!-- Mô tả sản phẩm -->
                <div class="mt-4">
                    <h4 class="fw-bold">Mô tả sản phẩm</h4>
                    <p class="text-secondary">{{ sanPhamChiTiet.moTa }}</p>
                </div>

                <div class="mt-4">
                    <router-link to="/" class="text-decoration-none text-dark fw-bold">⬅ Quay lại</router-link>
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
</style>
