<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

// URL API
const BASE_URL = "http://localhost:8080";
const urlKhuyenMai = `${BASE_URL}/khuyen-mai/addKM`;

// Dữ liệu form
const maKhuyenMai = ref("");
const tenKhuyenMai = ref("");
const ngayBatDau = ref("");  // Format 'YYYY-MM-DD'
const ngayKetThuc = ref(""); // Format 'YYYY-MM-DD'
const phanTramGiamGia = ref("");
const trangThai = ref(true); // Mặc định là hoạt động

// Trạng thái xử lý form
const isLoading = ref(false);
const errors = ref({});

// Hàm chuyển đổi ngày từ 'YYYY-MM-DD' sang định dạng 'YYYY-MM-DD HH:mm:ss'
const formatDate = (date) => {
    const newDate = new Date(date);
    const year = newDate.getFullYear();
    const month = String(newDate.getMonth() + 1).padStart(2, '0');  // Lấy tháng (tháng trong JavaScript tính từ 0)
    const day = String(newDate.getDate()).padStart(2, '0');
    const hours = String(newDate.getHours()).padStart(2, '0');
    const minutes = String(newDate.getMinutes()).padStart(2, '0');
    const seconds = String(newDate.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// Hàm xử lý thêm khuyến mãi
const handleAddKhuyenMai = async () => {
    if (!validateForm()) return; // Dừng nếu form không hợp lệ

    // Chuyển đổi ngày giờ thành định dạng mong muốn
    const ngayBatDauFormatted = formatDate(ngayBatDau.value);
    const ngayKetThucFormatted = formatDate(ngayKetThuc.value);

    const newKhuyenMai = {
        maKhuyenMai: maKhuyenMai.value,
        tenKhuyenMai: tenKhuyenMai.value,
        ngayBatDau: ngayBatDauFormatted,
        ngayKetThuc: ngayKetThucFormatted,
        phanTramGiamGia: phanTramGiamGia.value,
        trangThai: trangThai.value,
    };

    isLoading.value = true;

    try {
        const response = await axios.post(urlKhuyenMai, newKhuyenMai);
        console.log("Thêm khuyến mãi thành công:", response.data);
        alert("Thêm khuyến mãi thành công!");
        router.push('/admin/products/promotions');
    } catch (error) {
        console.error("Lỗi khi thêm khuyến mãi:", error.response ? error.response.data : error.message);
        errors.value.apiError = error.response?.data || "Có lỗi xảy ra, vui lòng thử lại!";
    } finally {
        isLoading.value = false;
    }
};

// Hàm kiểm tra validation
const validateForm = () => {
    errors.value = {};
    if (!maKhuyenMai.value) errors.value.maKhuyenMai = "Mã khuyến mãi không được để trống";
    if (!tenKhuyenMai.value) errors.value.tenKhuyenMai = "Tên khuyến mãi không được để trống";
    if (!ngayBatDau.value) errors.value.ngayBatDau = "Vui lòng chọn ngày bắt đầu";
    if (!ngayKetThuc.value) errors.value.ngayKetThuc = "Vui lòng chọn ngày kết thúc";
    if (!phanTramGiamGia.value) errors.value.phanTramGiamGia = "Vui lòng nhập phần trăm giảm giá";
    return Object.keys(errors.value).length === 0;
};
</script>

<template>
    <div class="p-4" style="min-height: 450px;">
        <h1 class="mb-4 text-center">Thêm khuyến mãi mới</h1>

        <div class="card p-4 shadow-sm">
            <form @submit.prevent="handleAddKhuyenMai">
                <!-- Mã khuyến mãi -->
                <div class="mb-3">
                    <label for="maKhuyenMai" class="form-label">Mã khuyến mãi</label>
                    <input v-model="maKhuyenMai" id="maKhuyenMai" type="text" class="form-control" placeholder="Nhập mã khuyến mãi">
                    <div v-if="errors.maKhuyenMai" class="text-danger">{{ errors.maKhuyenMai }}</div>
                </div>

                <!-- Tên khuyến mãi -->
                <div class="mb-3">
                    <label for="tenKhuyenMai" class="form-label">Tên khuyến mãi</label>
                    <input v-model="tenKhuyenMai" id="tenKhuyenMai" type="text" class="form-control" placeholder="Nhập tên khuyến mãi">
                    <div v-if="errors.tenKhuyenMai" class="text-danger">{{ errors.tenKhuyenMai }}</div>
                </div>

                <!-- Ngày bắt đầu -->
                <div class="mb-3">
                    <label for="ngayBatDau" class="form-label">Ngày bắt đầu</label>
                    <input v-model="ngayBatDau" id="ngayBatDau" type="date" class="form-control">
                    <div v-if="errors.ngayBatDau" class="text-danger">{{ errors.ngayBatDau }}</div>
                </div>

                <!-- Ngày kết thúc -->
                <div class="mb-3">
                    <label for="ngayKetThuc" class="form-label">Ngày kết thúc</label>
                    <input v-model="ngayKetThuc" id="ngayKetThuc" type="date" class="form-control">
                    <div v-if="errors.ngayKetThuc" class="text-danger">{{ errors.ngayKetThuc }}</div>
                </div>

                <!-- Phần trăm giảm giá -->
                <div class="mb-3">
                    <label for="phanTramGiamGia" class="form-label">Phần trăm giảm giá</label>
                    <input v-model="phanTramGiamGia" id="phanTramGiamGia" type="number" class="form-control" placeholder="Nhập phần trăm giảm giá">
                    <div v-if="errors.phanTramGiamGia" class="text-danger">{{ errors.phanTramGiamGia }}</div>
                </div>

                <!-- Trạng thái -->
                <div class="mb-3">
                    <label for="trangThai" class="form-label">Trạng thái</label>
                    <select v-model="trangThai" id="trangThai" class="form-select">
                        <option :value="true">Hoạt động</option>
                        <option :value="false">Ngừng hoạt động</option>
                    </select>
                </div>

                <!-- Thông báo lỗi API -->
                <div v-if="errors.apiError" class="alert alert-danger">{{ errors.apiError }}</div>

                <!-- Submit and Go Back -->
                <div class="d-flex gap-3">
                    <button type="submit" class="btn btn-primary" :disabled="isLoading">
                        {{ isLoading ? "Đang xử lý..." : "Thêm khuyến mãi" }}
                    </button>
                    <button type="button" class="btn btn-secondary" @click="router.go(-1)">
                        Quay lại
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>
