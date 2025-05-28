<template>
  <div class="container my-5">
    <!-- Header với tìm kiếm và bộ lọc -->
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2>Quản lý Voucher</h2>
      <button class="btn btn-primary px-4" @click="navigateToAddVoucher">+ Tạo mới</button>
    </div>

    <!-- Bộ lọc -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-3">
      <div class="d-flex flex-column flex-grow-1 me-3">
        <input type="text" v-model="searchQuery" class="form-control" placeholder="Tìm kiếm voucher theo mã, tên..."
          @input="searchVouchers" />
      </div>
      <div class="d-flex flex-column flex-grow-1 me-3">
        <div class="d-flex gap-2">
          <input type="date" v-model="startDate" class="form-control" placeholder="Từ ngày"
            @input="searchOtherFields" />
          <input type="date" v-model="endDate" class="form-control" placeholder="Đến ngày" @input="searchOtherFields" />
        </div>
      </div>
      <!-- Bộ lọc -->
      <div class="d-flex flex-wrap align-items-center gap-3">
        <div class="d-flex flex-column flex-grow-1 me-3">
          <label class="form-label fw-bold">Kiểu voucher</label>
          <select v-model="typeFilter" class="form-select" @change="searchOtherFields">
            <option value="all">Tất cả</option>
            <option value="public">Công khai</option>
            <option value="private">Cá nhân</option>
          </select>
        </div>

        <div class="d-flex flex-column flex-grow-1 me-3">
          <label class="form-label fw-bold">Trạng thái</label>
          <select v-model="statusFilter" class="form-select" @change="searchOtherFields">
            <option value="all">Tất cả</option>
            <option value="active">Đang diễn ra</option>
            <option value="expired">Đã kết thúc</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Danh sách voucher -->
    <div v-if="vouchers.length > 0" class="table-responsive">
      <table class="table table-striped table-bordered">
        <thead>
          <tr>
            <th>STT</th>
            <th>Mã</th>
            <th>Tên</th>
            <th>Kiểu</th>
            <th>Giá trị giảm</th>
            <th>Giá trị tối thiểu</th>
            <th>Giá trị giảm tối đa</th>
            <th>Số lượng</th>
            <th>Ngày Bắt Đầu</th>
            <th>Ngày Kết Thúc</th>
            <th>Trạng Thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(voucher, index) in paginatedVouchers" :key="voucher.id">
            <td>{{ index + 1 + (currentPage - 1) * pageSize }}</td>
            <td>{{ voucher.maVoucher }}</td>
            <td>{{ voucher.moTa }}</td>
            <td>
              <span class="badge badge-style"
                :class="voucher.kieuVoucher === 'public' ? 'badge-public' : 'badge-private'">
                {{ voucher.kieuVoucher === 'public' ? 'Công khai' : 'Cá nhân' }}
              </span>
            </td>
            <td>
              {{ voucher.giaTriGiam }}{{ Number(voucher.loaiVoucher) === 0 ? '%' : 'đ' }}
            </td>
            <td>{{ voucher.giaTriToiThieu }}đ</td>
            <td>{{ voucher.giaTriGiamToiDa }}đ</td>
            <td>{{ voucher.soLuong }}</td>
            <td>{{ formatDate(voucher.ngayBatDau) }}</td>
            <td>{{ formatDate(voucher.ngayKetThuc) }}</td>
            <td>
              <span class="badge badge-style" :class="voucher.trangThai === 1 ? 'badge-active' : 'badge-expired'">
                {{ voucher.trangThai === 1 ? 'Đang diễn ra' : 'Đã kết thúc' }}
              </span>
            </td>
            <td>
              <div class="d-flex justify-content-center">
                <button class="btn btn-light btn-sm" @click="navigateToEditVoucher(voucher.id)">
                  <i class="bi bi-eye"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
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

<script>
import axios from 'axios';

export default {
  data() {
    return {
      vouchers: [],
      statusFilter: 'all',
      typeFilter: 'all',
      searchQuery: '',
      startDate: '',
      endDate: '',
      currentPage: 1,
      pageSize: 5,
    };
  },
  
  computed: {
    totalPages() {
      return Math.ceil(this.vouchers.length / this.pageSize);
    },
    visiblePages() {
      let pages = [];
      let start = Math.max(1, this.currentPage - 2);
      let end = Math.min(this.totalPages, this.currentPage + 2);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      return pages;
    },
    paginatedVouchers() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      return this.vouchers.slice(startIndex, startIndex + this.pageSize);
    }
  },
  mounted() {
    this.fetchVouchers();
  },
  methods: {
    formatCurrency(value) {
      if (!value) return "0";
      return Number(value).toLocaleString("vi-VN");
    },

    async fetchVouchers() {
      try {
        let url = 'http://localhost:8080/admin/voucher';
        let response = await axios.get(url);
        const today = new Date();

        this.vouchers = response.data.map(v => {
          const startDate = new Date(v.ngayBatDau);
          const endDate = new Date(v.ngayKetThuc);
          let trangThai = 1; // Mặc định là "Đang diễn ra"

          if (v.soLuong === 0 || endDate < today) {
            trangThai = 0; // Nếu hết số lượng hoặc quá hạn, trạng thái là "Đã kết thúc"
          } else if (startDate > today) {
            trangThai = 0; // Nếu chưa đến ngày bắt đầu, trạng thái là "Chưa bắt đầu"
          }

          // Cập nhật cách tính giá trị giảm tối đa
          let giaTriGiamToiDa = v.loaiVoucher === 0
            ? (v.giaTriToiThieu * v.giaTriGiam) / 100
            : v.giaTriGiam;

          return {
            ...v,
            trangThai,
            kieuVoucher: v.kieuVoucher,
            loaiVoucher: Number(v.loaiVoucher),
            giaTriGiam: this.formatCurrency(v.giaTriGiam),
            giaTriToiThieu: this.formatCurrency(v.giaTriToiThieu),
            giaTriGiamToiDa: this.formatCurrency(giaTriGiamToiDa),
          };
        });
      } catch (error) {
        console.error('Lỗi khi lấy danh sách voucher:', error);
      }
    },

    async searchVouchers() {
      try {
        const params = {
          maVoucher: this.searchQuery || null,
          moTa: this.searchQuery || null,
          tuNgay: this.startDate || null,
          denNgay: this.endDate || null,
          kieuVoucher: this.typeFilter !== 'all' ? this.typeFilter : null,
          trangThai: this.statusFilter !== 'all' ? (this.statusFilter === 'active' ? 1 : 0) : null
        };

        const response = await axios.get('http://localhost:8080/admin/searchByMaAndMoTa', { params });

        this.vouchers = response.data.map(v => {
          let giaTriGiamToiDa = v.loaiVoucher === 0
            ? (v.giaTriToiThieu * v.giaTriGiam) / 100
            : v.giaTriGiam;

          return {
            ...v,
            trangThai: Number(v.trangThai),
            kieuVoucher: v.kieuVoucher,
            loaiVoucher: Number(v.loaiVoucher),
            giaTriGiam: this.formatCurrency(v.giaTriGiam),
            giaTriToiThieu: this.formatCurrency(v.giaTriToiThieu),
            giaTriGiamToiDa: this.formatCurrency(giaTriGiamToiDa),
          };
        });

      } catch (error) {
        console.error('Lỗi khi tìm kiếm voucher:', error);
      }
    },

    async searchOtherFields() {
      try {
        const params = {
          tuNgay: this.startDate ? this.startDate : null,  // Tìm kiếm theo ngày bắt đầu
          denNgay: this.endDate ? this.endDate : null,  // Tìm kiếm theo ngày kết thúc
          kieuVoucher: this.typeFilter === 'all' ? null : this.typeFilter,
          trangThai: this.statusFilter === 'all' ? null : this.statusFilter === 'active' ? 1 : 0,
        };

        // Gửi yêu cầu API tìm kiếm các trường còn lại
        const response = await axios.get('http://localhost:8080/admin/searchOtherFields', { params });

        // Cập nhật danh sách voucher sau khi tìm kiếm
        this.vouchers = response.data.content.map(v => ({
          ...v,
          trangThai: Number(v.trangThai),
          kieuVoucher: v.kieuVoucher,
          loaiVoucher: Number(v.loaiVoucher),
        }));
      } catch (error) {
        console.error('Lỗi khi tìm kiếm các trường còn lại:', error);
      }
    },
    navigateToAddVoucher() {
      this.$router.push('/admin/vouchers/add');
    },
    navigateToEditVoucher(id) {
      this.$router.push(`/admin/vouchers/edit/${id}`);
    },
    toggleVoucherStatus(id) {
      const voucher = this.vouchers.find(v => v.id === id);
      if (voucher) {
        voucher.trangThai = voucher.trangThai === 1 ? 0 : 1;
        axios.put(`http://localhost:8080/admin/voucher/${id}`, voucher)
          .then(() => this.fetchVouchers());
      }
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString("vi-VN");
    },
    exportExcel() {
      // Thực hiện xuất excel (có thể sử dụng thư viện như xlsx)
      console.log('Xuất Excel...');
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
    changePage(page) {
      this.currentPage = page;
    }

  },
};
</script>

<style scoped>
th {
  text-align: center !important;
  /* Căn giữa tiêu đề */
  vertical-align: middle !important;
  /* Căn giữa theo chiều dọc */
}

/* Định dạng chung cho badge */
.badge-style {
  display: inline-block;
  padding: 6px 12px;
  font-size: 14px;
  font-weight: bold;
  border-radius: 20px;
  text-align: center;
  min-width: 100px;
}

/* Kiểu voucher */
.badge-public {
  background-color: rgba(0, 123, 255, 0.1);
  color: #007bff;
  border: 1px solid #007bff;
}

.badge-private {
  background-color: rgba(40, 167, 69, 0.1);
  color: #28a745;
  border: 1px solid #28a745;
}

/* Trạng thái */
.badge-active {
  background-color: rgba(40, 167, 69, 0.1);
  color: #28a745;
  border: 1px solid #28a745;
}

.badge-expired {
  background-color: rgba(220, 53, 69, 0.1);
  color: #dc3545;
  border: 1px solid #dc3545;
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
