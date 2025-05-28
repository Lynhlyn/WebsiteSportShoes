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
const pageSize = ref(5);           // Số khách hàng mỗi trang (sửa thành ref)
const router = useRouter();
const urlKhachHang = "http://localhost:8080/khach-hang";

// Hàm lấy danh sách khách hàng
const fetchKhachHang = async () => {
    loading.value = true;
    errorMessage.value = "";

    try {
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
// Cập nhật trạng thái khách hàng
const updateCustomerStatus = async (id, currentStatus) => {
    const newStatus = !currentStatus;

    const isConfirmed = window.confirm('Bạn có chắc chắn muốn thay đổi trạng thái khách hàng này không?');

    if (!isConfirmed) {
        return;
    }

    try {
        // Cập nhật trạng thái của khách hàng
        const response = await axios.patch(`http://localhost:8080/khach-hang/${id}/status`, null, {
            params: { trangThai: newStatus }
        });

        // Kiểm tra nếu khách hàng đã được cập nhật thành công
        if (response.status === 200) {
            const customer = response.data;
            // Cập nhật trạng thái của tài khoản liên kết với khách hàng
            if (customer && customer.idTk) {
                // Cập nhật trạng thái tài khoản của khách hàng (sử dụng idTk từ customer)
                await axios.patch(`http://localhost:8080/tai-khoan/${customer.idTk}/status`, null, {
                    params: { trangThai: newStatus }
                });
            }

            // Sau khi cập nhật trạng thái cả khách hàng và tài khoản, tải lại danh sách khách hàng
            fetchKhachHang();
        }
    } catch (error) {
        console.error('Lỗi cập nhật trạng thái khách hàng:', error.response?.data || error.message);
        alert('Đã xảy ra lỗi khi cập nhật trạng thái khách hàng. Vui lòng thử lại.');
    }
};



// Watch khi có thay đổi từ các giá trị lọc
watch([searchQuery, statusFilter], () => {
    currentPage.value = 1;
    fetchKhachHang();
});

// Watch cho pageSize để reset trang hiện tại
watch(pageSize, () => {
    currentPage.value = 1;
});

// Phân trang
const paginatedKhachHangList = computed(() => {
    const startIndex = (currentPage.value - 1) * pageSize.value;
    return khachHangList.value.slice(startIndex, startIndex + pageSize.value);
});

// Tổng số trang
const totalPages = computed(() => {
    return Math.ceil(khachHangList.value.length / pageSize.value);
});

// Hiển thị danh sách trang hợp lý (ví dụ: hiển thị 5 trang trước & sau trang hiện tại)
const visiblePages = computed(() => {
    const maxPagesToShow = 5;
    const total = totalPages.value;
    const current = currentPage.value;

    if (total <= maxPagesToShow) return Array.from({ length: total }, (_, i) => i + 1);

    let start = Math.max(current - 2, 1);
    let end = Math.min(current + 2, total);

    if (start === 1) {
        end = Math.min(start + maxPagesToShow - 1, total);
    } else if (end === total) {
        start = Math.max(total - maxPagesToShow + 1, 1);
    }

    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});

// Chuyển trang
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

const changePage = (page) => {
    currentPage.value = page;
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
            </div>
            <div class="d-flex">
                <!-- Lọc trạng thái khách hàng -->
                <select v-model="statusFilter" class="form-control me-2">
                    <option value="">Chọn trạng thái</option>
                    <option value="true">Hoạt động</option>
                    <option value="false">Ngừng hoạt động</option>
                </select>
                <button class="btn btn-primary" @click="handleAddKhachHang">
                <i class="bi bi-plus-circle"></i>Thêm mới</button>
            </div>
        </div>

        <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
        <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>

        <table v-if="!loading && paginatedKhachHangList.length > 0" class="table table-striped table-hover">
            <thead class="table-dark">
                <tr class="text-center">
                    <th>STT</th>
                    <th>Mã khách hàng</th>
                    <th>Tên đăng nhập</th>
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
                    <td class="text-center">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                     <td>{{ khachHang.maKhachHang }}</td>
                    <td>{{ khachHang.taiKhoan }}</td>
                    <td>{{ khachHang.hoTen }}</td>
                    <td class="text-center">{{ khachHang.gioiTinh ? 'Nữ' : 'Nam' }}</td>
                    <td>{{ khachHang.email }}</td>
                    <td>{{ khachHang.soDienThoai || 'Không có' }}</td>
                    <td>{{ khachHang.ngayTao }}</td>
                    <td>{{ khachHang.ngaySua }}</td>
                    <td class="text-center">
                        <span class="badge" :class="khachHang.trangThai ? 'bg-success' : 'bg-danger'">
                            {{ khachHang.trangThai ? 'Hoạt động' : 'Đã khoá' }}
                        </span>
                    </td>
                    <td class="text-center">
                        <div class="d-flex justify-content-center gap-2">
      <button class="btn btn-sm"
                                                      :class="khachHang.trangThai ? 'btn-success' : 'btn-danger'"
                                                      @click="updateCustomerStatus(khachHang.id, khachHang.trangThai)">
                                                      <i
                                                            :class="khachHang.trangThai ? 'bi bi-check-circle' : 'bi bi-x-circle'"></i>
                                                </button>
                            <button class="btn btn-warning btn-sm" @click="handleEditKhachHang(khachHang.id)"><i class="bi bi-pencil"></i></button>
                        </div>

                    </td>

                </tr>
            </tbody>
        </table>

        <div v-else-if="!loading && paginatedKhachHangList.length === 0" class="alert alert-warning text-center">
            Không có khách hàng nào!
        </div>

        <!-- Phân trang -->
        <div class="pagination-container">
            <button @click="prevPage" :disabled="currentPage === 1" class="pagination-btn">
                <i class="bi bi-chevron-left"></i>
            </button>

            <div class="pagination-pages">
                <button v-for="page in visiblePages" :key="page" @click="changePage(page)"
                        :class="['pagination-page', { 'active': currentPage === page }]">
                    {{ page }}
                </button>
            </div>

            <button @click="nextPage" :disabled="currentPage === totalPages" class="pagination-btn">
                <i class="bi bi-chevron-right"></i>
            </button>

            <select v-model="pageSize" class="pagination-select" @change="currentPage = 1">
                <option v-for="option in [5, 10, 15, 20]" :key="option" :value="option">
                    {{ option }} / trang
                </option>
            </select>
        </div>
    </div>
</template>
<style>
.filter-container {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 10px;
}

.pagination-container {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 20px;
    gap: 10px;
}

.pagination-pages {
    display: flex;
    gap: 5px;
}

.pagination-btn {
    width: 40px;
    height: 40px;
    border: none;
    border-radius: 8px;
    background-color: #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.pagination-btn:hover {
    background-color: #dcdcdc;
}

.pagination-btn:disabled {
    background-color: #e0e0e0;
    cursor: not-allowed;
}

.pagination-page {
    width: 40px;
    height: 40px;
    border: none;
    border-radius: 8px;
    background-color: #f8f9fa;
    color: #007bff;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.pagination-page:hover {
    background-color: #e0e0e0;
}

.pagination-page.active {
    background-color: #007bff;
    color: white;
}

.pagination-select {
    width: 100px;
    height: 40px;
    border-radius: 8px;
    padding: 5px;
    border: 1px solid #ccc;
    cursor: pointer;
}
</style>
