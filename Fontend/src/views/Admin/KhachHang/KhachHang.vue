<script setup>
import { onMounted, ref, computed, watch } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const statusFilter = ref("");      // Lọc trạng thái khách hàng
const khachHangList = ref([]);     // Danh sách khách hàng
const loading = ref(false);        // Hiển thị trạng thái loading
const errorMessage = ref("");      // Thông báo lỗi
const searchQuery = ref("");       // Từ khoá tìm kiếm khách hàng
const currentPage = ref(1);        // Trang hiện tại
const pageSize = 5;                // Số khách hàng mỗi trang
const router = useRouter();
const urlKhachHang = "http://localhost:8080/khach-hang";

// Hàm lấy danh sách khách hàng
const fetchKhachHang = async () => {
    loading.value = true;
    errorMessage.value = "";

    try {
        // Gửi yêu cầu API để lấy dữ liệu khách hàng, bao gồm các thông tin tìm kiếm và lọc trạng thái
        const params = {
            keyword: searchQuery.value,
            trangThai: statusFilter.value !== "" ? (statusFilter.value === "true") : undefined
        };

        const response = await axios.get(urlKhachHang, { params });

        if (response.data && Array.isArray(response.data)) {
            khachHangList.value = response.data.map(kh => ({
                ...kh,
                ngayTao: kh.ngayTao ? new Date(kh.ngayTao).toLocaleDateString() : "Không có",
                ngaySua: kh.ngaySua ? new Date(kh.ngaySua).toLocaleDateString() : "Không có"
            }));
        } else {
            errorMessage.value = "Dữ liệu khách hàng không hợp lệ!";
        }
    } catch (error) {
        errorMessage.value = "Lỗi khi tải dữ liệu khách hàng. Vui lòng thử lại!";
        console.error("API Error: ", error);
    } finally {
        loading.value = false;
    }
};

// Watch khi có thay đổi từ các giá trị lọc
watch([searchQuery, statusFilter], () => {
    setTimeout(fetchKhachHang, 300);
});

// Phân trang
const paginatedKhachHangList = computed(() => {
    const startIndex = (currentPage.value - 1) * pageSize;
    return khachHangList.value.slice(startIndex, startIndex + pageSize);
});

const totalPages = computed(() => {
    return Math.ceil(khachHangList.value.length / pageSize);
});

const nextPage = () => {
    if (currentPage.value < totalPages.value) {
        currentPage.value++;
    }
};

const prevPage = () => {
    if (currentPage.value > 1) {
        currentPage.value--;
    }
};

// Các chức năng xử lý hành động
const handleDeleteKhachHang = async (id) => {
    if (confirm("Bạn có chắc chắn muốn xoá khách hàng này?")) {
        try {
            await axios.delete(`${urlKhachHang}/${id}`);
            alert("Xoá khách hàng thành công!");
            await fetchKhachHang();
        } catch (error) {
            console.error("Lỗi khi xoá khách hàng:", error);
            alert("Lỗi khi xoá khách hàng. Vui lòng thử lại!");
        }
    }
};

const handleEditKhachHang = (id) => {
    router.push(`/admin/customers/manage/update-khachhang/${id}`);
};

const handleAddKhachHang = () => {
    router.push('/admin/customers/manage/add-khachhang');
};

// Gọi API khi component được mount
onMounted(fetchKhachHang);
</script>

<template>
    <div class="p-4" style="min-height: 450px;">
        <h1 class="mb-4 text-center">Quản lý khách hàng</h1>

        <div class="mb-3 d-flex justify-content-between align-items-center">
            <div class="d-flex w-50">
                <input v-model="searchQuery" class="form-control me-2" type="text" placeholder="Tìm kiếm khách hàng..." />
                <button class="btn btn-secondary" @click="fetchKhachHang">Tìm kiếm</button>
            </div>
            <div class="d-flex">
                <!-- Lọc trạng thái khách hàng -->
                <select v-model="statusFilter" class="form-control me-2">
                    <option value="">Chọn trạng thái</option>
                    <option value="true">Hoạt động</option>
                    <option value="false">Ngừng hoạt động</option>
                </select>
                <button class="btn btn-success me-2" @click="handleAddKhachHang">Thêm mới</button>
            </div>
        </div>

        <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
        <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>

        <table v-if="!loading && paginatedKhachHangList.length > 0" class="table table-striped table-hover">
            <thead class="table-dark">
                <tr class="text-center">
                    <th>STT</th>
                    <th>Mã khách hàng</th>
                    <th>Tên khách hàng</th>
                    <th>Giới tính</th>
                    <th>Email</th>
                    <th>Số điện thoại</th>
                    <th>Ngày tạo</th>
                    <th>Ngày sửa</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(khachHang, index) in paginatedKhachHangList" :key="khachHang.id" class="align-middle">
                    <!-- STT -->
                    <td class="text-center">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                    <!-- Mã khách hàng -->
                    <td>{{ khachHang.maKhachHang }}</td>
                    <!-- Tên khách hàng -->
                    <td>{{ khachHang.hoTen }}</td>
                    <!-- Giới tính -->
                    <td class="text-center">{{ khachHang.gioiTinh ? 'Nam' : 'Nữ' }}</td>
                    <!-- Email -->
                    <td>{{ khachHang.email }}</td>
                    <!-- Số điện thoại -->
                    <td>{{ khachHang.soDienThoai || 'Không có' }}</td>
                    <!-- Ngày tạo -->
                    <td>{{ khachHang.ngayTao }}</td>
                    <!-- Ngày sửa -->
                    <td>{{ khachHang.ngaySua }}</td>
                    <!-- Trạng thái -->
                    <td class="text-center">
                        <span class="badge" :class="khachHang.trangThai ? 'bg-success' : 'bg-danger'">
                            {{ khachHang.trangThai ? 'Hoạt động' : 'Đã khoá' }}
                        </span>
                    </td>
                    <!-- Hành động -->
                    <td class="text-center">
                        <div class="d-flex justify-content-center gap-2">
                            <button class="btn btn-warning btn-sm" @click="handleEditKhachHang(khachHang.id)">Sửa</button>
                            <button class="btn btn-danger btn-sm" @click="handleDeleteKhachHang(khachHang.id)">Xoá</button>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>

        <div v-else-if="!loading && paginatedKhachHangList.length === 0" class="alert alert-warning text-center">
            Không có khách hàng nào!
        </div>

        <!-- Phân trang -->
        <div v-if="totalPages > 1" class="mt-3 d-flex justify-content-center">
            <button class="btn btn-primary me-2" @click="prevPage" :disabled="currentPage === 1">← Trang trước</button>
            <span class="align-self-center">Trang {{ currentPage }} / {{ totalPages }}</span>
            <button class="btn btn-primary ms-2" @click="nextPage" :disabled="currentPage === totalPages">Trang sau →</button>
        </div>
    </div>
</template>

