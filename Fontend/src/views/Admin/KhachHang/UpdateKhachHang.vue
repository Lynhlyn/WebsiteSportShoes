<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const id = route.params.id;
const urlKhachHang = `http://localhost:8080/khach-hang/${id}`;

const khachHang = ref({
    maKhachHang: '',
    hoTen: '',
    tenDangNhap: '',
    matKhau: '',
    gioiTinh: '',
    email: '',
    soDienThoai: '',
    trangThai: true
});

const errorMessage = ref('');
const errors = ref({});
const loading = ref(false);

const fetchKhachHang = async () => {
    loading.value = true;
    try {
        const response = await axios.get(urlKhachHang);
        khachHang.value = response.data;
    } catch (error) {
        errorMessage.value = 'Lỗi khi tải dữ liệu khách hàng!';
        console.error(error);
    } finally {
        loading.value = false;
    }
};

const validateForm = () => {
    errors.value = {};

    if (!khachHang.value.maKhachHang) errors.value.maKhachHang = "Mã khách hàng không được để trống";
    if (!khachHang.value.tenDangNhap) {
        errors.value.tenDangNhap = "Tên đăng nhập không được để trống";
    } else if (khachHang.value.tenDangNhap.length < 6) {
        errors.value.tenDangNhap = "Tên đăng nhập phải có ít nhất 6 ký tự";
    }
    if (!khachHang.value.hoTen) errors.value.hoTen = "Họ tên không được để trống";
    if (!khachHang.value.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(khachHang.value.email)) {
        errors.value.email = "Email không hợp lệ";
    }
    if (!khachHang.value.matKhau || khachHang.value.matKhau.length < 6) {
        errors.value.matKhau = "Mật khẩu phải có ít nhất 6 ký tự";
    }
    if (!khachHang.value.soDienThoai || !/^0\d{9}$/.test(khachHang.value.soDienThoai)) {
        errors.value.soDienThoai = "Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 số";
    }

    return Object.keys(errors.value).length === 0;
};

const updateKhachHang = async () => {
    if (!validateForm()) {
        return;
    }

    loading.value = true;
    try {
        await axios.put(urlKhachHang, khachHang.value);
        alert('Cập nhật khách hàng thành công!');
        router.push('/admin/customers');
    } catch (error) {
        errorMessage.value = 'Lỗi khi cập nhật khách hàng!';
        console.error(error);
    } finally {
        loading.value = false;
    }
};

onMounted(fetchKhachHang);
</script>

<template>
    <div class="p-4">
        <h1 class="mb-4 text-center">Cập nhật thông tin khách hàng</h1>
        <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
        <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>
        <form @submit.prevent="updateKhachHang" v-if="!loading">
            <div class="mb-3">
                <label class="form-label">Mã khách hàng</label>
                <input v-model="khachHang.maKhachHang" type="text" class="form-control" readonly />
                <div class="text-danger" v-if="errors.maKhachHang">{{ errors.maKhachHang }}</div>
            </div>
            <div class="mb-3">
                <label class="form-label">Họ tên</label>
                <input v-model="khachHang.hoTen" type="text" class="form-control" required />
                <div class="text-danger" v-if="errors.hoTen">{{ errors.hoTen }}</div>
            </div>
            <div class="mb-3">
                <label class="form-label">Tên đăng nhập</label>
                <input v-model="khachHang.tenDangNhap" type="text" class="form-control" required />
                <div class="text-danger" v-if="errors.tenDangNhap">{{ errors.tenDangNhap }}</div>
            </div>
            <div class="mb-3">
                <label class="form-label">Mật khẩu</label>
                <input v-model="khachHang.matKhau" type="text" class="form-control" />
                <div class="text-danger" v-if="errors.matKhau">{{ errors.matKhau }}</div>
            </div>
            <div class="mb-3">
                <label class="form-label">Giới tính</label>
                <select v-model="khachHang.gioiTinh" class="form-control">
                    <option :value="true">Nam</option>
                    <option :value="false">Nữ</option>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label">Email</label>
                <input v-model="khachHang.email" type="email" class="form-control" required />
                <div class="text-danger" v-if="errors.email">{{ errors.email }}</div>
            </div>
            <div class="mb-3">
                <label class="form-label">Số điện thoại</label>
                <input v-model="khachHang.soDienThoai" type="text" class="form-control" required />
                <div class="text-danger" v-if="errors.soDienThoai">{{ errors.soDienThoai }}</div>
            </div>
            <div class="mb-3">
                <label class="form-label">Trạng thái</label>
                <select v-model="khachHang.trangThai" class="form-control">
                    <option :value="true">Hoạt động</option>
                    <option :value="false">Ngừng hoạt động</option>
                </select>
            </div>
            <div class="d-flex justify-content-between">
                <button type="submit" class="btn btn-primary">Cập nhật</button>
                <button type="button" class="btn btn-secondary" @click="router.push('/admin/customers')">Hủy</button>
            </div>
        </form>
    </div>
</template>
