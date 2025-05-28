<template>
  <div class="modal fade" id="productDetailsModal" tabindex="-1" ref="modal">
    <div class="modal-dialog modal-xl">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Chi tiết sản phẩm: {{ product?.tenSanPham }}</h5>

          <button type="button" class="btn-close" @click="hide"></button>
        </div>
        <div class="modal-body">
          <div class="row mb-4">
            <div class="col">
              <button class="btn btn-primary" @click="showAddDetailModal">
                <i class="bi bi-plus-circle me-2"></i>Thêm chi tiết
              </button>
          </div>
           <div class="col text-end">
                     <button class="btn btn-success" @click="exportToExcel">
                       <i class="bi bi-file-earmark-excel me-2"></i> Excel
                     </button>
                   </div>

          </div>

          <!-- Details Table -->
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>Mã SPCT</th>
                  <th>Màu sắc</th>
                  <th>Size</th>
                  <th>Giá bán</th>
                  <th>Số lượng</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading">
                  <td colspan="10" class="text-center">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Đang tải...</span>
                    </div>
                  </td>
                </tr>
                <tr v-else-if="details.length === 0">
                  <td colspan="10" class="text-center">Chưa có chi tiết sản phẩm</td>
                </tr>
                <tr v-for="detail in paginatedDetails" :key="detail.id">
                  <td>{{ detail.maSPCT }}</td>
                  <td>{{ detail.tenMau }}</td>
                  <td>{{ detail.tenSize }}</td>
                  <td>{{ detail.giaBan.toLocaleString() }} đ</td>
                  <td>{{ detail.soLuong }}</td>
                  <td>
                                          <span class="badge" :class="detail.trangThai ? 'bg-success' : 'bg-danger'">
                                              {{ detail.trangThai ? 'Hoạt động' : 'Không hoạt động' }}
                                          </span>
                                      </td>
                  <td>
                    <div class="btn-group">
                      <button class="btn btn-sm"
                        :class="detail.trangThai ? 'btn-success' : 'btn-danger'"
                        @click="updateProductStatus (detail.id, detail.trangThai)">
                        <i :class="detail.trangThai ? 'bi bi-check-circle' : 'bi bi-x-circle'"></i>
                      </button>

                      <button
                        class="btn btn-sm btn-warning"
                        @click="editDetail(detail)"
                      >
                        <i class="bi bi-pencil"></i>
                      </button>

                       <!-- Button for view -->
                                <button class="btn btn-primary" @click="viewDetail(detail)">
                                                      <i class="bi bi-eye"></i>
                                                    </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <!-- Pagination Controls -->
         <!-- Pagination Controls -->
         <div class="d-flex justify-content-between align-items-center mt-4">
           <div class="d-flex align-items-center gap-2">
             <!-- Page Size Dropdown -->
             <select v-model="pageSize" class="form-select form-select-sm" style="width: 100px">
               <option :value="5">5 / trang</option>
               <option :value="10">10 / trang</option>
               <option :value="20">20 / trang</option>
             </select>
             <span class="text-muted">
               Hiển thị {{ startIndex + 1 }}-{{ endIndex }} trên tổng số {{ totalItems }} chi tiết sản phẩm
             </span>
           </div>

           <!-- Pagination -->
           <nav v-if="totalPages > 1">
             <ul class="pagination mb-0">
               <!-- Previous Page Button -->
               <li class="page-item" :class="{ disabled: currentPage === 1 }">
                 <button class="page-link" @click="changePage(currentPage - 1)">
                   <i class="bi bi-chevron-left"></i>
                 </button>
               </li>

               <!-- Page Numbers -->
               <li
                 v-for="page in totalPages"
                 :key="page"
                 class="page-item"
                 :class="{ active: currentPage === page }"
               >
                 <button class="page-link" @click="changePage(page)">{{ page }}</button>
               </li>

               <!-- Next Page Button -->
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
  </div>

  <!-- Add/Edit Detail Modal -->
  <product-detail-form
    ref="detailForm"
    :product-id="product?.id"
    @saved="handleDetailSaved"
  />

   <!-- Add Property Modals -->
  <add-detail-property-modal
    ref="khuyenMaiModal"
    title="Thêm khuyến mãi"
    property-name="tenKhuyenMai"
    property-label="khuyến mãi"
    :api-url="`${API_URL}/san-pham-chi-tiet/add-khuyen-mai`"
    @added="handlePropertyAdded('khuyenMai')"
  />
  <add-detail-property-modal
    ref="mauSacModal"
    title="Thêm màu sắc"
    property-name="tenMau"
    property-label="màu sắc"
    :api-url="`${API_URL}/san-pham-chi-tiet/add-mau-sac`"
    @added="handlePropertyAdded('mauSac')"
  />
  <add-detail-property-modal
    ref="sizeModal"
    title="Thêm size"
    property-name="tenSize"
    property-label="size"
    :api-url="`${API_URL}/san-pham-chi-tiet/add-size`"
    @added="handlePropertyAdded('size')"
  />
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { Modal } from 'bootstrap';
import moment from 'moment';
import axios from 'axios';
import { saveAs } from 'file-saver';
import * as XLSX from 'xlsx';
import ProductDetailForm from './ProductDetailForm.vue';

const API_URL = "http://localhost:8080";

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
});

const modal = ref(null);
const bsModal = ref(null);
const detailForm = ref(null);
const loading = ref(false);
const selectedDetail = ref(null);
const details = ref([]);
const currentPage = ref(1); // Current page
const pageSize = ref(5); // Items per page

 const exportToExcel = () => {
          const productName = props.product?.tenSanPham || "Sản phẩm";

          const dataRows = paginatedDetails.value.map(detail => ({
            'Mã SPCT': detail.maSPCT,
            'Màu sắc': detail.tenMau,
            'Size': detail.tenSize,
            'Giá bán': detail.giaBan,
            'Số lượng': detail.soLuong,
            'Trạng thái': detail.trangThai ? 'Hoạt động' : 'Không hoạt động'
          }));

          const wb = XLSX.utils.book_new();

          const ws = XLSX.utils.aoa_to_sheet([[productName]]);

          ws['!merges'] = [{ s: { r: 0, c: 0 }, e: { r: 0, c: 8 } }];

           if(!ws['A1'].s) ws['A1'].s = {};
                    ws['A1'].s.font = { bold: true, sz: 14 };
                    ws['A1'].s.alignment = { horizontal: "center", vertical: "center" };


          XLSX.utils.sheet_add_json(ws, dataRows, { origin: "A2", skipHeader: false });


          const colWidths = [
            { wch: 12 }, // Mã SPCT
            { wch: 12 }, // Màu sắc
            { wch: 8 },  // Size
            { wch: 12 }, // Giá bán
            { wch: 10 }, // Số lượng
            { wch: 14 }  // Trạng thái
          ];
          ws['!cols'] = colWidths;

          XLSX.utils.book_append_sheet(wb, ws, "Chi tiết sản phẩm");

          const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array', cellStyles: true });
          const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          saveAs(blob, `product_details_${new Date().toISOString().split('T')[0]}.xlsx`);
        };

const viewDetail = (detail) => {
  selectedDetail.value = detail;
  detailForm.value.show(detail, true);  // Pass true to show in view mode
};
// Define filters
const searchQuery = ref('');
const filterDanhMuc = ref('');
const filterThuongHieu = ref('');
const filterChatLieu = ref('');

// Computed values for pagination
const totalItems = computed(() => details.value.length); // Total number of items
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value)); // Total pages

// Start and end indices for pagination
const startIndex = computed(() => (currentPage.value - 1) * pageSize.value);
const endIndex = computed(() => Math.min(startIndex.value + pageSize.value, totalItems.value));

// Paginated details for the current page
const paginatedDetails = computed(() => {
  return details.value.slice(startIndex.value, endIndex.value);
});

// Fetch details from the API
const fetchDetails = async () => {
  if (!props.product?.id) {
    details.value = [];
    return;
  }

  loading.value = true;
  try {
    const response = await axios.get(`${API_URL}/san-pham-chi-tiet/by-san-pham/${props.product.id}`, {
      params: {
        page: currentPage.value,   // Current page
        size: pageSize.value       // Number of items per page
      }
    });
    details.value = response.data || [];
  } catch (error) {
    console.error('Error fetching product details:', error);
    details.value = [];
  } finally {
    loading.value = false;
  }
};

// Change page handler
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    fetchDetails(); // Re-fetch details when page changes
  }
};

// Update product status
const updateProductStatus = async (id, currentStatus) => {
  if (!confirm('Bạn có chắc chắn muốn thay đổi trạng thái sản phẩm này không?')) {
    return;
  }

  try {
    const newStatus = !currentStatus;

    const response = await axios.patch(`${API_URL}/san-pham-chi-tiet/${id}/status`, null, {
      params: { trangThai: newStatus },
    });

    if (response.data) {
      const updatedProduct = response.data;

      const productIndex = details.value.findIndex(product => product.id === id);
      if (productIndex !== -1) {
        details.value[productIndex].trangThai = newStatus;
      }

      alert("Cập nhật trạng thái thành công!");
    }
  } catch (error) {
    console.error('Lỗi cập nhật trạng thái sản phẩm:', error.response?.data || error.message);
    alert("Có lỗi xảy ra khi cập nhật trạng thái sản phẩm.");
  }
};

// Watch for changes in product prop
watch(() => props.product?.id, (newId) => {
  if (newId && bsModal.value?._isShown) {
    details.value = []; // Clear existing details
    fetchDetails(); // Fetch details for new product
  }
});

// Show modal
const show = async () => {
  details.value = []; // Clear existing details
  await fetchDetails();
  bsModal.value?.show();
};

// Hide modal
const hide = () => {
  details.value = []; // Clear details when hiding modal
  bsModal.value?.hide();
};

// Format date for display
const formatDate = (date) => {
  return date ? moment(date).format('DD/MM/YYYY') : 'N/A';
};

// Handle deleting product details
const deleteDetail = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn xóa chi tiết sản phẩm này?')) return;

  try {
    await axios.delete(`${API_URL}/san-pham-chi-tiet/${id}`);
    await fetchDetails();
    alert('Xóa chi tiết sản phẩm thành công');
  } catch (error) {
    console.error('Error deleting product detail:', error);
    alert('Có lỗi xảy ra khi xóa chi tiết sản phẩm');
  }
};

// Show add detail modal
const showAddDetailModal = () => {
  if (!props.product?.id)
    return;
  detailForm.value?.show();
};

// Edit product detail
const editDetail = (detail) => {
  if (!props.product?.id) return;
  detailForm.value?.show(detail);
};

// Handle detail saved
const handleDetailSaved = () => {
  fetchDetails();
};

// Watch filters
watch([searchQuery, filterDanhMuc, filterThuongHieu, filterChatLieu], () => {
  currentPage.value = 1; // Reset to page 1 when filters change
});

onMounted(() => {
  bsModal.value = new Modal(modal.value);
});

defineExpose({
  show
});
</script>
