<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const route = useRoute();
const customerId = route.params.id; // Get the customer ID from the route

// Form data
const hoTen = ref("");
const email = ref("");
const soDienThoai = ref("");
const gioiTinh = ref(null);
const trangThai = ref(true);
const tenDangNhap = ref("");
const matKhau = ref("");

const loading = ref(false);
const errorMessage = ref("");
const errors = ref({});

const urlKhachHang = "http://localhost:8080/khach-hang";

// Fetch the customer data for editing
const fetchCustomerData = async () => {
    loading.value = true;
    try {
        const response = await axios.get(`${urlKhachHang}/${customerId}`);
        const customer = response.data;

        hoTen.value = customer.hoTen;
        email.value = customer.email;
        soDienThoai.value = customer.soDienThoai;
        gioiTinh.value = customer.gioiTinh;
        trangThai.value = customer.trangThai;
        tenDangNhap.value = customer.taiKhoan.tenDangNhap;
        matKhau.value = customer.taiKhoan.matKhau;  // Optionally encrypt or hash this in real-world applications
    } catch (error) {
        errorMessage.value = "Lỗi khi tải dữ liệu khách hàng!";
    } finally {
        loading.value = false;
    }
};

// Form validation
const validateForm = () => {
    errors.value = {};  // Reset errors before validating

    if (!hoTen.value) errors.value.hoTen = "Họ tên không được để trống";
    if (!tenDangNhap.value || tenDangNhap.value.length < 6) errors.value.tenDangNhap = "Tên đăng nhập phải có ít nhất 6 ký tự";
    if (!email.value || !/^[^\s@]+@[^\s@]+\.(com)$/.test(email.value) || !email.value.endsWith('@gmail.com')) errors.value.email = "Email không hợp lệ";
    if (!matKhau.value || matKhau.value.length < 6) errors.value.matKhau = "Mật khẩu phải có ít nhất 6 ký tự";
    if (!soDienThoai.value || !/^0\d{9}$/.test(soDienThoai.value)) errors.value.soDienThoai = "Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 số";

    return Object.keys(errors.value).length === 0;
};

// Handle the form submission
const handleSubmit = async () => {
    const validationErrors = validateForm();
    if (!validationErrors) {
        return; // Stop if there are validation errors
    }

    const isConfirmed = window.confirm('Bạn có chắc chắn muốn cập nhật khách hàng này không?');
    if (!isConfirmed) {
        return; // Stop if user cancels
    }

    loading.value = true;
    errorMessage.value = "";

    const updatedCustomer = {
        hoTen: hoTen.value,
        email: email.value,
        soDienThoai: soDienThoai.value,
        gioiTinh: gioiTinh.value,
        trangThai: trangThai.value,
        taiKhoan: {
            tenDangNhap: tenDangNhap.value,
            matKhau: matKhau.value
        }
    };

    try {
        // Fetch customer data
        const customerResponse = await axios.get(`/khach-hang/${customerId}`);
        const customer = customerResponse.data;

        if (customer && customer.id) {
            // Update customer status (if needed)
            const newStatus = trangThai.value;  // Get status from form
            await axios.patch(`/tai-khoan/${customer.id}/status`, null, {
                params: { trangThai: newStatus },
            });
        }

        // Update customer information
        await axios.put(`${urlKhachHang}/${customerId}`, updatedCustomer);
        alert("Cập nhật khách hàng thành công!");
        router.push("/admin/customers");  // Redirect to the customer management page
    } catch (error) {
        errorMessage.value = "Lỗi khi cập nhật khách hàng!";
    } finally {
        loading.value = false;
    }
};

// Fetch customer data on page load
onMounted(fetchCustomerData);
</script>



<template>
    <div class="p-4">
        <h1 class="text-center mb-4">Sửa thông tin khách hàng</h1>

        <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
        <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>

        <div class="card p-4 shadow-sm">
            <form @submit.prevent="handleSubmit">
                <!-- Họ tên -->
                <div class="mb-3">
                    <label class="form-label">Họ tên</label>
                    <input v-model="hoTen" type="text" class="form-control" placeholder="Nhập họ tên" required />
                    <div class="text-danger" v-if="errors.hoTen">{{ errors.hoTen }}</div>
                </div>

                <!-- Tên đăng nhập -->
                <div class="mb-3">
                    <label class="form-label">Tên đăng nhập</label>
                    <input v-model="tenDangNhap" type="text" class="form-control" placeholder="Nhập tên đăng nhập" required />
                    <div class="text-danger" v-if="errors.tenDangNhap">{{ errors.tenDangNhap }}</div>
                </div>

                <!-- Mật khẩu -->
                <div class="mb-3">
                    <label class="form-label">Mật khẩu</label>
                    <input v-model="matKhau" type="text" class="form-control" placeholder="Nhập mật khẩu" />
                    <div class="text-danger" v-if="errors.matKhau">{{ errors.matKhau }}</div>
                </div>

                <!-- Email -->
                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input v-model="email" type="email" class="form-control" placeholder="Nhập email" required />
                    <div class="text-danger" v-if="errors.email">{{ errors.email }}</div>
                </div>

                <!-- Số điện thoại -->
                <div class="mb-3">
                    <label class="form-label">Số điện thoại</label>
                    <input v-model="soDienThoai" type="text" class="form-control" placeholder="Nhập số điện thoại" required />
                    <div class="text-danger" v-if="errors.soDienThoai">{{ errors.soDienThoai }}</div>
                </div>

                <!-- Giới tính -->
                <div class="mb-3">
                    <label class="form-label">Giới tính</label>
                    <select v-model="gioiTinh" class="form-select">
                        <option :value="true">Nữ</option>
                        <option :value="false">Nam</option>
                    </select>
                </div>

                <!-- Trạng thái -->
                <div class="mb-3">
                    <label class="form-label">Trạng thái</label>
                    <select v-model="trangThai" class="form-select">
                        <option :value="true">Hoạt động</option>
                        <option :value="false">Ngừng hoạt động</option>
                    </select>
                </div>

                 <div class="d-flex gap-3">
                <button type="submit" class="btn btn-primary" :disabled="loading">
                    {{ loading ? "Đang xử lý..." : "Cập nhật khách hàng" }}
                </button>
                <button type="button" class="btn btn-secondary" @click="router.push('/admin/customers')">
                    Quay lại
                </button>
                </div>
            </form>
        </div>
    </div>
</template>
