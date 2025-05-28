<template>
  <div class="modal fade" tabindex="-1" ref="modal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ title }}</h5>
          <button type="button" class="btn-close" @click="hide"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleSubmit">
            <!-- Regular property input for non-KhuyenMai -->
            <template v-if="propertyName !== 'tenKhuyenMai'">
              <div class="mb-3">
                <label class="form-label">Tên {{ propertyLabel }}</label>
                <input
                  v-model="form[propertyName]"
                  type="text"
                  class="form-control"
                  required
                />
                <small v-if="errors[propertyName]" class="text-danger">
                  {{ errors[propertyName] }}
                </small>
              </div>
            </template>

            <!-- KhuyenMai specific fields -->
            <template v-else>
              <div class="mb-3">
                <label class="form-label">Mã khuyến mãi</label>
                <input
                  v-model="form.maKhuyenMai"
                  type="text"
                  class="form-control"
                  required
                />
                <small v-if="errors.maKhuyenMai" class="text-danger">
                  {{ errors.maKhuyenMai }}
                </small>
              </div>

              <div class="mb-3">
                <label class="form-label">Tên khuyến mãi</label>
                <input
                  v-model="form.tenKhuyenMai"
                  type="text"
                  class="form-control"
                  required
                />
                <small v-if="errors.tenKhuyenMai" class="text-danger">
                  {{ errors.tenKhuyenMai }}
                </small>
              </div>

              <div class="mb-3">
                <label class="form-label">Phần trăm giảm giá (%)</label>
                <input
                  v-model="form.phanTramGiamGia"
                  type="number"
                  min="0"
                  max="100"
                  step="0.1"
                  class="form-control"
                  required
                />
                <small v-if="errors.phanTramGiamGia" class="text-danger">
                  {{ errors.phanTramGiamGia }}
                </small>
              </div>

              <div class="mb-3">
                <label class="form-label">Ngày bắt đầu</label>
                <input
                  v-model="form.ngayBatDau"
                  type="datetime-local"
                  class="form-control"
                  required
                />
                <small v-if="errors.ngayBatDau" class="text-danger">
                  {{ errors.ngayBatDau }}
                </small>
              </div>

              <div class="mb-3">
                <label class="form-label">Ngày kết thúc</label>
                <input
                  v-model="form.ngayKetThuc"
                  type="datetime-local"
                  class="form-control"
                  required
                />
                <small v-if="errors.ngayKetThuc" class="text-danger">
                  {{ errors.ngayKetThuc }}
                </small>
              </div>
            </template>

            <div class="d-flex gap-2">
              <button type="submit" class="btn btn-primary flex-grow-1" :disabled="loading">
                <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                Thêm mới
              </button>
              <button type="button" class="btn btn-secondary" @click="hide">
                Hủy
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Modal } from 'bootstrap';
import axios from 'axios';
import moment from 'moment';

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  propertyName: {
    type: String,
    required: true
  },
  propertyLabel: {
    type: String,
    required: true
  },
  apiUrl: {
    type: String,
    required: true
  }
});

const emit = defineEmits(['added']);

const modal = ref(null);
const bsModal = ref(null);
const loading = ref(false);
const errors = ref({});
const form = ref({});

const show = () => {
  resetForm();
  bsModal.value.show();
};

const hide = () => {
  bsModal.value.hide();
  resetForm();
};

const resetForm = () => {
  if (props.propertyName === 'tenKhuyenMai') {
    form.value = {
      maKhuyenMai: '',
      tenKhuyenMai: '',
      phanTramGiamGia: '',
      ngayBatDau: '',
      ngayKetThuc: '',
      trangThai: true
    };
  } else {
    form.value = {
      [props.propertyName]: '',
      trangThai: true
    };
  }
  errors.value = {};
};

const validateKhuyenMaiForm = () => {
  errors.value = {};

  if (!form.value.maKhuyenMai?.trim()) {
    errors.value.maKhuyenMai = 'Mã khuyến mãi không được để trống';
  }

  if (!form.value.tenKhuyenMai?.trim()) {
    errors.value.tenKhuyenMai = 'Tên khuyến mãi không được để trống';
  }

  if (!form.value.phanTramGiamGia || form.value.phanTramGiamGia < 0 || form.value.phanTramGiamGia > 100) {
    errors.value.phanTramGiamGia = 'Phần trăm giảm giá phải từ 0 đến 100';
  }

  if (!form.value.ngayBatDau) {
    errors.value.ngayBatDau = 'Ngày bắt đầu không được để trống';
  }

  if (!form.value.ngayKetThuc) {
    errors.value.ngayKetThuc = 'Ngày kết thúc không được để trống';
  }

  if (form.value.ngayBatDau && form.value.ngayKetThuc) {
    const startDate = moment(form.value.ngayBatDau);
    const endDate = moment(form.value.ngayKetThuc);
    
    if (endDate.isBefore(startDate)) {
      errors.value.ngayKetThuc = 'Ngày kết thúc phải sau ngày bắt đầu';
    }
  }

  return Object.keys(errors.value).length === 0;
};

const validateRegularForm = () => {
  errors.value = {};
  
  if (!form.value[props.propertyName]?.trim()) {
    errors.value[props.propertyName] = `Vui lòng nhập tên ${props.propertyLabel}`;
    return false;
  }
  
  return true;
};

const handleSubmit = async () => {
  const isValid = props.propertyName === 'tenKhuyenMai' 
    ? validateKhuyenMaiForm()
    : validateRegularForm();

  if (!isValid) return;

  if (!window.confirm('Bạn có chắc chắn muốn thêm thông tin này không?')) {
    return; // Dừng lại nếu người dùng không xác nhận
  }

  loading.value = true;
  try {
    let dataToSend = { ...form.value };
    
    if (props.propertyName === 'tenKhuyenMai') {
      dataToSend = {
        ...dataToSend,
        ngayBatDau: moment(form.value.ngayBatDau).format('YYYY-MM-DD HH:mm:ss'),
        ngayKetThuc: moment(form.value.ngayKetThuc).format('YYYY-MM-DD HH:mm:ss'),
        phanTramGiamGia: parseFloat(form.value.phanTramGiamGia)
      };
    }

    await axios.post(props.apiUrl, dataToSend);
    emit('added');
    hide();
  } catch (err) {
    console.error('Error adding item:', err);
    errors.value = { general: 'Có lỗi xảy ra khi thêm mới' };
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  bsModal.value = new Modal(modal.value);
});

defineExpose({
  show
});
</script>

<style scoped>
.modal-dialog {
  max-width: 500px;
}

.form-control:focus,
.form-select:focus {
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}
</style>
