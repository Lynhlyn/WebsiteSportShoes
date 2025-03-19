<script setup>
import { ref, onMounted, watch } from "vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const id = route.params.id;
const BASE_URL = "http://localhost:8080/san-pham-chi-tiet";

const SPCT = ref({
      maSPCT: "",
      sanPham: null,
      mauSac: null,
      khuyenMai: null,
      size: null,
      giaBan: 0,
      soLuong: 0,
      trangThai: true,
      phanTramGiamGia: 0,
      ngayBatDau: "",
      ngayKetThuc: "",
});

const sanPhamList = ref([]);
const mauSacList = ref([]);
const khuyenMaiList = ref([]);
const sizeList = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const errors = ref({});

const fetchDropdownData = async () => {
      loading.value = true;
      try {
            const [spRes, msRes, kmRes, sizeRes] = await Promise.all([
                  axios.get(`${BASE_URL}/san-pham`), // Kiểm tra đường dẫn này
                  axios.get(`${BASE_URL}/mau-sac`),
                  axios.get(`${BASE_URL}/khuyen-mai`),
                  axios.get(`${BASE_URL}/size`),
            ]);
            sanPhamList.value = spRes.data;
            mauSacList.value = msRes.data;
            khuyenMaiList.value = kmRes.data;
            sizeList.value = sizeRes.data;
      } catch (error) {
            console.error("Lỗi tải dữ liệu từ API:", error.response?.data || error.message);
            errorMessage.value = "Không thể tải dữ liệu từ máy chủ.";
      } finally {
            loading.value = false;
      }
};

const fetchSanPhamChiTiet = async () => {
      if (!id) {
            errorMessage.value = "ID sản phẩm chi tiết không hợp lệ!";
            return;
      }
      loading.value = true;
      try {
            const response = await axios.get(`${BASE_URL}/${id}`);
            if (response.data) {
                  SPCT.value = {
                        maSPCT: response.data.maSPCT,
                        sanPham: response.data.sanPham ? response.data.sanPham.id : null,
                        mauSac: response.data.mauSac ? response.data.mauSac.id : null,
                        khuyenMai: response.data.khuyenMai ? response.data.khuyenMai.id : null,
                        size: response.data.size ? response.data.size.id : null,
                        giaBan: response.data.giaBan,
                        soLuong: response.data.soLuong,
                        trangThai: response.data.trangThai,
                  };
            } else {
                  errorMessage.value = "Không tìm thấy sản phẩm chi tiết.";
            }
      } catch (error) {
            errorMessage.value = "Lỗi khi tải dữ liệu sản phẩm.";
            console.error("API Error: ", error);
      } finally {
            loading.value = false;
      }
};

// Khi chọn khuyến mãi, tự động cập nhật phần trăm giảm giá và ngày hiệu lực
watch(() => SPCT.value.khuyenMai, (newVal) => {
      if (newVal) {
            const selectedKM = khuyenMaiList.value.find(km => km.id === newVal);
            if (selectedKM) {
                  SPCT.value.phanTramGiamGia = selectedKM.phanTramGiamGia || 0;
                  SPCT.value.ngayBatDau = selectedKM.ngayBatDau || "";
                  SPCT.value.ngayKetThuc = selectedKM.ngayKetThuc || "";
            }
      } else {
            SPCT.value.phanTramGiamGia = 0;
            SPCT.value.ngayBatDau = "";
            SPCT.value.ngayKetThuc = "";
      }
});


const validateForm = () => {
      errors.value = {};
      if (!SPCT.value.maSPCT.trim()) errors.value.maSPCT = "Mã SPCT không được để trống!";
      if (!SPCT.value.sanPham) errors.value.sanPham = "Vui lòng chọn sản phẩm!";
      if (!SPCT.value.mauSac) errors.value.mauSac = "Vui lòng chọn màu sắc!";
      if (!SPCT.value.khuyenMai) errors.value.khuyenMai = "Vui lòng chọn khuyến mãi!";
      if (!SPCT.value.size) errors.value.size = "Vui lòng chọn size!";
      if (!SPCT.value.giaBan || SPCT.value.giaBan <= 0) errors.value.giaBan = "Giá bán phải lớn hơn 0!";
      if (!SPCT.value.soLuong || SPCT.value.soLuong <= 0) errors.value.soLuong = "Số lượng phải lớn hơn 0!";
      return Object.keys(errors.value).length === 0;
};

const updateSPCT = async () => {
      if (!validateForm()) return;
      loading.value = true;
      errorMessage.value = "";
      successMessage.value = "";
      try {
            const payload = {
                  ...SPCT.value,
                  sanPham: sanPhamList.value.find(sp => sp.id === SPCT.value.sanPham) || null,
                  mauSac: mauSacList.value.find(ms => ms.id === SPCT.value.mauSac) || null,
                  khuyenMai: khuyenMaiList.value.find(km => km.id === SPCT.value.khuyenMai) || null,
                  size: sizeList.value.find(s => s.id === SPCT.value.size) || null,
            };

            await axios.put(`${BASE_URL}/${id}`, payload);
            successMessage.value = "Cập nhật sản phẩm chi tiết thành công!";
            setTimeout(() => router.push("/admin/products/details"), 1500);
      } catch (error) {
            errorMessage.value = "Lỗi khi cập nhật sản phẩm chi tiết.";
            console.error("Lỗi khi cập nhật sản phẩm:", error);
      } finally {
            loading.value = false;
      }
};

onMounted(async () => {
      await fetchDropdownData();
      await fetchSanPhamChiTiet();
});
</script>

<template>
      <div class="container mt-4">
            <h2 class="text-center">Cập nhật sản phẩm chi tiết</h2>

            <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
            <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>
            <div v-if="successMessage" class="alert alert-success text-center">{{ successMessage }}</div>

            <form v-if="!loading" @submit.prevent="updateSPCT">
                  <!-- Mã SPCT -->
                  <div class="mb-3">
                        <label class="form-label">Mã SPCT</label>
                        <input v-model="SPCT.maSPCT" type="text" class="form-control"
                              placeholder="Nhập mã sản phẩm chi tiết">
                        <small v-if="errors.maSPCT" class="text-danger">{{ errors.maSPCT }}</small>
                  </div>

                  <!-- Sản phẩm -->
                  <div class="mb-3">
                        <label class="form-label">Sản phẩm</label>
                        <select v-model="SPCT.sanPham" class="form-select" :disabled="loading || !sanPhamList.length">
                              <option value="" disabled>Chọn sản phẩm</option>
                              <option v-for="sp in sanPhamList" :key="sp.id" :value="sp.id">
                                    {{ sp.tenSanPham }}
                              </option>

                        </select>
                        <small v-if="errors.sanPham" class="text-danger">{{ errors.sanPham }}</small>
                  </div>

                  <!-- Màu sắc -->
                  <div class="mb-3">
                        <label class="form-label">Màu sắc</label>
                        <select v-model="SPCT.mauSac" class="form-select" :disabled="loading || !mauSacList.length">
                              <option value="" disabled>Chọn màu sắc</option>
                              <option v-for="ms in mauSacList" :key="ms.id" :value="ms.id">
                                    {{ ms.tenMau }}
                              </option>
                        </select>
                        <small v-if="errors.mauSac" class="text-danger">{{ errors.mauSac }}</small>
                  </div>

                  <!-- Khuyến mãi -->
                  <div class="mb-3">
                        <label class="form-label">Khuyến mãi</label>
                        <select v-model="SPCT.khuyenMai" class="form-select"
                              :disabled="loading || !khuyenMaiList.length">
                              <option value="" disabled>Chọn khuyến mãi</option>
                              <option v-for="km in khuyenMaiList" :key="km.id" :value="km.id">
                                    {{ km.tenKhuyenMai }} - {{ km.phanTramGiamGia }}%
                              </option>
                        </select>
                        <small v-if="errors.khuyenMai" class="text-danger">{{ errors.khuyenMai }}</small>
                  </div>

                  <!-- Size -->
                  <div class="mb-3">
                        <label class="form-label">Size</label>
                        <select v-model="SPCT.size" class="form-select" :disabled="loading || !sizeList.length">
                              <option value="" disabled>Chọn size</option>
                              <option v-for="s in sizeList" :key="s.id" :value="s.id">{{ s.tenSize }}</option>
                        </select>
                        <small v-if="errors.size" class="text-danger">{{ errors.size }}</small>
                  </div>

                  <!-- Giá bán -->
                  <div class="mb-3">
                        <label class="form-label">Giá bán</label>
                        <input v-model="SPCT.giaBan" type="number" class="form-control"
                              placeholder="Nhập giá bán sản phẩm">
                        <small v-if="errors.giaBan" class="text-danger">{{ errors.giaBan }}</small>
                  </div>

                  <!-- Số lượng -->
                  <div class="mb-3">
                        <label class="form-label">Số lượng</label>
                        <input v-model="SPCT.soLuong" type="number" class="form-control"
                              placeholder="Nhập số lượng sản phẩm">
                        <small v-if="errors.soLuong" class="text-danger">{{ errors.soLuong }}</small>
                  </div>

                  <!-- Trạng thái -->
                  <div class="mb-3">
                        <label class="form-label">Trạng thái</label>
                        <select v-model="SPCT.trangThai" class="form-select">
                              <option :value="true">Hoạt động</option>
                              <option :value="false">Ngừng hoạt động</option>
                        </select>
                  </div>

                  <button type="submit" class="btn btn-primary" :disabled="loading">Cập nhật</button>
                  <router-link to="/admin/products/details" class="btn btn-secondary ms-2">Hủy</router-link>
            </form>
      </div>
</template>
