<template>
  <div class="container mt-5">
    <div class="card shadow rounded-4 p-4">
      <h2 class="text-center mb-4">📦 Quản Lý Đơn Hàng Online</h2>

      <!-- Bộ lọc tìm kiếm -->
      <div class="row gy-3 mb-4">
        <div class="col-12 col-md-6">
          <input v-model="search" type="text" class="form-control"
            placeholder="🔍 Tìm mã đơn hàng hoặc tên khách hàng..." />
        </div>

        <div class="col-6 col-md-3">
          <input type="date" v-model="startDate" class="form-control" placeholder="Từ ngày" />
        </div>
        <div class="col-6 col-md-3">
          <input type="date" v-model="endDate" class="form-control" placeholder="Đến ngày" />
        </div>

        <div class="col-6 col-md-3">
          <select v-model="statusFilter" class="form-select">
            <option value="">📌 Trạng thái</option>
            <option value="Hoàn thành">Hoàn thành </option>
            <option value="Đã thanh toán">Đã thanh toán</option>
            <option value="Chờ thanh toán">Chờ thanh toán</option>
            <option value="Đã giao hàng">Đã giao hàng</option>
            <option value="Đang vận chuyển">Đang vận chuyển</option>
            <option value="Chờ giao hàng">Chờ giao hàng</option>
            <option value="Đã xác nhận">Đã xác nhận</option>
            <option value="Chờ xác nhận">Chờ xác nhận</option>
            <option value="Đã hủy">Đã hủy</option>
          </select>
        </div>

        <div class="col-6 col-md-3">
          <select v-model="customerFilter" class="form-select">
            <option value="">👥 Khách hàng</option>
            <option v-for="customer in customers" :key="customer.id" :value="customer.id">{{
              customer.hoTen }}</option>
          </select>
        </div>
      </div>

      <!-- Bảng đơn hàng -->
      <div class="table-responsive">
        <table class="table table-hover align-middle text-center table-bordered">
          <thead class="">
            <tr>
              <th>ID</th>
              <th>Mã Đơn Hàng</th>
              <th>Khách Hàng</th>
              <th>Phương Thức</th>
              <th>Ngày Tạo</th>
              <th>Tổng Tiền</th>
              <th>Phí Giao Hàng</th>
              <th>Trạng Thái</th>
              <th>Hành Động</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in paginatedOrders" :key="order.id">
              <td>{{ order.id }}</td>
              <td class="fw-bold text-primary">{{ order.maDonHang }}</td>
              <td>{{ order.khachHang?.hoTen || 'Khách vãng lai' }}</td>
              <td>{{ order.phuongThucThanhToan?.tenPhuongThuc || 'Chưa chọn' }}</td>
              <td>{{ order.ngayTaoFormatted }}</td>
              <td class="text-success fw-bold">{{ order.tongTien?.toLocaleString() || '0' }} đ</td>
              <td class="text-warning fw-bold">{{ order.chiPhiGiaoHang?.toLocaleString() || '0' }} đ</td>
              <td>
                <span :class="getStatusClass(order.trangThaiDonHang)">
                  <i :class="getStatusIcon(order.trangThaiDonHang)"></i> {{ order.trangThaiDonHang }}
                </span>

              </td>
              <td>
                <div class="btn-group btn-group-sm">
                  <a :href="`/xac-nhan?id=${order.id}`" class="btn btn-info">
                    <i class="bi bi-eye"></i>
                  </a>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Phân trang -->
      <div class="d-flex justify-content-between align-items-center mt-4 flex-wrap">
        <div class="btn-group">
          <button @click="prevPage" :disabled="currentPage === 1" class="btn btn-outline-primary">
            <i class="bi bi-chevron-left"></i>
          </button>
          <button v-for="page in visiblePages" :key="page" @click="changePage(page)"
            :class="['btn', currentPage === page ? 'btn-primary' : 'btn-outline-primary']">
            {{ page }}
          </button>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="btn btn-outline-primary">
            <i class="bi bi-chevron-right"></i>
          </button>
        </div>

        <div>
          <select v-model="pageSize" class="form-select w-auto mt-2 mt-md-0" @change="currentPage = 1">
            <option v-for="option in [5, 10, 15, 20]" :key="option" :value="option">
              {{ option }} / trang
            </option>
          </select>
        </div>
      </div>
    </div>


  </div>
</template>


<script>
import axios from "axios";

export default {
  data() {
    return {
      orders: [],
      order: null,
      search: "",
      startDate: "", // Khai báo startDate
      endDate: "",
      selectedOrder: null,
      currentPage: 1, // Trang hiện tại
      pageSize: 5, // Số đơn hàng mỗi trang
      statusFilter: "", // Lọc theo trạng thái
      paymentMethodFilter: "", // Lọc theo phương thức thanh toán
      customers: [], // Dữ liệu khách hàng
      customerFilter: "", // Lọc theo khách hàng
    };
  },
  computed: {
    // Lọc đơn hàng theo tìm kiếm, nhân viên, trạng thái và phương thức thanh toán
    filteredOrders() {
      let filtered = this.orders;
      filtered = filtered.filter(order => order.loaiDonHang === true);
      // Lọc theo tìm kiếm
      if (this.search) {
        const searchLower = this.search.toLowerCase().trim();
        filtered = filtered.filter((order) => {
          const maDonHangLower = order.maDonHang?.toLowerCase() || "";
          const tenKhachHangLower = order.khachHang?.hoTen?.toLowerCase() || "";
          return maDonHangLower.includes(searchLower) || tenKhachHangLower.includes(searchLower);
        });
      }

      // Lọc theo trạng thái
      if (this.statusFilter) {
        filtered = filtered.filter(order => order.trangThaiDonHang === this.statusFilter);
      }
      // Lọc theo khách hàng
      if (this.customerFilter) {
        filtered = filtered.filter(order => order.khachHang?.id === this.customerFilter);
      }

      // Lọc theo khoảng ngày (startDate và endDate)
      if (this.startDate || this.endDate) {
        const startDate = this.startDate ? new Date(this.startDate + "T00:00:00") : null;  // Set giờ 00:00:00 cho startDate
        const endDate = this.endDate ? new Date(this.endDate + "T23:59:59") : null;         // Set giờ 23:59:59 cho endDate

        // Validate if startDate is greater than endDate
        if (startDate && endDate && startDate > endDate) {
          alert("Ngày bắt đầu không thể lớn hơn ngày kết thúc!");
          return [];  // Trả về danh sách rỗng nếu ngày bắt đầu lớn hơn ngày kết thúc
        }

        filtered = filtered.filter(order => {
          const ngayTao = new Date(order.ngayTao);  // Chuyển đổi ngày tạo của đơn hàng thành đối tượng Date
          const isAfterStartDate = startDate ? ngayTao >= startDate : true;  // Nếu có startDate, kiểm tra ngày tạo >= startDate
          const isBeforeEndDate = endDate ? ngayTao <= endDate : true;  // Nếu có endDate, kiểm tra ngày tạo <= endDate
          return isAfterStartDate && isBeforeEndDate;  // Kiểm tra cả hai điều kiện
        });
      }

      return filtered;
    },

    // Phân trang đơn hàng
    paginatedOrders() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      return this.filteredOrders.slice(startIndex, startIndex + this.pageSize);
    },

    // Tổng số trang
    totalPages() {
      return Math.ceil(this.filteredOrders.length / this.pageSize);
    },

    // Các trang hiển thị cho phân trang
    visiblePages() {
      const maxPagesToShow = 5;
      const total = this.totalPages;
      const current = this.currentPage;
      let start = Math.max(current - 2, 1);
      let end = Math.min(current + 2, total);

      if (start === 1) {
        end = Math.min(start + maxPagesToShow - 1, total);
      } else if (end === total) {
        start = Math.max(total - maxPagesToShow + 1, 1);
      }

      return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    },
  },
  methods: {
    async searchOrders() {
      try {
        console.log("Start Date: ", this.startDate);  // Log kiểm tra
        console.log("End Date: ", this.endDate);      // Log kiểm tra

        // Chuyển đổi startDate và endDate sang ISO String với thời gian rõ ràng
        const start = this.startDate ? new Date(this.startDate + "T00:00:00").toISOString() : null; // Set thời gian bắt đầu là 00:00:00
        const end = this.endDate ? new Date(this.endDate + "T23:59:59").toISOString() : null;       // Set thời gian kết thúc là 23:59:59

        console.log("Start Date formatted: ", start);  // Kiểm tra ISO String
        console.log("End Date formatted: ", end);        // Kiểm tra ISO String

        // Tạo đối tượng params với các giá trị đã chuyển đổi
        const params = {
          search: this.search || null,
          startDate: start,  // Đảm bảo rằng ngày được gửi đúng định dạng
          endDate: end,        // Đảm bảo rằng ngày được gửi đúng định dạng
          customerId: this.customerFilter || null,
        };

        // Gửi request lên backend
        const response = await axios.get('http://localhost:8080/don-hang/searchByDateRange', { params });
        console.log("Sending request with params:", params);  // Kiểm tra các tham số gửi lên
        this.orders = response.data; // Cập nhật dữ liệu đơn hàng với dữ liệu trả về
      } catch (error) {
        console.error("Lỗi khi tìm kiếm đơn hàng:", error);
      }
    },

    calculateDiscount() {
      const order = this.selectedOrder && this.selectedOrder.donHang;
      if (!order || !order.voucher) return 0;  // Nếu không có voucher, trả về 0

      const voucher = order.voucher;
      let discount = 0;

      // Nếu voucher là loại tiền mặt (loaiVoucher = 1), áp dụng giaTriGiam
      if (voucher.loaiVoucher === 1 && order.tongTien >= voucher.giaTriToiThieu) {
        discount = voucher.giaTriGiam;  // Giảm tiền mặt cố định
      }

      // Nếu voucher là loại phần trăm (loaiVoucher = 0), áp dụng phần trăm giảm
      if (voucher.loaiVoucher === 0 && order.tongTien >= voucher.giaTriToiThieu) {
        discount = (order.tongTien * voucher.giaTriGiam) / 100;  // Giảm theo phần trăm
      }

      // Trả về giá trị giảm sau khi tính toán
      return discount;
    },

    // Lấy danh sách tất cả khách hàng từ API
    async fetchCustomers() {
      try {
        const response = await axios.get('http://localhost:8080/khach-hang');
        this.customers = response.data;
      } catch (error) {
        console.error("Lỗi khi lấy danh sách khách hàng:", error);
      }
    },

    async fetchOrders() {
      try {
        const response = await axios.get("http://localhost:8080/don-hang");
        this.orders = response.data;
        this.orders = response.data.map(order => {
          // Format 'ngayTao' to 'dd/MM/yyyy'
          order.ngayTaoFormatted = order.ngayTao
            ? new Date(order.ngayTao).toLocaleString("vi-VN", {
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
              hour: "2-digit",
              minute: "2-digit",
              hour12: false // 24-hour format
            }) : "Không có";
          return order;
        });
        this.totalPages = Math.ceil(this.orders.length / this.pageSize);
      } catch (error) {
        console.error("Error fetching orders:", error);
      }
    },

    async detailOrder(id) {
      try {
        const response = await axios.get(`http://localhost:8080/don-hang-chi-tiet/${id}`);
        this.selectedOrder = response.data;  // Gán selectedOrder để hiển thị trong modal
      } catch (error) {
        console.error("Lỗi khi lấy chi tiết đơn hàng:", error);
      }
    },

    closeModal() {
      this.selectedOrder = null;
    },

    // Chuyển trang
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },

    changePage(page) {
      this.currentPage = page;
    },

    getStatusClass(status) {
      switch (status) {
        case "Chờ xác nhận":
          return "badge bg-warning text-dark";
        case "Đã xác nhận":
          return "badge bg-primary-emphasis";
        case "Chờ giao hàng":
          return "badge bg-info text-dark";
        case "Đang vận chuyển":
          return "badge bg-success-emphasis";
        case "Đã giao hàng":
          return "badge bg-primary";
        case "Chờ thanh toán":
          return "badge bg-warning";
        case "Đã thanh toán":
          return "badge bg-success";
        case "Hoàn thành":
          return "badge bg-secondary";
        default:
          return "badge bg-danger";
      }
    },

    getStatusIcon(status) {
      switch (status) {
        case "Chờ xác nhận":
          return "bi bi-hourglass-split text-warning";  // icon for waiting
        case "Đã xác nhận":
          return "bi bi-check-circle text-primary";  // icon for confirmed
        case "Chờ giao hàng":
          return "bi bi-box text-info";  // icon for waiting for delivery
        case "Đang vận chuyển":
          return "bi bi-truck text-success";  // icon for in transit
        case "Đã giao hàng":
          return "bi bi-check2-circle text-danger";  // icon for delivered
        case "Chờ thanh toán":
          return "bi bi-credit-card text-danger";  // icon for waiting for payment
        case "Đã thanh toán":
          return "bi bi-check2-circle text-success";  // icon for paid
        case "Hoàn thành":
          return "bi bi-check-all text-success";  // icon for completed
        default:
          return "bi bi-question-circle text-secondary";  // default icon for unknown status
      }
    },
  },
  watch: {
    startDate(newValue) {
      console.log("Start date changed:", newValue);
    },
    endDate(newValue) {
      console.log("End date changed:", newValue);
    }
  },

  async mounted() {
    // Bỏ phần check localStorage vì không cần thiết cho trang quản lý đơn hàng
    // const savedOrder = localStorage.getItem("recentOrder");
    // if (savedOrder) {
    //   this.order = JSON.parse(savedOrder);  // Parse and store the order
    // } else {
    //   alert("Không tìm thấy đơn hàng!");
    // }
    
    // Lấy danh sách khách hàng và đơn hàng
    await this.fetchCustomers();
    await this.fetchOrders();
  },
};
</script>



<style scoped>
/* Lớp phủ nền khi modal xuất hiện */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  /* Lớp phủ mờ */
  z-index: 9999;
  /* Đảm bảo lớp phủ nằm trên các phần khác */
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Modal */
.modal.fade.show {
  display: block;
  z-index: 10000;
  /* Đảm bảo modal nằm trên lớp phủ */
  animation: fadeIn 0.3s ease-out;
  /* Thêm hiệu ứng fade-in khi modal xuất hiện */
}

/* Hiệu ứng khi mở modal */
@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: translateY(-30px);
  }

  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Tiêu đề bảng trong Modal */
.modal-body .table thead th {
  background-color: #007bff;
  /* Màu nền xanh dương */
  color: white;
  /* Màu chữ trắng */
  font-weight: bold;
  /* Làm đậm chữ */
  text-align: center;
  /* Căn giữa nội dung tiêu đề */
  border: 1px solid #007bff;
  /* Đặt màu viền */
}

/* Tiêu đề bảng khi hover */
.modal-body .table thead th:hover {
  background-color: #0056b3;
  /* Màu nền khi hover */
  cursor: pointer;
}


/* Modal content */
/* Modal Background */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  /* Nền mờ */
  z-index: 9999;
  /* Đảm bảo modal nằm trên tất cả các phần tử */
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Cải thiện hiển thị giá gốc */
.table td.price-goc {
  text-decoration: line-through;
  color: #888;
  /* Màu xám cho giá gốc */
}

/* Cải thiện hiển thị giá gốc */
.table td.price-km {

  color: red;
  /* Màu xám cho giá gốc */
}

/* Modal */
.modal.fade.show {
  display: block;
  z-index: 10000;
  animation: fadeIn 0.3s ease-out;
}

/* Hiệu ứng fade-in */
@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: translateY(-30px);
  }

  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Modal content */
.modal-content {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  max-width: 1000px;
  /* Điều chỉnh chiều rộng modal */
  width: 100%;
  animation: zoomIn 0.4s ease-out;
}

/* Hiệu ứng zoom-in */
@keyframes zoomIn {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }

  100% {
    transform: scale(1);
    opacity: 1;
  }
}

/* Hiệu ứng cho các nút trong modal */
.modal-header .btn-close {
  background-color: transparent;
  border: none;
  font-size: 1.5rem;
  color: #000;
  cursor: pointer;
  transition: color 0.3s ease;
}

.modal-header .btn-close:hover {
  color: #007bff;
}

/* Cải thiện bảng trong modal */
.modal-body .table {
  margin-top: 20px;
  width: 100%;
  border-collapse: collapse;
  text-align: center;
  max-height: 400px;
  /* Giới hạn chiều cao của bảng */
  overflow-y: auto;
  /* Cho phép cuộn dọc nếu bảng dài */
  display: block;
  /* Chuyển bảng thành block để cuộn */
  overflow-x: auto;
  /* Cho phép cuộn ngang nếu bảng rộng */
}

/* Cải thiện hiệu ứng hover cho các nút */
.modal-body .btn-info {
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.modal-body .btn-info:hover {
  background-color: #0b5ed7;
  color: #fff;
  transform: scale(1.05);
}


.container {
  max-width: 1100px;
}

/* Căn chỉnh bảng */
.table th,
.table td {
  vertical-align: middle;
}

.table-hover tbody tr:hover {
  background-color: rgba(0, 123, 255, 0.05);
}

/* Căn giữa tiêu đề */
h2 {
  font-weight: bold;
}

/* Hiệu ứng hover cho nút */
.btn-info {
  transition: all 0.3s ease-in-out;
}

.btn-info:hover {
  background-color: #0b5ed7;
  color: #fff;
}

/* Định dạng badge trạng thái */
.badge {
  font-size: 0.9rem;
  padding: 8px 12px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.badge i {
  font-size: 1rem;
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

/* Thêm hiệu ứng cho input tìm kiếm */
.input-search {
  width: 80%;
  padding: 10px 15px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  background-color: #f8f9fa;
  font-size: 16px;
  transition: all 0.3s ease;
}

.input-search:focus {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.5);
}

/* Thêm hiệu ứng hover cho nút tìm kiếm */
.btn-search {
  padding: 10px 20px;
  border: 1px solid #007bff;
  background-color: #007bff;
  color: white;
  font-size: 16px;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.btn-search:hover {
  background-color: #0056b3;
  cursor: pointer;
}

/* Đổi màu cho các thẻ select để lọc */
.select-filter {
  padding: 10px 15px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  background-color: #f8f9fa;
  font-size: 16px;
  transition: all 0.3s ease;
}

.select-filter:focus {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.5);
}

/* Style cho các button lọc */
.filter-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.filter-buttons select {
  padding: 10px 15px;
  font-size: 16px;
  border-radius: 8px;
  background-color: #f8f9fa;
  border: 1px solid #ced4da;
  transition: border-color 0.3s ease;
}

.filter-buttons select:focus {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.5);
}

/* Các button lọc thêm: tạo hiệu ứng hover */
.filter-buttons button {
  padding: 10px 15px;
  border-radius: 8px;
  background-color: #007bff;
  color: white;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

.filter-buttons button:hover {
  background-color: #0056b3;
  cursor: pointer;
}

/* Cải thiện việc căn chỉnh các phần tử lọc */
.filter-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  gap: 15px;
}

/* Các phần tử lọc trong màn hình nhỏ */
@media screen and (max-width: 768px) {
  .filter-container {
    flex-direction: column;
    align-items: flex-start;
  }

  .input-search {
    width: 100%;
    margin-bottom: 10px;
  }

  .filter-buttons {
    flex-direction: column;
    width: 100%;
  }

  .filter-buttons select,
  .filter-buttons button {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>
