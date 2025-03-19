<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const id = route.params.id; // Get product ID from the route params
const BASE_URL = "http://localhost:8080/san-pham";  // Your API endpoint

const product = ref({
  tenSanPham: "",
  moTa: "",
  trangThai: true,
  danhMuc: null,
  thuongHieu: null,
  chatLieu: null,
  deGiay: null,
  anhDauTien: "",
  danhSachAnh: []  // Correct property name for images
});

const loading = ref(false);
const errorMessage = ref("");

// Fetch the product details by ID
const fetchProductDetails = async () => {
  if (!id) {
    errorMessage.value = "ID sản phẩm không hợp lệ!";
    return;
  }

  loading.value = true;
  try {
    const response = await axios.get(`${BASE_URL}/${id}`);
    if (response.data) {
      product.value = response.data;
    } else {
      errorMessage.value = "Không tìm thấy sản phẩm!";
    }
  } catch (error) {
    console.error("Error fetching product details:", error);
    errorMessage.value = "Lỗi khi tải dữ liệu sản phẩm.";
  } finally {
    loading.value = false;
  }
};

// Fetch product details when the component is mounted
onMounted(() => {
  fetchProductDetails();
});
</script>

<template>
  <div class="container mt-4">
    <h2 class="text-center">Chi Tiết Sản Phẩm</h2>

    <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
    <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>

    <div v-if="!loading && product">
      <div class="row">
        <div class="col-md-4">
          <!-- Display product images with a carousel -->
          <div id="carouselExampleCaptions" class="carousel slide mb-4" data-bs-ride="carousel">
            <div class="carousel-indicators">
              <!-- Dynamically generate the indicators based on the number of images -->
              <button
                v-for="(image, index) in product.danhSachAnh"
                :key="index"
                :data-bs-target="'#carouselExampleCaptions'"
                :data-bs-slide-to="index"
                :class="index === 0 ? 'active' : ''"
                :aria-label="'Slide ' + (index + 1)"
              ></button>
            </div>
            <div class="carousel-inner">
              <!-- Dynamically generate the carousel items based on the images -->
              <div
                v-for="(image, index) in product.danhSachAnh"
                :key="index"
                class="carousel-item"
                :class="index === 0 ? 'active' : ''"
              >
                <img :src="image" class="d-block w-100" :alt="'Ảnh sản phẩm ' + (index + 1)" />
              </div>
            </div>
            <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide="prev">
              <span class="carousel-control-prev-icon" aria-hidden="true"></span>
              <span class="visually-hidden">Previous</span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide="next">
              <span class="carousel-control-next-icon" aria-hidden="true"></span>
              <span class="visually-hidden">Next</span>
            </button>
          </div>
        </div>
        <div class="col-md-8">
          <div class="card p-4">
            <h3 class="text-primary">{{ product.tenSanPham }}</h3>
            <div class="mb-3">
              <p><strong>Mô tả:</strong> {{ product.moTa || "Không có mô tả" }}</p>
              <p><strong>Danh Mục:</strong> {{ product.danhMuc || "Không có" }}</p>
              <p><strong>Thương hiệu:</strong> {{ product.thuongHieu || "Không có"}}</p>
              <p><strong>Chất liệu:</strong> {{ product.chatLieu || "Không có" }}</p>
              <p><strong>Đế giày:</strong> {{ product.deGiay || "Không có" }}</p>
              <p><strong>Trạng thái:</strong>
                <span :class="product.trangThai ? 'text-success' : 'text-danger'">
                  {{ product.trangThai ? 'Hoạt động' : 'Ngừng bán' }}
                </span>
              </p>
            </div>
            <div class="text-center mt-3">
              <router-link to="/admin/products/manage" class="btn btn-secondary">Quay lại</router-link>
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

.carousel-container {
  position: relative;
}

.carousel-inner {
  display: flex;
  overflow: hidden;
}

.carousel-item img {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.carousel-prev, .carousel-next {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 24px;
  border: none;
  padding: 10px;
  cursor: pointer;
}

.carousel-prev {
  left: 10px;
}

.carousel-next {
  right: 10px;
}
</style>
