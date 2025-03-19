<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const id = route.params.id;

const urlKhuyenMai = "http://localhost:8080/khuyen-mai"; // URL API

// Dữ liệu khuyến mãi
const khuyenMai = ref({
      maKhuyenMai: "",
      tenKhuyenMai: "",
      ngayBatDau: "",
      ngayKetThuc: "",
      phanTramGiamGia: "",
      trangThai: true, // mặc định là true (hoạt động)
});

// Trạng thái loading và thông báo lỗi/success
const loading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");

// Hàm chuyển đổi ngày từ 'YYYY-MM-DD' sang 'YYYY-MM-DD HH:mm:ss'
const formatDate = (date) => {
      const newDate = new Date(date);
      const year = newDate.getFullYear();
      const month = String(newDate.getMonth() + 1).padStart(2, '0'); // Tháng (từ 0-11)
      const day = String(newDate.getDate()).padStart(2, '0');
      const hours = String(newDate.getHours()).padStart(2, '0');
      const minutes = String(newDate.getMinutes()).padStart(2, '0');
      const seconds = String(newDate.getSeconds()).padStart(2, '0');

      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// Load dữ liệu khuyến mãi từ backend
const fetchKhuyenMai = async () => {
  if (!id) {
    errorMessage.value = "ID khuyến mãi không hợp lệ!";
    return;
  }
  loading.value = true;
  try {
    const response = await axios.get(`${urlKhuyenMai}/${id}`);
    if (response.data) {
      khuyenMai.value = {
        maKhuyenMai: response.data.maKhuyenMai,
        tenKhuyenMai: response.data.tenKhuyenMai,
        ngayBatDau: response.data.ngayBatDau.split('T')[0],  // Lấy ngày theo định dạng 'YYYY-MM-DD'
        ngayKetThuc: response.data.ngayKetThuc.split('T')[0],  // Lấy ngày theo định dạng 'YYYY-MM-DD'
        phanTramGiamGia: response.data.phanTramGiamGia,
        trangThai: response.data.trangThai === true, // Giữ giá trị `true`/`false`
      };
    } else {
      throw new Error("Không tìm thấy khuyến mãi!");
    }
  } catch (error) {
    errorMessage.value = "Lỗi khi tải dữ liệu khuyến mãi.";
    console.error("API Error: ", error);
  } finally {
    loading.value = false;
  }
};


// Gửi yêu cầu cập nhật khuyến mãi
const updateKhuyenMai = async () => {
      if (!khuyenMai.value.tenKhuyenMai.trim()) {
            errorMessage.value = "Tên khuyến mãi không được để trống!";
            return;
      }

      loading.value = true;
      errorMessage.value = "";
      successMessage.value = "";

      try {
            // Chuyển đổi ngày theo định dạng 'YYYY-MM-DD HH:mm:ss'
            const payload = {
                  ...khuyenMai.value,
                  ngayBatDau: formatDate(khuyenMai.value.ngayBatDau),
                  ngayKetThuc: formatDate(khuyenMai.value.ngayKetThuc),
                  trangThai: khuyenMai.value.trangThai ? 1 : 0,  // Chuyển đổi true/false thành 1/0
            };

            await axios.put(`${urlKhuyenMai}/${id}`, payload);
            successMessage.value = "Cập nhật khuyến mãi thành công!";
            setTimeout(() => router.push("/admin/products/promotions"), 1500);
      } catch (error) {
            errorMessage.value = "Lỗi khi cập nhật khuyến mãi. Vui lòng thử lại!";
            console.error("Lỗi khi cập nhật khuyến mãi:", error);
      } finally {
            loading.value = false;
      }
};

onMounted(async () => {
      await fetchKhuyenMai();
});
</script>

<template>
      <div class="container mt-4">
            <h2 class="text-center">Cập nhật khuyến mãi</h2>

            <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
            <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>
            <div v-if="successMessage" class="alert alert-success text-center">{{ successMessage }}</div>

            <form v-if="!loading" @submit.prevent="updateKhuyenMai">
                  <div class="mb-3">
                        <label class="form-label">Mã khuyến mãi</label>
                        <input v-model="khuyenMai.maKhuyenMai" class="form-control" required disabled />
                  </div>

                  <div class="mb-3">
                        <label class="form-label">Tên khuyến mãi</label>
                        <input v-model="khuyenMai.tenKhuyenMai" class="form-control" required />
                  </div>

                  <div class="mb-3">
                        <label class="form-label">Ngày bắt đầu</label>
                        <input v-model="khuyenMai.ngayBatDau" type="date" class="form-control" required />
                  </div>

                  <div class="mb-3">
                        <label class="form-label">Ngày kết thúc</label>
                        <input v-model="khuyenMai.ngayKetThuc" type="date" class="form-control" required />
                  </div>

                  <div class="mb-3">
                        <label class="form-label">Phần trăm giảm giá</label>
                        <input v-model="khuyenMai.phanTramGiamGia" type="number" class="form-control" required />
                  </div>

                 <div class="mb-3">
                         <label class="form-label">Trạng thái</label>
                         <select v-model="khuyenMai.trangThai" class="form-select">
                           <option :value="true">Hoạt động</option>
                           <option :value="false">Ngừng hoạt động</option>
                         </select>
                       </div>




                  <button type="submit" class="btn btn-primary" :disabled="loading">Cập nhật</button>
                  <router-link to="/admin/products/promotions" class="btn btn-secondary ms-2">Hủy</router-link>
            </form>
      </div>
</template>
