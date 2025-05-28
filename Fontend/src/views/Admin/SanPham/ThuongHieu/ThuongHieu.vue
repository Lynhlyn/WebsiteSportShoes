<template>
  <div class="container py-4">
  <!-- Toast Messages -->
<div class="toast-container position-fixed top-0 end-0 p-3">
  <div 
    class="toast align-items-center text-white border-0" 
    :class="message.type === 'success' ? 'bg-success' : 'bg-danger'"
    role="alert" 
    ref="toast"
  >
    <div class="d-flex">
      <div class="toast-body">
        {{ message.text }}
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
    </div>
  </div>
</div>

    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="mb-0">Quản lý Thương Hiệu</h2>
      <button class="btn btn-primary" @click="showFormModal()">
        <i class="bi bi-plus-circle me-2"></i>Thêm thương hiệu
      </button>
    </div>

    <!-- Search Controls -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3 align-items-center">
          <div class="col-md-6">
            <div class="input-group">
              <input
                v-model="searchQuery"
                type="text"
                class="form-control"
                placeholder="Tìm kiếm thương hiệu..."
                @input="handleSearch"
              />
              <button class="btn btn-outline-secondary" type="button" @click="handleSearch">
                <i class="bi bi-search"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ThuongHieu List -->
    <div class="card">
      <div class="card-body">
        <h5 class="card-title mb-3">Danh sách thương hiệu</h5>

        <!-- Table -->
        <div class="table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th>STT</th>
                <th>
                  <div class="d-flex align-items-center gap-2">
                    Tên thương hiệu
                    <button 
                      class="btn btn-link btn-sm p-0" 
                      @click="toggleSort('tenThuongHieu')"
                    >
                      <i class="bi" :class="getSortIcon('tenThuongHieu')"></i>
                    </button>
                  </div>
                </th>
                <th>
                  <div class="d-flex align-items-center gap-2">
                    Ngày tạo
                    <button 
                      class="btn btn-link btn-sm p-0" 
                      @click="toggleSort('ngayTao')"
                    >
                      <i class="bi" :class="getSortIcon('ngayTao')"></i>
                    </button>
                  </div>
                </th>
                <th>Ngày sửa</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading" class="text-center">
                <td colspan="6">
                  <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Đang tải...</span>
                  </div>
                </td>
              </tr>
              <tr v-else-if="paginatedThuongHieus.length === 0">
                <td colspan="6" class="text-center">Không có dữ liệu</td>
              </tr>
              <tr v-for="(thuongHieu, index) in paginatedThuongHieus" :key="thuongHieu.id">
                <td>{{ startIndex + index + 1 }}</td>
                <td>{{ thuongHieu.tenThuongHieu }}</td>
                <td>{{ formatDate(thuongHieu.ngayTao) }}</td>
                <td>{{ formatDate(thuongHieu.ngaySua) }}</td>
                <td>
                  <span 
                    class="badge"
                    :class="thuongHieu.trangThai ? 'bg-success' : 'bg-danger'"
                  >
                    {{ thuongHieu.trangThai ? 'Hoạt động' : 'Không hoạt động' }}
                  </span>
                </td>
                <td>
                  <div class="d-flex gap-2">
                    <button 
                      class="btn btn-warning btn-sm"
                      @click="showFormModal(thuongHieu)"
                    >
                      <i class="bi bi-pencil"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div class="d-flex justify-content-between align-items-center mt-4">
          <div class="text-muted">
            Hiển thị {{ startIndex + 1 }} đến {{ endIndex }} trên tổng số {{ filteredThuongHieus.length }} mục
          </div>
          <div class="d-flex align-items-center gap-3">
            <select v-model="pageSize" class="form-select" style="width: auto;">
              <option :value="5">5 / trang</option>
              <option :value="10">10 / trang</option>
              <option :value="20">20 / trang</option>
              <option :value="50">50 / trang</option>
            </select>
            <nav v-if="totalPages > 1">
              <ul class="pagination mb-0">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                  <button class="page-link" @click="changePage(currentPage - 1)">
                    <i class="bi bi-chevron-left"></i>
                  </button>
                </li>
                <li 
                  v-for="page in visiblePages" 
                  :key="page" 
                  class="page-item"
                  :class="{ active: currentPage === page, disabled: page === '...' }"
                >
                  <button 
                    class="page-link" 
                    @click="page !== '...' && changePage(page)"
                  >
                    {{ page }}
                  </button>
                </li>
                <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                  <button class="page-link" @click="changePage(currentPage + 1)">
                    <i class="bi bi-chevron-right"></i>
                  </button>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
    </div>

    <!-- Form Modal -->
    <div class="modal fade" id="formModal" tabindex="-1" ref="formModal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEditing ? 'Cập nhật' : 'Thêm' }} thương hiệu</h5>
            <button type="button" class="btn-close" @click="hideFormModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleSubmit">
              <div class="mb-3">
                <label class="form-label">Tên thương hiệu</label>
                <input
                  v-model="formData.tenThuongHieu"
                  type="text"
                  class="form-control"
                  required
                  :class="{ 'is-invalid': errors.tenThuongHieu }"
                />
                <div class="invalid-feedback">{{ errors.tenThuongHieu }}</div>
              </div>
              
              <div class="mb-3">
                <label class="form-label">Trạng thái</label>
                <select v-model="formData.trangThai" class="form-select">
                  <option :value="true">Hoạt động</option>
                  <option :value="false">Không hoạt động</option>
                </select>
              </div>

              <div class="d-flex gap-2 justify-content-end">
                <button type="button" class="btn btn-secondary" @click="hideFormModal">
                  Hủy
                </button>
                <button type="submit" class="btn btn-primary" :disabled="loading">
                  <span v-if="loading" class="spinner-border spinner-border-sm me-1"></span>
                  {{ isEditing ? 'Cập nhật' : 'Thêm mới' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" ref="deleteModal">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Xác nhận xóa</h5>
            <button type="button" class="btn-close" @click="hideDeleteConfirm"></button>
          </div>
          <div class="modal-body">
            <p>Bạn có chắc chắn muốn xóa thương hiệu "{{ selectedThuongHieu?.tenThuongHieu }}"?</p>
            <p class="text-danger mb-0">Hành động này không thể hoàn tác!</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="hideDeleteConfirm">
              Hủy
            </button>
            <button 
              type="button" 
              class="btn btn-danger" 
              @click="confirmDelete"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-1"></span>
              Xóa
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import axios from 'axios';
import { Modal, Toast } from 'bootstrap';

import debounce from 'lodash/debounce';

interface ThuongHieu {
  id?: number;
  tenThuongHieu: string;
  ngayTao?: string;
  ngaySua?: string;
  trangThai: boolean;
}

const API_URL = 'http://localhost:8080/thuong-hieu';

// State
const thuongHieus = ref<ThuongHieu[]>([]);
const loading = ref(false);
const searchQuery = ref('');
const errors = ref({ tenThuongHieu: '' });
const isEditing = ref(false);
const formModal = ref<HTMLElement | null>(null);
const deleteModal = ref<HTMLElement | null>(null);
const selectedThuongHieu = ref<ThuongHieu | null>(null);
let bsFormModal: Modal | null = null;
let bsDeleteModal: Modal | null = null;

// Pagination state
const currentPage = ref(1);
const pageSize = ref(10);
const sortField = ref('ngayTao');
const sortDirection = ref<'asc' | 'desc'>('desc');
// Toast
const toast = ref<HTMLElement | null>(null);
let bsToast: Toast | null = null;
const message = ref({ type: 'success', text: '' });

const showMessage = (text: string, type: 'success' | 'error' = 'success') => {
  message.value = { text, type };
  bsToast?.show();
};

const formData = ref<ThuongHieu>({
  tenThuongHieu: '',
  trangThai: true
});

// Computed properties
const filteredThuongHieus = computed(() => {
  let result = [...thuongHieus.value];
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(th => 
      th.tenThuongHieu.toLowerCase().includes(query)
    );
  }
  
  result.sort((a, b) => {
    const aValue = a[sortField.value as keyof ThuongHieu];
    const bValue = b[sortField.value as keyof ThuongHieu];
    
    if (sortDirection.value === 'asc') {
      return aValue < bValue ? -1 : 1;
    } else {
      return aValue > bValue ? -1 : 1;
    }
  });
  
  return result;
});

const totalPages = computed(() => Math.ceil(filteredThuongHieus.value.length / pageSize.value));
const startIndex = computed(() => (currentPage.value - 1) * pageSize.value);
const endIndex = computed(() => Math.min(startIndex.value + pageSize.value, filteredThuongHieus.value.length));

const paginatedThuongHieus = computed(() => {
  return filteredThuongHieus.value.slice(startIndex.value, endIndex.value);
});

const visiblePages = computed(() => {
  const delta = 2;
  const range = [];
  const rangeWithDots = [];
  let l;

  for (let i = 1; i <= totalPages.value; i++) {
    if (
      i === 1 || 
      i === totalPages.value || 
      (i >= currentPage.value - delta && i <= currentPage.value + delta)
    ) {
      range.push(i);
    }
  }

  range.forEach(i => {
    if (l) {
      if (i - l === 2) {
        rangeWithDots.push(l + 1);
      } else if (i - l !== 1) {
        rangeWithDots.push('...');
      }
    }
    rangeWithDots.push(i);
    l = i;
  });

  return rangeWithDots;
});

// Methods
const fetchThuongHieus = async () => {
  loading.value = true;
  try {
    const response = await axios.get(API_URL);
    thuongHieus.value = response.data;
  } catch (error) {
    console.error('Error fetching brands:', error);
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formData.value.tenThuongHieu.trim()) {
    errors.value.tenThuongHieu = 'Tên thương hiệu không được để trống';
    return;
  }

   // Hiển thị hộp thoại xác nhận trước khi thực hiện thao tác thêm hoặc cập nhật
    const isConfirmed = window.confirm(
      isEditing.value ? 'Bạn có chắc chắn muốn cập nhật thương hiệu này không?' : 'Bạn có chắc chắn muốn thêm thương hiệu này không?'
    );

    // Nếu không xác nhận, dừng lại
    if (!isConfirmed) {
      return;
    }

  loading.value = true;
  try {
    if (isEditing.value) {
      await axios.put(`${API_URL}/${formData.value.id}`, formData.value);
       showMessage('Cập nhật thương hiệu thành công!');
    } else {
      await axios.post(`${API_URL}/add`, formData.value);
        showMessage('Thêm thương hiệu thành công!');
    }
    await fetchThuongHieus();
    hideFormModal();
  } catch (error) {
    console.error('Error saving brand:', error);
      showMessage('Có lỗi xảy ra khi lưu thương hiệu', 'error');
  } finally {
    loading.value = false;
  }
};

const showFormModal = (thuongHieu?: ThuongHieu) => {
  if (thuongHieu) {
    formData.value = { ...thuongHieu };
    isEditing.value = true;
  } else {
    resetForm();
  }
  bsFormModal?.show();
};

const hideFormModal = () => {
  bsFormModal?.hide();
  resetForm();
};

const showDeleteConfirm = (thuongHieu: ThuongHieu) => {
  selectedThuongHieu.value = thuongHieu;
  bsDeleteModal?.show();
};

const hideDeleteConfirm = () => {
  bsDeleteModal?.hide();
  selectedThuongHieu.value = null;
};

const confirmDelete = async () => {
  if (!selectedThuongHieu.value?.id) return;

  loading.value = true;
  try {
    await axios.delete(`${API_URL}/${selectedThuongHieu.value.id}`);
    await fetchThuongHieus();
    hideDeleteConfirm();
    showMessage('Xóa thương hiệu thành công!');
  } catch (error) {
    console.error('Error deleting brand:', error);
      showMessage('Có lỗi xảy ra khi xóa thương hiệu', 'error');
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  formData.value = {
    tenThuongHieu: '',
    trangThai: true
  };
  isEditing.value = false;
  errors.value.tenThuongHieu = '';
};

const handleSearch = debounce(() => {
  currentPage.value = 1;
}, 300);

const changePage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

const toggleSort = (field: string) => {
  if (sortField.value === field) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortField.value = field;
    sortDirection.value = 'asc';
  }
};

const getSortIcon = (field: string) => {
  if (sortField.value !== field) return 'bi-arrow-down-up';
  return sortDirection.value === 'asc' ? 'bi-arrow-up' : 'bi-arrow-down';
};

const formatDate = (date?: string) => {
  if (!date) return 'N/A';
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(new Date(date));
};


// Watchers
watch(pageSize, () => {
  currentPage.value = 1;
});

// Lifecycle
onMounted(() => {
  fetchThuongHieus();
  if (toast.value) {
  bsToast = new Toast(toast.value);
}

  if (formModal.value) {
    bsFormModal = new Modal(formModal.value);
  }
  if (deleteModal.value) {
    bsDeleteModal = new Modal(deleteModal.value);
  }
});
</script>

<style scoped>
.card {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.table th {
  background-color: #f8f9fa;
}

.btn-sm {
  padding: 0.25rem 0.5rem;
}

.invalid-feedback {
  display: block;
}

.modal-content {
  border-radius: 8px;
}

.modal-header {
  background-color: #f8f9fa;
  border-radius: 8px 8px 0 0;
}

.modal-footer {
  background-color: #f8f9fa;
  border-radius: 0 0 8px 8px;
}

.pagination {
  margin-bottom: 0;
}

.page-link {
  padding: 0.375rem 0.75rem;
}

.btn-link {
  color: inherit;
  text-decoration: none;
}

.btn-link:hover {
  color: var(--bs-primary);
}
</style>