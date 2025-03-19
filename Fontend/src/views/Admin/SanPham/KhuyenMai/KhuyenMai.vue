<script setup>
import { onMounted, ref, computed, watch } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
const statusFilter = ref("");
const khuyenMaiList = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const searchQuery = ref("");
const startDate = ref("");  // Ngày bắt đầu
const endDate = ref("");    // Ngày kết thúc
const router = useRouter();
const urlKhuyenMai = "http://localhost:8080/khuyen-mai";

const currentPage = ref(1);
const pageSize = 5;

// const fetchKhuyenMai = async () => {
//     loading.value = true;
//     errorMessage.value = "";
//     try {
//         // Xử lý giá trị của trangThai: nếu filter trạng thái, truyền đúng giá trị true/false, nếu không thì undefined
//         const params = {
//             keyword: searchQuery.value,
//             startDate: startDate.value,
//             endDate: endDate.value,
//             trangThai: statusFilter.value !== "" ? (statusFilter.value === "true") : undefined // Lọc trạng thái nếu có
//         };

//         const response = await axios.get(urlKhuyenMai, { params });

//         if (response.data && Array.isArray(response.data)) {
//             khuyenMaiList.value = response.data.map(km => ({
//                 ...km,
//                 ngayBatDau: km.ngayBatDau ? new Date(km.ngayBatDau).toLocaleDateString() : "Không có",
//                 ngayKetThuc: km.ngayKetThuc ? new Date(km.ngayKetThuc).toLocaleDateString() : "Không có"
//             }));
//         } else {
//             throw new Error("API trả về dữ liệu không hợp lệ");
//         }
//     } catch (error) {
//         errorMessage.value = "Lỗi khi tải dữ liệu khuyến mãi. Vui lòng thử lại!";
//         console.error("API Error: ", error);
//     } finally {
//         loading.value = false;
//     }
// };
const fetchKhuyenMai = async () => {
    loading.value = true;
    errorMessage.value = "";
    try {
        // Xử lý giá trị của trangThai: nếu filter trạng thái, truyền đúng giá trị true/false, nếu không thì undefined
        const params = {
            keyword: searchQuery.value,
            startDate: startDate.value,  // Lọc theo ngày bắt đầu
            endDate: endDate.value,      // Lọc theo ngày kết thúc
            trangThai: statusFilter.value !== "" ? (statusFilter.value === "true") : undefined // Lọc trạng thái nếu có
        };

        const response = await axios.get(urlKhuyenMai, { params });

        if (response.data && Array.isArray(response.data)) {
            khuyenMaiList.value = response.data.map(km => ({
                ...km,
                ngayBatDau: km.ngayBatDau ? new Date(km.ngayBatDau).toLocaleDateString() : "Không có",
                ngayKetThuc: km.ngayKetThuc ? new Date(km.ngayKetThuc).toLocaleDateString() : "Không có"
            }));
        } else {
            throw new Error("API trả về dữ liệu không hợp lệ");
        }
    } catch (error) {
        errorMessage.value = "Lỗi khi tải dữ liệu khuyến mãi. Vui lòng thử lại!";
        console.error("API Error: ", error);
    } finally {
        loading.value = false;
    }
};



// Xuất excel
const exportToExcel = () => {
    if (khuyenMaiList.value.length === 0) {
        alert("Không có dữ liệu để xuất!");
        return;
    }

    const exportData = khuyenMaiList.value.map((km, index) => ({
        STT: index + 1,
        "Mã khuyến mãi": km.maKhuyenMai,
        "Tên khuyến mãi": km.tenKhuyenMai,
        "Ngày bắt đầu": km.ngayBatDau,
        "Ngày kết thúc": km.ngayKetThuc,
        "Phần trăm giảm giá": km.phanTramGiamGia,
        "Ngày tạo": km.ngayTao,
        "Ngày sửa": km.ngaySua,
        "Trạng thái": km.trangThai ? "Hoạt động" : "Ngừng áp dụng"
    }));

    const ws = XLSX.utils.json_to_sheet(exportData);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "DanhSachKhuyenMai");

    ws['!cols'] = [
        { wch: 5 },
        { wch: 25 },
        { wch: 25 },
        { wch: 15 },
        { wch: 15 },
        { wch: 20 },
        { wch: 15 },
        { wch: 15 },
        { wch: 15 }
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

    ws['!autofilter'] = { ref: `A1:I1` };

    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const data = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(data, "DanhSachKhuyenMai.xlsx");
};

// Gọi API tìm kiếm khi người dùng nhập vào ô tìm kiếm
watch([searchQuery, startDate, endDate, statusFilter], () => {
    setTimeout(fetchKhuyenMai, 300);
});

const paginatedKhuyenMaiList = computed(() => {
    const startIndex = (currentPage.value - 1) * pageSize;
    return khuyenMaiList.value.slice(startIndex, startIndex + pageSize);
});

const totalPages = computed(() => {
    return Math.ceil(khuyenMaiList.value.length / pageSize);
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

const handleDeleteKhuyenMai = async (id) => {
    if (confirm("Bạn có chắc chắn muốn xoá khuyến mãi này?")) {
        try {
            await axios.delete(`${urlKhuyenMai}/${id}`);
            alert("Xoá khuyến mãi thành công!");
            await fetchKhuyenMai();
        } catch (error) {
            console.error("Lỗi khi xoá khuyến mãi:", error);
            alert("Lỗi khi xoá khuyến mãi. Vui lòng thử lại!");
        }
    }
};

const handleEditKhuyenMai = (id) => {
    router.push(`/admin/khuyen-mai/manage/update-khuyenmai/${id}`);
};

const handleAddKhuyenMai = () => {
    router.push('/admin/khuyen-mai/manage/add-khuyenmai');
};

onMounted(fetchKhuyenMai);
</script>

<template>
    <div class="p-4" style="min-height: 450px;">
        <h1 class="mb-4 text-center">Quản lý khuyến mãi</h1>

        <div class="mb-3 d-flex justify-content-between align-items-center">
    <div class="d-flex w-45">
        <input v-model="searchQuery" class="form-control me-2" type="text" placeholder="Tìm kiếm khuyến mãi theo mã..." />
        <button class="btn btn-secondary" @click="fetchKhuyenMai">Tìm kiếm</button>
    </div>
    <div class="d-flex">
        <input v-model="startDate" type="date" class="form-control me-2" placeholder="Ngày bắt đầu" />
        <input v-model="endDate" type="date" class="form-control me-2" placeholder="Ngày kết thúc" />
        <!-- Thêm phần lọc trạng thái -->
        <select v-model="statusFilter" class="form-control me-2">
            <option value="">Chọn trạng thái</option>
            <option value="true">Hoạt động</option>
            <option value="false">Ngừng hoạt động</option>
        </select>
        <button class="btn btn-success me-2" @click="handleAddKhuyenMai">Thêm mới</button>
    </div>
</div>



        <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>
        <div v-if="errorMessage" class="alert alert-danger text-center">{{ errorMessage }}</div>

        <table v-if="!loading && paginatedKhuyenMaiList.length > 0" class="table table-striped table-hover">
            <thead class="table-dark">
                <tr class="text-center">
                    <th>STT</th>
                    <th>Mã khuyến mãi</th>
                    <th>Tên khuyến mãi</th>
                    <th>Ngày bắt đầu</th>
                    <th>Ngày kết thúc</th>
                    <th>Phần trăm giảm giá</th>
                    <th>Ngày tạo</th>
                    <th>Ngày sửa</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(khuyenMai, index) in paginatedKhuyenMaiList" :key="khuyenMai.id" class="align-middle">
                    <td class="text-center">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                    <td>{{ khuyenMai.maKhuyenMai }}</td>
                    <td>{{ khuyenMai.tenKhuyenMai }}</td>
                    <td>{{ khuyenMai.ngayBatDau }}</td>
                    <td>{{ khuyenMai.ngayKetThuc }}</td>
                    <td>{{ khuyenMai.phanTramGiamGia }}%</td>
                    <td class="text-center">{{ khuyenMai.ngayTao }}</td>
                    <td class="text-center">{{ khuyenMai.ngaySua }}</td>
                    <td class="text-center">
                        <span class="badge" :class="khuyenMai.trangThai ? 'bg-success' : 'bg-danger'">
                            {{ khuyenMai.trangThai ? 'Hoạt động' : 'Ngừng áp dụng' }}
                        </span>
                    </td>
                    <td class="text-center">
                        <div class="d-flex justify-content-center gap-2">
                            <button class="btn btn-warning btn-sm" @click="handleEditKhuyenMai(khuyenMai.id)">Sửa</button>
                            <button class="btn btn-danger btn-sm" @click="handleDeleteKhuyenMai(khuyenMai.id)">Xoá</button>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>

        <div v-else-if="!loading && paginatedKhuyenMaiList.length === 0" class="alert alert-warning text-center">
            Không có khuyến mãi nào!
        </div>

        <!-- Phân trang -->
        <div v-if="totalPages > 1" class="mt-3 d-flex justify-content-center">
            <button class="btn btn-primary me-2" @click="prevPage" :disabled="currentPage === 1">← Trang trước</button>
            <span class="align-self-center">Trang {{ currentPage }} / {{ totalPages }}</span>
            <button class="btn btn-primary ms-2" @click="nextPage" :disabled="currentPage === totalPages">Trang sau →</button>
        </div>
    </div>
</template>
