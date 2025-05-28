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
      <h2 class="mb-0">Quản lý Chất Liệu</h2>
      <button class="btn btn-primary" @click="showFormModal()">
        <i class="bi bi-plus-circle me-2"></i>Thêm chất liệu
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
                placeholder="Tìm kiếm chất liệu..."
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

    <!-- ChatLieu List -->
    <div class="card">
      <div class="card-body">
        <h5 class="card-title mb-3">Danh sách chất liệu</h5>

        <!-- Table -->
        <div class="table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th>STT</th>
                <th>
                  <div class="d-flex align-items-center gap-2">
                    Tên chất liệu
                    <button 
                      class="btn btn-link btn-sm p-0" 
                      @click="toggleSort('tenChatLieu')"
                    >
                      <i class="bi" :class="getSortIcon('tenChatLieu')"></i>
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
              <tr v-else-if="paginatedChatLieus.length === 0">
                <td colspan="6" class="text-center">Không có dữ liệu</td>
              </tr>
              <tr v-for="(chatLieu, index) in paginatedChatLieus" :key="chatLieu.id">
                <td>{{ startIndex + index + 1 }}</td>
                <td>{{ chatLieu.tenChatLieu }}</td>
                <td>{{ formatDate(chatLieu.ngayTao) }}</td>
                <td>{{ formatDate(chatLieu.ngaySua) }}</td>
                <td>
                  <span 
                    class="badge"
                    :class="chatLieu.trangThai ? 'bg-success' : 'bg-danger'"
                  >
                    {{ chatLieu.trangThai ? 'Hoạt động' : 'Không hoạt động' }}
                  </span>
                </td>
                <td>
                  <div class="d-flex gap-2">
                    <button 
                      class="btn btn-warning btn-sm"
                      @click="showFormModal(chatLieu)"
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
            Hiển thị {{ startIndex + 1 }} đến {{ endIndex }} trên tổng số {{ filteredChatLieus.length }} mục
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
            <h5 class="modal-title">{{ isEditing ? 'Cập nhật' : 'Thêm' }} chất liệu</h5>
            <button type="button" class="btn-close" @click="hideFormModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleSubmit">
              <div class="mb-3">
                <label class="form-label">Tên chất liệu</label>
                <input
                  v-model="formData.tenChatLieu"
                  type="text"
                  class="form-control"
                  required
                  :class="{ 'is-invalid': errors.tenChatLieu }"
                />
                <div class="invalid-feedback">{{ errors.tenChatLieu }}</div>
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
            <p>Bạn có chắc chắn muốn xóa chất liệu "{{ selectedChatLieu?.tenChatLieu }}"?</p>
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

interface ChatLieu {
  id?: number;
  tenChatLieu: string;
  ngayTao?: string;
  ngaySua?: string;
  trangThai: boolean;
}

const API_URL = 'http://localhost:8080/chat-lieu';

// State
const chatLieus = ref<ChatLieu[]>([]);
const loading = ref(false);
const searchQuery = ref('');
const errors = ref({ tenChatLieu: '' });
const isEditing = ref(false);
const formModal = ref<HTMLElement | null>(null);
const deleteModal = ref<HTMLElement | null>(null);
const selectedChatLieu = ref<ChatLieu | null>(null);
let bsFormModal: Modal | null = null;
let bsDeleteModal: Modal | null = null;

// Toast
const toast = ref<HTMLElement | null>(null);
let bsToast: Toast | null = null;
const message = ref({ type: 'success', text: '' });

const showMessage = (text: string, type: 'success' | 'error' = 'success') => {
  message.value = { text, type };
  bsToast?.show();
};

// Pagination state
const currentPage = ref(1);
const pageSize = ref(10);
const sortField = ref('ngayTao');
const sortDirection = ref<'asc' | 'desc'>('desc');

const formData = ref<ChatLieu>({
  tenChatLieu: '',
  trangThai: true
});

// Computed properties
const filteredChatLieus = computed(() => {
  let result = [...chatLieus.value];
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(cl => 
      cl.tenChatLieu.toLowerCase().includes(query)
    );
  }
  
  result.sort((a, b) => {
    const aValue = a[sortField.value as keyof ChatLieu];
    const bValue = b[sortField.value as keyof ChatLieu];
    
    if (sortDirection.value === 'asc') {
      return aValue < bValue ? -1 : 1;
    } else {
      return aValue > bValue ? -1 : 1;
    }
  });
  
  return result;
});

const totalPages = computed(() => Math.ceil(filteredChatLieus.value.length / pageSize.value));
const startIndex = computed(() => (currentPage.value - 1) * pageSize.value);
const endIndex = computed(() => Math.min(startIndex.value + pageSize.value, filteredChatLieus.value.length));

const paginatedChatLieus = computed(() => {
  return filteredChatLieus.value.slice(startIndex.value, endIndex.value);
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
const fetchChatLieus = async () => {
  loading.value = true;
  try {
    const response = await axios.get(API_URL);
    chatLieus.value = response.data;
  } catch (error) {
    console.error('Error fetching materials:', error);
    showMessage('Lỗi khi tải dữ liệu', 'error');
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formData.value.tenChatLieu.trim()) {
    errors.value.tenChatLieu = 'Tên chất liệu không được để trống';
    return;
  }

   // Hiển thị hộp thoại xác nhận trước khi thực hiện thao tác thêm hoặc cập nhật
    const isConfirmed = window.confirm(
      isEditing.value ? 'Bạn có chắc chắn muốn cập nhật chất liệu này không?' : 'Bạn có chắc chắn muốn thêm chất liệu này không?'
    );

    // Nếu không xác nhận, dừng lại
    if (!isConfirmed) {
      return;
    }

  loading.value = true;
  try {
    if (isEditing.value) {
      await axios.put(`${API_URL}/${formData.value.id}`, formData.value);
      showMessage('Cập nhật chất liệu thành công!');
    } else {
      await axios.post(`${API_URL}/add`, formData.value);
      showMessage('Thêm chất liệu thành công!');
    }
    await fetchChatLieus();
    hideFormModal();
  } catch (error) {
    console.error('Error saving material:', error);
    showMessage('Có lỗi xảy ra khi lưu chất liệu', 'error');
  } finally {
    loading.value = false;
  }
};

const showFormModal = (chatLieu?: ChatLieu) => {
  if (chatLieu) {
    formData.value = { ...chatLieu };
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

const showDeleteConfirm = (chatLieu: ChatLieu) => {
  selectedChatLieu.value = chatLieu;
  bsDeleteModal?.show();
};

const hideDeleteConfirm = () => {
  bsDeleteModal?.hide();
  selectedChatLieu.value = null;
};

const confirmDelete = async () => {
  if (!selectedChatLieu.value?.id) return;

  loading.value = true;
  try {
    await axios.delete(`${API_URL}/${selectedChatLieu.value.id}`);
    await fetchChatLieus();
    hideDeleteConfirm();
    showMessage('Xóa chất liệu thành công!');
  } catch (error) {
    console.error('Error deleting material:', error);
    showMessage('Có lỗi xảy ra khi xóa chất liệu', 'error');
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  formData.value = {
    tenChatLieu: '',
    trangThai: true
  };
  isEditing.value = false;
  errors.value.tenChatLieu = '';
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
  fetchChatLieus();
  if (formModal.value) {
    bsFormModal = new Modal(formModal.value);
  }
  if (deleteModal.value) {
    bsDeleteModal = new Modal(deleteModal.value);
  }
  if (toast.value) {
    bsToast = new Toast(toast.value);
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

.toast {
  z-index: 9999;
}
</style>