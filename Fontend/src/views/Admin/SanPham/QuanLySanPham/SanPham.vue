<script setup>
import { onMounted, ref, computed, watch } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";


const sanPhamList = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const searchQuery = ref("");
const router = useRouter();
const urlSanPham = "http://localhost:8080/san-pham";

const currentPage = ref(1);
const pageSize = ref(5); // Số sản phẩm trên mỗi trang

// Bộ lọc
const selectedTrangThai = ref("");
const selectedDanhMuc = ref("");
const selectedThuongHieu = ref("");
const selectedChatLieu = ref("");
const selectedDeGiay = ref("");

const danhMucList = ref([]);
const thuongHieuList = ref([]);
const chatLieuList = ref([]);
const deGiayList = ref([]);

const fetchFilterData = async () => {
    try {
        const [danhMucRes, thuongHieuRes, chatLieuRes, deGiayRes] = await Promise.all([
            axios.get("http://localhost:8080/danh-muc"),
            axios.get("http://localhost:8080/thuong-hieu"),
            axios.get("http://localhost:8080/chat-lieu"),
            axios.get("http://localhost:8080/de-giay")
        ]);

        danhMucList.value = danhMucRes.data || [];
        thuongHieuList.value = thuongHieuRes.data || [];
        chatLieuList.value = chatLieuRes.data || [];
        deGiayList.value = deGiayRes.data || [];
    } catch (error) {
        console.error("Lỗi khi tải dữ liệu bộ lọc: ", error);
    }
};

const fetchSanPham = async () => {
    loading.value = true;
    errorMessage.value = "";
    try {
        const response = await axios.get(urlSanPham, {
            params: searchQuery.value ? { keyword: searchQuery.value } : {}
        });

        if (response.data && Array.isArray(response.data)) {
            sanPhamList.value = response.data.map(sp => ({
                ...sp,
                ngayTao: sp.ngayTao ? new Date(sp.ngayTao).toLocaleDateString("vi-VN") : "Không có",
                ngaySua: sp.ngaySua ? new Date(sp.ngaySua).toLocaleDateString("vi-VN") : "Không có",
                urlAnh: sp.anhDauTien && isValidURL(sp.anhDauTien)
                    ? sp.anhDauTien
                    : 'https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg'
            }));
        } else {
            throw new Error("API trả về dữ liệu không hợp lệ");
        }
    } catch (error) {
        console.error("Lỗi khi tải danh sách sản phẩm: ", error);
        errorMessage.value = "Không thể tải danh sách sản phẩm.";
    } finally {
        loading.value = false;
    }
};
//Xuat excel
const exportToExcel = () => {
    if (sanPhamList.value.length === 0) {
        alert("Không có dữ liệu để xuất!");
        return;
    }

    const exportData = sanPhamList.value.map((sp, index) => ({
        STT: index + 1,
        "Tên sản phẩm": sp.tenSanPham,
        "Mô tả": sp.moTa,
        "Danh Mục": sp.danhMuc || "Không có",
        "Thương Hiệu": sp.thuongHieu || "Không có",
        "Chất liệu": sp.chatLieu || "Không có",
        "Đế giày": sp.deGiay || "Không có",
        "Ngày tạo": sp.ngayTao,
        "Ngày sửa": sp.ngaySua,
        "Trạng thái": sp.trangThai ? "Hoạt động" : "Ngừng bán"
    }));

    // Tạo worksheet và workbook
    const ws = XLSX.utils.json_to_sheet(exportData);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "DanhSachSanPham");

    // Định dạng cột để không bị mất chữ
    ws['!cols'] = [
        { wch: 5 },  // STT
        { wch: 25 }, // Tên sản phẩm
        { wch: 25 }, // Mô tả
        { wch: 20 }, // Danh Mục
        { wch: 20 }, // Thương Hiệu
        { wch: 20 }, // Chất liệu
        { wch: 20 }, // Đế giày
        { wch: 15 }, // Ngày tạo
        { wch: 15 }, // Ngày sửa
        { wch: 15 }  // Trạng thái
    ];

    // Bôi đậm và căn giữa tiêu đề
    const range = XLSX.utils.decode_range(ws['!ref']);
    for (let col = range.s.c; col <= range.e.c; col++) {
        const cellAddress = XLSX.utils.encode_cell({ r: 0, c: col });
        if (ws[cellAddress]) {
            ws[cellAddress].s = {
                font: { bold: true },
                alignment: { horizontal: "center", vertical: "center" }
            };
        }
    }

    // Thêm border cho tất cả các ô
    Object.keys(ws).forEach(cell => {
        if (cell[0] !== '!') {
            ws[cell].s = {
                border: {
                    top: { style: "thin" },
                    bottom: { style: "thin" },
                    left: { style: "thin" },
                    right: { style: "thin" }
                }
            };
        }
    });

    // Cố định hàng tiêu đề khi cuộn
    ws['!autofilter'] = { ref: `A1:J1` };

    // Xuất file Excel
    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const data = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(data, "DanhSachSanPham.xlsx");
};



// Hàm kiểm tra URL hợp lệ
const isValidURL = (url) => {
    try {
        const parsedUrl = new URL(url);
        return /\.(jpeg|jpg|png|webp|gif)$/i.test(parsedUrl.pathname);
    } catch (_) {
        return false;
    }
};


// Theo dõi thay đổi của bộ lọc và tìm kiếm
watch(
    [searchQuery, selectedTrangThai, selectedDanhMuc, selectedThuongHieu, selectedChatLieu, selectedDeGiay],
    () => {
        currentPage.value = 1;
        fetchSanPham();
    }
);

// Theo dõi thay đổi của `pageSize`
watch(pageSize, () => {
    currentPage.value = 1;
});

// Danh sách sản phẩm theo trang
const paginatedSanPhamList = computed(() => {
    const startIndex = (currentPage.value - 1) * pageSize.value;
    return sanPhamList.value.slice(startIndex, startIndex + pageSize.value);
});

// Tổng số trang
const totalPages = computed(() => {
    return Math.ceil(sanPhamList.value.length / pageSize.value);
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

// Xóa sản phẩm
const handleDeleteSanPham = async (id) => {
    if (!confirm("Bạn có chắc chắn muốn xoá sản phẩm này?")) return;

    try {
        const response = await axios.delete(`${urlSanPham}/${id}`);
        alert(response.data); // Hiển thị thông báo từ server
        fetchSanPham(); // Cập nhật danh sách
    } catch (error) {
        console.error("Lỗi khi xoá sản phẩm:", error.response?.data || error.message);
        alert(error.response?.data || "Lỗi khi xoá sản phẩm!");
    }
};

// Chỉnh sửa sản phẩm
const handleEditSanPham = (id) => {
    router.push(`/admin/products/manage/update-sanpham/${id}`);
};

// Thêm sản phẩm mới
const handleAddSanPham = () => {
    router.push('/admin/products/manage/add-sanpham');
};

onMounted(() => {
    fetchSanPham();
    fetchFilterData();
});
</script>

<template>
    <div class="p-4" style="min-height: 450px;">
        <h2 class="mb-4 text-center">Quản lý sản phẩm</h2>

        <!-- Thanh tìm kiếm và nút thêm mới -->
        <div class="mb-3 d-flex justify-content-between align-items-center">
            <div class="d-flex w-50">
                <input v-model="searchQuery" class="form-control" type="text"
                    placeholder="Tìm kiếm sản phẩm theo tên..." @click="fetchSanPham" />
            </div>
            <button class="btn btn-info ms-2" @click="exportToExcel">Xuất Excel</button>
            <button class="btn btn-success" @click="handleAddSanPham">
                <i class="bi bi-plus-circle"></i> Thêm mới
            </button>

        </div>
        <!-- Bộ lọc -->
        <div class="filter-container">
            <div class="flex-grow-1">
                <label class="form-label fw-bold">Trạng thái</label>
                <select v-model="selectedTrangThai" class="form-select">
                    <option value="">Tất cả</option>
                    <option value="true">Hoạt động</option>
                    <option value="false">Ngừng bán</option>
                </select>
            </div>
            <div class="flex-grow-1">
                <label class="form-label fw-bold">Danh Mục</label>
                <select v-model="selectedDanhMuc" class="form-select">
                    <option value="">Tất cả</option>
                    <option v-for="dm in danhMucList" :key="dm.id" :value="dm.tenDanhMuc">{{ dm.tenDanhMuc }}</option>
                </select>
            </div>
            <div class="flex-grow-1">
                <label class="form-label fw-bold">Thương hiệu</label>
                <select v-model="selectedThuongHieu" class="form-select">
                    <option value="">Tất cả</option>
                    <option v-for="th in thuongHieuList" :key="th.id" :value="th.tenThuongHieu">{{ th.tenThuongHieu }}
                    </option>
                </select>
            </div>
            <div class="flex-grow-1">
                <label class="form-label fw-bold">Chất liệu</label>
                <select v-model="selectedChatLieu" class="form-select">
                    <option value="">Tất cả</option>
                    <option v-for="cl in chatLieuList" :key="cl.id" :value="cl.tenChatLieu">{{ cl.tenChatLieu }}
                    </option>
                </select>
            </div>
            <div class="flex-grow-1">
                <label class="form-label fw-bold">Đế giày</label>
                <select v-model="selectedDeGiay" class="form-select">
                    <option value="">Tất cả</option>
                    <option v-for="dg in deGiayList" :key="dg.id" :value="dg.tenDeGiay">{{ dg.tenDeGiay }}</option>
                </select>
            </div>
        </div>

        <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
        <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>

        <table v-if="!loading && paginatedSanPhamList.length > 0" class="table table-striped table-hover">
            <thead class="table-dark">
                <tr class="text-center">
                    <th>STT</th>
                    <th>Ảnh</th>
                    <th>Tên sản phẩm</th>
                    <th>Mô tả</th>
                    <th>Danh Mục</th>
                    <th>Thương Hiệu</th>
                    <th>Chất liệu</th>
                    <th>Đế giày</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(sanPham, index) in paginatedSanPhamList" :key="sanPham.id" class="align-middle">
                    <td class="text-center">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                    <td class="text-center">
                        <img :src="sanPham.urlAnh" alt="Ảnh sản phẩm"
                            style="width: 120px; height: 100px; object-fit: cover; border-radius: 5px;">
                    </td>
                    <td>{{ sanPham.tenSanPham }}</td>
                    <td>{{ sanPham.moTa }}</td>
                    <td>{{ sanPham.danhMuc || 'Không có' }}</td>
                    <td>{{ sanPham.thuongHieu || 'Không có' }}</td>
                    <td>{{ sanPham.chatLieu || 'Không có' }}</td>
                    <td>{{ sanPham.deGiay || 'Không có' }}</td>
                    <td class="text-center">
                        <span class="badge" :class="sanPham.trangThai ? 'bg-success' : 'bg-danger'">
                            {{ sanPham.trangThai ? 'Hoạt động' : 'Ngừng bán' }}
                        </span>
                    </td>


                    <td class="text-center">
                        <div class="d-flex justify-content-center gap-2">
                            <button class="btn btn-warning btn-sm" @click="handleEditSanPham(sanPham.id)">
                                <i class="bi bi-pencil-square"></i>
                            </button>
                            <button class="btn btn-danger btn-sm" @click="handleDeleteSanPham(sanPham.id)">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </td>

                </tr>
            </tbody>
        </table>

        <div v-else-if="!loading && paginatedSanPhamList.length === 0" class="alert alert-warning text-center">
            Không có sản phẩm nào!
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
