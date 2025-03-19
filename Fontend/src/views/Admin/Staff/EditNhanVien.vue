<template>
  <div class="container my-5">
    <!-- Title -->
    <h2 class="text-center mb-4">Chỉnh Sửa Nhân Viên</h2>

    <!-- Form Chỉnh Sửa Nhân Viên -->
    <form @submit.prevent="editEmployee">
      <div class="row">
        <!-- Tên Nhân Viên -->
        <div class="col-md-6">
          <input v-model="employee.hoTen" type="text" class="form-control" placeholder="Tên Nhân Viên *" required />
        </div>

        <!-- Email -->
        <div class="col-md-6">
          <input v-model="employee.email" type="email" class="form-control" placeholder="Email *" required />
        </div>

        <!-- Số Điện Thoại -->
        <div class="col-md-6">
          <input v-model="employee.soDienThoai" type="text" class="form-control" placeholder="Số Điện Thoại *" required />
        </div>

        <!-- Ngày Sinh -->
        <div class="col-md-6">
          <input v-model="employee.namSinh" type="date" class="form-control" placeholder="Ngày Sinh *" required />
        </div>

        <!-- Giới Tính -->
        <div class="col-md-6">
          <div class="d-flex align-items-center">
            <label class="me-2">
              <input type="radio" v-model="employee.gioiTinh" value="1" class="me-1" /> Nam
            </label>
            <label>
              <input type="radio" v-model="employee.gioiTinh" value="2" class="me-1" /> Nữ
            </label>
          </div>
        </div>

        <!-- Tỉnh/Thành Phố -->
        <div class="col-md-4">
          <select v-model="employee.city" class="form-select" required>
            <option value="">Tỉnh/Thành phố *</option>
            <!-- Add options here -->
          </select>
        </div>

        <!-- Quận/Huyện -->
        <div class="col-md-4">
          <select v-model="employee.district" class="form-select" required>
            <option value="">Quận/Huyện *</option>
            <!-- Add options here -->
          </select>
        </div>

        <!-- Xã/Phường -->
        <div class="col-md-4">
          <select v-model="employee.ward" class="form-select" required>
            <option value="">Xã/Phường *</option>
            <!-- Add options here -->
          </select>
        </div>

        <!-- Địa Chỉ -->
        <div class="col-md-12">
          <input v-model="employee.diaChi" type="text" class="form-control" placeholder="Số nhà/Ngõ/Đường *" required />
        </div>
      </div>

      <!-- Buttons -->
      <div class="d-flex gap-3 mt-4 justify-content-center">
        <button type="submit" class="btn btn-primary">Lưu Thay Đổi</button>
        <button type="reset" class="btn btn-secondary" @click="cancelEdit">Hủy</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      employee: {
        id: "",
        hoTen: "",
        email: "",
        soDienThoai: "",
        namSinh: "",
        gioiTinh: 1, // Default to "Nam"
        city: "",
        district: "",
        ward: "",
        diaChi: "",
      },
    };
  },
  mounted() {
    this.fetchEmployeeData();
  },
  methods: {
    // Lấy dữ liệu nhân viên khi trang được tải
    async fetchEmployeeData() {
      const { id } = this.$route.params; // Lấy id từ URL
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/nhanvien/${id}`);
        this.employee = response.data; // Cập nhật thông tin nhân viên
      } catch (error) {
        console.error("Lỗi khi lấy dữ liệu nhân viên:", error);
      }
    },

    // Cập nhật thông tin nhân viên
    async editEmployee() {
      try {
        const response = await axios.put(
            `${import.meta.env.VITE_API_URL}/api/nhanvien/${this.employee.id}`,
            this.employee
        );
        console.log("Nhân viên đã được cập nhật:", response.data);

        // Chuyển hướng hoặc thông báo thành công
        this.$router.push("/admin/staff"); // Ví dụ: chuyển về trang danh sách nhân viên
      } catch (error) {
        console.error("Lỗi khi cập nhật nhân viên:", error);
      }
    },

    // Hủy chỉnh sửa
    cancelEdit() {
      this.$router.push("/admin/staff"); // Quay về trang danh sách nhân viên nếu người dùng hủy
    }
  },
};
</script>

<style scoped>
/* Optional styles to make the form look better */
.form-select {
  margin-bottom: 15px;
}
</style>
