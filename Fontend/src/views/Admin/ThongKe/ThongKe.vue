<template>
  <div class="container mt-4">
    <h2 class="fw-bold text-center text-primary">Thống Kê</h2>
    <div class="row g-4 justify-content-center">
      <div class="col-md-4" v-for="(stat, index) in stats" :key="index">
        <div class="card shadow-lg p-4 text-center border-0">
          <h5 class="text-primary fw-bold">{{ stat.title }}</h5>
          <h3 class="fw-bold text-dark">{{ stat.revenue }}</h3>
          <p class="text-muted">{{ stat.products }} sản phẩm</p>
          <p class="text-muted">{{ stat.orders }} đơn hàng thành công</p>
        </div>
      </div>
    </div>




    <div class="d-flex gap-2 mt-4 justify-content-center flex-wrap">
      <button class="btn" :class="activeButton === 'ngay' ? 'btn-primary' : 'btn-outline-primary'"
        @click="setActiveButton('ngay')">
        NGÀY
      </button>

      <button class="btn" :class="activeButton === 'thang' ? 'btn-primary' : 'btn-outline-primary'"
        @click="setActiveButton('thang')">
        THÁNG
      </button>

      <button class="btn" :class="activeButton === 'nam' ? 'btn-primary' : 'btn-outline-primary'"
        @click="setActiveButton('nam')">
        NĂM
      </button>
      <!-- <button class="btn btn-primary">TÙY CHỈNH</button> -->
 
    </div>
  </div>

  <div class="container mt-5">
    <div class="row g-3 d-flex align-items-stretch justify-content-center">
      <div class="col-lg-6 col-md-6 d-flex">
        <div class="card shadow-lg p-3 border-0 d-flex flex-column flex-grow-1 h-100">
          <h5 class="text-primary fw-bold text-center pb-3"> Sản Phẩm Bán Chạy</h5>
          <table class="table table-striped">
            <thead>
              <tr>
                <th>#</th>
                <th>Sản phẩm</th>
                <th>Số lượng bán</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(product, index) in bestSellingProducts" :key="index">
                <td>{{ index + 1 }}</td>
                <td>{{ product.name }}</td>
                <td>{{ product.sold }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Biểu đồ trạng thái đơn hàng -->
      <div class="col-lg-6 col-md-6 d-flex">
        <div class="card shadow-lg p-3 border-0 d-flex flex-column flex-grow-1 h-100">
          <h5 class="text-primary fw-bold text-center pb-3">Trạng Thái Đơn Hàng</h5>
          <div class="flex-grow-1 d-flex justify-content-center align-items-center">
            <Pie :data="orderStatusData" :options="chartOptions" style="max-width: 320px; max-height: 320px;" />
          </div>
        </div>
      </div>

    </div>
  </div>
  <div class="container mt-5">
    <div class="row g-3 d-flex align-items-stretch justify-content-center">
      <!-- Bảng sản phẩm sắp hết hàng -->
      <div class="col-lg-6 col-md-6 d-flex">
        <div class="card shadow-lg p-3 border-0 d-flex flex-column flex-grow-1 h-100">
          <h5 class="text-primary fw-bold text-center pb-3">Sản Phẩm Sắp Hết Hàng</h5>
          <table class="table table-striped text-center">
            <thead>
              <tr>
                <th>#</th>
                <th>Sản phẩm</th>
                <th>Số lượng</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(product, index) in paginatedProducts" :key="index">
                <td>{{ index + 1 + (currentPage - 1) * pageSize }}</td>
                <td>{{ product.name }}</td>
                <td>{{ product.stock }}</td>
              </tr>
            </tbody>
          </table>

          <!-- Phân trang -->
          <div class="d-flex justify-content-center mt-3">
            <nav>
              <ul class="pagination">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                  <button class="page-link" @click="prevPage">‹</button>
                </li>
                <li v-for="page in totalPages" :key="page" class="page-item" :class="{ active: page === currentPage }">
                  <button class="page-link" @click="setPage(page)">{{ page }}</button>
                </li>
                <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                  <button class="page-link" @click="nextPage">›</button>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>


    </div>
  </div>

</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip, Legend, ArcElement } from 'chart.js';
import { Bar, Pie } from 'vue-chartjs';
import 'bootstrap/dist/css/bootstrap.min.css';

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend, ArcElement);

export default defineComponent({
  components: { Bar, Pie },
  setup() {
    const activeButton = ref('ngay');
    const startDate = ref('');
    const endDate = ref('');
    const stats = ref([]);

    const chartData = ref({
      labels: [],
      datasets: [{
        label: 'Doanh thu',
        data: [],
        backgroundColor: '#007bff',
        borderColor: '#007bff',
        borderWidth: 1,
        hoverBackgroundColor: 'gray',
      }]
    });
    const bestSellingProducts = ref([]);
    const orderStatusData = ref({ labels: [], datasets: [{ data: [], backgroundColor: ['#28a745', '#dc3545', '#ffc107', '#007bff', '#17a2b8', '#6f42c1'] }] });

    const fetchData = async (type) => {
      try {
        const productRes = await axios.get(`http://localhost:8080/api/thong-ke/san-pham-ban-chay/${type}`);
        const orderRes = await axios.get(`http://localhost:8080/api/thong-ke/trang-thai-don-hang/${type}`);

        console.log("Dữ liệu sản phẩm bán chạy:", productRes.data);
        console.log("Dữ liệu trạng thái đơn hàng:", orderRes.data);

        bestSellingProducts.value = productRes.data.map((item, index) => ({
          id: index + 1,
          name: item.tenSanPham || item[1],
          sold: item.soLuongBan || item[2]
        }));

        // Tính tổng số đơn hàng
        const totalOrders = orderRes.data.reduce((sum, item) => sum + (item.tongDonHang || 0), 0);

        orderStatusData.value = {
          labels: orderRes.data.map(item => item.trangThai),
          datasets: [{
            data: orderRes.data.map(item =>
              totalOrders > 0 ? ((item.tongDonHang / totalOrders) * 100).toFixed(2) : 0
            ),
            backgroundColor: ['#28a745', '#dc3545', '#ffc107', '#007bff', '#17a2b8', '#6f42c1']
          }]
        };
      } catch (error) {
        console.error(`Lỗi khi lấy dữ liệu ${type}:`, error);
      }
    };

    const setActiveButton = (type) => {
      activeButton.value = type; // Đổi trạng thái active
      fetchData(type); // Gọi lại API khi bấm nút
    };






    const chartOptions = ref({
      responsive: true,
      plugins: {
        legend: {
          display: true, // Hiển thị chú thích (màu sắc, trạng thái)
          position: 'top'
        },
        tooltip: {
          callbacks: {
            label: (tooltipItem) => {
              let value = tooltipItem.raw; // Lấy giá trị data của phần được hover
              return ` ${value}%`; // Chỉ hiển thị phần trăm khi di chuột vào
            }
          }
        },
        datalabels: {
          display: false // Ẩn số trên biểu đồ
        }
      }
    });



    const lowStockProducts = ref([]);
    const currentPage = ref(1);
    const pageSize = ref(5); // Số sản phẩm hiển thị trên mỗi trang

    const totalPages = computed(() => {
      return Math.ceil(lowStockProducts.value.length / pageSize.value);
    });

    const paginatedProducts = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      return lowStockProducts.value.slice(start, start + pageSize.value);
    });

    const fetchLowStockProducts = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/thong-ke/san-pham-sap-het-hang");
        lowStockProducts.value = response.data.map(item => ({
          name: item.tenSanPham,
          stock: item.soLuong,
        }));
      } catch (error) {
        console.error("Lỗi khi lấy danh sách sản phẩm sắp hết hàng:", error);
      }
    };

    const prevPage = () => {
      if (currentPage.value > 1) {
        currentPage.value--;
      }
    };

    const nextPage = () => {
      if (currentPage.value < totalPages.value) {
        currentPage.value++;
      }
    };

    const setPage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
      }
    };


    // **Hàm lấy dữ liệu sản phẩm bán chạy từ API**


    // **Hàm lấy dữ liệu thống kê doanh thu từ API**
    const fetchStatistics = async () => {
      try {
        const [ngayRes, thangRes, namRes] = await Promise.all([
          axios.get("http://localhost:8080/api/thong-ke/doanh-thu/don-hang/ngay"),
          axios.get("http://localhost:8080/api/thong-ke/doanh-thu/don-hang/thang-nay"),
          axios.get("http://localhost:8080/api/thong-ke/doanh-thu/don-hang/nam"),
        ]);

        console.log("Dữ liệu từ API:", ngayRes.data, thangRes.data, namRes.data);

        stats.value = [
          {
            title: "Doanh thu hôm nay",
            revenue: formatCurrency(ngayRes.data?.[0]?.tongDoanhThu || 0),
            products: ngayRes.data?.[0]?.tongSoLuongBan || 0,
            orders: ngayRes.data?.[0]?.tongDonHang || 0,
          },
          {
            title: "Doanh thu tháng này",
            revenue: formatCurrency(thangRes.data?.[0]?.tongDoanhThu || 0),
            products: thangRes.data?.[0]?.tongSoLuongBan || 0,
            orders: thangRes.data?.[0]?.tongDonHang || 0,
          },
          {
            title: "Doanh thu năm nay",
            revenue: formatCurrency(namRes.data?.[0]?.tongDoanhThu || 0),
            products: namRes.data?.[0]?.tongSoLuongBan || 0,
            orders: namRes.data?.[0]?.tongDonHang || 0,
          },
        ];

        if (thangRes.data.length > 0) {
          chartData.value.labels = thangRes.data.map(item => `Tháng ${item.thang}`);

          chartData.value.datasets[0].data = thangRes.data.map(item => item.tongDoanhThu || 0);
        }
      } catch (error) {
        console.error("Lỗi khi lấy dữ liệu:", error);
      }
    };

    // **Hàm định dạng tiền tệ (VND)**
    const formatCurrency = (amount) => {
      if (isNaN(amount) || amount == null) return "0 VND";
      return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(amount);
    };

    // **Gọi các API khi component được mounted**
    onMounted(() => {
      fetchStatistics();

      fetchLowStockProducts();
      fetchData('ngay')
    });

    return {
      startDate,
      endDate,
      stats,
      chartData,
      orderStatusData,
      chartOptions,
      lowStockProducts,
      bestSellingProducts,
      paginatedProducts,
      currentPage,
      totalPages,
      prevPage,
      nextPage,
      setPage,
      pageSize,
      fetchData,
      activeButton, setActiveButton
    };
  }
});
</script>



<style>
.card {
  border-radius: 12px;
  transition: all 0.3s ease-in-out;
  text-align: center;
  background-color: #f8f9fa;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0px 10px 15px rgba(0, 0, 0, 0.2);
}

.btn {
  font-weight: bold;
  padding: 10px 15px;
}

.text-primary {
  color: #080909 !important;
}
</style>