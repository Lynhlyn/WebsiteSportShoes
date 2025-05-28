<template>
  <div class="container py-4">
    <h2 class="text-center mb-4">Cập nhật khuyến mãi</h2>

    <div class="card shadow-sm">
      <div class="card-body">
        <div v-if="loading" class="text-center py-4">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>

        <form v-else @submit.prevent="handleSubmit" class="needs-validation" novalidate>
          <!-- Mã khuyến mãi -->
          <div class="mb-3">
            <label for="maKhuyenMai" class="form-label">Mã khuyến mãi</label>
            <input
              v-model="formData.maKhuyenMai"
              type="text"
              class="form-control"
              id="maKhuyenMai"
              disabled
            />
          </div>

          <!-- Tên khuyến mãi -->
          <div class="mb-3">
            <label for="tenKhuyenMai" class="form-label">Tên khuyến mãi <span class="text-danger">*</span></label>
            <input
              v-model="formData.tenKhuyenMai"
              type="text"
              class="form-control"
              id="tenKhuyenMai"
              :class="{ 'is-invalid': errors.tenKhuyenMai }"
              required
            />
            <div class="invalid-feedback">{{ errors.tenKhuyenMai }}</div>
          </div>

          <!-- Ngày bắt đầu -->
          <div class="mb-3">
            <label for="ngayBatDau" class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
            <input
              v-model="formData.ngayBatDau"
              type="datetime-local"
              class="form-control"
              id="ngayBatDau"
              :class="{ 'is-invalid': errors.ngayBatDau }"
              step="1"
              required
            />
            <div class="invalid-feedback">{{ errors.ngayBatDau }}</div>
          </div>

          <!-- Ngày kết thúc -->
          <div class="mb-3">
            <label for="ngayKetThuc" class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
            <input
              v-model="formData.ngayKetThuc"
              type="datetime-local"
              class="form-control"
              id="ngayKetThuc"
              :class="{ 'is-invalid': errors.ngayKetThuc }"
              :min="formData.ngayBatDau"
              step="1"
              required
            />
            <div class="invalid-feedback">{{ errors.ngayKetThuc }}</div>
          </div>

          <!-- Phần trăm giảm giá -->
          <div class="mb-3">
            <label for="phanTramGiamGia" class="form-label">Phần trăm giảm giá <span class="text-danger">*</span></label>
            <div class="input-group">
              <input
                v-model="formData.phanTramGiamGia"
                type="number"
                class="form-control"
                id="phanTramGiamGia"
                :class="{ 'is-invalid': errors.phanTramGiamGia }"
                min="0"
                max="100"
                required
              />
              <span class="input-group-text">%</span>
              <div class="invalid-feedback">{{ errors.phanTramGiamGia }}</div>
            </div>
          </div>

          <!-- Trạng thái -->
          <div class="mb-3">
            <label for="trangThai" class="form-label">Trạng thái</label>
            <select v-model="formData.trangThai" class="form-select" id="trangThai">
              <option :value="true">Hoạt động</option>
              <option :value="false">Không hoạt động</option>
            </select>
          </div>

          <!-- API Error -->
          <div v-if="errors.api" class="alert alert-danger mb-3">
            {{ errors.api }}
          </div>

          <!-- Form Actions -->
          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary" :disabled="loading">
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              {{ loading ? 'Đang xử lý...' : 'Cập nhật' }}
            </button>
            <button type="button" class="btn btn-secondary" @click="router.go(-1)">
              Quay lại
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const API_URL = "http://localhost:8080/khuyen-mai";

// Form state
const loading = ref(false);
const formData = ref({
  maKhuyenMai: '',
  tenKhuyenMai: '',
  ngayBatDau: '',
  ngayKetThuc: '',
  phanTramGiamGia: '',
  trangThai: true
});

const errors = ref({
  tenKhuyenMai: '',
  ngayBatDau: '',
  ngayKetThuc: '',
  phanTramGiamGia: '',
  api: ''
});

// Methods
const formatDateTime = (dateString) => {
  if (!dateString) return null;
  const date = new Date(dateString);
  return date.toISOString().slice(0, 19).replace('T', ' ');
};

const formatDateTimeForInput = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toISOString().slice(0, 19);
};

const validateForm = () => {
  let isValid = true;
  errors.value = {
    tenKhuyenMai: '',
    ngayBatDau: '',
    ngayKetThuc: '',
    phanTramGiamGia: '',
    api: ''
  };

  // Tên khuyến mãi
  if (!formData.value.tenKhuyenMai.trim()) {
    errors.value.tenKhuyenMai = 'Vui lòng nhập tên khuyến mãi';
    isValid = false;
  } else if (formData.value.tenKhuyenMai.length > 50) {
    errors.value.tenKhuyenMai = 'Tên khuyến mãi không được vượt quá 50 ký tự';
    isValid = false;
  }

  // Ngày bắt đầu
  if (!formData.value.ngayBatDau) {
    errors.value.ngayBatDau = 'Vui lòng chọn ngày bắt đầu';
    isValid = false;
  }

  // Ngày kết thúc
  if (!formData.value.ngayKetThuc) {
    errors.value.ngayKetThuc = 'Vui lòng chọn ngày kết thúc';
    isValid = false;
  } else if (formData.value.ngayBatDau) {
    const startDate = new Date(formData.value.ngayBatDau);
    const endDate = new Date(formData.value.ngayKetThuc);
    if (endDate <= startDate) {
      errors.value.ngayKetThuc = 'Ngày kết thúc phải lớn hơn ngày bắt đầu';
      isValid = false;
    }
  }

  // Phần trăm giảm giá
  const discount = Number(formData.value.phanTramGiamGia);
  if (!formData.value.phanTramGiamGia) {
    errors.value.phanTramGiamGia = 'Vui lòng nhập phần trăm giảm giá';
    isValid = false;
  } else if (isNaN(discount) || discount < 0 || discount > 100) {
    errors.value.phanTramGiamGia = 'Phần trăm giảm giá phải từ 0 đến 100';
    isValid = false;
  }

  return isValid;
};

const fetchKhuyenMai = async () => {
  if (!route.params.id) return;
  
  loading.value = true;
  try {
    const response = await axios.get(`${API_URL}/${route.params.id}`);
    const data = response.data;
    
    formData.value = {
      maKhuyenMai: data.maKhuyenMai,
      tenKhuyenMai: data.tenKhuyenMai,
      ngayBatDau: formatDateTimeForInput(data.ngayBatDau),
      ngayKetThuc: formatDateTimeForInput(data.ngayKetThuc),
      phanTramGiamGia: data.phanTramGiamGia,
      trangThai: data.trangThai
    };
  } catch (error) {
    console.error('Error fetching promotion:', error);
    errors.value.api = 'Không thể tải thông tin khuyến mãi. Vui lòng thử lại!';
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!validateForm()) return;
 const isConfirmed = window.confirm('Bạn có chắc chắn muốn sửa khuyến mãi này không?');

  // Nếu người dùng không xác nhận, dừng việc gửi dữ liệu
  if (!isConfirmed) {
    return; // Dừng lại nếu không xác nhận
  }
  loading.value = true;
  errors.value.api = '';

  try {
    const response = await axios.put(`${API_URL}/${route.params.id}`, {
      ...formData.value,
      ngayBatDau: formatDateTime(formData.value.ngayBatDau),
      ngayKetThuc: formatDateTime(formData.value.ngayKetThuc),
      phanTramGiamGia: Number(formData.value.phanTramGiamGia)
    });

    if (response.data) {
      alert('Cập nhật khuyến mãi thành công!');
      router.push('/admin/products/promotions');
    }
  } catch (error) {
    console.error('Error updating promotion:', error);
    errors.value.api = error.response?.data?.message || 'Có lỗi xảy ra khi cập nhật khuyến mãi. Vui lòng thử lại!';
  } finally {
    loading.value = false;
  }
};

// Lifecycle
onMounted(() => {
  fetchKhuyenMai();
});
</script>

<style scoped>
.card {
  border-radius: 8px;
}

.form-label {
  font-weight: 500;
}

.invalid-feedback {
  display: block;
}

input[type="datetime-local"] {
  min-width: 250px;
}
</style>