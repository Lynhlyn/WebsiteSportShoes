<template>
  <div class="modal fade" id="productFormModal" tabindex="-1" ref="modal">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ isEditing ? 'Cập nhật' : 'Thêm' }} sản phẩm</h5>
          <button type="button" class="btn-close" @click="hide"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleSubmit" enctype="multipart/form-data">
            <!-- Image Upload -->
            <ImageUpload
              ref="imageUpload"
              v-model:value="selectedFiles"
              v-model:currentImages="currentImages"
              :error="errors.files"
              :showEditHint="isEditing"
            />

            <!-- Product Name -->
            <div class="mb-3">
              <label class="form-label">Tên sản phẩm</label>
              <input
                v-model="form.tenSanPham"
                type="text"
                class="form-control"
                required
              />
              <small v-if="errors.tenSanPham" class="text-danger">{{ errors.tenSanPham }}</small>
            </div>

            <!-- Description -->
            <div class="mb-3">
              <label class="form-label">Mô tả</label>
              <textarea
                v-model="form.moTa"
                class="form-control"
                rows="3"
                required
              ></textarea>
              <small v-if="errors.moTa" class="text-danger">{{ errors.moTa }}</small>
            </div>

            <div class="row">
              <!-- Category -->
              <div class="col-md-6 mb-3">
                <label class="form-label">Danh mục</label>
                <div class="input-group">
                  <select v-model="form.danhMucId" class="form-select" required>
                    <option value="">Chọn danh mục</option>
                    <option v-for="dm in danhMucList" :key="dm.id" :value="dm.id">
                      {{ dm.tenDanhMuc }}
                    </option>
                  </select>
                  <button type="button" class="btn btn-outline-primary" @click="showAddDanhMuc">
                    <i class="bi bi-plus"></i>
                  </button>
                </div>
                <small v-if="errors.danhMucId" class="text-danger">{{ errors.danhMucId }}</small>
              </div>

              <!-- Brand -->
              <div class="col-md-6 mb-3">
                <label class="form-label">Thương hiệu</label>
                <div class="input-group">
                  <select v-model="form.thuongHieuId" class="form-select" required>
                    <option value="">Chọn thương hiệu</option>
                    <option v-for="th in thuongHieuList" :key="th.id" :value="th.id">
                      {{ th.tenThuongHieu }}
                    </option>
                  </select>
                  <button type="button" class="btn btn-outline-primary" @click="showAddThuongHieu">
                    <i class="bi bi-plus"></i>
                  </button>
                </div>
                <small v-if="errors.thuongHieuId" class="text-danger">{{ errors.thuongHieuId }}</small>
              </div>

              <!-- Material -->
              <div class="col-md-6 mb-3">
                <label class="form-label">Chất liệu</label>
                <div class="input-group">
                  <select v-model="form.chatLieuId" class="form-select" required>
                    <option value="">Chọn chất liệu</option>
                    <option v-for="cl in chatLieuList" :key="cl.id" :value="cl.id">
                      {{ cl.tenChatLieu }}
                    </option>
                  </select>
                  <button type="button" class="btn btn-outline-primary" @click="showAddChatLieu">
                    <i class="bi bi-plus"></i>
                  </button>
                </div>
                <small v-if="errors.chatLieuId" class="text-danger">{{ errors.chatLieuId }}</small>
              </div>

              <!-- Sole -->
              <div class="col-md-6 mb-3">
                <label class="form-label">Đế giày</label>
                <div class="input-group">
                  <select v-model="form.deGiayId" class="form-select" required>
                    <option value="">Chọn đế giày</option>
                    <option v-for="dg in deGiayList" :key="dg.id" :value="dg.id">
                      {{ dg.tenDeGiay }}
                    </option>
                  </select>
                  <button type="button" class="btn btn-outline-primary" @click="showAddDeGiay">
                    <i class="bi bi-plus"></i>
                  </button>
                </div>
                <small v-if="errors.deGiayId" class="text-danger">{{ errors.deGiayId }}</small>
              </div>
            </div>

            <div class="d-flex gap-2">
              <button type="submit" class="btn btn-primary flex-grow-1" :disabled="loading">
                <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                {{ isEditing ? 'Cập nhật' : 'Thêm mới' }}
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

  <!-- Add Property Modals -->
  <add-property-modal
    ref="danhMucModal"
    title="Thêm danh mục"
    property-name="tenDanhMuc"
    :api-url="`${API_URL}/danh-muc/add`"
    @added="handlePropertyAdded('danhMuc')"
  />
  <add-property-modal
    ref="thuongHieuModal"
    title="Thêm thương hiệu"
    property-name="tenThuongHieu"
    :api-url="`${API_URL}/thuong-hieu/add`"
    @added="handlePropertyAdded('thuongHieu')"
  />
  <add-property-modal
    ref="chatLieuModal"
    title="Thêm chất liệu"
    property-name="tenChatLieu"
    :api-url="`${API_URL}/chat-lieu/add`"
    @added="handlePropertyAdded('chatLieu')"
  />
  <add-property-modal
    ref="deGiayModal"
    title="Thêm đế giày"
    property-name="tenDeGiay"
    :api-url="`${API_URL}/de-giay/add`"
    @added="handlePropertyAdded('deGiay')"
  />
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Modal } from 'bootstrap';
import AddPropertyModal from './AddPropertyModal.vue';
import ImageUpload from './ImageUpload.vue';
import axios from 'axios';

const API_URL = "http://localhost:8080";
const emit = defineEmits(['saved', 'updated']);

const modal = ref(null);
const bsModal = ref(null);
const loading = ref(false);
const isEditing = ref(false);
const editingId = ref(null);
const errors = ref({});
const selectedFiles = ref([]);
const currentImages = ref([]);
const imageUpload = ref(null);

// Property modals
const danhMucModal = ref(null);
const thuongHieuModal = ref(null);
const chatLieuModal = ref(null);
const deGiayModal = ref(null);

// Form data
const form = ref({
  tenSanPham: '',
  moTa: '',
  danhMucId: '',
  thuongHieuId: '',
  chatLieuId: '',
  deGiayId: '',
  trangThai: true
});

// Dropdown data
const danhMucList = ref([]);
const thuongHieuList = ref([]);
const chatLieuList = ref([]);
const deGiayList = ref([]);

// Methods
const show = (product = null) => {
  if (product) {
    isEditing.value = true;
    editingId.value = product.id;
    currentImages.value = product.danhSachAnh || [];
    
    const danhMucId = danhMucList.value.find(dm => dm.tenDanhMuc === product.danhMuc)?.id;
    const thuongHieuId = thuongHieuList.value.find(th => th.tenThuongHieu === product.thuongHieu)?.id;
    const chatLieuId = chatLieuList.value.find(cl => cl.tenChatLieu === product.chatLieu)?.id;
    const deGiayId = deGiayList.value.find(dg => dg.tenDeGiay === product.deGiay)?.id;

    form.value = {
      tenSanPham: product.tenSanPham,
      moTa: product.moTa,
      danhMucId: danhMucId,
      thuongHieuId: thuongHieuId,
      chatLieuId: chatLieuId,
      deGiayId: deGiayId,
      trangThai: product.trangThai
    };
  } else {
    isEditing.value = false;
    editingId.value = null;
    resetForm();
  }
  bsModal.value.show();
};

const hide = () => {
  bsModal.value.hide();
  resetForm();
};

const resetForm = () => {
  form.value = {
    tenSanPham: '',
    moTa: '',
    danhMucId: '',
    thuongHieuId: '',
    chatLieuId: '',
    deGiayId: '',
    trangThai: true
  };
  selectedFiles.value = [];
  currentImages.value = [];
  errors.value = {};
  if (imageUpload.value) {
    imageUpload.value.reset();
  }
};

const validateForm = async () => {
  errors.value = {};
  
  if (!form.value.tenSanPham.trim()) {
    errors.value.tenSanPham = 'Tên sản phẩm không được để trống';
  } else {
    try {
      const response = await axios.get(`${API_URL}/san-pham/check-name`, {
        params: { 
          tenSanPham: form.value.tenSanPham,
          id: editingId.value
        }
      });
      if (response.data.exists) {
        errors.value.tenSanPham = 'Tên sản phẩm đã tồn tại';
      }
    } catch (error) {
      console.error('Error checking product name:', error);
    }
  }
  
  if (!form.value.moTa.trim() || form.value.moTa.length < 25) {
    errors.value.moTa = 'Mô tả phải có ít nhất 25 ký tự';
  }
  
  if (!form.value.danhMucId) {
    errors.value.danhMucId = 'Vui lòng chọn danh mục';
  }
  
  if (!form.value.thuongHieuId) {
    errors.value.thuongHieuId = 'Vui lòng chọn thương hiệu';
  }
  
  if (!form.value.chatLieuId) {
    errors.value.chatLieuId = 'Vui lòng chọn chất liệu';
  }
  
  if (!form.value.deGiayId) {
    errors.value.deGiayId = 'Vui lòng chọn đế giày';
  }
  
  if (!isEditing.value && selectedFiles.value.length === 0) {
    errors.value.files = 'Vui lòng chọn ít nhất một hình ảnh';
  }

  return Object.keys(errors.value).length === 0;
};

const handleSubmit = async () => {
  const action = isEditing.value ? "Cập nhật" : "Thêm mới";
  if (!confirm(`Bạn có chắc chắn muốn ${action} sản phẩm này?`)) {
    return; // Dừng lại nếu người dùng không xác nhận
  }
  if (!await validateForm()) return;

  const data = {
    tenSanPham: form.value.tenSanPham,
    moTa: form.value.moTa,
    danhMuc: { id: form.value.danhMucId },
    thuongHieu: { id: form.value.thuongHieuId },
    chatLieu: { id: form.value.chatLieuId },
    deGiay: { id: form.value.deGiayId },
    trangThai: form.value.trangThai
  };

  loading.value = true;
  try {
    const formData = new FormData();
    formData.append('sanPham', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    
    if (selectedFiles.value.length > 0) {
      selectedFiles.value.forEach(file => {
        formData.append('files', file);
      });
    }

    if (isEditing.value) {
      // Add oldImageUrls parameter for updating
      const params = new URLSearchParams();
      currentImages.value.forEach(url => {
        params.append('oldImageUrls', url);
      });

      await axios.put(
        `${API_URL}/san-pham/updateSPWithFile/${editingId.value}?${params.toString()}`, 
        formData,
        {
          headers: { 'Content-Type': 'multipart/form-data' }
        }
      );
      emit('updated');
    } else {
      await axios.post(`${API_URL}/san-pham/addSPWithFile`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      emit('saved');
    }
    hide();
  } catch (error) {
    console.error('Lỗi khi lưu sản phẩm:', error);
    alert('Có lỗi xảy ra khi lưu sản phẩm');
  } finally {
    loading.value = false;
  }
};

// Property modal methods
const showAddDanhMuc = () => danhMucModal.value.show();
const showAddThuongHieu = () => thuongHieuModal.value.show();
const showAddChatLieu = () => chatLieuModal.value.show();
const showAddDeGiay = () => deGiayModal.value.show();

const handlePropertyAdded = async (type) => {
  await fetchDropdownData();
};

// Fetch dropdown data
const fetchDropdownData = async () => {
  try {
    const [danhMucRes, thuongHieuRes, chatLieuRes, deGiayRes] = await Promise.all([
      axios.get(`${API_URL}/danh-muc`),
      axios.get(`${API_URL}/thuong-hieu`),
      axios.get(`${API_URL}/chat-lieu`),
      axios.get(`${API_URL}/de-giay`)
    ]);

    danhMucList.value = danhMucRes.data;
    thuongHieuList.value = thuongHieuRes.data;
    chatLieuList.value = chatLieuRes.data;
    deGiayList.value = deGiayRes.data;
  } catch (error) {
    console.error('Error fetching dropdown data:', error);
  }
};

onMounted(() => {
  bsModal.value = new Modal(modal.value);
  fetchDropdownData();
});

defineExpose({
  show
});
</script>

<style scoped>
.modal-lg {
  max-width: 800px;
}

.input-group .btn {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
</style>