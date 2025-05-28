<template>
  <div class="container py-4">
    <h2 class="text-center mb-4">Quản lý Khuyến Mãi</h2>

    <!-- Search and Filters -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3">
          <!-- Search -->
          <div class="col-md-4">
            <div class="input-group">
              <input
                v-model="searchQuery"
                type="text"
                class="form-control"
                placeholder="Tìm kiếm theo mã khuyến mãi..."
              />
              <button class="btn btn-outline-secondary" @click="handleSearch">
                <i class="bi bi-search"></i>
              </button>
            </div>
          </div>

          <!-- Date Range -->
          <div class="col-md-3">
            <input
              v-model="startDate"
              type="date"
              class="form-control"
              placeholder="Ngày bắt đầu"
            />
          </div>
          <div class="col-md-3">
            <input
              v-model="endDate"
              type="date"
              class="form-control"
              placeholder="Ngày kết thúc"
              :min="startDate"
            />
          </div>

          <!-- Status Filter -->
          <div class="col-md-2">
            <select v-model="statusFilter" class="form-select">
              <option value="">Tất cả trạng thái</option>
              <option value="true">Hoạt động</option>
              <option value="false">Ngừng hoạt động</option>
            </select>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="d-flex justify-content-end gap-2 mt-3">
          <button class="btn btn-secondary" @click="resetFilters">
            <i class="bi bi-x-circle me-1"></i>
            Đặt lại
          </button>
          <button class="btn btn-success" @click="exportToExcel">
            <i class="bi bi-file-excel me-1"></i>
            Xuất Excel
          </button>
          <button class="btn btn-primary" @click="handleAddKhuyenMai">
            <i class="bi bi-plus-circle me-1"></i>
            Thêm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Loading and Error States -->
    <div v-if="loading" class="alert alert-info text-center">
      <div class="spinner-border spinner-border-sm me-2" role="status"></div>
      Đang tải dữ liệu...
    </div>
    <div v-if="errorMessage" class="alert alert-danger text-center">
      {{ errorMessage }}
    </div>

    <!-- KhuyenMai List -->
    <div class="card">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-striped table-hover align-middle">
            <thead class="table-light">
              <tr>
                <th class="text-center" style="width: 60px">STT</th>
                <th>Mã khuyến mãi</th>
                <th>Tên khuyến mãi</th>
                <th>Ngày bắt đầu</th>
                <th>Ngày kết thúc</th>
                <th class="text-center">% Giảm</th>
                <th>Ngày tạo</th>
                <th>Ngày sửa</th>
                <th class="text-center">Trạng thái</th>
                <th class="text-center" style="width: 120px">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="paginatedKhuyenMaiList.length === 0">
                <td colspan="10" class="text-center py-4">
                  <div class="text-muted">Không có khuyến mãi nào!</div>
                </td>
              </tr>
              <tr v-for="(khuyenMai, index) in paginatedKhuyenMaiList" :key="khuyenMai.id">
                <td class="text-center">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                <td>{{ khuyenMai.maKhuyenMai }}</td>
                <td>{{ khuyenMai.tenKhuyenMai }}</td>
                <td>{{ formatDateTime(khuyenMai.ngayBatDau) }}</td>
                <td>{{ formatDateTime(khuyenMai.ngayKetThuc) }}</td>
                <td class="text-center">{{ khuyenMai.phanTramGiamGia }}%</td>
                <td>{{ formatDateTime(khuyenMai.ngayTao) }}</td>
                <td>{{ formatDateTime(khuyenMai.ngaySua) }}</td>
                <td class="text-center">
                  <span 
                    class="badge"
                    :class="getPromotionStatus(khuyenMai)"
                  >
                    {{ getPromotionStatusText(khuyenMai) }}
                  </span>
                </td>
                <td>
                  <div class="d-flex justify-content-center gap-2">
                    <button 
                      class="btn btn-warning btn-sm" 
                      @click="handleEditKhuyenMai(khuyenMai.id)"
                      title="Sửa"
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
        <div v-if="totalPages > 1" class="d-flex justify-content-between align-items-center mt-4">
          <div class="text-muted">
            Hiển thị {{ startIndex + 1 }} đến {{ endIndex }} trên tổng số {{ filteredKhuyenMaiList.length }} mục
          </div>
          <nav>
            <ul class="pagination mb-0">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="prevPage">
                  <i class="bi bi-chevron-left"></i>
                </button>
              </li>
              <li class="page-item">
                <span class="page-link">
                  Trang {{ currentPage }} / {{ totalPages }}
                </span>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="nextPage">
                  <i class="bi bi-chevron-right"></i>
                </button>
              </li>
            </ul>
          </nav>
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
            <p>Bạn có chắc chắn muốn xóa khuyến mãi "{{ selectedKhuyenMai?.tenKhuyenMai }}"?</p>
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

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { Modal } from 'bootstrap';
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import debounce from 'lodash/debounce';

const router = useRouter();
const urlKhuyenMai = "http://localhost:8080/khuyen-mai";

// State
const khuyenMaiList = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const searchQuery = ref("");
const startDate = ref("");
const endDate = ref("");
const statusFilter = ref("");
const currentPage = ref(1);
const pageSize = 5;
const deleteModal = ref(null);
const selectedKhuyenMai = ref(null);
let bsDeleteModal = null;

// Computed Properties
const filteredKhuyenMaiList = computed(() => {
  let result = [...khuyenMaiList.value];

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(km => 
      km.maKhuyenMai.toLowerCase().includes(query) ||
      km.tenKhuyenMai.toLowerCase().includes(query)
    );
  }

  if (startDate.value) {
    result = result.filter(km => new Date(km.ngayBatDau) >= new Date(startDate.value));
  }

  if (endDate.value) {
    result = result.filter(km => new Date(km.ngayKetThuc) <= new Date(endDate.value));
  }

  if (statusFilter.value !== "") {
    const now = new Date();
    result = result.filter(km => {
      const startDate = new Date(km.ngayBatDau);
      const endDate = new Date(km.ngayKetThuc);
      const isActive = now >= startDate && now <= endDate;
      return isActive === (statusFilter.value === "true");
    });
  }

  return result;
});

const totalPages = computed(() => Math.ceil(filteredKhuyenMaiList.value.length / pageSize));
const startIndex = computed(() => (currentPage.value - 1) * pageSize);
const endIndex = computed(() => Math.min(startIndex.value + pageSize, filteredKhuyenMaiList.value.length));

const paginatedKhuyenMaiList = computed(() => {
  return filteredKhuyenMaiList.value.slice(startIndex.value, endIndex.value);
});

// Methods
const fetchKhuyenMai = async () => {
  loading.value = true;
  errorMessage.value = "";
  try {
    const response = await axios.get(urlKhuyenMai);
    if (response.data && Array.isArray(response.data)) {
      khuyenMaiList.value = response.data;
    } else {
      throw new Error("API trả về dữ liệu không hợp lệ");
    }
  } catch (error) {
    errorMessage.value = "Lỗi khi tải dữ liệu khuyến mãi. Vui lòng thử lại!";
    console.error("API Error: ", error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = debounce(() => {
  currentPage.value = 1;
}, 300);

const resetFilters = () => {
  searchQuery.value = "";
  startDate.value = "";
  endDate.value = "";
  statusFilter.value = "";
  currentPage.value = 1;
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const handleAddKhuyenMai = () => {
  router.push('/admin/khuyen-mai/manage/add-khuyenmai');
};

const handleEditKhuyenMai = (id) => {
  router.push(`/admin/khuyen-mai/manage/update-khuyenmai/${id}`);
};

const showDeleteConfirm = (khuyenMai) => {
  selectedKhuyenMai.value = khuyenMai;
  bsDeleteModal?.show();
};

const hideDeleteConfirm = () => {
  bsDeleteModal?.hide();
  selectedKhuyenMai.value = null;
};

const confirmDelete = async () => {
  if (!selectedKhuyenMai.value?.id) return;

  loading.value = true;
  try {
    await axios.delete(`${urlKhuyenMai}/${selectedKhuyenMai.value.id}`);
    await fetchKhuyenMai();
    hideDeleteConfirm();
    alert("Xóa khuyến mãi thành công!");
  } catch (error) {
    console.error("Lỗi khi xóa khuyến mãi:", error);
    alert("Lỗi khi xóa khuyến mãi. Vui lòng thử lại!");
  } finally {
    loading.value = false;
  }
};

const getPromotionStatus = (khuyenMai) => {
  const now = new Date();
  const startDate = khuyenMai.ngayBatDau ? new Date(khuyenMai.ngayBatDau) : null;
  const endDate = khuyenMai.ngayKetThuc ? new Date(khuyenMai.ngayKetThuc) : null;

  if (!startDate || !endDate) return 'bg-secondary';
  
  if (now < startDate) return 'bg-warning';
  if (now > endDate) return 'bg-danger';
  return 'bg-success';
};

const getPromotionStatusText = (khuyenMai) => {
  const now = new Date();
  const startDate = khuyenMai.ngayBatDau ? new Date(khuyenMai.ngayBatDau) : null;
  const endDate = khuyenMai.ngayKetThuc ? new Date(khuyenMai.ngayKetThuc) : null;

  if (!startDate || !endDate) return 'Chưa thiết lập';
  
  if (now < startDate) return 'Chưa bắt đầu';
  if (now > endDate) return 'Đã kết thúc';
  return 'Đang áp dụng';
};

const formatDateTime = (dateString) => {
  if (!dateString) return 'N/A';
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(date);
};


const exportToExcel = () => {
  if (khuyenMaiList.value.length === 0) {
    alert("Không có dữ liệu để xuất!");
    return;
  }

  const exportData = khuyenMaiList.value.map((km, index) => ({
    STT: index + 1,
    "Mã khuyến mãi": km.maKhuyenMai,
    "Tên khuyến mãi": km.tenKhuyenMai,
    "Ngày bắt đầu": formatDateTime(km.ngayBatDau),
    "Ngày kết thúc": formatDateTime(km.ngayKetThuc),
    "Phần trăm giảm giá": km.phanTramGiamGia,
    "Ngày tạo": formatDateTime(km.ngayTao),
    "Ngày sửa": formatDateTime(km.ngaySua),
    "Trạng thái": getPromotionStatusText(km)
  }));

  const ws = XLSX.utils.json_to_sheet(exportData);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "DanhSachKhuyenMai");

  ws['!cols'] = [
    { wch: 5 },
    { wch: 25 },
    { wch: 25 },
    { wch: 20 },
    { wch: 20 },
    { wch: 20 },
    { wch: 20 },
    { wch: 20 },
    { wch: 15 }
  ];

  const range = XLSX.utils.decode_range(ws['!ref']);
  for (let col = range.s.c; col <= range.e.c; col++) {
    const cellAddress = XLSX.utils.encode_cell({ r: 0, c: col });
    if (ws[cellAddress]) {
      ws[cellAddress].s = {
        font: { bold: true },
        alignment: { horizontal: "center", vertical: "center" }
      };
    }
  }

  Object.keys(ws).forEach(cell => {
    if (cell[0] !== '!') {
      ws[cell].s = {
        border: {
          top: { style: "thin" },
          bottom: { style: "thin" },
          left: { style: "thin" },
          right: { style: "thin" }
        }
      };
    }
  });

  ws['!autofilter'] = { ref: `A1:I1` };

  const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
  const data = new Blob([excelBuffer], { type: "application/octet-stream" });
  saveAs(data, "DanhSachKhuyenMai.xlsx");
};

// Watchers
watch([searchQuery, startDate, endDate, statusFilter], () => {
  currentPage.value = 1;
});

// Lifecycle
onMounted(() => {
  fetchKhuyenMai();
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
</style>