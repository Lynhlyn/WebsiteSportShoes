<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const id = route.params.id; // Lấy id từ URL
const BASE_URL = "http://localhost:8080/san-pham-chi-tiet";

const SPCT = ref({
  maSPCT: "",
  sanPham: null,
  mauSac: null,
  khuyenMai: null,
  size: null,
  giaBan: 0,
  soLuong: 0,
  trangThai: true,
  phanTramGiamGia: 0,
  ngayBatDau: "",
  ngayKetThuc: "",
});

const sanPhamList = ref([]);
const mauSacList = ref([]);
const khuyenMaiList = ref([]);
const sizeList = ref([]);
const loading = ref(false);
const errorMessage = ref("");

// Hàm lấy dữ liệu danh sách (sản phẩm, màu sắc, khuyến mãi, size)
const fetchDropdownData = async () => {
  loading.value = true;
  try {
    const [spRes, msRes, kmRes, sizeRes] = await Promise.all([
      axios.get(`${BASE_URL}/san-pham`),
      axios.get(`${BASE_URL}/mau-sac`),
      axios.get(`${BASE_URL}/khuyen-mai`),
      axios.get(`${BASE_URL}/size`),
    ]);
    sanPhamList.value = spRes.data;
    mauSacList.value = msRes.data;
    khuyenMaiList.value = kmRes.data;
    sizeList.value = sizeRes.data;
  } catch (error) {
    console.error("Lỗi tải dữ liệu từ API:", error.response?.data || error.message);
    errorMessage.value = "Không thể tải dữ liệu từ máy chủ.";
  } finally {
    loading.value = false;
  }
};

// Hàm lấy chi tiết sản phẩm chi tiết (SPCT)
const fetchSanPhamChiTiet = async () => {
  if (!id) {
    errorMessage.value = "ID sản phẩm chi tiết không hợp lệ!";
    return;
  }
  loading.value = true;
  try {
    const response = await axios.get(`${BASE_URL}/${id}`);
    if (response.data) {
      const { khuyenMai } = response.data; // Lấy thông tin khuyến mãi

      SPCT.value = {
        maSPCT: response.data.maSPCT,
        sanPham: response.data.sanPham ? response.data.sanPham.tenSanPham : null,
        mauSac: response.data.mauSac ? response.data.mauSac.tenMau : null,
        khuyenMai: khuyenMai ? khuyenMai.tenKhuyenMai : null,
        size: response.data.size ? response.data.size.tenSize : null,
        giaBan: response.data.giaBan,
        soLuong: response.data.soLuong,
        trangThai: response.data.trangThai,
        phanTramGiamGia: khuyenMai ? khuyenMai.phanTramGiamGia : 0,
        ngayBatDau: khuyenMai && khuyenMai.ngayBatDau ? new Date(khuyenMai.ngayBatDau).toLocaleDateString("vi-VN") : "Không có",
        ngayKetThuc: khuyenMai && khuyenMai.ngayKetThuc ? new Date(khuyenMai.ngayKetThuc).toLocaleDateString("vi-VN") : "Không có",
      };
    } else {
      errorMessage.value = "Không tìm thấy sản phẩm chi tiết.";
    }
  } catch (error) {
    errorMessage.value = "Lỗi khi tải dữ liệu sản phẩm.";
    console.error("API Error: ", error);
  } finally {
    loading.value = false;
  }
};

// Lấy dữ liệu khi component được mount
onMounted(async () => {
  await fetchDropdownData();
  await fetchSanPhamChiTiet();
});
</script>


<template>
  <div class="container mt-4">
    <h2 class="text-center">Chi Tiết Sản Phẩm</h2>

    <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
    <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>

    <div v-if="!loading && SPCT">
      <div class="row">
        <div class="col-md-4">
          <!-- Hiển thị ảnh sản phẩm -->
          <div class="card">
            <img :src="SPCT.urlAnh || 'https://via.placeholder.com/150'" alt="Ảnh sản phẩm" class="img-fluid rounded">
          </div>
        </div>
        <div class="col-md-8">
          <div class="card p-4">
            <h3 class="text-primary">{{ SPCT.sanPham }}</h3>
            <div class="mb-3">
              <p><strong>Mã SPCT:</strong> {{ SPCT.maSPCT }}</p>
              <p><strong>Màu sắc:</strong> {{ SPCT.mauSac || "Không có" }}</p>
              <p><strong>Khuyến mãi:</strong> {{ SPCT.khuyenMai || "Không có" }}</p>
              <p><strong>Phần trăm giảm giá:</strong> {{ SPCT.phanTramGiamGia ? SPCT.phanTramGiamGia + "%" : "0%" }}</p>
              <p><strong>Ngày bắt đầu:</strong> {{ SPCT.ngayBatDau }}</p>
              <p><strong>Ngày kết thúc:</strong> {{ SPCT.ngayKetThuc }}</p>
              <p><strong>Size:</strong> {{ SPCT.size || "Không có" }}</p>
              <p><strong>Giá bán:</strong> {{ SPCT.giaBan.toLocaleString() }} đ</p>
              <p><strong>Số lượng:</strong> {{ SPCT.soLuong }}</p>
              <p><strong>Danh mục:</strong> {{ SPCT.tenDanhMuc ? SPCT.tenDanhMuc : "Không có" }}</p> <!-- Display Danh Mục -->
              <p><strong>Đế giày:</strong> {{ SPCT.tenDeGiay ? SPCT.tenDeGiay : "Không có" }}</p> <!-- Display Đế Giày -->
              <p><strong>Chất liệu:</strong> {{ SPCT.tenChatLieu ? SPCT.tenChatLieu : "Không có" }}</p> <!-- Display Chất Liệu -->
              <p><strong>Thương hiệu:</strong> {{ SPCT.tenThuongHieu ? SPCT.tenThuongHieu : "Không có" }}</p> <!-- Display Thương Hiệu -->
              <p><strong>Trạng thái:</strong>
                <span :class="SPCT.trangThai ? 'text-success' : 'text-danger'">{{ SPCT.trangThai ? 'Hoạt động' : 'Ngừng bán' }}</span>
              </p>
            </div>
            <div class="text-center mt-3">
              <router-link to="/admin/products/details" class="btn btn-secondary">Quay lại</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
.container {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 20px;
  background-color: #fff;
}

.card img {
  max-width: 100%;
  border-radius: 8px;
}

.card p {
  font-size: 16px;
  margin: 5px 0;
}

.card h3 {
  font-size: 24px;
  color: #007bff;
}

.text-success {
  color: green;
}

.text-danger {
  color: red;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  text-decoration: none;
}

.btn-secondary:hover {
  background-color: #5a6268;
}
</style>
