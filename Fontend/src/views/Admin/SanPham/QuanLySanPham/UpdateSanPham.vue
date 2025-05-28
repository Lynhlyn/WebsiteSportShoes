<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const id = route.params.id;

const urlSanPham = "http://localhost:8080/san-pham";

const sanPham = ref({
      tenSanPham: "",
      moTa: "",
      danhMuc: null,  // Keep it as id for proper binding
      thuongHieu: null,  // Keep it as id for proper binding
      chatLieu: null,  // Keep it as id for proper binding
      deGiay: null,  // Keep it as id for proper binding
      trangThai: true,
});

const danhMucList = ref([]);
const thuongHieuList = ref([]);
const chatLieuList = ref([]);
const deGiayList = ref([]);
const selectedFiles = ref([]);
const hinhAnhList = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");

// Load danh sách danh mục, thương hiệu, chất liệu, đế giày
const fetchDropdownData = async () => {
      try {
            const [danhMucRes, thuongHieuRes, chatLieuRes, deGiayRes] = await Promise.all([
                  axios.get("http://localhost:8080/danh-muc"),
                  axios.get("http://localhost:8080/thuong-hieu"),
                  axios.get("http://localhost:8080/chat-lieu"),
                  axios.get("http://localhost:8080/de-giay"),
            ]);

            danhMucList.value = danhMucRes.data;
            thuongHieuList.value = thuongHieuRes.data;
            chatLieuList.value = chatLieuRes.data;
            deGiayList.value = deGiayRes.data;
      } catch (error) {
            console.error("Lỗi khi tải dữ liệu danh mục:", error);
      }
};

// Load dữ liệu sản phẩm theo ID
const fetchSanPham = async () => {
      if (!id) {
            errorMessage.value = "ID sản phẩm không hợp lệ!";
            return;
      }
      loading.value = true;
      try {
            const response = await axios.get(`${urlSanPham}/${id}`);
            if (response.data) {
                  sanPham.value = response.data;
                  hinhAnhList.value = response.data.hinhAnhs || [];
            } else {
                  throw new Error("Không tìm thấy sản phẩm!");
            }
      } catch (error) {
            errorMessage.value = "Lỗi khi tải dữ liệu sản phẩm.";
            console.error("API Error: ", error);
      } finally {
            loading.value = false;
      }
};

// Gửi yêu cầu cập nhật sản phẩm
const updateSanPham = async () => {
      if (!sanPham.value.tenSanPham.trim()) {
            errorMessage.value = "Tên sản phẩm không được để trống!";
            return;
      }

      const formData = new FormData();
      formData.append("tenSanPham", sanPham.value.tenSanPham);
      formData.append("moTa", sanPham.value.moTa);
      formData.append("trangThai", sanPham.value.trangThai);
      hinhAnhList.value.forEach((img) => formData.append("urlAnh", img.url));
      selectedFiles.value.forEach((file) => formData.append("uploadedFiles", file));

      try {
            await axios.put(`${urlSanPham}/${id}`, formData, {
                  headers: { "Content-Type": "multipart/form-data" },
            });
            successMessage.value = "Cập nhật sản phẩm thành công!";
            setTimeout(() => router.push("/admin/products/manage"), 1500);
      } catch (error) {
            errorMessage.value = "Lỗi khi cập nhật sản phẩm.";
            console.error("Lỗi:", error);
      }
};

onMounted(() => {
      fetchDropdownData();
      fetchSanPham();
});
</script>

<template>
      <div class="container mt-4">
            <div class="card shadow-lg p-4">
                  <h2 class="text-center mb-4">Cập nhật sản phẩm</h2>

                  <div v-if="loading" class="alert alert-info text-center fade show">
                        <span class="spinner-border spinner-border-sm"></span> Đang tải dữ liệu...
                  </div>
                  <div v-if="errorMessage" class="alert alert-danger text-center fade show">{{ errorMessage }}</div>
                  <div v-if="successMessage" class="alert alert-success text-center fade show">{{ successMessage }}
                  </div>

                  <form v-if="!loading" @submit.prevent="updateSanPham">
                        <div class="mb-3">
                              <label class="form-label">Tên sản phẩm</label>
                              <input v-model="sanPham.tenSanPham" class="form-control shadow-sm" required />
                        </div>

                        <div class="mb-3">
                              <label class="form-label">Mô tả</label>
                              <textarea v-model="sanPham.moTa" class="form-control shadow-sm"></textarea>
                        </div>

                        <div class="row">
                              <div class="col-md-6 mb-3">
                                    <label class="form-label">Danh Mục</label>
                                    <select v-model="sanPham.danhMuc" class="form-select shadow-sm">
                                          <option v-for="dm in danhMucList" :key="dm.id" :value="dm.id">{{ dm.tenDanhMuc
                                                }}</option>
                                    </select>
                              </div>

                              <div class="col-md-6 mb-3">
                                    <label class="form-label">Thương Hiệu</label>
                                    <select v-model="sanPham.thuongHieu" class="form-select shadow-sm">
                                          <option v-for="th in thuongHieuList" :key="th.id" :value="th.id">{{
                                                th.tenThuongHieu }}</option>
                                    </select>
                              </div>
                        </div>

                        <div class="row">
                              <div class="col-md-6 mb-3">
                                    <label class="form-label">Chất Liệu</label>
                                    <select v-model="sanPham.chatLieu" class="form-select shadow-sm">
                                          <option v-for="cl in chatLieuList" :key="cl.id" :value="cl.id">{{
                                                cl.tenChatLieu }}</option>
                                    </select>
                              </div>

                              <div class="col-md-6 mb-3">
                                    <label class="form-label">Đế giày</label>
                                    <select v-model="sanPham.deGiay" class="form-select shadow-sm">
                                          <option v-for="dg in deGiayList" :key="dg.id" :value="dg.id">{{ dg.tenDeGiay
                                                || "Không có dữ liệu" }}</option>
                                    </select>
                              </div>
                        </div>

                        <div class="mb-3">
                              <label class="form-label">Trạng thái</label>
                              <select v-model="sanPham.trangThai" class="form-select shadow-sm">
                                    <option :value="true">Hoạt động</option>
                                    <option :value="false">Ngừng hoạt động</option>
                              </select>
                        </div>

                        <div class="d-flex gap-3">
                              <button type="submit" class="btn btn-primary btn-lg shadow-sm" :disabled="loading">
                                    <span v-if="loading" class="spinner-border spinner-border-sm"></span>
                                    <i v-else class="bi bi-save"></i> Cập nhật
                              </button>
                              <button type="button" class="btn btn-secondary btn-lg shadow-sm" @click="router.go(-1)">
                                    <i class="bi bi-arrow-left"></i> Hủy
                              </button>
                        </div>
                  </form>
            </div>
      </div>
</template>

<style>
.form-control:focus,
.form-select:focus {
      border-color: black;
      box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

.card {
      border-radius: 10px;
      border: none;
}

.btn {
      border-radius: 8px;
      font-weight: 500;
}
</style>
