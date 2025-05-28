<template>
  <div class="card">
    <div class="card-body">
      <!-- Search and Filters -->
      <div class="row g-3 mb-4">
        <div class="col-md-3">
          <input
            v-model="searchQuery"
            type="text"
            class="form-control"
            placeholder="Tìm kiếm sản phẩm..."
            @input="handleSearch"
          />
        </div>
        <div class="col-md-2">
          <select v-model="filterDanhMuc" class="form-select">
            <option value="">Tất cả danh mục</option>
            <option v-for="dm in danhMucList" :key="dm.id" :value="dm.id">
              {{ dm.tenDanhMuc }}
            </option>
          </select>
        </div>
        <div class="col-md-2">
          <select v-model="filterThuongHieu" class="form-select">
            <option value="">Tất cả thương hiệu</option>
            <option v-for="th in thuongHieuList" :key="th.id" :value="th.id">
              {{ th.tenThuongHieu }}
            </option>
          </select>
        </div>
        <div class="col-md-2">
          <select v-model="filterChatLieu" class="form-select">
            <option value="">Tất cả chất liệu</option>
            <option v-for="cl in chatLieuList" :key="cl.id" :value="cl.id">
              {{ cl.tenChatLieu }}
            </option>
          </select>
        </div>
        <div class="col-md-2">
          <select v-model="filterDeGiay" class="form-select">
            <option value="">Tất cả đế giày</option>
            <option v-for="dg in deGiayList" :key="dg.id" :value="dg.id">
              {{ dg.tenDeGiay }}
            </option>
          </select>
        </div>
        <div class="col-md-1">
          <button class="btn btn-success w-100" @click="exportToExcel">
            <i class="bi bi-file-excel me-2"></i>Excel
          </button>
        </div>
      </div>

      <!-- Products Table -->
      <div class="table-responsive">
        <table class="table">
          <thead class="table-light">
            <tr>
              <th>STT</th>
              <th>Ảnh</th>
              <th>Tên sản phẩm</th>
              <th>Danh mục</th>
              <th>Thương hiệu</th>
              <th>Chất liệu</th>
              <th>Đế giày</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="9" class="text-center">
                <div class="spinner-border text-primary" role="status">
                  <span class="visually-hidden">Đang tải...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="paginatedProducts.length === 0">
              <td colspan="9" class="text-center">Không có sản phẩm nào</td>
            </tr>
            <tr v-for="(product, index) in paginatedProducts" :key="product.id">
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td>
                <img
                  :src="product.anhDauTien"
                  :alt="product.tenSanPham"
                  class="product-image"
                />
              </td>
              <td>{{ product.tenSanPham }}</td>
              <td>{{ product.danhMuc }}</td>
              <td>{{ product.thuongHieu }}</td>
              <td>{{ product.chatLieu }}</td>
              <td>{{ product.deGiay }}</td>
              <td>
                <span
                  class="badge"
                  :class="product.trangThai ? 'bg-success' : 'bg-danger'"
                >
                  {{ product.trangThai ? 'Hoạt động' : 'Không hoạt động' }}
                </span>
              </td>
              <td>
                <div class="btn-group">
                  <button
                    class="btn btn-sm btn-info"
                    @click="$emit('view-details', product)"
                  >
                    <i class="bi bi-eye"></i>
                  </button>
                  <button
                    class="btn btn-sm btn-warning"
                    @click="$emit('edit-product', product)"
                  >
                    <i class="bi bi-pencil"></i>
                  </button>
                  <!-- <button
                    class="btn btn-sm btn-danger"
                    @click="handleDelete(product.id)"
                  >
                    <i class="bi bi-trash"></i>
                  </button> -->
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="d-flex justify-content-between align-items-center mt-4">
        <div class="d-flex align-items-center gap-2">
          <select v-model="pageSize" class="form-select form-select-sm" style="width: 100px">
            <option :value="5">5 / trang</option>
            <option :value="10">10 / trang</option>
            <option :value="20">20 / trang</option>
          </select>
          <span class="text-muted">
            Hiển thị {{ startIndex + 1 }}-{{ endIndex }} trên tổng số {{ totalItems }} sản phẩm
          </span>
        </div>
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
              :class="{ active: currentPage === page }"
            >
              <button class="page-link" @click="changePage(page)">{{ page }}</button>
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
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import axios from 'axios';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

const props = defineProps({
  products: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['view-details', 'edit-product', 'deleted']);

// Filters
const searchQuery = ref('');
const filterDanhMuc = ref('');
const filterThuongHieu = ref('');
const filterChatLieu = ref('');
const filterDeGiay = ref('');

// Pagination
const currentPage = ref(1);
const pageSize = ref(10);

// Dropdown data
const danhMucList = ref([]);
const thuongHieuList = ref([]);
const chatLieuList = ref([]);
const deGiayList = ref([]);

// Fetch dropdown data
const fetchDropdownData = async () => {
  try {
    const [danhMucRes, thuongHieuRes, chatLieuRes, deGiayRes] = await Promise.all([
      axios.get(`${"http://localhost:8080"}/danh-muc`),
      axios.get(`${"http://localhost:8080"}/thuong-hieu`),
      axios.get(`${"http://localhost:8080"}/chat-lieu`),
      axios.get(`${"http://localhost:8080"}/de-giay`)
    ]);

    danhMucList.value = danhMucRes.data;
    thuongHieuList.value = thuongHieuRes.data;
    chatLieuList.value = chatLieuRes.data;
    deGiayList.value = deGiayRes.data;
  } catch (error) {
    console.error('Error fetching dropdown data:', error);
  }
};

// Filter products
// Filter products
const filteredProducts = computed(() => {
  return props.products.filter(product => {
    const matchesSearch = product.tenSanPham.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchesDanhMuc = !filterDanhMuc.value || danhMucList.value.find(dm => dm.id === filterDanhMuc.value)?.tenDanhMuc === product.danhMuc;
    const matchesThuongHieu = !filterThuongHieu.value || thuongHieuList.value.find(th => th.id === filterThuongHieu.value)?.tenThuongHieu === product.thuongHieu;
    const matchesChatLieu = !filterChatLieu.value || chatLieuList.value.find(cl => cl.id === filterChatLieu.value)?.tenChatLieu === product.chatLieu;
    const matchesDeGiay = !filterDeGiay.value || deGiayList.value.find(dg => dg.id === filterDeGiay.value)?.tenDeGiay === product.deGiay;

    return matchesSearch && matchesDanhMuc && matchesThuongHieu && matchesChatLieu && matchesDeGiay;
  });
});


// Pagination computed properties
const totalItems = computed(() => filteredProducts.value.length);
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));
const startIndex = computed(() => (currentPage.value - 1) * pageSize.value);
const endIndex = computed(() => Math.min(startIndex.value + pageSize.value, totalItems.value));
const paginatedProducts = computed(() => {
  return filteredProducts.value.slice(startIndex.value, endIndex.value);
});

const visiblePages = computed(() => {
  const delta = 2;
  const range = [];
  const rangeWithDots = [];
  let l;

  for (let i = 1; i <= totalPages.value; i++) {
    if (i === 1 || i === totalPages.value ||
        (i >= currentPage.value - delta && i <= currentPage.value + delta)) {
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
const handleSearch = () => {
  currentPage.value = 1;
};

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};
const handleDelete = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) return;

  try {
    await axios.delete(`${"http://localhost:8080"}/san-pham/${id}`);
 emit('delete-product');
  } catch (error) {
    console.error('Error deleting product:', error);
    alert('Có lỗi xảy ra khi xóa sản phẩm');
  }
};
const exportToExcel = () => {
  const data = filteredProducts.value.map(product => ({
    'Tên sản phẩm': product.tenSanPham,
    'Danh mục': product.danhMuc,
    'Thương hiệu': product.thuongHieu,
    'Chất liệu': product.chatLieu,
    'Đế giày': product.deGiay,
    'Trạng thái': product.trangThai ? 'Hoạt động' : 'Không hoạt động'
  }));

  const ws = XLSX.utils.json_to_sheet(data);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, 'Products');

  const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
  const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
  saveAs(blob, `products_${new Date().toISOString().split('T')[0]}.xlsx`);
};

// Watch for filter changes
watch([searchQuery, filterDanhMuc, filterThuongHieu, filterChatLieu, filterDeGiay], () => {
  currentPage.value = 1;
});

onMounted(() => {
  fetchDropdownData();
});
</script>

<style scoped>
.product-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.btn-group .btn {
  padding: 0.25rem 0.5rem;
}

.table th {
  white-space: nowrap;
}

.form-control:focus,
.form-select:focus {
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.table > :not(caption) > * > * {
  padding: 1rem 0.5rem;
}
</style>