<script setup>
import { ref, computed, onMounted, watch } from 'vue'; // Đã thêm 'watch' vào đây
import axios from 'axios';
import { useRouter } from 'vue-router';

axios.defaults.baseURL = 'http://localhost:8080';
const employeeVaiTroId = ref(null);

const employees = ref([]);
const searchQuery = ref('');
const statusFilter = ref('all');
const currentPage = ref(1);
const pageSize = ref(5);

// Các thuộc tính tính toán cho phân trang
const totalItems = computed(() => filteredEmployees.value.length);
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));
const startIndex = computed(() => (currentPage.value - 1) * pageSize.value);
const endIndex = computed(() => Math.min(startIndex.value + pageSize.value, totalItems.value));
const paginatedEmployees = computed(() => {
    return filteredEmployees.value.slice(startIndex.value, endIndex.value);
});

// Cập nhật phân trang khi thay đổi kích thước trang
watch(pageSize, () => {
    currentPage.value = 1; // Đặt lại trang hiện tại khi thay đổi pageSize
});

// Các phương thức thay đổi trang
const changePage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
    }
};
const newEmployee = ref({
    tenDangNhap: '',  // Tên đăng nhập
    matKhau: '',      // Mật khẩu
    vaiTro: { id: null },  // Đảm bảo vai trò là một đối tượng với trường id
    hoTen: '',
    namSinh: '',
    email: '',
    soDienThoai: '',
    gioiTinh: null,
    diaChi: '',
    trangThai: true
});




const employee = ref({});
const errors = ref({});
const successMessage = ref('');
const router = useRouter();

// Lọc danh sách nhân viên theo tìm kiếm và trạng thái
const filteredEmployees = computed(() => {
    if (!Array.isArray(employees.value)) {
        return [];
    }
    return employees.value.filter(emp => {
        const search = searchQuery.value.toLowerCase();
        const matchesSearch =
            emp.maNhanVien?.toString().toLowerCase().includes(search) ||
            emp.tenDangNhap?.toLowerCase().includes(search);
        const matchesStatus =
            statusFilter.value === 'all' ||
            (statusFilter.value === 'active' && emp.trangThai) ||
            (statusFilter.value === 'inactive' && !emp.trangThai);

        return matchesSearch && matchesStatus;
    });
});

const nextPage = () => {
    if (currentPage.value < totalPages.value) currentPage.value++;
};

// Lấy danh sách nhân viên từ API
const fetchEmployees = async () => {
    try {
        const response = await axios.get('/nhan-vien');
        employees.value = Array.isArray(response.data) ? response.data : [];
    } catch (error) {
        console.error('Lỗi tải danh sách nhân viên:', error.response?.data || error.message);
        employees.value = [];
    }
};

const checkDuplicate = async (email, phone) => {
    try {
        const response = await axios.get('/api/nhan-vien/check-duplicate', {
            params: { email, phone }
        });
        return response.data;
    } catch (error) {
        console.error('Lỗi khi kiểm tra trùng email và số điện thoại:', error);
        return { emailExists: false, phoneExists: false };
    }
};

const validateForm =async  () => {
    errors.value = {}; // Reset errors trước khi kiểm tra

    // Kiểm tra các trường bắt buộc
   if (!newEmployee.value.tenDangNhap) {
          errors.value.tenDangNhap = 'Tên đăng nhập không được để trống';
      } else {
          // Kiểm tra trùng tên đăng nhập trong cơ sở dữ liệu
          const isUsernameDuplicate = await checkUsernameDuplicate(newEmployee.value.tenDangNhap);
          if (isUsernameDuplicate) {
              errors.value.tenDangNhap = 'Tên đăng nhập đã tồn tại';
          }
      }
    if (!newEmployee.value.matKhau || newEmployee.value.matKhau.length < 6) {
        errors.value.matKhau = 'Mật khẩu phải có ít nhất 6 ký tự';
    }
    if (!newEmployee.value.hoTen) {
        errors.value.hoTen = 'Họ và tên không được để trống';
    }
    if (!newEmployee.value.namSinh || newEmployee.value.namSinh < 1900 || newEmployee.value.namSinh > new Date().getFullYear()) {
        errors.value.namSinh = 'Năm sinh không hợp lệ';
    }

    // Kiểm tra email với regex và chắc chắn phần đuôi là '@gmail.com'
    if (!newEmployee.value.email) {
        errors.value.email = 'Email không được để trống';
    } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(newEmployee.value.email)) {
        errors.value.email = 'Email phải có định dạng đúng';
    } else if (checkEmailDuplicate(newEmployee.value.email)) {
        errors.value.email = 'Email này đã tồn tại';
    }

    // Kiểm tra số điện thoại
    if (!newEmployee.value.soDienThoai || !/^\d{10}$/.test(newEmployee.value.soDienThoai)) {
        errors.value.soDienThoai = 'Số điện thoại phải có 10 chữ số';
    } else if (checkPhoneDuplicate(newEmployee.value.soDienThoai)) {
        errors.value.soDienThoai = 'Số điện thoại này đã tồn tại';
    }

    if (!newEmployee.value.diaChi) {
        errors.value.diaChi = 'Địa chỉ không được để trống';
    }
    if (newEmployee.value.trangThai === undefined || newEmployee.value.trangThai === null) {
        errors.value.trangThai = 'Vui lòng chọn trạng thái!';
    }

    // Kiểm tra giới tính
    if (newEmployee.value.gioiTinh === undefined || newEmployee.value.gioiTinh === null) {
        errors.value.gioiTinh = 'Vui lòng chọn giới tính!';
    }

    // Kiểm tra vai trò
     if (newEmployee.value.vaiTro.id === null) {
            errors.value.vaiTro = 'Vui lòng chọn vai trò';
        }

    // In các lỗi ra để debug
    console.log(errors.value); // Debugging: In ra lỗi nếu có

    return Object.keys(errors.value).length === 0;
};


// Kiểm tra trùng email trong danh sách nhân viên
const checkEmailDuplicate = (email) => {
    return employees.value.some(emp => emp.email === email);
};

// Kiểm tra trùng số điện thoại trong danh sách nhân viên
const checkPhoneDuplicate = (phone) => {
    return employees.value.some(emp => emp.soDienThoai === phone);
};
const checkUsernameDuplicate = async (username) => {
    try {
        // Gửi yêu cầu kiểm tra tên đăng nhập từ backend
        const response = await axios.get(`http://localhost:8080/tai-khoan/check-username?username=${username}`);

        // Kiểm tra xem tên đăng nhập có trùng hay không
        return response.data.exists; // Trả về true nếu tồn tại, false nếu không tồn tại
    } catch (error) {
        console.error("Lỗi khi kiểm tra tên đăng nhập:", error);
        return false; // Nếu có lỗi, coi như không có trùng tên
    }
};


// Thêm nhân viên mới
const addEmployee = async () => {
    console.log('Dữ liệu gửi đi: ', newEmployee.value);
  const isValid = await validateForm();  // Chờ validate trước
    if (!isValid) {
        console.log('Form validation failed');
        return;  // Nếu không hợp lệ, không tiến hành tiếp
    }


    // Xác nhận thêm nhân viên
    if (!confirm('Bạn có chắc chắn muốn thêm nhân viên này không?')) {
        console.log('User canceled');
        return;
    }

    try {
        // Tạo tài khoản trước
        const accountResponse = await axios.post('/tai-khoan/add', {
            tenDangNhap: newEmployee.value.tenDangNhap,
            matKhau: newEmployee.value.matKhau,
            vaiTro: { id: newEmployee.value.vaiTro }, // Vai trò cần là đối tượng với id
            trangThai: true,
        });

        // Kiểm tra nếu accountResponse trả về id hợp lệ
        if (!accountResponse.data || !accountResponse.data.id) {
            console.error('Không thể tạo tài khoản. Không có ID trả về');
            return;
        }

        const accountId = accountResponse.data.id;  // Lấy ID tài khoản tạo mới
        console.log('Account created with ID:', accountId);

        // Tạo nhân viên với ID tài khoản mới
        const response = await axios.post('/nhan-vien', {
            taiKhoan: { id: accountId} ,
            hoTen: newEmployee.value.hoTen,
            gioiTinh: newEmployee.value.gioiTinh,
            namSinh: newEmployee.value.namSinh,
            soDienThoai: newEmployee.value.soDienThoai,
            email: newEmployee.value.email,
            diaChi: newEmployee.value.diaChi,
            trangThai: newEmployee.value.trangThai,
        });

        // Thông báo thành công
        successMessage.value = 'Bạn đã thêm nhân viên thành công!';
        setTimeout(() => {
            window.location.reload(); // Reload trang hiện tại
        }, 500);

        // Reset form sau khi thành công
        newEmployee.value = {
            tenDangNhap: '',
            hoTen: '',
            matKhau: '',
            namSinh: '',
            email: '',
            soDienThoai: '',
            gioiTinh: null,
            diaChi: '',
            vaiTro: null,
            trangThai: true
        };

    } catch (error) {
        console.error('Lỗi thêm nhân viên:', error.response?.data || error.message);
        successMessage.value = ''; // Đảm bảo không có thông báo thành công khi có lỗi
    }
};







// Cập nhật trạng thái nhân viên
const updateEmployeeStatus = async (id, currentStatus) => {
    if (!confirm('Bạn có chắc chắn muốn thay đổi trạng thái nhân viên này không?')) {
        return;
    }
    try {
        const newStatus = !currentStatus;
        await axios.patch(`/nhan-vien/${id}/status`, null, {
            params: { trangThai: newStatus },
        });
   const employeeResponse = await axios.get(`/nhan-vien/${id}`);
        const employee = employeeResponse.data;

        if (employee && employee.taiKhoan && employee.taiKhoan.id) {
            // Cập nhật trạng thái tài khoản của nhân viên
            await axios.patch(`/tai-khoan/${employee.taiKhoan.id}/status`, null, {
                params: { trangThai: newStatus },
            });
        }
        fetchEmployees();
    } catch (error) {
        console.error('Lỗi cập nhật trạng thái:', error.response?.data || error.message);
    }
};

// Mở modal chỉnh sửa
const openEditModal = (emp) => {
    employee.value = { ...emp };
    employeeVaiTroId.value = emp.taiKhoan?.vaiTro?.id || null;
};

;
const validateEditForm = () => {
    errors.value = {};

    if (!employee.value.taiKhoan?.tenDangNhap) {
        errors.value.tenDangNhap = 'Tên đăng nhập không được để trống';
    }
    if (!employee.value.hoTen) {
        errors.value.hoTen = 'Họ và tên không được để trống';
    }
    if (!employee.value.email || !/^[\w-\.]+@gmail\.com$/.test(employee.value.email)) {
        errors.value.email = 'Email không hợp lệ!';
    } else if (isEmailDuplicated(employee.value.email, employee.value.id)) {
        errors.value.email = 'Email này đã tồn tại!';
    }
    if (!employee.value.soDienThoai || !/^\d{10}$/.test(employee.value.soDienThoai)) {
        errors.value.soDienThoai = 'Số điện thoại phải có 10 chữ số';
    } else if (isPhoneDuplicated(employee.value.soDienThoai, employee.value.id)) {
        errors.value.soDienThoai = 'Số điện thoại đã tồn tại!';
    }
    if (!employee.value.diaChi) {
        errors.value.diaChi = 'Địa chỉ không được để trống!';
    }
    if (employee.value.trangThai === undefined || employee.value.trangThai === null) {
        errors.value.trangThai = 'Vui lòng chọn trạng thái!';
    }

    return Object.keys(errors.value).length === 0;
};


// Kiểm tra trùng email trong danh sách nhân viên, ngoại trừ nhân viên hiện tại
const isEmailDuplicated = (email, currentId) => {
    console.log('Kiểm tra trùng email:', email, currentId);

    // Kiểm tra nếu danh sách nhân viên rỗng hoặc không hợp lệ
    if (!employees.value || employees.value.length === 0) {
        console.log('Danh sách nhân viên rỗng');
        return false; // Không có nhân viên để kiểm tra
    }

    const isDuplicated = employees.value.some(emp => emp.email === email && emp.id !== currentId);
    console.log('Email có trùng không:', isDuplicated);
    return isDuplicated;
};

// Kiểm tra trùng số điện thoại trong danh sách nhân viên, ngoại trừ nhân viên hiện tại
const isPhoneDuplicated = (phone, currentId) => {
    console.log('Kiểm tra trùng số điện thoại:', phone, currentId);

    // Kiểm tra nếu danh sách nhân viên rỗng hoặc không hợp lệ
    if (!employees.value || employees.value.length === 0) {
        console.log('Danh sách nhân viên rỗng');
        return false; // Không có nhân viên để kiểm tra
    }

    const isDuplicated = employees.value.some(emp => emp.soDienThoai === phone && emp.id !== currentId);
    console.log('Số điện thoại có trùng không:', isDuplicated);
    return isDuplicated;
};


// Chỉnh sửa nhân viên
const editEmployee = async () => {
    if (!validateEditForm()) {return};
    if (!confirm('Bạn có chắc chắn muốn cập nhật thông tin nhân viên này không?')){ return;}

    try {
        const updatedEmployee = {
            ...employee.value,
            taiKhoan: {
                ...employee.value.taiKhoan,
                vaiTro: { id: employeeVaiTroId.value }
            }
        };

        const response = await axios.put(`/nhan-vien/${employee.value.id}`, updatedEmployee);

        if (response.status === 200) {
            alert('Cập nhật thông tin nhân viên thành công!');
            setTimeout(() => window.location.reload(), 500);
        }
    } catch (error) {
        console.error('Lỗi cập nhật nhân viên:', error.response?.data || error.message);
        alert('Cập nhật thất bại, vui lòng thử lại!');
    }
};


// Quay lại trang danh sách khi hủy
const cancelEdit = () => {
    router.push("/admin/staff"); // Quay lại trang danh sách nhân viên nếu người dùng hủy
};

// Tải dữ liệu khi component được mounted
onMounted(fetchEmployees);
</script>


<template>
      <div class="container my-5">
            <!-- Thông báo lỗi -->

            <!-- Header and Add Employee Button -->
            <div class="d-flex justify-content-between mb-4">
                  <h2>Quản lý Nhân Viên</h2>
                  <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addEmployeeModal">
                        <i class="bi bi-plus-circle"></i> Thêm Nhân Viên
                  </button>
            </div>

            <!-- Thông báo thành công -->
            <div v-if="successMessage" class="alert alert-success">
                  {{ successMessage }}
            </div>

            <!-- Search and Status Filter Row -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                  <div class="search-container flex-grow-1 me-3">
                        <input v-model="searchQuery" type="text" class="form-control"
                              placeholder="Tìm kiếm theo mã hoặc tên đăng nhập" />
                  </div>

                  <div class="status-filters d-flex align-items-center">
                        <div class="form-check form-check-inline">
                              <input class="form-check-input" type="radio" id="all" value="all"
                                    v-model="statusFilter" />
                              <label class="form-check-label" for="all">Tất Cả</label>
                        </div>
                        <div class="form-check form-check-inline">
                              <input class="form-check-input" type="radio" id="active" value="active"
                                    v-model="statusFilter" />
                              <label class="form-check-label" for="active">Hoạt động</label>
                        </div>
                        <div class="form-check form-check-inline">
                              <input class="form-check-input" type="radio" id="inactive" value="inactive"
                                    v-model="statusFilter" />
                              <label class="form-check-label" for="inactive">Ngừng hoạt động</label>
                        </div>
                  </div>
            </div>

            <!-- Employee List Table -->
            <div v-if="employees.length > 0" class="table-responsive">
                  <table class="table table-striped table-bordered">
                        <thead>
                              <tr>
                                    <th>STT</th>
                                    <th>Mã NV</th>
                                    <th>Họ Tên</th>
                                    <th>Tên đăng Nhập</th>

                                    <th>Giới Tính</th>
                                    <th>Năm Sinh</th>
                                    <th>SDT</th>
                                    <th>Email</th>
                                    <th>Địa chỉ</th>
                                    <th>Vai Trò</th>
                                    <th>Trạng Thái</th>
                                    <th>Thao Tác</th>
                              </tr>
                        </thead>
                        <tbody>
                              <tr v-for="(employee, index) in paginatedEmployees" :key="employee.id">
                                    <td>{{ index + 1 + (currentPage - 1) * pageSize }}</td>
                                    <td>{{ employee.maNhanVien }}</td>
                                    <td>{{ employee.hoTen }}</td>
                                    <td>{{ employee.taiKhoan.tenDangNhap }}</td>

                                    <td>{{ employee.gioiTinh ? 'Nữ' : 'Nam' }}</td>
                                    <td>{{ employee.namSinh }}</td>
                                    <td>{{ employee.soDienThoai }}</td>
                                    <td>{{ employee.email }}</td>
                                    <td>{{ employee.diaChi }}</td>
                                    <td>{{ employee.taiKhoan.vaiTro.tenVaiTro}}</td>
                                    <td>
                                          <span class="status-label"
                                                :class="employee.trangThai ? 'status-active' : 'status-inactive'">
                                                {{ employee.trangThai ? 'Hoạt động' : 'Ngừng hoạt động' }}
                                          </span>
                                    </td>
                                    <td>
                                          <div class="d-flex justify-content-center">
                                                <button class="btn btn-sm"
                                                      :class="employee.trangThai ? 'btn-success' : 'btn-danger'"
                                                      @click="updateEmployeeStatus(employee.id, employee.trangThai)">
                                                      <i
                                                            :class="employee.trangThai ? 'bi bi-check-circle' : 'bi bi-x-circle'"></i>
                                                </button>
                                                <button class="btn btn-warning btn-sm" data-bs-toggle="modal"
                                                      data-bs-target="#editEmployeeModal"
                                                      @click="openEditModal(employee)">
                                                      <i class="bi bi-pencil"></i>
                                                </button>
                                          </div>
                                    </td>
                              </tr>
                        </tbody>
                  </table>

            </div>

            <!-- Pagination -->
            <div class="d-flex justify-content-between align-items-center mt-4">
                <div class="d-flex align-items-center gap-2">
                    <!-- Dropdown để chọn số lượng nhân viên mỗi trang -->
                    <select v-model="pageSize" class="form-select form-select-sm" style="width: 120px">
                        <option :value="5">5 / trang</option>
                        <option :value="10">10 / trang</option>
                        <option :value="15">15 / trang</option>
                        <option :value="20">20 / trang</option>
                    </select>
                    <span class="text-muted">
                        Hiển thị {{ startIndex + 1 }}-{{ endIndex }} trên tổng số {{ totalItems }} nhân viên
                    </span>
                </div>
                <nav v-if="totalPages > 1">
                        <ul class="pagination mb-0">
                            <li class="page-item" :class="{ disabled: currentPage === 1 }">
                                <button class="page-link" @click="changePage(currentPage - 1)">
                                    <i class="bi bi-chevron-left"></i>
                                </button>
                            </li>
                            <li v-for="page in totalPages" :key="page" class="page-item" :class="{ active: currentPage === page }">
                                <button class="page-link" @click="changePage(page)">{{ page }}</button>
                            </li>
                            <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                                <button class="page-link" @click="changePage(currentPage + 1)">
                                    <i class="bi bi-chevron-right"></i>
                                </button>
                            </li>
                        </ul>
                    </nav>
            </div>

      </div>

     <!-- Thêm Nhân Viên Modal -->
     <div id="addEmployeeModal" class="modal modal-xl fade">
         <div class="modal-dialog">
             <div class="modal-content">
                 <form @submit.prevent="addEmployee">
                     <div class="container mt-5 p-4 bg-white shadow-lg rounded-lg">
                         <h2 class="text-center mb-4">Thêm Nhân Viên Mới</h2>
                         <div class="row">
                             <div class="col-md-8">
                                 <div class="row g-3">

                                     <!-- Tên đăng nhập -->
                                     <div class="col-md-6">
                                         <label class="form-label">Tên đăng nhập</label>
                                         <input v-model="newEmployee.tenDangNhap" type="text"
                                             placeholder="Tên đăng nhập *" class="form-control"  />
                                         <span v-if="errors.tenDangNhap" class="text-danger">{{ errors.tenDangNhap }}</span>
                                     </div>

                                     <!-- Họ và tên -->
                                     <div class="col-md-6">
                                         <label class="form-label">Họ và tên</label>
                                         <input v-model="newEmployee.hoTen" type="text"
                                             placeholder="Họ và tên *" class="form-control"  />
                                         <span v-if="errors.hoTen" class="text-danger">{{ errors.hoTen }}</span>
                                     </div>

                                     <!-- Năm sinh -->
                                     <div class="col-md-6">
                                         <label class="form-label">Năm sinh</label>
                                         <input v-model="newEmployee.namSinh" type="number"
                                             placeholder="Năm Sinh *" class="form-control"  />
                                         <span v-if="errors.namSinh" class="text-danger">{{ errors.namSinh }}</span>
                                     </div>

                                     <!-- Mật khẩu -->
                                     <div class="col-md-6">
                                         <label class="form-label">Mật khẩu</label>
                                         <input v-model="newEmployee.matKhau" type="password"
                                             placeholder="Mật khẩu *" class="form-control"  />
                                         <span v-if="errors.matKhau" class="text-danger">{{ errors.matKhau }}</span>
                                     </div>

                                     <!-- Email -->
                                     <div class="col-md-6">
                                         <label class="form-label">Email</label>
                                         <input v-model="newEmployee.email" type="email"
                                             placeholder="Email *" class="form-control"  />
                                         <span v-if="errors.email" class="text-danger">{{ errors.email }}</span>
                                     </div>
    <div class="col-md-12">
                                    <label class="form-label">Địa chỉ</label>
                                    <input v-model="newEmployee.diaChi" type="text"
                                        placeholder="Địa chỉ *" class="form-control"  />
                                    <span v-if="errors.diaChi" class="text-danger">{{ errors.diaChi }}</span>
                                </div>
                                     <!-- Số điện thoại -->
                                     <div class="col-md-6">
                                         <label class="form-label">Số điện thoại</label>
                                         <input v-model="newEmployee.soDienThoai" type="text"
                                             placeholder="Số Điện Thoại *" class="form-control"  />
                                         <span v-if="errors.soDienThoai" class="text-danger">{{ errors.soDienThoai }}</span>
                                     </div>

                                     <!-- Giới tính -->
                                     <div class="col-md-6">
                                         <label class="form-label">Giới tính</label>
                                         <div class="d-flex align-items-center">
                                             <label class="me-2">
                                                 <input type="radio" v-model="newEmployee.gioiTinh" value="1" class="me-1" /> Nam
                                             </label>
                                             <label>
                                                 <input type="radio" v-model="newEmployee.gioiTinh" value="0" class="me-1" /> Nữ
                                             </label>
                                         </div>
                                         <span v-if="errors.gioiTinh" class="text-danger">{{ errors.gioiTinh }}</span>
                                     </div>

                                     <!-- Vai trò -->
                                     <div class="col-md-6">
                                         <label class="form-label">Vai trò</label>
                                         <div class="d-flex">
                                             <div class="form-check form-check-inline">
                                                 <input class="form-check-input" type="radio" id="roleAdmin" value="1" v-model="newEmployee.vaiTro" />
                                                 <label class="form-check-label" for="roleAdmin">Admin</label>
                                             </div>
                                             <div class="form-check form-check-inline">
                                                 <input class="form-check-input" type="radio" id="roleUser" value="2" v-model="newEmployee.vaiTro" />
                                                 <label class="form-check-label" for="roleUser">User</label>
                                             </div>
                                         </div>
                                         <span v-if="errors.vaiTro" class="text-danger">{{ errors.vaiTro }}</span>
                                     </div>

                                     <!-- Trạng thái -->
                                     <div class="col-md-6">
                                         <label class="form-label">Trạng thái</label>
                                         <div class="d-flex">
                                             <div class="form-check form-check-inline">
                                                 <input class="form-check-input" type="radio"
                                                     id="statusActive" value="true"
                                                     v-model="newEmployee.trangThai" />
                                                 <label class="form-check-label" for="statusActive">Hoạt động</label>
                                             </div>
                                             <div class="form-check form-check-inline">
                                                 <input class="form-check-input" type="radio"
                                                     id="statusInactive" value="false"
                                                     v-model="newEmployee.trangThai" />
                                                 <label class="form-check-label" for="statusInactive">Ngừng hoạt động</label>
                                             </div>
                                         </div>
                                         <span v-if="errors.trangThai" class="text-danger">{{ errors.trangThai }}</span>
                                     </div>
                                 </div>

                                 <!-- Submit and Cancel buttons -->
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



    <!-- Chỉnh sửa nhân viên -->
    <div id="editEmployeeModal" class="modal modal-xl fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <form @submit.prevent="editEmployee">
                    <div class="container mt-5 p-4 bg-white shadow-lg rounded-lg">
                        <h2 class="text-center mb-4">Chỉnh Sửa Nhân Viên</h2>
                        <div class="row">
                            <div class="col-md-8">
                                <div class="row g-3">
                                    <!-- Mã nhân viên (readonly) -->
                                    <div class="col-md-6">
                                        <label class="form-label">Mã nhân viên</label>
                                        <input v-model="employee.maNhanVien" type="text" placeholder="Mã Nhân Viên *" class="form-control" readonly />
                                    </div>



                                    <!-- Họ và tên -->
                                    <div class="col-md-6">
                                        <label class="form-label">Họ và tên</label>
                                        <input v-model="employee.hoTen" type="text" placeholder="Tên Nhân Viên *" class="form-control"  />
                                        <span v-if="errors.hoTen" class="text-danger">{{ errors.hoTen }}</span>
                                    </div>

                                    <!-- Email -->
                                    <div class="col-md-6">
                                        <label class="form-label">Email</label>
                                        <input v-model="employee.email" type="email" placeholder="Email *" class="form-control"  />
                                        <span v-if="errors.email" class="text-danger">{{ errors.email }}</span>
                                    </div>

                                    <!-- Số điện thoại -->
                                    <div class="col-md-6">
                                        <label class="form-label">Số điện thoại</label>
                                        <input v-model="employee.soDienThoai" type="text" placeholder="Số Điện Thoại *" class="form-control"  />
                                        <span v-if="errors.soDienThoai" class="text-danger">{{ errors.soDienThoai }}</span>
                                    </div>

                                    <!-- Năm sinh -->
                                    <div class="col-md-6">
                                        <label class="form-label">Năm sinh</label>
                                        <input v-model="employee.namSinh" type="number" placeholder="Năm Sinh *" class="form-control"  />
                                         <span v-if="errors.namSinh" class="text-danger">{{ errors.namSinh }}</span>
                                    </div>

                                    <!-- Giới tính -->
                                    <div class="col-md-6">
                                        <label class="form-label">Giới tính</label>
                                        <div class="d-flex align-items-center">
                                            <label class="me-2">
                                                <input type="radio" v-model="employee.gioiTinh" value="0" class="me-1" /> Nam
                                            </label>
                                            <label>
                                                <input type="radio" v-model="employee.gioiTinh" value="1" class="me-1" /> Nữ
                                            </label>
                                        </div>
                                    </div>

                                    <!-- Vai trò -->
                                    <div class="col-md-6">
                                        <label class="form-label">Vai trò</label>
                                        <div class="d-flex">
                                            <div class="form-check form-check-inline">
                                                <input class="form-check-input" type="radio" v-model="employeeVaiTroId" value="1" />
                                                <label class="form-check-label" for="editRoleAdmin">Admin</label>
                                            </div>
                                            <div class="form-check form-check-inline">
                                                <input class="form-check-input" type="radio" v-model="employeeVaiTroId" value="2" />
                                                <label class="form-check-label" for="editRoleUser">User</label>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Địa chỉ -->
                                    <div class="col-md-12">
                                        <label class="form-label">Địa chỉ</label>
                                        <input v-model="employee.diaChi" type="text" placeholder="Địa Chỉ *" class="form-control" />
                                        <span v-if="errors.diaChi" class="text-danger">{{ errors.diaChi }}</span>
                                    </div>

                                </div>

                                <!-- Submit and Cancel buttons -->
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

<style scoped>
/* Tổng thể */
.container {
      max-width: 1200px;
      background: #fff;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

/* Header */
h2 {
      font-size: 1.8rem;
      font-weight: bold;
      color: #333;
}

/* Tìm kiếm và bộ lọc */
.search-container input {
      padding: 10px;
      border-radius: 5px;
      border: 1px solid #ccc;
      width: 100%;
}

.status-filters .form-check {
      margin-right: 15px;
}

/* Bảng danh sách nhân viên */
.table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
}

.table th,
.table td {
      padding: 12px;
      text-align: center;
}

.table thead {
      background: #007bff;
      color: white;
}

.table-striped tbody tr:nth-child(odd) {
      background: #f8f9fa;
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

/* Nút hành động */
.btn {
      font-size: 14px;
      padding: 5px 10px;
      border-radius: 5px;
}

.btn-success {
      background: #28a745;
      border: none;
}

.btn-danger {
      background: #dc3545;
      border: none;
}

.btn-warning {
      background: #ffc107;
      border: none;
}

/* Phân trang */
.pagination-container {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-top: 20px;
}

.pagination-btn {
      background: #007bff;
      color: white;
      border: none;
      padding: 8px 12px;
      border-radius: 5px;
      cursor: pointer;
}

.pagination-btn:disabled {
      background: #ccc;
      cursor: not-allowed;
}

.pagination-page {
      font-size: 16px;
      font-weight: bold;
}

/* Modal */
.modal-content {
      padding: 20px;
      border-radius: 10px;
}
</style>