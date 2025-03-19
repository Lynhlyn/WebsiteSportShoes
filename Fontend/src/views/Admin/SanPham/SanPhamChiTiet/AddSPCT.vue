<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const BASE_URL = "http://localhost:8080/san-pham-chi-tiet";

const maSPCT = ref("");
const tenSanPham = ref("");
const tenMau = ref("");
const phanTramGiamGia = ref("");
const ngayBatDau = ref("");
const ngayKetThuc = ref("");
const tenSize = ref("");
const giaBan = ref("");
const soLuong = ref("");
const trangThai = ref(true);

const sanPhamList = ref([]);
const mauSacList = ref([]);
const khuyenMaiList = ref([]);
const selectedKhuyenMai = ref("");
const sizeList = ref([]);

const isLoading = ref(false);
const errors = ref({});

const fetchDropdownData = async () => {
      try {
            const responses = await Promise.allSettled([
                  axios.get(`${BASE_URL}/san-pham`),
                  axios.get(`${BASE_URL}/mau-sac`),
                  axios.get(`${BASE_URL}/khuyen-mai`),
                  axios.get(`${BASE_URL}/size`),
            ]);

            responses.forEach((res, index) => {
                  if (res.status === "fulfilled") {
                        switch (index) {
                              case 0: sanPhamList.value = res.value.data; break;
                              case 1: mauSacList.value = res.value.data; break;
                              case 2: khuyenMaiList.value = res.value.data; break;
                              case 3: sizeList.value = res.value.data; break;
                        }
                  } else {
                        console.error("Lỗi tải dữ liệu:", res.reason);
                  }
            });
      } catch (error) {
            alert("Không thể tải dữ liệu từ máy chủ.");
      }
};


// Theo dõi thay đổi khi chọn khuyến mãi
watch(selectedKhuyenMai, (newVal) => {
      if (newVal) {
            const selected = khuyenMaiList.value.find(km => km.id === newVal);
            if (selected) {
                  phanTramGiamGia.value = selected.phanTramGiamGia || "";
                  ngayBatDau.value = selected.ngayBatDau || "";
                  ngayKetThuc.value = selected.ngayKetThuc || "";
            }
      } else {
            phanTramGiamGia.value = "";
            ngayBatDau.value = "";
            ngayKetThuc.value = "";
      }
});


const validateForm = () => {
      errors.value = {};
      if (!maSPCT.value.trim()) errors.value.maSPCT = "Mã SPCT không được trống";
      if (!tenSanPham.value) errors.value.tenSanPham = "Vui lòng chọn sản phẩm";
      if (!tenMau.value) errors.value.tenMau = "Vui lòng chọn màu sắc";
      if (!selectedKhuyenMai.value) errors.value.selectedKhuyenMai = "Vui lòng chọn khuyến mãi";
      if (!tenSize.value) errors.value.tenSize = "Vui lòng chọn size";
      if (!giaBan.value || isNaN(giaBan.value) || Number(giaBan.value) <= 0) {
            errors.value.giaBan = "Giá bán phải là số và lớn hơn 0";
      }
      if (!soLuong.value || isNaN(soLuong.value) || Number(soLuong.value) <= 0) {
            errors.value.soLuong = "Số lượng phải là số và lớn hơn 0";
      }

      console.log(errors);
      return Object.keys(errors.value).length === 0;
};

const handleAddSPCT = async () => {
      if (!validateForm()) return;

      const newSPCT = {
            maSPCT: maSPCT.value.trim(),
            sanPham: { id: tenSanPham.value },  
            mauSac: { id: tenMau.value },       
            khuyenMai: { id: selectedKhuyenMai.value },  
            phanTramGiamGia: Number(phanTramGiamGia.value),
            ngayBatDau: ngayBatDau.value,
            ngayKetThuc: ngayKetThuc.value,
            size: { id: tenSize.value },        
            giaBan: Number(giaBan.value),
            soLuong: Number(soLuong.value),
            trangThai: trangThai.value
      };

      try {
            const response = await axios.post(`${BASE_URL}/add-spct`, newSPCT);
            alert("Sản phẩm đã được thêm thành công!");
            router.push('/admin/products/details');
      } catch (error) {
            console.error("Lỗi khi thêm sản phẩm:", error.response?.data || error.message);
            errors.value.apiError = error.response?.data?.message || "Có lỗi xảy ra!";
      }
};


onMounted(() => {
      fetchDropdownData();
});

</script>
<template>
      <div class="p-4" style="min-height: 450px;">
            <h1 class="mb-4 text-center">Thêm sản phẩm chi tiết mới</h1>

            <div class="card p-4 shadow-sm">
                  <form @submit.prevent="handleAddSPCT">
                        <!-- Mã SPCT -->
                        <div class="mb-3">
                              <label class="form-label">Mã SPCT</label>
                              <input v-model="maSPCT" type="text" class="form-control"
                                    placeholder="Nhập mã sản phẩm chi tiết">
                              <small v-if="errors.maSPCT" class="text-danger">{{ errors.maSPCT }}</small>
                        </div>
                        <!-- Sản phẩm -->
                        <div class="mb-3">
                              <label class="form-label">Sản phẩm</label>
                              <select v-model="tenSanPham" class="form-select"
                                    :disabled="isLoading || !sanPhamList.length">
                                    <option value="" disabled>Chọn sản phẩm</option>
                                    <option v-for="sp in sanPhamList" :key="sp.id" :value="sp.id">{{ sp.tenSanPham }}
                                    </option>
                              </select>
                              <small v-if="errors.tenSanPham" class="text-danger">{{ errors.tenSanPham }}</small>
                        </div>

                        <!-- Màu sắc -->
                        <div class="mb-3">
                              <label class="form-label">Màu sắc</label>
                              <select v-model="tenMau" class="form-select" :disabled="isLoading || !mauSacList.length">
                                    <option value="" disabled>Chọn màu sắc</option>
                                    <option v-for="ms in mauSacList" :key="ms.id" :value="ms.id">{{ ms.tenMau }}
                                    </option>
                              </select>
                              <small v-if="errors.tenMau" class="text-danger">{{ errors.tenMau }}</small>
                        </div>

                        <!-- Thông tin khuyến mãi -->
                        <div class="mb-3">
                              <label class="form-label">Khuyến mãi</label>
                              <select v-model="selectedKhuyenMai" class="form-select"
                                    :disabled="isLoading || !khuyenMaiList.length">
                                    <option value="" disabled>Chọn khuyến mãi</option>
                                    <option v-for="km in khuyenMaiList" :key="km.id" :value="km.id">
                                          {{ km.tenKhuyenMai }} - {{ km.phanTramGiamGia }}%
                                    </option>
                              </select>
                              <small v-if="errors.selectedKhuyenMai" class="text-danger">{{ errors.selectedKhuyenMai
                              }}</small>
                        </div>

                        <!-- Size -->
                        <div class="mb-3">
                              <label class="form-label">Size</label>
                              <select v-model="tenSize" class="form-select" :disabled="isLoading || !sizeList.length">
                                    <option value="" disabled>Chọn size</option>
                                    <option v-for="s in sizeList" :key="s.id" :value="s.id">{{ s.tenSize }}
                                    </option>
                              </select>
                              <small v-if="errors.tenSize" class="text-danger">{{ errors.tenSize }}</small>
                        </div>

                        <!-- Giá bán -->
                        <div class="mb-3">
                              <label class="form-label">Giá bán</label>
                              <input v-model="giaBan" type="text" class="form-control"
                                    placeholder="Nhập giá bán sản phẩm">
                              <small v-if="errors.giaBan" class="text-danger">{{ errors.giaBan }}</small>
                        </div>

                        <!-- Số lượng -->
                        <div class="mb-3">
                              <label class="form-label">Số lượng</label>
                              <input v-model="soLuong" type="text" class="form-control"
                                    placeholder="Nhập số lượng sản phẩm">
                              <small v-if="errors.soLuong" class="text-danger">{{ errors.soLuong }}</small>
                        </div>

                        <!-- Trạng thái -->
                        <div class="mb-3">
                              <label class="form-label">Trạng thái</label>
                              <select v-model="trangThai" class="form-select">
                                    <option :value="true">Hoạt động</option>
                                    <option :value="false">Ngừng hoạt động</option>
                              </select>
                        </div>

                        <!-- Thông báo lỗi API -->
                        <div v-if="errors.apiError" class="alert alert-danger">{{ errors.apiError }}</div>

                        <div class="d-flex gap-3">
                              <button type="submit" class="btn btn-primary" :disabled="isLoading">
                                    {{ isLoading ? "Đang xử lý..." : "Thêm sản phẩm chi tiết" }}
                              </button>
                              <button type="button" class="btn btn-secondary" @click="router.go(-1)">
                                    Quay lại
                              </button>
                        </div>
                  </form>
            </div>
      </div>
</template>