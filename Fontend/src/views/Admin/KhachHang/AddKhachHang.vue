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


    if (!tenDangNhap.value || tenDangNhap.value.length < 6) {
        errors.value.tenDangNhap = "Tên đăng nhập phải có ít nhất 6 ký tự";
    }
    if (!hoTen.value) errors.value.hoTen = "Họ tên không được để trống";
     if (!email.value || !/^[^\s@]+@[^\s@]+\.(com)$/.test(email.value) || !email.value.endsWith('@gmail.com')) {
            errors.value.email = "Địa chỉ Gmail hợp lệ";
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

// Hàm tạo mã khách hàng
const generateMaKhachHang = async () => {
    try {
        // Lấy danh sách khách hàng hiện có
        const response = await axios.get(`${BASE_URL}/khach-hang`);
        const khachHangList = response.data;

        let newMaKH = "KH01"; // Mặc định bắt đầu từ KH01

        // Kiểm tra nếu danh sách khách hàng có dữ liệu
        if (khachHangList && khachHangList.length > 0) {
            // Lấy mã khách hàng lớn nhất và tăng thêm 1
            const maxMaKH = khachHangList.reduce((max, kh) => {
                const numericPart = parseInt(kh.maKhachHang.replace('KH0', ''), 10); // Tách phần số
                return numericPart > max ? numericPart : max;
            }, 0);

            // Tạo mã mới cho khách hàng
            newMaKH = `KH0${maxMaKH + 1}`;
        }

        // Kiểm tra xem mã khách hàng mới có tồn tại không
        const exists = await checkMaKhachHangExists(newMaKH);
        if (exists) {
            // Nếu mã khách hàng đã tồn tại, tạo mã mới
            newMaKH = `KH${parseInt(newMaKH.replace('KH0', ''), 10) + 1}`;
        }

        return newMaKH;

    } catch (error) {
        console.error("Lỗi khi lấy danh sách khách hàng:", error.response ? error.response.data : error.message);
        return "KH01"; // Mặc định là KH01 nếu có lỗi
    }
};

// Kiểm tra mã khách hàng đã tồn tại trong cơ sở dữ liệu hay chưa
const checkMaKhachHangExists = async (maKhachHang) => {
    try {
        const response = await axios.get(`${BASE_URL}/khach-hang/${maKhachHang}`);
        return response.status === 200; // Nếu mã đã tồn tại, trả về true
    } catch (error) {
        return false; // Nếu không tìm thấy mã khách hàng (lỗi 404), trả về false
    }
};

// Hàm xử lý thêm khách hàng
const handleAddKhachHang = async () => {
    if (!validateForm()) return;

    // Xác nhận trước khi thêm khách hàng
        const isConfirmed = window.confirm('Bạn có chắc chắn muốn thêm khách hàng này không?');

        if (!isConfirmed) {
            return; // Dừng lại nếu người dùng không xác nhận
        }

    const newKhachHang = {
        maKhachHang: await generateMaKhachHang(),
        hoTen: hoTen.value,
        email: email.value,
        soDienThoai: soDienThoai.value,
        gioiTinh: gioiTinh.value,
        trangThai: trangThai.value,
        taiKhoan: {
                    tenDangNhap: tenDangNhap.value,
                    matKhau: matKhau.value,
                }
    };

    isLoading.value = true;

    try {
        await axios.post(urlKhachHang, newKhachHang);
        alert("Thêm khách hàng thành công!");
        router.go(-1);
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
                    <input v-model="matKhau" type="password" class="form-control" placeholder="Nhập mật khẩu">
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
                        <option value="false">Nam</option>
                        <option value="true">Nữ</option>
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
