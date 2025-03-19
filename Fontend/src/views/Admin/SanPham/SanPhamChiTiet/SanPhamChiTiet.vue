<script setup>
import { onMounted, ref, computed, watch } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";

// Khai báo các biến cần thiết trong hàm setup
const selectedTrangThai = ref(null); // Trạng thái lọc (Hoạt động/Ngừng bán)
const selectedStartDate = ref("");  // Lọc theo ngày bắt đầu
const selectedEndDate = ref("");    // Lọc theo ngày kết thúc
const selectedSize = ref("");       // Lọc theo size
const selectedMauSac = ref("");     // Lọc theo màu sắc

// Các danh sách bộ lọc
const sizeList = ref([]);     // Khai báo biến size
const colorList = ref([]);    // Khai báo biến màu sắc

// Các biến reactivity khác
const sanPhamList = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const searchQuery = ref("");
const router = useRouter();
const urlSanPhamChiTiet = "http://localhost:8080/san-pham-chi-tiet";

// Các biến phân trang
const currentPage = ref(1);
const pageSize = ref(5);
const totalItems = ref(0); // Tổng số sản phẩm từ API


// Lấy dữ liệu bộ lọc
const fetchFilterData = async () => {
    try {
        const [sizeRes, colorRes] = await Promise.all([
            axios.get("http://localhost:8080/size"),
            axios.get("http://localhost:8080/mau-sac")
        ]);
        sizeList.value = sizeRes.data || [];
        colorList.value = colorRes.data || [];
    } catch (error) {
        console.error("Lỗi khi tải dữ liệu bộ lọc:", error);
    }
};

// Hàm xuất Excel
const exportToExcel = () => {
    if (sanPhamList.value.length === 0) {
        alert("Không có dữ liệu để xuất!");
        return;
    }

    const exportData = sanPhamList.value.map((sp, index) => ({
        STT: index + 1,
        "Mã SPCT": sp.maSPCT,
        "Tên sản phẩm": sp.tenSanPham,
        "Màu sắc": sp.tenMau || "Không có",
        "Khuyến mãi": sp.tenKhuyenMai || "Không có",
        "Phần trăm giảm giá": sp.phanTramGiamGia ? sp.phanTramGiamGia + "%" : "0%",
        "Ngày bắt đầu": sp.ngayBatDau,
        "Ngày kết thúc": sp.ngayKetThuc,
        "Size": sp.tenSize || "Không có",
        "Giá bán": sp.giaBan ? sp.giaBan.toLocaleString() + " đ" : "Không có",
        "Số lượng": sp.soLuong,
        "Trạng thái": sp.trangThai ? "Hoạt động" : "Ngừng bán"
    }));

    const ws = XLSX.utils.json_to_sheet(exportData);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "DanhSachSanPham");

    ws['!cols'] = [
        { wch: 5 },  // STT
        { wch: 20 }, // Mã SPCT
        { wch: 25 }, // Tên sản phẩm
        { wch: 20 }, // Màu sắc
        { wch: 20 }, // Khuyến mãi
        { wch: 25 }, // Phần trăm giảm giá
        { wch: 15 }, // Ngày bắt đầu
        { wch: 15 }, // Ngày kết thúc
        { wch: 15 }, // Size
        { wch: 15 }, // Giá bán
        { wch: 15 }, // Số lượng
        { wch: 15 }  // Trạng thái
    ];

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

    ws['!autofilter'] = { ref: `A1:N1` };

    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const data = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(data, "DanhSachSanPhamChiTiet.xlsx");
};

// Hàm định dạng ngày bắt đầu
const formatDateTime = (dateStr) => {
    if (!dateStr) return null;
    const date = new Date(dateStr);
    return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
};

// Hàm định dạng ngày kết thúc
const formatEndDateTime = (dateStr) => {
    if (!dateStr) return null;
    const date = new Date(dateStr);
    return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
};

const fetchSanPhamChiTiet = async () => {
    loading.value = true;
    errorMessage.value = "";
    console.log("🚀 Trạng thái trước khi gửi API:", selectedTrangThai.value);
    const params = {
        page: currentPage.value - 1,
        size: pageSize.value,
        startDate: selectedStartDate.value ? selectedStartDate.value + "T00:00:00" : null,
        endDate: selectedEndDate.value ? selectedEndDate.value + "T23:59:59" : null,
    trangThai: selectedTrangThai.value !== null ? (selectedTrangThai.value ? "true" : "false") : null,
    };

    if (searchQuery.value.trim()) params.keyword = searchQuery.value.trim();
    if (selectedSize.value) params.sizeId = selectedSize.value;
    if (selectedMauSac.value) params.colorId = selectedMauSac.value;

    console.log("📢 Gửi yêu cầu API với params:", params); // Kiểm tra tham số gửi đi
    try {
        const response = await axios.get(urlSanPhamChiTiet, { params });
        console.log("📢 Phản hồi từ API:", response.data);
        sanPhamList.value = response.data.content || response.data;
        totalItems.value = response.data.totalElements || response.data.length;
    } catch (error) {
        console.error("❌ Lỗi khi tải dữ liệu từ API:", error);
        errorMessage.value = "Lỗi khi tải dữ liệu sản phẩm. Vui lòng thử lại!";
    } finally {
        loading.value = false;
    }
};



// Theo dõi thay đổi của bộ lọc và tìm kiếm


// Hàm xử lý khi thay đổi pageSize
const onPageSizeChange = () => {
    currentPage.value = 1;  // Đặt lại trang hiện tại về trang đầu tiên
    fetchSanPhamChiTiet();  // Tải lại dữ liệu sản phẩm theo pageSize mới
};

// Tính toán danh sách sản phẩm theo phân trang
const paginatedSanPhamList = computed(() => {
    const startIndex = (currentPage.value - 1) * pageSize;  // Tính chỉ số bắt đầu của sản phẩm trên trang hiện tại
    return sanPhamList.value.slice(startIndex, startIndex + pageSize);  // Trả về danh sách sản phẩm của trang hiện tại
});

const totalPages = computed(() => {
    return totalItems.value ? Math.ceil(totalItems.value / pageSize.value) : 1;
});



// Hiển thị danh sách trang hợp lý (hiển thị tối đa 5 trang)
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
        currentPage.value++;  // Chuyển sang trang tiếp theo
        fetchSanPhamChiTiet();  // Tải lại dữ liệu cho trang mới
    }
};

const prevPage = () => {
    if (currentPage.value > 1) {
        currentPage.value--;  // Chuyển về trang trước
        fetchSanPhamChiTiet();  // Tải lại dữ liệu cho trang trước
    }
};

const changePage = (page) => {
    if (page !== currentPage.value) {
        currentPage.value = page;
        fetchSanPhamChiTiet();
    }
};

watch(
    [searchQuery, selectedTrangThai, selectedStartDate, selectedEndDate, selectedSize, selectedMauSac],
    () => {
        currentPage.value = 1; // Reset trang về đầu tiên khi lọc
        fetchSanPhamChiTiet();
    }
);



const handleEditSanPhamChiTiet = (id) => {
    router.push(`/admin/products/details/update-spct/${id}`);
};

const handleAddSanPhamChiTiet = () => {
    router.push("/admin/products/details/add-spct");
};

const handleViewSanPhamChiTiet = (id) => {
    router.push(`/admin/products/details/view-spct/${id}`);
};

// Lấy dữ liệu khi component được mount
onMounted(() => {
    fetchFilterData();
    fetchSanPhamChiTiet();
});
</script>
<template>
    <div class="p-4" style="min-height: 450px">
        <h1 class="mb-4 text-center">Quản lý sản phẩm chi tiết</h1>

        <!-- Thanh tìm kiếm và nút thêm mới -->
        <div class="mb-3 d-flex justify-content-between align-items-center">
            <div class="d-flex w-50">
                <input v-model="searchQuery" class="form-control me-2" type="text" placeholder="Tìm kiếm sản phẩm theo tên..." />
            </div>
            <button class="btn btn-info ms-2" @click="exportToExcel">
                <i class="bi bi-file-earmark-excel"></i> Xuất Excel
            </button>
            <button class="btn btn-success" @click="handleAddSanPhamChiTiet">
                <i class="bi bi-plus-circle"></i> Thêm mới
            </button>
        </div>

        <!-- Bộ lọc -->
        <div class="filter-container">
            <div class="filter-item">
                <label class="form-label fw-bold">Trạng thái</label>
                <select v-model="selectedTrangThai" class="form-select">
                    <option :value="null">Tất cả</option>
                    <option :value="true">Hoạt động</option>
                    <option :value="false">Ngừng bán</option>
                </select>
            </div>
            <div class="filter-item">
                <label class="form-label fw-bold">Ngày bắt đầu</label>
                <input v-model="selectedStartDate" type="date" class="form-control" />
            </div>
            <div class="filter-item">
                <label class="form-label fw-bold">Ngày kết thúc</label>
                <input v-model="selectedEndDate" type="date" class="form-control" />
            </div>
            <div class="filter-item">
                <label class="form-label fw-bold">Size</label>
                <select v-model="selectedSize" class="form-select">
                    <option value="">Tất cả</option>
                    <option v-for="size in sizeList" :key="size.id" :value="size.id">{{ size.tenSize }}</option>
                </select>
            </div>
            <div class="filter-item">
                <label class="form-label fw-bold">Màu sắc</label>
                <select v-model="selectedMauSac" class="form-select">
                    <option value="">Tất cả</option>
                    <option v-for="color in colorList" :key="color.id" :value="color.id">{{ color.tenMau }}</option>
                </select>
            </div>
        </div>

        <!-- Bảng sản phẩm -->
        <table v-if="!loading && sanPhamList.length > 0" class="table table-striped table-hover">
            <thead class="table-dark">
                <tr class="text-center">
                    <th>STT</th>
                    <th>Mã SPCT</th>
                    <th>Tên sản phẩm</th>
                    <th>Màu sắc</th>
                    <th>Danh mục</th> <!-- New Column: Danh Mục -->
                    <th>Đế giày</th>  <!-- New Column: Đế Giày -->
                    <th>Chất liệu</th> <!-- New Column: Chất Liệu -->
                    <th>Thương hiệu</th> <!-- New Column: Thương Hiệu -->
                    <th>Phần trăm giảm giá</th>
                    <th>Ngày bắt đầu</th>
                    <th>Ngày kết thúc</th>
                    <th>Size</th>
                    <th>Số lượng</th>
                    <th>Giá bán</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(spct, index) in sanPhamList" :key="spct.id" class="align-middle">
                    <td class="text-center">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                    <td>{{ spct.maSPCT }}</td>
                    <td>{{ spct.tenSanPham }}</td>
                    <td>{{ spct.tenMau || "Không có" }}</td>
                    <td class="text-center">{{ spct.tenDeGiay ? spct.tenDeGiay : "Không có" }}</td>
                    <td class="text-center">{{ spct.tenThuongHieu ? spct.tenThuongHieu : "Không có" }}</td>
                    <td class="text-center">{{ spct.tenDanhMuc ? spct.tenDanhMuc : "Không có" }}</td>
                    <td class="text-center">{{ spct.tenChatLieu ? spct.tenChatLieu : "Không có" }}</td>
                    <td>{{ spct.phanTramGiamGia ? spct.phanTramGiamGia + "%" : "0%" }}</td>
                    <td class="text-center">{{ formatDateTime(spct.ngayBatDau) }}</td>
                    <td class="text-center">{{ formatEndDateTime(spct.ngayKetThuc) }}</td>
                    <td class="text-center">{{ spct.tenSize || "Không có" }}</td>
                    <td class="text-center">{{ spct.soLuong }}</td>
                    <td class="text-center">{{ spct.giaBan.toLocaleString() }} đ</td>
                    <td class="text-center">
                        <span class="badge" :class="spct.trangThai ? 'bg-success' : 'bg-danger'">
                            {{ spct.trangThai ? "Hoạt động" : "Ngừng bán" }}
                        </span>
                    </td>
                    <td class="text-center">
                        <div class="d-flex justify-content-center gap-2">
                            <button class="btn btn-warning btn-sm" @click="handleEditSanPhamChiTiet(spct.id)">
                                <i class="bi bi-pencil-square"></i>
                            </button>
                            <button class="btn btn-info btn-sm" @click="handleViewSanPhamChiTiet(spct.id)">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>

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

            <select v-model="pageSize" class="pagination-select" @change="onPageSizeChange">
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
    gap: 20px;
    margin-bottom: 20px;
}

.filter-item {
    flex: 1 1 200px;
}

.filter-item label {
    display: block;
    margin-bottom: 5px;
}

.filter-item select,
.filter-item input {
    width: 100%;
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
