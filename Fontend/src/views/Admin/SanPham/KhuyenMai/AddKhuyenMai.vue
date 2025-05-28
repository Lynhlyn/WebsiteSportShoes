<template>
  <div class="container py-4">
    <h2 class="text-center mb-4">Thêm khuyến mãi mới</h2>

    <div class="card shadow-sm">
      <div class="card-body">
        <form @submit.prevent="handleSubmit" class="needs-validation" novalidate>
          <!-- Mã khuyến mãi -->


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
              :min="minStartDate"
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
              {{ loading ? 'Đang xử lý...' : 'Thêm khuyến mãi' }}
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
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const API_URL = "http://localhost:8080/khuyen-mai/addKM";

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
  maKhuyenMai: '',
  tenKhuyenMai: '',
  ngayBatDau: '',
  ngayKetThuc: '',
  phanTramGiamGia: '',
  api: ''
});

// Computed
const minStartDate = computed(() => {
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  return now.toISOString().slice(0, 16);
});

// Methods
const formatDateTime = (dateString) => {
  if (!dateString) return null;
  const date = new Date(dateString);
  return date.toISOString().slice(0, 19).replace('T', ' ');
};

const validateForm = () => {
  let isValid = true;
  errors.value = {
    maKhuyenMai: '',
    tenKhuyenMai: '',
    ngayBatDau: '',
    ngayKetThuc: '',
    phanTramGiamGia: '',
    api: ''
  };

  // Mã khuyến mãi


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
  } else {
    const startDate = new Date(formData.value.ngayBatDau);
    const now = new Date();
    if (startDate < now) {
      errors.value.ngayBatDau = 'Ngày bắt đầu phải lớn hơn hoặc bằng thời điểm hiện tại';
      isValid = false;
    }
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
const generateMaKhuyenMai = async () => {
  try {
    // Gọi API để lấy tất cả mã khuyến mãi
    const response = await axios.get(`http://localhost:8080/khuyen-mai`);

    const khuyenMaiList = response.data; // Danh sách các khuyến mãi hiện có

    if (khuyenMaiList && khuyenMaiList.length > 0) {
      // Lấy mã khuyến mãi lớn nhất
      const maxMaKM = khuyenMaiList.reduce((max, km) => {
        const numericPart = parseInt(km.maKhuyenMai.replace('KM', ''), 10); // Tách phần số
        return numericPart > max ? numericPart : max;
      }, 0);

      // Tạo mã mới cho khuyến mãi
      return `KM${maxMaKM + 1}`;
    } else {
      // Nếu không có khuyến mãi nào, bắt đầu từ KM1
      return "KM1";
    }
  } catch (error) {
    console.error("Error fetching promotion list:", error);
    return "KM1"; // Mặc định là KM1 nếu có lỗi
  }
};


const handleSubmit = async () => {
  if (!validateForm()) return;
 const isConfirmed = window.confirm('Bạn có chắc chắn muốn thêm khuyến mãi này không?');

  // Nếu người dùng không xác nhận, dừng việc gửi dữ liệu
  if (!isConfirmed) {
    return; // Dừng lại nếu không xác nhận
  }
  formData.value.maKhuyenMai = await generateMaKhuyenMai();
  loading.value = true;
  errors.value.api = '';

  try {
    const response = await axios.post(API_URL, {
      ...formData.value,
      ngayBatDau: formatDateTime(formData.value.ngayBatDau),
      ngayKetThuc: formatDateTime(formData.value.ngayKetThuc),
      phanTramGiamGia: Number(formData.value.phanTramGiamGia)
    });

    if (response.data) {
      alert('Thêm khuyến mãi thành công!');
      router.push('/admin/products/promotions');
    }
  } catch (error) {
    console.error('Error adding promotion:', error);
    errors.value.api = error.response?.data?.message || 'Có lỗi xảy ra khi thêm khuyến mãi. Vui lòng thử lại!';
  } finally {
    loading.value = false;
  }
};
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