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
            <div class="mb-3">
              <label class="form-label">Tên</label>
              <input
                v-model="form[propertyName]"
                type="text"
                class="form-control"
                required
              />
              <small v-if="error" class="text-danger">{{ error }}</small>
            </div>
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

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  propertyName: {
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
const error = ref('');
const form = ref({});

const show = () => {
  form.value = {
    [props.propertyName]: '',
  trangThai: true
  };
  error.value = '';
  bsModal.value.show();
};

const hide = () => {
  bsModal.value.hide();
};

const handleSubmit = async () => {
  if (!form.value[props.propertyName].trim()) {
    error.value = 'Vui lòng nhập tên';
    return;
  }
  // Thêm xác nhận trước khi gửi dữ liệu
    const isConfirmed = window.confirm('Bạn có chắc chắn muốn thêm mới thông tin này không?');
    if (!isConfirmed) {
      return; // Dừng lại nếu người dùng không xác nhận
    }

  loading.value = true;
  try {
    await axios.post(props.apiUrl, form.value);
    emit('added');
    hide();
  } catch (err) {
    error.value = 'Có lỗi xảy ra khi thêm mới';
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