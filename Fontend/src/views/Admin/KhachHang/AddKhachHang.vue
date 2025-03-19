<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

const BASE_URL = "http://localhost:8080";
const urlKhachHang = `${BASE_URL}/khach-hang/addKH`;

// Dữ liệu form
const maKhachHang = ref("");
const tenDangNhap = ref("");
const hoTen = ref("");
const email = ref("");
const matKhau = ref("");
const soDienThoai = ref("");
const gioiTinh = ref("");
const trangThai = ref(true);

// Trạng thái xử lý form
const isLoading = ref(false);
const errors = ref({});

// Xử lý lỗi validation
const validateForm = () => {
    errors.value = {};

    if (!maKhachHang.value) errors.value.maKhachHang = "Mã khách hàng không được để trống";
    if (!tenDangNhap.value || tenDangNhap.value.length < 6) {
        errors.value.tenDangNhap = "Tên đăng nhập phải có ít nhất 6 ký tự";
    }
    if (!hoTen.value) errors.value.hoTen = "Họ tên không được để trống";
    if (!email.value || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
        errors.value.email = "Email không hợp lệ";
    }
    if (!matKhau.value || matKhau.value.length < 6) {
        errors.value.matKhau = "Mật khẩu phải có ít nhất 6 ký tự";
    }
    if (!soDienThoai.value || !/^0\d{9}$/.test(soDienThoai.value)) {
        errors.value.soDienThoai = "Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 số";
    }
    if (!gioiTinh.value) errors.value.gioiTinh = "Vui lòng chọn giới tính";


    // Trả về true nếu không có lỗi, false nếu có lỗi
    return Object.keys(errors.value).length === 0;
};


// Hàm xử lý thêm khách hàng
const handleAddKhachHang = async () => {
    if (!validateForm()) return;

    const newKhachHang = {
        maKhachHang: maKhachHang.value,
        tenDangNhap: tenDangNhap.value,
        hoTen: hoTen.value,
        email: email.value,
        matKhau: matKhau.value,
        soDienThoai: soDienThoai.value,
        gioiTinh: gioiTinh.value,
        trangThai: trangThai.value,
    };

    isLoading.value = true;

    try {
        await axios.post(urlKhachHang, newKhachHang);
        alert("Thêm khách hàng thành công!");
        router.push('/admin/customers');
    } catch (error) {
        console.error("Lỗi khi thêm khách hàng:", error.response ? error.response.data : error.message);
        errors.value.apiError = error.response?.data || "Có lỗi xảy ra, vui lòng thử lại!";
    } finally {
        isLoading.value = false;
    }
};
</script>

<template>
    <div class="p-4" style="min-height: 450px;">
        <h1 class="mb-4 text-center">Thêm khách hàng mới</h1>

        <div class="card p-4 shadow-sm">
            <form @submit.prevent="handleAddKhachHang">
                <div class="mb-3">
                    <label class="form-label">Mã khách hàng</label>
                    <input v-model="maKhachHang" type="text" class="form-control" placeholder="Nhập mã khách hàng">
                    <small v-if="errors.maKhachHang" class="text-danger">{{ errors.maKhachHang }}</small>
                </div>
                <div class="mb-3">
                    <label class="form-label">Tên đăng nhập</label>
                    <input v-model="tenDangNhap" type="text" class="form-control" placeholder="Nhập tên đăng nhập">
                    <small v-if="errors.tenDangNhap" class="text-danger">{{ errors.tenDangNhap }}</small>
                </div>
                <div class="mb-3">
                    <label class="form-label">Họ tên</label>
                    <input v-model="hoTen" type="text" class="form-control" placeholder="Nhập họ tên">
                    <small v-if="errors.hoTen" class="text-danger">{{ errors.hoTen }}</small>
                </div>
                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input v-model="email" type="email" class="form-control" placeholder="Nhập email">
                    <small v-if="errors.email" class="text-danger">{{ errors.email }}</small>
                </div>
                <div class="mb-3">
                    <label class="form-label">Mật khẩu</label>
                    <input v-model="matKhau" type="text" class="form-control" placeholder="Nhập mật khẩu">
                    <small v-if="errors.matKhau" class="text-danger">{{ errors.matKhau }}</small>
                </div>
                <div class="mb-3">
                    <label class="form-label">Số điện thoại</label>
                    <input v-model="soDienThoai" type="text" class="form-control" placeholder="Nhập số điện thoại">
                    <small v-if="errors.soDienThoai" class="text-danger">{{ errors.soDienThoai }}</small>
                </div>
                <div class="mb-3">
                    <label class="form-label">Giới tính</label>
                    <select v-model="gioiTinh" class="form-select">
                        <option value="" disabled>Chọn giới tính</option>
                        <option value="true">Nam</option>
                        <option value="false">Nữ</option>
                    </select>
                     <small v-if="errors.gioiTinh" class="text-danger">{{ errors.gioiTinh }}</small>
                </div>
                <div class="mb-3">
                    <label class="form-label">Trạng thái</label>
                    <select v-model="trangThai" class="form-select">
                        <option :value="true">Hoạt động</option>
                        <option :value="false">Ngừng hoạt động</option>
                    </select>
                </div>
                <div v-if="errors.apiError" class="alert alert-danger">{{ errors.apiError }}</div>
                <div class="d-flex gap-3">
                    <button type="submit" class="btn btn-primary" :disabled="isLoading">
                        {{ isLoading ? "Đang xử lý..." : "Thêm khách hàng" }}
                    </button>
                    <button type="button" class="btn btn-secondary" @click="router.go(-1)">
                        Quay lại
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>
