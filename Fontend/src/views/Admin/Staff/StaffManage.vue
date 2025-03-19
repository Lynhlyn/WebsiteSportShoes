<template>
  <div class="container my-5">
    <!-- Header and Add Employee Button -->
    <div class="d-flex justify-content-between mb-4">
      <h2>Quản lý Nhân Viên</h2>
      <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addEmployeeModal">
        <i class="bi bi-plus-circle"></i> Thêm Nhân Viên
      </button>
    </div>

    <!-- Search and Status Filter Row -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div class="search-container flex-grow-1 me-3">
        <input v-model="searchQuery" type="text" class="form-control" placeholder="Tìm kiếm theo mã hoặc tên đăng nhập"/>
      </div>

      <div class="status-filters d-flex align-items-center">
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="all" value="all" v-model="statusFilter"/>
          <label class="form-check-label" for="all">Tất Cả</label>
        </div>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="active" value="active" v-model="statusFilter"/>
          <label class="form-check-label" for="active">Hoạt động</label>
        </div>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="inactive" value="inactive" v-model="statusFilter"/>
          <label class="form-check-label" for="inactive">Ngừng hoạt động</label>
        </div>
      </div>
    </div>

    <!-- Employee List Table -->
    <div v-if="employees.length > 0" class="table-responsive">
      <table class="table table-striped table-bordered">
        <thead>
        <tr>
          <th>#</th>
          <th>Mã NV</th>
          <th>Tên Đăng Nhập</th>
          <th>Họ Tên</th>
          <th>Giới Tính</th>
          <th>Năm Sinh</th>
          <th>Ngày Tạo</th>
          <th>Ngày Sửa</th>
          <th>SDT</th>
          <th>Email</th>
          <th>Vai Trò</th>
          <th>Trạng Thái</th>
          <th>Thao Tác</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(employee, index) in paginatedEmployees" :key="employee.id">
          <td>{{ index + 1 + (currentPage - 1) * pageSize }}</td>
          <td>{{ employee.maNhanVien }}</td>
          <td>{{ employee.tenDangNhap }}</td>
          <td>{{ employee.hoTen }}</td>
          <td>{{ employee.gioiTinh ? 'Nam' : 'Nữ' }}</td>
          <td>{{ employee.namSinh }}</td>
          <td>{{ employee.ngayTao }}</td>
          <td>{{ employee.ngaySua }}</td>
          <td>{{ employee.soDienThoai }}</td>
          <td>{{ employee.email }}</td>
          <td>{{ employee.vaiTro ? 'Admin' : 'User' }}</td>
          <td>
              <span class="status-label" :class="employee.trangThai ? 'status-active' : 'status-inactive'">
                {{ employee.trangThai ? 'Hoạt động' : 'Ngừng hoạt động' }}
              </span>
          </td>
          <td>
            <div class="d-flex justify-content-center">
              <button class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#editEmployeeModal" @click="openEditModal(employee)">
                <i class="bi bi-pencil"></i>
              </button>
              <button class="btn btn-primary btn-sm ms-2" @click="deleteEmployee(employee.id)">
                <i class="bi bi-arrow-repeat"></i>
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination-container d-flex justify-content-end align-items-center mt-4">
      <button @click="prevPage" :disabled="currentPage === 1" class="pagination-btn">
        <i class="bi bi-chevron-left"></i>
      </button>
      <span class="pagination-page mx-2">{{ currentPage }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages" class="pagination-btn">
        <i class="bi bi-chevron-right"></i>
      </button>
    </div>
  </div>

  <!-- Add Employee Modal -->
  <div id="addEmployeeModal" class="modal modal-xl fade">
    <div class="modal-dialog">
      <div class="modal-content">
        <form @submit.prevent="addEmployee">
          <div class="container mt-5 p-4 bg-white shadow-lg rounded-lg">
            <h2 class="text-center mb-4">Thêm Nhân Viên Mới</h2>
            <div class="row">
              <!-- Avatar Upload -->
<!--              <div class="col-md-4 d-flex flex-column align-items-center border rounded p-4">-->
<!--                <label class="cursor-pointer text-center">-->
<!--                  <img v-if="avatarPreview" :src="avatarPreview" class="rounded-circle mb-2" width="100" height="100"/>-->
<!--                  <div v-else class="rounded-circle bg-light d-flex align-items-center justify-content-center mb-2" style="width:100px; height:100px;">-->
<!--                    + Upload-->
<!--                  </div>-->
<!--                  <input type="file" class="d-none" @change="handleFileChange"/>-->
<!--                </label>-->
<!--              </div>-->

              <!-- Employee Form -->
              <div class="col-md-8">
                <div class="row g-3">
                  <div class="col-md-6">
                    <input v-model="newEmployee.tenDangNhap" type="text" placeholder="Tên Nhân Viên *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <input v-model="newEmployee.hoTen" type="text" placeholder="ho ten *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <input v-model="newEmployee.matKhau" type="password" placeholder="Mật Khẩu *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <input v-model="newEmployee.namSinh" type="number" placeholder="Năm Sinh *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <input v-model="newEmployee.email" type="email" placeholder="Email *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <input v-model="newEmployee.soDienThoai" type="text" placeholder="Số Điện Thoại *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <div class="d-flex align-items-center">
                      <label class="me-2">
                        <input type="radio" v-model="newEmployee.gioiTinh" value="1" class="me-1"/> Nam
                      </label>
                      <label>
                        <input type="radio" v-model="newEmployee.gioiTinh" value="2" class="me-1"/> Nữ
                      </label>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <input v-model="newEmployee.diaChi" type="text" placeholder="Địa Chỉ *" class="form-control"/>
                  </div>
              </div>
                <div class="d-flex gap-3 mt-4">
                  <button type="submit" class="btn btn-primary">THÊM</button>
                  <button type="reset" class="btn btn-secondary" data-bs-dismiss="modal">HỦY</button>
                </div>
              </div>
            </div>
          </div>

        </form>
      </div>
    </div>
  </div>

  <!-- Edit Employee Modal -->
  <div id="editEmployeeModal" class="modal modal-xl fade">
    <div class="modal-dialog">
      <div class="modal-content">
        <form @submit.prevent="editEmployee">
          <div class="container mt-5 p-4 bg-white shadow-lg rounded-lg">
            <h2 class="text-center mb-4">Chỉnh Sửa Nhân Viên</h2>
            <div class="row">
              <div class="col-md-8">
                <div class="row g-3">
                  <div class="col-md-6">
                    <input v-model="employee.hoTen" type="text" placeholder="Tên Nhân Viên *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <input v-model="employee.tenDangNhap" type="text" placeholder="Ho Ten *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">

                    <input v-model="employee.email" type="email" placeholder="Email *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <input v-model="employee.soDienThoai" type="text" placeholder="Số Điện Thoại *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <input v-model="employee.namSinh" type="number" placeholder="Năm Sinh *" class="form-control" required/>
                  </div>
                  <div class="col-md-6">
                    <div class="d-flex align-items-center">
                      <label class="me-2">
                        <input type="radio" v-model="employee.gioiTinh" value="1" class="me-1"/> Nam
                      </label>
                      <label>
                        <input type="radio" v-model="employee.gioiTinh" value="2" class="me-1"/> Nữ
                      </label>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <input v-model="employee.diaChi" type="text" placeholder="Địa Chỉ *" class="form-control"/>
                  </div>
                </div>
                <div class="d-flex gap-3 mt-4">
                  <button type="submit" class="btn btn-primary">LƯU THAY ĐỔI</button>
                  <button type="reset" class="btn btn-secondary" data-bs-dismiss="modal">HỦY</button>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import {Modal} from 'bootstrap';
import $ from 'jquery';

export default {
  data() {
    return {
      employees: [],
      newEmployee: {
        tenDangNhap: "abc_123",
        matKhau: "123123",
        hoTen: "tuan",
        gioiTinh: "1",
        namSinh: "2004",
        ngayTao: "2021-10-10",
        ngaySua: "2021-10-10",
        soDienThoai: "123456789",
        email: "abc@gmail.com",
        diaChi: "ha noi",
      },
      employee: {
        id: "",
        maNhanVien: "",
        tenDangNhap: "",
        matKhau: "",
        hoTen: "",
        gioiTinh: 0,
        namSinh: "",
        ngayTao: "",
        ngaySua: "",
        soDienThoai: "",
        email: "",
        diaChi: "",
      },
      avatarPreview: null,
      searchQuery: '',
      statusFilter: 'all',
      currentPage: 1,
      pageSize: 5,
      addModal: null,
      editModal: null,
    };
  },
  computed: {
    filteredEmployees() {
      return this.employees.filter((employee) => {
        const matchesQuery =
            employee.maNhanVien.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
            employee.tenDangNhap.toLowerCase().includes(this.searchQuery.toLowerCase());

        const matchesStatus =
            this.statusFilter === 'all' ||
            (this.statusFilter === 'active' && employee.trangThai === 1) ||
            (this.statusFilter === 'inactive' && employee.trangThai === 0);

        return matchesQuery && matchesStatus;
      });
    },
    paginatedEmployees() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      return this.filteredEmployees.slice(startIndex, startIndex + this.pageSize);
    },
    totalPages() {
      return Math.ceil(this.filteredEmployees.length / this.pageSize);
    }
  },
  mounted() {
    this.addModal = new Modal('#addEmployeeModal');
    this.editModal = new Modal('#editEmployeeModal');
    this.fetchEmployees();
  },
  methods: {
    formatDate(value) {
      const formatter = new Intl.DateTimeFormat("en-GB", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit"
      });
      return formatter.format(new Date(value));
    },
    async fetchEmployees() {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/nhanvien`);
        this.employees = response.data;
      } catch (error) {
        console.error('Lỗi khi lấy dữ liệu nhân viên:', error);
      }
    },
    prevPage() {
      if (this.currentPage > 1) this.currentPage--;
    },
    nextPage() {
      if (this.currentPage < this.totalPages) this.currentPage++;
    },
    navigateToAddEmployee() {
      this.$router.push(`${import.meta.env.VITE_API_URL}/nhanvien/add`);
    },
    async toggleEmployeeStatus(employeeId) {
      try {
        const employee = this.employees.find(e => e.id === employeeId);
        if (!employee) return;

        const newStatus = employee.trangThai === 1 ? 0 : 1;
        await axios.put(`${import.meta.env.VITE_API_URL}/nhanvien/${employeeId}/status`, {trang_thai: newStatus});
        this.employees = this.employees.map(e => e.id === employeeId ? {...e, trang_thai: newStatus} : e);
      } catch (error) {
        console.error('Lỗi khi cập nhật trạng thái:', error);
      }
    },
    async addEmployee() {
      try {
        console.log('New Employee:', this.newEmployee);
        const response = await axios.post(`${import.meta.env.VITE_API_URL}/nhanvien`, this.newEmployee);
        this.employees.push(response.data);
        this.addModal.hide();
        $('.modal-backdrop').remove();
      } catch (error) {
        console.error('Lỗi khi thêm nhân viên:', error);
      }
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.avatarPreview = URL.createObjectURL(file);
      }
    },
    handleSubmit() {
      console.log("Submitted Data:", this.formData);
    },
    async openEditModal(employee) {
      this.employee = { ...employee };  // Clone the employee data to edit
      this.editModal.show();
    },
    async editEmployee() {
      try {
        console.log('Updated Employee:', this.employee);
        const response = await axios.put(`${import.meta.env.VITE_API_URL}/nhanvien/${this.employee.id}`, this.employee);
        const index = this.employees.findIndex(emp => emp.id === this.employee.id);
        if (index !== -1) {
          this.employees[index] = response.data;
        }
        this.editModal.hide();
        $('.modal-backdrop').remove();
      } catch (error) {
        console.error('Lỗi khi cập nhật nhân viên:', error);
      }
    },
    async deleteEmployee(employeeId) {
      try {
        await axios.delete(`${import.meta.env.VITE_API_URL}/nhanvien/${employeeId}`);
        this.employees = this.employees.filter(e => e.id !== employeeId);
      } catch (error) {
        console.error('Lỗi khi xóa nhân viên:', error);
      }
    },
  }

};
</script>
