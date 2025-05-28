<template>
   <div class="modal fade" tabindex="-1" ref="modal">
     <div class="modal-dialog modal-lg">
       <div class="modal-content">
         <div class="modal-header">
           <h5 class="modal-title">
             {{ isViewMode ? "Xem" : isEditing ? "Cập nhật" : "Thêm" }} chi tiết sản phẩm
           </h5>
           <button type="button" class="btn-close" @click="hide"></button>
         </div>
         <div class="modal-body">
           <!-- Loading and Error Message -->
           <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
           <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>

           <!-- Add / Edit Form (only visible in Add or Edit mode) -->
           <form v-if="!isViewMode" @submit.prevent="handleSubmit">


             <div class="mb-3">
               <label class="form-label">Màu sắc</label>
               <div class="input-group">
                 <select v-model="form.mauSacId" class="form-select" required>
                   <option value="">Chọn màu sắc</option>
                   <option
                     v-for="color in mauSacList"
                     :key="color.id"
                     :value="color.id"
                   >
                     {{ color.tenMau }}
                   </option>
                 </select>
                 <button
                   type="button"
                   class="btn btn-outline-primary"
                   @click="mauSacModal.show()"
                 >
                   <i class="bi bi-plus"></i>
                 </button>
               </div>
               <small v-if="errors.mauSacId" class="text-danger">{{ errors.mauSacId }}</small>
             </div>

             <div class="mb-3">
               <label class="form-label">Size</label>
               <div class="input-group">
                 <select v-model="form.sizeIds" class="form-select" multiple required>
                   <option v-for="size in sizeList" :key="size.id" :value="size.id">
                     {{ size.tenSize }}
                   </option>
                 </select>
                 <button type="button" class="btn btn-outline-primary" @click="sizeModal.show()">
                   <i class="bi bi-plus"></i>
                 </button>
               </div>
               <small class="text-muted">Giữ Ctrl để chọn nhiều size</small>
               <small v-if="errors.sizeIds" class="text-danger">{{ errors.sizeIds }}</small>
             </div>
             <div class="mb-3">
               <label class="form-label">Giá bán</label>
               <input
                 v-model="form.giaBan"
                 type="number"
                 class="form-control"
                 required
               />
               <small v-if="errors.giaBan" class="text-danger">{{ errors.giaBan }}</small>
             </div>

             <div class="mb-3">
               <label class="form-label">Số lượng</label>
               <input
                 v-model="form.soLuong"
                 type="number"
                 class="form-control"
                 required
               />
               <small v-if="errors.soLuong" class="text-danger">{{ errors.soLuong }}</small>
             </div>

             <div class="d-flex gap-2">
               <button
                 type="submit"
                 class="btn btn-primary flex-grow-1"
                 :disabled="loading"
               >
                 <span
                   v-if="loading"
                   class="spinner-border spinner-border-sm me-2"
                 ></span>
                 {{ isEditing ? "Cập nhật" : "Thêm mới" }}
               </button>
               <button type="button" class="btn btn-secondary" @click="hide">
                 Hủy
               </button>
             </div>
           </form>

           <!-- Display Product Info (only visible in View Mode) -->
           <div v-if="isViewMode && form" class="row">
             <!-- Product Image Carousel -->
             <div class="col-md-5">
               <div id="carouselExampleCaptions" class="carousel slide mb-4" data-bs-ride="carousel">
                 <div class="carousel-indicators">
                   <!-- Dynamically generate the indicators based on the number of images -->
                   <button
                     v-for="(image, index) in form.danhSachAnh"
                     :key="index"
                     :data-bs-target="'#carouselExampleCaptions'"
                     :data-bs-slide-to="index"
                     :class="index === 0 ? 'active' : ''"
                     :aria-label="'Slide ' + (index + 1)"
                   ></button>
                 </div>
                 <div class="carousel-inner">
                   <!-- Dynamically generate the carousel items based on the images -->
                   <div
                     v-for="(image, index) in form.danhSachAnh"
                     :key="index"
                     class="carousel-item"
                     :class="index === 0 ? 'active' : ''"
                   >
                     <img :src="image" class="d-block w-100" :alt="'Ảnh sản phẩm ' + (index + 1)" />
                   </div>
                 </div>
                 <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide="prev">
                   <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                   <span class="visually-hidden">Previous</span>
                 </button>
                 <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide="next">
                   <span class="carousel-control-next-icon" aria-hidden="true"></span>
                   <span class="visually-hidden">Next</span>
                 </button>
               </div>
             </div>

             <!-- Product Details -->
             <div class="col-md-7">
               <div class="card p-4 shadow-sm rounded">
                 <h3 class="text-primary font-weight-bold">{{ form.maSPCT }}</h3>
                 <div class="mb-3">
                   <p><strong>Màu sắc:</strong> {{ form.mauSacName }}</p>
                   <p><strong>Size:</strong> {{ form.sizeName }}</p>
                   <p><strong>Giá bán:</strong> {{ form.giaBan.toLocaleString() }} đ</p>
                   <p><strong>Số lượng:</strong> {{ form.soLuong }}</p>
                   <p><strong>Danh mục:</strong> {{ form.tenDanhMuc || 'Không có' }}</p>
                   <p><strong>Đế giày:</strong> {{ form.tenDeGiay || 'Không có' }}</p>
                   <p><strong>Chất liệu:</strong> {{ form.tenChatLieu || 'Không có' }}</p>
                   <p><strong>Thương hiệu:</strong> {{ form.tenThuongHieu || 'Không có' }}</p>
                   <p><strong>Trạng thái:</strong>
                     <span :class="form.trangThai ? 'text-success' : 'text-danger'">
                       {{ form.trangThai ? 'Hoạt động' : 'Ngừng bán' }}
                     </span>
                   </p>
                 </div>
               </div>
             </div>
           </div>


         </div>
       </div>
     </div>
   </div>

   <!-- Add Property Modals -->

   <add-detail-property-modal
     ref="mauSacModal"
     title="Thêm màu sắc"
     property-name="tenMau"
     property-label="màu sắc"
     :api-url="`${API_URL}/mau-sac`"
     @added="handlePropertyAdded('mauSac')"
   />
   <add-detail-property-modal
     ref="sizeModal"
     title="Thêm size"
     property-name="tenSize"
     property-label="size"
     :api-url="`${API_URL}/size`"
     @added="handlePropertyAdded('size')"
   />
 </template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { Modal } from "bootstrap";
import axios from "axios";
import AddDetailPropertyModal from './AddDetailPropertyModal.vue';

const API_URL = "http://localhost:8080";
const props = defineProps({
  productId: {
    type: Number,
    required: true,
  },
  detail: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(["saved"]);

const modal = ref(null);
const bsModal = ref(null);
const loading = ref(false);
const isEditing = ref(false);

const isViewMode = ref(false);
const editingId = ref(null);
const errors = ref({});

const khuyenMaiModal = ref(null);
const mauSacModal = ref(null);
const sizeModal = ref(null);

const form = ref({
  maSPCT: "",
  mauSacId: "",
  sizeId: "",
  giaBan: "",
  soLuong: "",
  trangThai: true,
});

const mauSacList = ref([]);
const sizeList = ref([]);
const khuyenMaiList = ref([]);

const show = async (detail = null, viewMode = false) => {
  isViewMode.value = viewMode;

  if (detail) {
    // Chỉnh sửa SPCT
    isEditing.value = true;
    editingId.value = detail.id;

    // Lấy ID của sản phẩm chính từ SPCT
    const productId = detail.sanPhamId;  // Lấy ID của sản phẩm chính

    // Kiểm tra xem productId có hợp lệ không
    if (!productId) {
      console.error("Sản phẩm không có ID!");
      return;
    }

    // Lấy ID của màu sắc, size, và khuyến mãi
    const mauSacId = mauSacList.value.find(m => m.tenMau === detail.tenMau)?.id;
    const sizeId = sizeList.value.find(s => s.tenSize === detail.tenSize)?.id;
    const khuyenMaiId = khuyenMaiList.value.find(k => k.tenKhuyenMai === detail.tenKhuyenMai)?.id;

    try {
      // Gọi API để lấy thông tin sản phẩm chính từ ID sản phẩm chính
      const response = await axios.get(`${API_URL}/san-pham/${productId}`);
      const product = response.data; // Lấy dữ liệu sản phẩm chính

      // Cập nhật form với dữ liệu sản phẩm chi tiết và sản phẩm chính
      form.value = {
        mauSacId: mauSacId || '',
        sizeIds: detail.sizeIds || (sizeId ? [sizeId] : []),
        maSPCT: detail.maSPCT || "Không có",
        mauSacName: detail.tenMau || "Không có",
        sizeName: detail.tenSize || "Không có",
        giaBan: detail.giaBan || 0,
        soLuong: detail.soLuong || 0,
        tenDanhMuc: detail.tenDanhMuc || "Không có",
        tenDeGiay: detail.tenDeGiay || "Không có",
        tenChatLieu: detail.tenChatLieu || "Không có",
        tenThuongHieu: detail.tenThuongHieu || "Không có",
        trangThai: detail.trangThai,
       danhSachAnh: product.danhSachAnh || [],
      };

      // Hiển thị modal sau khi đã lấy dữ liệu
      bsModal.value.show();

    } catch (error) {
      console.error("Lỗi khi lấy dữ liệu sản phẩm chính:", error);
      // Hiển thị thông báo lỗi nếu không thể lấy dữ liệu sản phẩm
    }

  } else {
    // Thêm mới SPCT
    isEditing.value = false;
    editingId.value = null;

    // Reset form cho sản phẩm mới
    form.value = {
      maSPCT: "",
      mauSacId: "",
      sizeId: "",
      giaBan: "",
      soLuong: "",
      trangThai: true,
      urlAnh: "https://via.placeholder.com/150",  // Default placeholder image
    };

    // Hiển thị modal thêm sản phẩm mới
    bsModal.value.show();
  }
};


const hide = () => {
  bsModal.value.hide();
  resetForm();
};

const resetForm = () => {
  form.value = {
    maSPCT: "",
    mauSacId: "",
    sizeIds: [], // Updated to array
    giaBan: "",
    soLuong: "",
    trangThai: true,
  };
  errors.value = {};
};
const generateMaSPCT = async (sanPhamId) => {
  try {
    // Get all product details
    const response = await axios.get(`${API_URL}/san-pham-chi-tiet/all`);
    const spctList = response.data;

    // Find the highest numeric part across all SPCTs
    let maxNumber = 0;
    spctList.forEach(spct => {
      if (spct.maSPCT && spct.maSPCT.startsWith('SPCT')) {
        const matches = spct.maSPCT.match(/SPCT(\d+)/);
        if (matches) {
          const number = parseInt(matches[1], 10);
          maxNumber = Math.max(maxNumber, number);
        }
      }
    });

    // Generate new code with padding (e.g., SPCT001, SPCT002)
    const nextNumber = maxNumber + 1;
    const paddedNumber = String(nextNumber).padStart(3, '0');
    const newCode = `SPCT${paddedNumber}`;

    // Verify the generated code is unique
    while (spctList.some(spct => spct.maSPCT === newCode)) {
      maxNumber++;
      const paddedNumber = String(maxNumber).padStart(3, '0');
      newCode = `SPCT${paddedNumber}`;
    }

    return newCode;
  } catch (error) {
    console.error("Error generating SPCT code:", error);
    // Generate a timestamp-based code as fallback
    const timestamp = Date.now().toString().slice(-6);
    return `SPCT${timestamp}`;
  }
};
const validateForm = async () => {
  errors.value = {};

  if (!form.value.mauSacId) {
    errors.value.mauSacId = "Vui lòng chọn màu sắc";
  }

  if (!form.value.sizeIds || form.value.sizeIds.length === 0) {
    errors.value.sizeIds = "Vui lòng chọn ít nhất một size";
  }

  if (!form.value.giaBan || Number(form.value.giaBan) <= 0) {
    errors.value.giaBan = "Giá bán phải lớn hơn 0";
  }

  if (!form.value.soLuong || Number(form.value.soLuong) <= 0) {
    errors.value.soLuong = "Số lượng phải lớn hơn 0";
  }
 try {
    const checkPromises = form.value.sizeIds.map((sizeId) =>
      axios.get(`${API_URL}/san-pham-chi-tiet/check-duplicate`, {
        params: {
          sanPhamId: props.productId,
          mauSacId: form.value.mauSacId,
          sizeId: sizeId,
          excludeId: isEditing.value && editingId.value ? editingId.value : null
        }
      })
    );

    const results = await Promise.all(checkPromises);

    // Lấy ra các size bị trùng
    const duplicatedSizes = form.value.sizeIds.filter((_, idx) => results[idx].data.exists);

    if (duplicatedSizes.length > 0) {
      // Lấy tên màu
      const tenMau = mauSacList.value.find(m => m.id === form.value.mauSacId)?.tenMau || "Không xác định";

      // Ghép từng size bị trùng với màu sắc
      const details = duplicatedSizes.map(sizeId => {
        const tenSize = sizeList.value.find(s => s.id === sizeId)?.tenSize || `ID: ${sizeId}`;
        return `Giày Màu ${tenMau} - Size ${tenSize}`;
      });

      errors.value.sizeIds = ` ${details.join(", ")} đã tồn tại !`;
    }
  } catch (err) {
    console.error("Lỗi kiểm tra trùng:", err);
    errors.value.sizeIds = "Lỗi khi kiểm tra trùng sản phẩm";
  }

  return Object.keys(errors.value).length === 0;
};

const handleSubmit = async () => {
  if (!(await validateForm())) return;

  loading.value = true;
  try {
    // Create an array of promises for each size
    const promises = form.value.sizeIds.map(async (sizeId) => {
      const maSPCT = await generateMaSPCT(props.productId);
      const data = {
        sanPham: { id: props.productId },
        mauSac: { id: form.value.mauSacId },
        size: { id: sizeId },
        giaBan: Number(form.value.giaBan),
        soLuong: Number(form.value.soLuong),
        trangThai: form.value.trangThai,
        maSPCT: maSPCT,
      };

      if (isEditing.value) {
        return axios.put(`${API_URL}/san-pham-chi-tiet/${editingId.value}`, data);
      } else {
        return axios.post(`${API_URL}/san-pham-chi-tiet/add-spct`, data);
      }
    });

    if (!isEditing.value && !window.confirm(`Bạn có chắc chắn muốn thêm ${form.value.sizeIds.length} chi tiết sản phẩm không?`)) {
      return;
    }

    await Promise.all(promises);
    alert(isEditing.value ? 'Cập nhật chi tiết sản phẩm thành công' : 'Thêm chi tiết sản phẩm thành công');
    emit("saved");
    hide();
  } catch (error) {
    console.error("Error saving product details:", error);
    alert("Có lỗi xảy ra khi lưu chi tiết sản phẩm");
  } finally {
    loading.value = false;
  }
};


const handlePropertyAdded = async (type) => {
  await fetchDropdownData();
};



const fetchDropdownData = async () => {
  try {
    const [mauSacRes, sizeRes, khuyenMaiRes] = await Promise.all([
      axios.get(`${API_URL}/san-pham-chi-tiet/mau-sac`),
      axios.get(`${API_URL}/san-pham-chi-tiet/size`),
      axios.get(`${API_URL}/san-pham-chi-tiet/khuyen-mai`),
    ]);

    mauSacList.value = mauSacRes.data;
    sizeList.value = sizeRes.data;
    khuyenMaiList.value = khuyenMaiRes.data;
  } catch (error) {
    console.error("Error fetching dropdown data:", error);
  }
};

onMounted(() => {
  bsModal.value = new Modal(modal.value);
  fetchDropdownData();
});

defineExpose({
  show,
  fetchDropdownData,
});
</script>
<style scoped>
/* Card with shadow and rounded corners */
.card {
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* Carousel image */
.carousel-item img {
  object-fit: cover; /* To make images fill the carousel container */
  height: 400px; /* Fixed height for images */
  border-radius: 8px;
}

/* Add hover effect on carousel image */
.carousel-item img:hover {
  transform: scale(1.05);
  transition: transform 0.3s ease-in-out;
}

/* Adjust card padding */
.card p {
  font-size: 1rem;
  line-height: 1.5;
}

/* Apply margin and spacing to buttons */
button {
  margin-top: 10px;
}

.carousel-control-prev-icon,
.carousel-control-next-icon {
  background-color: #000;
}
</style>