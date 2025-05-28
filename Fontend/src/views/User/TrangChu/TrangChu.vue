<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

const danhMucList = ref([]);
const thuongHieuList = ref([]);
const chatLieuList = ref([]);
const deGiayList = ref([]);
const mauSacList = ref([]);
const sizeList = ref([]);
// const khuyenMaiList = ref([]);
const sanPhamList = ref([]);
const spctList = ref([]);
const loading = ref(false);
const selectedFilters = ref({
    thuongHieuList: "",
    danhMucList: "",
    deGiayList: "",
    chatLieuList: "",
    mauSacList: "",
    sizeList: ""
});
const currentPage = ref(1);
const itemsPerPage = 6;

const totalPages = computed(() => Math.ceil(sanPhamList.value.length / itemsPerPage));

const changePage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
    }
};


const fetchData = async () => {
    try {
        loading.value = true;

        const responses = await Promise.all([
            axios.get("http://localhost:8080/danh-muc"),
            axios.get("http://localhost:8080/thuong-hieu"),
            axios.get("http://localhost:8080/chat-lieu"),
            axios.get("http://localhost:8080/de-giay"),
            axios.get("http://localhost:8080/san-pham-chi-tiet/mau-sac"),
            axios.get("http://localhost:8080/san-pham-chi-tiet/size"),
            axios.get("http://localhost:8080/san-pham-chi-tiet/khuyen-mai"),
            axios.get("http://localhost:8080/san-pham"),
            axios.get("http://localhost:8080/san-pham-chi-tiet/spct")
        ]);

        danhMucList.value = responses[0].data || [];
        thuongHieuList.value = responses[1].data || [];
        chatLieuList.value = responses[2].data || [];
        deGiayList.value = responses[3].data || [];
        mauSacList.value = responses[4].data || [];
        sizeList.value = responses[5].data || [];
        // khuyenMaiList.value = responses[6].data || [];
        spctList.value = responses[8].data || [];

        sanPhamList.value = (responses[7].data || []).map((product) => {
            const spct = spctList.value.find(sp => sp.sanPham.id === product.id);
            const giaGoc = spct?.giaBan ?? 0;
            

            return {
                ...product,
                giaBan: giaGoc > 0 ? giaGoc : 'Liên hệ',
              
                urlAnh: isValidURL(product.anhDauTien) ? product.anhDauTien
                    : "https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg"
            };
        });


    } catch (error) {
        console.error("Lỗi khi tải dữ liệu:", error);
    } finally {
        loading.value = false;
    }
};

const isValidURL = (url) => {
    return url && /https?:\/\/.+\.(jpeg|jpg|png|webp|gif)$/i.test(url);
};

const formatCurrency = (price) => {
    return Number(price).toLocaleString("vi-VN", { style: "currency", currency: "VND" });
};
const filterByDanhMuc = async () => {
    try {
        loading.value = true;
        console.log(" Đang lọc theo danh mục:", selectedFilters.value.danhMucList);

        const response = await axios.get("http://localhost:8080/san-pham/filter", {
            params: { tenDanhMuc: selectedFilters.value.danhMucList,
                tenThuongHieu:selectedFilters.value.thuongHieuList,
                tenChatLieu:selectedFilters.value.chatLieuList,
                tenDeGiay:selectedFilters.value.tenDeGiay
             }
        });

        console.log(" API trả về:", response.data);

        // Sau khi nhận được sản phẩm, kết hợp với spctList để lấy giá
        sanPhamList.value = (response.data || []).map((product) => {
            const spct = spctList.value.find(sp => sp.sanPham.id === product.id);
            console.log(` Sản phẩm ID ${product.id} - Chi tiết sản phẩm`, spct);

            
            const giaGoc = spct?.giaBan ?? 0;
           
            return {
                ...product,
                giaBan: giaGoc > 0 ? giaGoc : "Liên hệ",
               
                urlAnh: isValidURL(product.anhDauTien) ? product.anhDauTien
                    : "https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg"
            };
        });

    } catch (error) {
        console.error("❌ Lỗi khi lọc danh mục:", error);
    } finally {
        loading.value = false;
    }
};
const filterByMauSacAndSize = async () => {
    try {
        loading.value = true;
        console.log("🔎 Đang lọc theo màu sắc và size:", selectedFilters.value.mauSacList, selectedFilters.value.sizeList);

        const response = await axios.get("http://localhost:8080/api/san-pham-chi-tiet/filter", {
            params: {
                tenMau: selectedFilters.value.mauSacList,
                tenSize: selectedFilters.value.sizeList
            }
        });

        console.log("🟢 API trả về:", response.data);

        // Danh sách sản phẩm sau khi lọc
        const filteredProducts = response.data || [];

        // Cập nhật sanPhamList với giá và khuyến mãi từ spctList
        sanPhamList.value = filteredProducts.map((spct) => {
            const product = spct.sanPham;

           

            const giaGoc = spct.giaBan ?? 0;
          

            return {
                ...product,
                giaBan: giaGoc > 0 ? giaGoc : "Liên hệ",
               
                urlAnh: isValidURL(product.anhDauTien) ? product.anhDauTien
                    : "https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg"
            };
        });

    } catch (error) {
        console.error("❌ Lỗi khi lọc theo màu sắc & size:", error);
    } finally {
        loading.value = false;
    }
};





onMounted(fetchData);
</script>

<template>
    <div class="container mt-4">
        <div class="marquee-container bg-dark text-white py-2">
            <marquee>🚚 Miễn phí giao hàng từ đơn 500K | 🔄 Miễn phí đổi trả trong 30 ngày | ✅ Cam kết 100% chính hãng
            </marquee>
        </div>

        <div class="row mt-3">
            <aside class="col-md-3">
        <div class="sidebar p-3 border rounded bg-light">
            <h5 class="fw-bold text-primary">Bộ lọc sản phẩm</h5>

            <!-- Bộ lọc danh mục -->
            <div class="filter-section mb-3">
                <h6 class="text-secondary">Danh mục</h6>
                <select class="form-select" v-model="selectedFilters.danhMucList" @change="filterByDanhMuc">
                    <option value="">Tất cả</option>
                    <option v-for="item in danhMucList" :key="item.id" :value="item.tenDanhMuc">
                        {{ item.tenDanhMuc }}
                    </option>
                </select>
            </div>

            <!-- Bộ lọc thương hiệu -->
            <div class="filter-section mb-3">
                <h6 class="text-secondary">Thương hiệu</h6>
                <select class="form-select" v-model="selectedFilters.thuongHieuList" @change="filterByDanhMuc">
                    <option value="">Tất cả</option>
                    <option v-for="item in thuongHieuList" :key="item.id" :value="item.tenThuongHieu">
                        {{ item.tenThuongHieu }}
                    </option>
                </select>
            </div>

            <!-- Bộ lọc màu sắc -->
            <div class="filter-section mb-3">
                <h6 class="text-secondary">Màu sắc</h6>
                <select class="form-select" v-model="selectedFilters.mauSacList" @change="filterByMauSacAndSize">
                    <option value="">Tất cả</option>
                    <option v-for="item in mauSacList" :key="item.id" :value="item.tenMau">
                        {{ item.tenMau }}
                    </option>
                </select>
            </div>

            <!-- Bộ lọc Size -->
            <div class="filter-section mb-3">
                <h6 class="text-secondary">Size</h6>
                <select class="form-select" v-model="selectedFilters.sizeList" @change="filterByMauSacAndSize">
                    <option value="">Tất cả</option>
                    <option v-for="item in sizeList" :key="item.id" :value="item.tenSize">
                        {{ item.tenSize }}
                    </option>
                </select>
            </div>

            <!-- Bộ lọc kiểu đế -->
            <div class="filter-section mb-3">
                <h6 class="text-secondary">Kiểu đế</h6>
                <select class="form-select" v-model="selectedFilters.deGiayList" @change="filterByDanhMuc">
                    <option value="">Tất cả</option>
                    <option v-for="item in deGiayList" :key="item.id" :value="item.tenDeGiay">
                        {{ item.tenDeGiay }}
                    </option>
                </select>
            </div>

            <!-- Bộ lọc chất liệu -->
            <div class="filter-section mb-3">
                <h6 class="text-secondary">Chất liệu</h6>
                <select class="form-select" v-model="selectedFilters.chatLieuList" @change="filterByDanhMuc">
                    <option value="">Tất cả</option>
                    <option v-for="item in chatLieuList" :key="item.id" :value="item.tenChatLieu">
                        {{ item.tenChatLieu }}
                    </option>
                </select>
            </div>

        </div>
    </aside>



            <main class="col-md-9">
                <h5 class="fw-bold mb-3">Danh sách sản phẩm</h5>

                <div v-if="loading" class="text-center">
                    <span class="spinner-border text-primary"></span> Đang tải...
                </div>

                <div v-else-if="sanPhamList.length === 0" class="text-center text-danger">
                    <p>Không có sản phẩm nào để hiển thị.</p>
                </div>

                <div v-else class="row">
                    <div v-for="(sanPham, index) in sanPhamList" :key="sanPham.id" class="col-md-4 mb-4"
                        v-show="index >= (currentPage - 1) * itemsPerPage && index < currentPage * itemsPerPage">
                        <div class="card product-card shadow-sm">
                            <img :src="sanPham.urlAnh" class="card-img-top" alt="Hình ảnh sản phẩm">
                            <div class="card-body">
                                <h6 class="card-title text-truncate">{{ sanPham.tenSanPham }}</h6>

                                <div v-if="sanPham.phanTramGiam" class="discount-badge">
                                    -{{ sanPham.phanTramGiam }}%
                                </div>

                                <p v-if="sanPham.giaKhuyenMai" class="card-text text-danger fw-bold">
                                    {{ formatCurrency(sanPham.giaKhuyenMai) }}
                                    <span class="text-muted text-decoration-line-through">
                                        {{ formatCurrency(sanPham.giaBan) }}
                                    </span>
                                </p>

                                <p v-else class="card-text text-danger fw-bold">
                                    {{ formatCurrency(sanPham.giaBan) }}
                                </p>

                                <p class="card-text small text-muted">Danh mục: {{ sanPham.danhMuc }}</p>
                                <p class="card-text small text-muted">Thương hiệu: {{ sanPham.thuongHieu }}</p>
                                <router-link :to="'/san-pham/' + sanPham.id" class="btn btn-primary w-100">
                                    Xem chi tiết
                                </router-link>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
        </div>
        <div class="pagination-container mt-4 d-flex justify-content-center">
            <ul class="pagination">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                    <button class="page-link" @click="changePage(currentPage - 1)"><</button>
                </li>

                <li v-for="page in totalPages" :key="page" class="page-item" :class="{ active: currentPage === page }">
                    <button class="page-link" @click="changePage(page)">{{ page }}</button>
                </li>

                <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                    <button class="page-link" @click="changePage(currentPage + 1)">></button>
                </li>
            </ul>
        </div>


    </div>
</template>



<style scoped>
.marquee-container {
    font-size: 14px;
    font-weight: bold;
    text-align: center;
}

.sidebar {
    background: white;
    border-radius: 10px;
}

.filter-section {
    margin-bottom: 15px;
}

.filter-section h6 {
    font-weight: bold;
    border-bottom: 2px solid #ddd;
    padding-bottom: 5px;
    margin-bottom: 10px;
}

.filter-item {
    padding: 5px 0;
    cursor: pointer;
    transition: color 0.3s;
}

.filter-item:hover {
    color: #007bff;
}

.product-card {
    transition: all 0.3s ease-in-out;
    border-radius: 10px;
}

.product-card:hover {
    transform: scale(1.05);
    box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.15);
}

.discount-badge {
    position: absolute;
    top: 10px;
    left: 10px;
    background-color: orange;
    color: white;
    padding: 5px 10px;
    font-weight: bold;
    border-radius: 5px;
}

.card-img-top {
    position: relative;
}
</style>
