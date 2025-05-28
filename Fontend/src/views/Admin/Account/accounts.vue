<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
const showViewModal = ref(false);
axios.defaults.baseURL = 'http://localhost:8080';

const accounts = ref([]);
const searchQuery = ref('');
const statusFilter = ref('all');
const currentPage = ref(1);
const pageSize = ref(5);
;
const selectedAccount = ref({});
const showEditModal = ref(false);
const successMessage = ref('');
const roleFilter = ref('all');
const employeeVaiTroId = ref(null);  // Để lưu id vai trò của nhân viên

// Tính toán số lượng item tổng và số trang
const totalItems = computed(() => filteredAccounts.value.length);
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));
const startIndex = computed(() => (currentPage.value - 1) * pageSize.value);
const endIndex = computed(() => Math.min(startIndex.value + pageSize.value, totalItems.value));

// Lọc các tài khoản theo trạng thái và vai trò
const filteredAccounts = computed(() => {
  return accounts.value.filter(acc => {
    const search = searchQuery.value.toLowerCase();
    const matchesSearch = acc.tenDangNhap?.toLowerCase().includes(search);

    // Lọc theo trạng thái
    const matchesStatus =
      statusFilter.value === 'all' ||
      (statusFilter.value === 'active' && acc.trangThai) ||
      (statusFilter.value === 'inactive' && !acc.trangThai);

    // Lọc theo vai trò
    const matchesRole =
      roleFilter.value === 'all' || acc.vaiTro?.tenVaiTro === roleFilter.value;

    return matchesSearch && matchesStatus && matchesRole;
  });
});

// Phân trang tài khoản
const paginatedAccounts = computed(() => {
  return filteredAccounts.value.slice(startIndex.value, endIndex.value);
});

// Chuyển trang
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

// Lấy danh sách tài khoản từ API
const fetchAccounts = async () => {
  try {
    const response = await axios.get('/tai-khoan');
    accounts.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('Lỗi tải danh sách tài khoản:', error.response?.data || error.message);
  }
};
// Hàm mở modal "Xem"
const openViewModal = async (account) => {
  // Sao chép dữ liệu tài khoản vào selectedAccount
  selectedAccount.value = { ...account };

  try {
    let response;

    if (account.vaiTro.id === 2 || account.vaiTro.id === 1) { // Nhân viên
      // Lấy danh sách tất cả nhân viên
      response = await axios.get(`/nhan-vien`);
      // Duyệt qua danh sách để tìm nhân viên có id trùng với account.id
      const employee = response.data.find(emp => emp.taiKhoan.id === account.id);
      if (employee) {
        selectedAccount.value = { ...selectedAccount.value, ...employee };
      }
    } else if (account.vaiTro.id === 3) { // Khách hàng
      // Lấy danh sách tất cả khách hàng
      response = await axios.get(`/khach-hang`);
      // Duyệt qua danh sách để tìm khách hàng có id trùng với account.id
      const customer = response.data.find(cust => cust.idTk === account.id);
      if (customer) {
        selectedAccount.value = { ...selectedAccount.value, ...customer };
      }
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin tài khoản:', error);
  }

  // Hiển thị modal
  showViewModal.value = true;
};


// Hàm đóng modal "Xem"
const closeViewModal = () => {
  showViewModal.value = false; // Ẩn modal
};

// Mở modal chỉnh sửa
const openEditModal = (account) => {
  selectedAccount.value = { ...account };
  employeeVaiTroId.value = account.vaiTro?.id || null;  // Gán vai trò từ tài khoản
  showEditModal.value = true;
};

// Cập nhật tài khoản
const updateAccount = async () => {
  if (!selectedAccount.value) return;

  const isConfirmed = window.confirm('Bạn có chắc chắn muốn cập nhật tài khoản này?');

  if (!isConfirmed) {
    return;
  }

  try {
    // Cập nhật vai trò
    selectedAccount.value.vaiTro = { id: employeeVaiTroId.value };

    // Cập nhật trạng thái từ selectedAccount
    const newStatus = selectedAccount.value.trangThai;

    // Kiểm tra vai trò và cập nhật trạng thái phù hợp
    if (selectedAccount.value.vaiTro.id === 1) {  // Ví dụ, vai trò "1" là nhân viên
      // Cập nhật trạng thái nhân viên
      try {
        const employeeResponse = await axios.get(`/nhan-vien/${selectedAccount.value.id}`);
        const employee = employeeResponse.data;

        if (employee && employee.id) {
          await axios.patch(`/nhan-vien/${employee.id}/status`, null, {
            params: { trangThai: newStatus },
          });
        }
      } catch (error) {
        console.warn("Không tìm thấy nhân viên với id:", selectedAccount.value.id);
      }
    } else if (selectedAccount.value.vaiTro.id === 3) {  // Ví dụ, vai trò "2" là khách hàng
      // Cập nhật trạng thái khách hàng
      try {
        const customerResponse = await axios.get(`/khach-hang/${selectedAccount.value.id}`);
        const customer = customerResponse.data;

        if (customer && customer.idTk) {
          await axios.patch(`/khach-hang/${customer.idTk}/status`, null, {
            params: { trangThai: newStatus },
          });
        }
      } catch (error) {
        console.warn("Không tìm thấy khách hàng với id:", selectedAccount.value.id);
      }
    }

    // Cập nhật thông tin tài khoản chính
    await axios.put(`/tai-khoan/${selectedAccount.value.id}`, selectedAccount.value);

    successMessage.value = 'Cập nhật tài khoản thành công!';
    fetchAccounts();
    showEditModal.value = false;
  } catch (error) {
    console.error('Lỗi cập nhật tài khoản:', error.response?.data || error.message);
    alert('Đã xảy ra lỗi khi cập nhật tài khoản. Vui lòng thử lại.');
  }
};








// Gọi hàm khi component được mount
onMounted(fetchAccounts);
</script>

<template>
  <div class="container my-5">
    <!-- Header -->
    <div class="d-flex justify-content-between mb-4">
      <h2>Quản lý Tài Khoản</h2>
    </div>

    <!-- Thông báo thành công -->
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <!-- Tìm kiếm và bộ lọc trạng thái -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div class="search-container flex-grow-1 me-3">
        <input v-model="searchQuery" type="text" class="form-control" placeholder="Tìm kiếm theo tên đăng nhập" />
      </div>
      <div class="status-filters d-flex align-items-center gap-3">
        <label for="status" class="form-label me-2">Trạng thái</label>
        <select v-model="statusFilter" class="form-select" style="width: 150px">
          <option value="all">Tất Cả</option>
          <option value="active">Hoạt động</option>
          <option value="inactive">Ngừng hoạt động</option>
        </select>

        <label for="role" class="form-label me-2">Vai trò</label>
        <select v-model="roleFilter" class="form-select" style="width: 150px">
          <option value="all">Tất Cả</option>
          <option value="ADMIN">Admin</option>
          <option value="Nhân viên">Nhân viên</option>
          <option value="Khách hàng">Khách hàng</option>
        </select>
      </div>
    </div>

    <!-- Danh sách tài khoản -->
    <div v-if="accounts.length > 0" class="table-responsive">
      <table class="table table-striped table-bordered">
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên Đăng Nhập</th>
            <th>Vai Trò</th>
            <th>Trạng Thái</th>
            <th>Thao Tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(account, index) in paginatedAccounts" :key="account.id">
            <td>{{ index + 1 + (currentPage - 1) * pageSize }}</td>
            <td>{{ account.tenDangNhap }}</td>
            <td>{{ account.vaiTro?.tenVaiTro || 'Chưa có' }}</td>
            <td>
              <span class="status-label" :class="account.trangThai ? 'status-active' : 'status-inactive'">
                {{ account.trangThai ? 'Hoạt động' : 'Ngừng hoạt động' }}
              </span>
            </td>
            <td>
             <button type="button" class="btn btn-info" @click="openViewModal(account)">
                  Xem
                </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <div class="d-flex justify-content-between align-items-center mt-4">
      <div class="d-flex align-items-center gap-2">
        <select v-model="pageSize" class="form-select form-select-sm" style="width: 120px">
          <option :value="5">5 / trang</option>
          <option :value="10">10 / trang</option>
          <option :value="15">15 / trang</option>
          <option :value="20">20 / trang</option>
        </select>
        <span class="text-muted">
          Hiển thị {{ startIndex + 1 }}-{{ endIndex }} trên tổng số {{ totalItems }} tài khoản
        </span>
      </div>
      <nav v-if="totalPages > 1">
        <ul class="pagination mb-0">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="changePage(currentPage - 1)"><i class="bi bi-chevron-left"></i></button>
          </li>
          <li v-for="page in totalPages" :key="page" class="page-item" :class="{ active: currentPage === page }">
            <button class="page-link" @click="changePage(page)">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <button class="page-link" @click="changePage(currentPage + 1)"><i class="bi bi-chevron-right"></i></button>
          </li>
        </ul>
      </nav>
    </div>


    <!-- Modal Xem tài khoản -->
    <div v-if="showViewModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.5)">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Xem thông tin tài khoản</h5>
            <button type="button" class="btn-close" @click="showViewModal = false"></button>
          </div>
          <!-- Modal Body -->
          <div class="modal-body">
            <!-- Hiển thị tên đăng nhập và trạng thái -->
            <div class="mb-3">
              <label class="form-label">Tên đăng nhập</label>
              <input type="text" class="form-control" v-model="selectedAccount.tenDangNhap" disabled />
            </div>
            <div class="mb-3">
              <label class="form-label">Trạng thái</label>
              <select class="form-select" v-model="selectedAccount.trangThai" disabled>
                <option :value="true">Hoạt động</option>
                <option :value="false">Ngừng hoạt động</option>
              </select>
            </div>

            <!-- Hiển thị thông tin tùy theo vai trò -->
                       <div v-if="selectedAccount.vaiTro.id === 1 || selectedAccount.vaiTro.id === 2">

              <!-- Thông tin nhân viên -->
              <div class="mb-3">
                <label class="form-label">Tên nhân viên</label>
                <input type="text" class="form-control" v-model="selectedAccount.hoTen" disabled />
              </div>
              <div class="mb-3">
                <label class="form-label">Số điện thoại</label>
                <input type="text" class="form-control" v-model="selectedAccount.soDienThoai" disabled />
              </div>
              <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" class="form-control" v-model="selectedAccount.email" disabled />
              </div>
              <div class="mb-3">
                <label class="form-label">Địa chỉ</label>
                <input type="text" class="form-control" v-model="selectedAccount.diaChi" disabled />
              </div>
            </div>

            <div v-if="selectedAccount.vaiTro.id === 3">
              <!-- Thông tin khách hàng -->
              <div class="mb-3">
                <label class="form-label">Mã khách hàng</label>
                <input type="text" class="form-control" v-model="selectedAccount.maKhachHang" disabled />
              </div>
              <div class="mb-3">
                <label class="form-label">Số điện thoại</label>
                <input type="text" class="form-control" v-model="selectedAccount.soDienThoai" disabled />
              </div>
              <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" class="form-control" v-model="selectedAccount.email" disabled />
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showViewModal = false">Đóng</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 1200px;
  background: #fff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
.status-label {
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 5px;
}
.status-active {
  color: #28a745;
  background: #d4edda;
}
.status-inactive {
  color: #dc3545;
  background: #f8d7da;
}
</style>

