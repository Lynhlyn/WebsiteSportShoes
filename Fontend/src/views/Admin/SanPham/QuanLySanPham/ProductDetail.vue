<script setup>
import { ref, onMounted, computed,watch } from "vue";
import { useRoute,useRouter } from "vue-router";
import axios from "axios";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
const route = useRoute();
const router = useRouter();
const sanPhamChiTietList = ref([]);
const defaultImage = "https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg";
// Khai báo các biến cần thiết trong hàm setup
const selectedTrangThai = ref(null ); // Trạng thái lọc (Hoạt động/Ngừng bán)
const selectedStartDate = ref("");  // Lọc theo ngày bắt đầu
const selectedEndDate = ref("");    // Lọc theo ngày kết thúc
const selectedSize = ref("");       // Lọc theo size
const selectedMauSac = ref("");     // Lọc theo màu sắc
const selectedSanPhamId = ref(null);
// Các danh sách bộ lọc
const sizeList = ref([]);     // Khai báo biến size
const colorList = ref([]);    // Khai báo biến màu sắc
// Phân trang
const currentPage = ref(1);
const itemsPerPage = ref(5); // Số sản phẩm hiển thị trên mỗi trang

// Tính tổng số trang
const totalPages = computed(() => Math.ceil(sanPhamChiTietList.value.length / itemsPerPage.value));

// Lọc danh sách theo trang hiện tại
const paginatedList = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    const end = start + itemsPerPage.value;
    return sanPhamChiTietList.value.slice(start, end);
});

const fetchSanPhamChiTiet = async () => {
    try {
        const productId = route.params.id;
        if (!productId) {
            console.error("ID sản phẩm không hợp lệ");
            return;
        }

        console.log("Fetching data for product ID:", productId);

        const spctRes = await axios.get("http://localhost:8080/san-pham-chi-tiet/san-pham-chi-tiet", {
            params: { idSanPham: productId }
        });

        console.log("Dữ liệu sản phẩm chi tiết API trả về:", spctRes.data);

        sanPhamChiTietList.value = spctRes.data || [];
    } catch (error) {
        console.error("Lỗi khi tải sản phẩm chi tiết:", error);
        sanPhamChiTietList.value = [];
    }
};
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
const filterMS = async () => {
    try {
        const productId = route.params.id;
        if (!productId) return;

        // Khởi tạo params
        const params = { idSanPham: Number(productId) };

        if (selectedSize.value) params.idSize = Number(selectedSize.value);
        if (selectedMauSac.value) params.idMauSac = Number(selectedMauSac.value);
        if (selectedTrangThai.value !== null) params.trangThai = selectedTrangThai.value ? 1 : 0;
        if (selectedStartDate.value) params.ngayBatDau = selectedStartDate.value;
        if (selectedEndDate.value) params.ngayKetThuc = selectedEndDate.value;

        console.log("Gửi yêu cầu lọc với params:", params);

        const response = await axios.get("http://localhost:8080/san-pham-chi-tiet/loc", { params });

        console.log("Dữ liệu sản phẩm sau khi lọc:", response.data);
        sanPhamChiTietList.value = response.data || [];
    } catch (error) {
        console.error("Lỗi khi lọc sản phẩm:", error);
        sanPhamChiTietList.value = [];
    }
};




// Tự động lọc khi chọn size/màu



const formatCurrency = (price) => {
    return price ? Number(price).toLocaleString("vi-VN", { style: "currency", currency: "VND" }) : "0 VND";
};
const exportToExcel = () => {
    if (sanPhamChiTietList.value.length === 0) {
        alert("Không có dữ liệu để xuất!");
        return;
    }

    const exportData = sanPhamChiTietList.value.map((sp, index) => ({
        STT: index + 1,
        "Mã SPCT": sp.maSPCT || "Không có",
        "Tên sản phẩm": sp.sanPham?.tenSanPham || "Không có",
        "Màu sắc": sp.mauSac?.tenMau || "Không có",
        "Size": sp.size?.tenSize || "Không có",
        "Khuyến mãi": sp.khuyenMai?.tenKhuyenMai || "Không có",
        "Phần trăm giảm giá": sp.khuyenMai?.phanTramGiamGia ? sp.khuyenMai.phanTramGiamGia + "%" : "0%",
        "Ngày bắt đầu": sp.khuyenMai?.ngayBatDau || "Không có",
        "Ngày kết thúc": sp.khuyenMai?.ngayKetThuc || "Không có",
        "Giá bán": sp.giaBan ? sp.giaBan.toLocaleString() + " đ" : "Không có",
        "Số lượng": sp.soLuong || "0",
        "Trạng thái": sp.trangThai ? "Hoạt động" : "Ngừng bán"
    }));

    const ws = XLSX.utils.json_to_sheet(exportData);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "DanhSachSanPham");

    ws['!cols'] = [
        { wch: 5 },  // STT
        { wch: 20 }, // Mã SPCT
        { wch: 25 }, // Tên sản phẩm
        { wch: 15 }, // Màu sắc
        { wch: 10 }, // Size
        { wch: 20 }, // Khuyến mãi
        { wch: 15 }, // Phần trăm giảm giá
        { wch: 15 }, // Ngày bắt đầu
        { wch: 15 }, // Ngày kết thúc
        { wch: 15 }, // Giá bán
        { wch: 10 }, // Số lượng
        { wch: 15 }  // Trạng thái
    ];

    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const data = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(data, "DanhSachSanPhamChiTiet.xlsx");
};


// Chuyển trang
const changePage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
    }
};
const handleEditSanPhamChiTiet = (id) => {
    router.push(`/admin/products/details/update-spct/${id}`);
};
watch([selectedTrangThai, selectedStartDate, selectedEndDate, selectedMauSac, selectedSize], () => {
    if (
        !selectedMauSac.value &&
        !selectedSize.value &&
        selectedTrangThai.value === null &&
        !selectedStartDate.value &&
        !selectedEndDate.value
    ) {
        fetchSanPhamChiTiet(); // Gọi lại API khi tất cả bộ lọc bị xóa
    } else {
        filterMS(); // Gọi API lọc nếu có bất kỳ bộ lọc nào thay đổi
    }
});


onMounted(() => {
    selectedSanPhamId.value = route.params.id; 
    fetchFilterData();
    fetchSanPhamChiTiet();
});

</script>


<template>
    <div class="container">
        <h2>Chi tiết sản phẩm</h2>

        <!-- Thanh tìm kiếm và nút thêm mới -->
        <div class="mb-3 d-flex justify-content-between align-items-center">
            <div class="d-flex w-50">
                
            </div>
            <div>
                <button class="btn btn-info ms-2" @click="exportToExcel">
                <i class="bi bi-file-earmark-excel"></i> Xuất Excel
            </button>
                
            </div>
        </div>

        <!-- Bộ lọc -->
        <div class="d-flex flex-wrap gap-3 mb-3">
            <div>
                <label class="form-label fw-bold">Trạng thái</label>
                <select v-model="selectedTrangThai" class="form-select">
                    <option :value="null">Tất cả</option>
                    <option :value="true">Hoạt động</option>
                    <option :value="false">Ngừng bán</option>
                </select>
            </div>
            <div>
                <label class="form-label fw-bold">Ngày bắt đầu</label>
                <input v-model="selectedStartDate" type="date" class="form-control" />
            </div>
            <div>
                <label class="form-label fw-bold">Ngày kết thúc</label>
                <input v-model="selectedEndDate" type="date" class="form-control" />
            </div>
            <div>
                <label class="form-label fw-bold">Size</label>
                <select v-model="selectedSize" class="form-select">
                    <option value="">Tất cả</option>
                    <option v-for="size in sizeList" :key="size.id" :value="size.id">{{ size.tenSize }}</option>
                </select>
            </div>
            <div>
                <label class="form-label fw-bold">Màu sắc</label>
                <select v-model="selectedMauSac" class="form-select">
                    <option value="">Tất cả</option>
                    <option v-for="color in colorList" :key="color.id" :value="color.id">{{ color.tenMau }}</option>
                </select>
            </div>
        </div>

        <!-- Bảng danh sách sản phẩm chi tiết -->
        <div v-if="sanPhamChiTietList.length === 0" class="alert alert-warning">
            Không có dữ liệu sản phẩm chi tiết.
        </div>

        <table v-else class="table table-bordered">
    <thead class="table-dark">
        <tr>
            <th>#</th>
            <th>Ảnh</th>
            <th>Mã</th>
            <th>Thương Hiệu</th>
            <th>Danh mục</th>
            <th>Đế giày</th>
            <th>Chất liệu</th>
            <th>Màu sắc</th>
            <th>Size</th>
            <th>Giá bán</th>
            <th>Số lượng</th> 
            <th>Trạng thái</th>
            <th>Hành động</th>
        </tr>
    </thead>
    <tbody>
        <tr v-for="(spct, index) in sanPhamChiTietList" :key="spct.id">
            <td>{{ index + 1 }}</td>
            <td>
                <img :src="spct.sanPham?.anhDauTien || defaultImage" alt="Ảnh sản phẩm" width="50" height="50">
            </td>
            <td>{{ spct.maSPCT || "Không có" }}</td>
            <td>{{ spct.sanPham?.thuongHieu?.tenThuongHieu || 'Không có' }}</td>
            <td>{{ spct.sanPham?.danhMuc?.tenDanhMuc || 'Không có' }}</td>
            <td>{{ spct.sanPham?.deGiay?.tenDeGiay || 'Không có' }}</td>
            <td>{{ spct.sanPham?.chatLieu?.tenChatLieu || 'Không có' }}</td>
            <td>{{ spct.mauSac?.tenMau || "Không có" }}</td>
            <td>{{ spct.size?.tenSize || "Không có" }}</td>
            <td>{{ formatCurrency(spct.giaBan) }}</td>
            <td>{{ spct.soLuong || "0" }}</td> <!-- Hiển thị số lượng -->
            <td>
                <span class="badge" :class="spct.trangThai ? 'bg-success' : 'bg-danger'">
                    {{ spct.trangThai ? "Hoạt động" : "Ngừng hoạt động" }}
                </span>
            </td>
            <td>
                <button class="btn btn-primary btn-sm" @click="handleEditSanPhamChiTiet(spct.id)">
                    <i class="bi bi-pencil"></i>
                </button>
            </td>
        </tr>
    </tbody>
</table>


        <button @click="$router.push('/admin/products/manage')" class="btn btn-secondary">Quay lại</button>
    </div>
</template>

