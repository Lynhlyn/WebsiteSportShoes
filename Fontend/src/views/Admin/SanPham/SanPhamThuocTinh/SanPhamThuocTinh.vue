<template>
  <div class="container-fluid py-4">
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

    <h2 class="text-center mb-4">Quản lý sản phẩm</h2>

    <!-- Action Buttons -->
    <div class="mb-4 d-flex gap-2">
      <button class="btn btn-primary" @click="showProductModal">
        <i class="bi bi-plus-circle me-2"></i>Thêm sản phẩm mới
      </button>

      <!-- Import Excel Button -->
      <!-- <div class="d-flex align-items-center">
        <input
          type="file"
          ref="fileInput"
          class="d-none"
          accept=".xlsx,.xls"
          @change="handleFileImport"
        />
        <button
          class="btn btn-success"
          @click="$refs.fileInput.click()"
          :disabled="importing"
        >
          <i class="bi bi-file-earmark-excel me-2"></i>
          {{ importing ? 'Đang nhập...' : 'Nhập từ Excel' }}
        </button>
      </div> -->

      <!-- Download Template Button -->
      <!-- <button
        class="btn btn-outline-primary"
        @click="downloadTemplate"
      >
        <i class="bi bi-download me-2"></i>Tải mẫu Excel
      </button> -->
    </div>

    <!-- Product List -->
    <product-list
      :products="products"
      :loading="loading"
      @view-details="showProductDetails"
      @edit-product="editProduct"
      @delete-product="fetchProducts"
    />

    <!-- Modals -->
    <product-form
      ref="productForm"
      @saved="handleProductSaved"
      @updated="handleProductUpdated"
    />

    <product-details
      ref="productDetails"
      :product="selectedProduct"
    />

    <!-- Import Result Modal -->
    <div class="modal fade" ref="importResultModal" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Kết quả nhập Excel</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div v-if="importResults.success > 0" class="alert alert-success">
              {{ importResults.success }} sản phẩm đã được nhập thành công
            </div>
            <div v-if="importResults.errors.length > 0">
              <div class="alert alert-danger">
                {{ importResults.errors.length }} lỗi được tìm thấy:
              </div>
              <ul class="list-group">
                <li
                  v-for="(error, index) in importResults.errors"
                  :key="index"
                  class="list-group-item text-danger"
                >
                  Dòng {{ error.row }}: {{ error.message }}
                </li>
              </ul>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { Modal, Toast } from 'bootstrap';
import axios from 'axios';
import * as XLSX from 'xlsx';
import ProductList from './product/ProductList.vue';
import ProductForm from './product/ProductForm.vue';
import ProductDetails from './product/ProductDetails.vue';

const API_URL = "http://localhost:8080";
const products = ref([]);
const loading = ref(false);
const importing = ref(false);
const selectedProduct = ref(null);
const importResults = ref({
  success: 0,
  errors: []
});

// Toast
const toast = ref(null);
let bsToast = null;
const message = ref({ type: 'success', text: '' });

const showMessage = (text, type = 'success') => {
  message.value = { text, type };
  bsToast?.show();
};

// Reference data for validation
const danhMucs = ref([]);
const thuongHieus = ref([]);
const chatLieus = ref([]);
const deGiays = ref([]);

const productForm = ref(null);
const productDetails = ref(null);
const fileInput = ref(null);
const importResultModal = ref(null);
let bsImportResultModal = null;

const fetchProducts = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`${API_URL}/san-pham`);
    products.value = response.data;
  } catch (error) {
    console.error('Error fetching products:', error);
    showMessage('Lỗi khi tải danh sách sản phẩm', 'error');
  } finally {
    loading.value = false;
  }
};

const fetchReferenceData = async () => {
  try {
    const [danhMucRes, thuongHieuRes, chatLieuRes, deGiayRes] = await Promise.all([
      axios.get(`${API_URL}/danh-muc`),
      axios.get(`${API_URL}/thuong-hieu`),
      axios.get(`${API_URL}/chat-lieu`),
      axios.get(`${API_URL}/de-giay`)
    ]);

    danhMucs.value = danhMucRes.data;
    thuongHieus.value = thuongHieuRes.data;
    chatLieus.value = chatLieuRes.data;
    deGiays.value = deGiayRes.data;
  } catch (error) {
    console.error('Error fetching reference data:', error);
    showMessage('Lỗi khi tải dữ liệu tham chiếu', 'error');
  }
};

const showProductModal = () => {
  productForm.value.show(null);
};

const editProduct = (product) => {
  productForm.value.show(product);
};

const showProductDetails = async (product) => {
  // Prevent multiple calls
  if (selectedProduct.value?.id === product.id) {
    return;
  }

  try {
    selectedProduct.value = product;
    // Wait for the next DOM update cycle
    await nextTick();
    // Show the modal only after the prop has been updated
    productDetails.value?.show();
  } catch (error) {
    console.error('Error showing product details:', error);
    showMessage('Lỗi khi hiển thị chi tiết sản phẩm', 'error');
  }
};

const handleProductSaved = () => {
  fetchProducts();
  showMessage('Thêm sản phẩm thành công!');
};

const handleProductUpdated = () => {
  fetchProducts();
  showMessage('Cập nhật sản phẩm thành công!');
};

const downloadTemplate = () => {
  const template = [
    ['Tên sản phẩm', 'Mô tả', 'Danh mục', 'Thương hiệu', 'Chất liệu', 'Đế giày', 'Trạng thái'],
    ['Giày thể thao Nike Air', 'Giày thể thao cao cấp abdcefghjklmabdcefghjklmabdcefghjklm...', 'Giày chạy bộ', 'Giày ADIDAS', 'Tổng hợp', '	Đế EVA siêu nhẹ', 'true'],
    ['Giày da công sở', 'Giày da công sở nam abdcefghjklmabdcefghjklmabdcefghjklm...', 'Giày chạy bộ', 'Giày NIKE', 'Polyurethane', 'Đế cao su chịu lực', 'true']
  ];

  const ws = XLSX.utils.aoa_to_sheet(template);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, 'Template');

  // Auto-size columns
  const colWidths = template[0].map((col, i) => {
    return {wch: Math.max(...template.map(row => row[i].length))};
  });
  ws['!cols'] = colWidths;

  try {
    XLSX.writeFile(wb, 'product_template.xlsx');
    showMessage('Tải mẫu Excel thành công!');
  } catch (error) {
    console.error('Error downloading template:', error);
    showMessage('Lỗi khi tải mẫu Excel', 'error');
  }
};

const validateExcelData = (data) => {
  const errors = [];
  const validProducts = [];

  // Skip header row
  for (let i = 1; i < data.length; i++) {
    const row = data[i];
    const rowNumber = i + 1;
    const product = {
      tenSanPham: row[0],
      moTa: row[1],
      danhMuc: row[2],
      thuongHieu: row[3],
      chatLieu: row[4],
      deGiay: row[5],
      trangThai: row[6]?.toString().toLowerCase() === 'true'
    };

    // Required field validation
    if (!product.tenSanPham) {
      errors.push({ row: rowNumber, message: 'Tên sản phẩm không được để trống' });
      continue;
    }

    if (!product.moTa || product.moTa.length < 25) {
      errors.push({ row: rowNumber, message: 'Mô tả phải có ít nhất 25 ký tự' });
      continue;
    }

    // Reference data validation
    const normalizeString = (str) => str.trim().toLowerCase();

    const danhMuc = danhMucs.value.find(dm => normalizeString(dm.tenDanhMuc) === normalizeString(product.danhMuc));
    if (!danhMuc) {
      errors.push({ row: rowNumber, message: `Danh mục "${product.danhMuc}" không tồn tại` });
      continue;
    }

    const thuongHieu = thuongHieus.value.find(th => normalizeString(th.tenThuongHieu) === normalizeString(product.thuongHieu));
    if (!thuongHieu) {
      errors.push({ row: rowNumber, message: `Thương hiệu "${product.thuongHieu}" không tồn tại` });
      continue;
    }

    const chatLieu = chatLieus.value.find(cl => normalizeString(cl.tenChatLieu) === normalizeString(product.chatLieu));
    if (!chatLieu) {
      errors.push({ row: rowNumber, message: `Chất liệu "${product.chatLieu}" không tồn tại` });
      continue;
    }

    const deGiay = deGiays.value.find(dg => normalizeString(dg.tenDeGiay) === normalizeString(product.deGiay));
    if (!deGiay) {
      errors.push({ row: rowNumber, message: `Đế giày "${product.deGiay}" không tồn tại` });
      continue;
    }

    // Add IDs to the product object
    product.danhMucId = danhMuc.id;
    product.thuongHieuId = thuongHieu.id;
    product.chatLieuId = chatLieu.id;
    product.deGiayId = deGiay.id;

    validProducts.push(product);
  }

  return { errors, validProducts };
};

const handleFileImport = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  importing.value = true;
  importResults.value = {
    success: 0,
    errors: []
  };

  try {
    const data = await readExcelFile(file);

    // Validate data against reference data
    const { errors, validProducts } = validateExcelData(data);

    if (errors.length > 0) {
      importResults.value.errors = errors;
      bsImportResultModal.show();
      showMessage('Có lỗi trong file Excel. Vui lòng kiểm tra chi tiết.', 'error');
      return;
    }

    // Convert valid products to FormData
    const formData = new FormData();
    formData.append('products', JSON.stringify(validProducts));

    const response = await axios.post(`${API_URL}/san-pham/import-batch`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    importResults.value = {
      success: validProducts.length,
      errors: []
    };

    await fetchProducts();
    bsImportResultModal.show();
    showMessage(`Đã nhập thành công ${validProducts.length} sản phẩm!`);
  } catch (error) {
    console.error('Error importing products:', error);
    importResults.value.errors.push({
      row: 'N/A',
      message: 'Có lỗi xảy ra khi nhập file. Vui lòng kiểm tra định dạng file và thử lại.'
    });
    bsImportResultModal.show();
    showMessage('Lỗi khi nhập file Excel', 'error');
  } finally {
    importing.value = false;
    event.target.value = '';
  }
};

const readExcelFile = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const data = new Uint8Array(e.target.result);
        const workbook = XLSX.read(data, { type: 'array' });
        const worksheet = workbook.Sheets[workbook.SheetNames[0]];
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        resolve(jsonData);
      } catch (error) {
        reject(error);
      }
    };
    reader.onerror = (error) => reject(error);
    reader.readAsArrayBuffer(file);
  });
};

onMounted(async () => {
  await Promise.all([
    fetchProducts(),
    fetchReferenceData()
  ]);

  if (importResultModal.value) {
    bsImportResultModal = new Modal(importResultModal.value);
  }
  if (toast.value) {
    bsToast = new Toast(toast.value);
  }
});
</script>

<style scoped>
.list-group-item {
  font-size: 0.9rem;
}

.toast-container {
  z-index: 9999;
}

.toast {
  z-index: 9999;
}
</style>