<template>
  <div class="tabs">
    <div v-for="tab in tabs" :key="tab" class="tab" :class="{ active: activeTab === tab }" @click="activeTab = tab">
      {{ tab }}
    </div>
  </div>

  <div class="order-card" v-for="order in filteredOrders" :key="order.maDon">
    <div class="order-header">
      <div><strong>Mã đơn:</strong> {{ order.maDonHang }}</div>
      <div class="order-status">Trạng thái: {{ order.trangThai }}</div>
    </div>

    <!-- Lặp qua các sản phẩm trong đơn hàng -->
    <div class="order-product" v-for="item in order.items" :key="item.maSPCT">
      <img :src="item.hinhAnh" alt="Ảnh sản phẩm" class="product-img" />
      <div class="order-product-info">
        <div><strong>{{ item.tenSanPham }}</strong></div>
        <div>
          Màu sắc: {{ item.mauSac.tenMau }} &nbsp;|&nbsp;
          Số lượng: {{ item.soLuong }} &nbsp;|&nbsp;
          Kích thước: {{ item.size.tenSize }} &nbsp;|&nbsp;
          Giá: {{ item.giaBan.toLocaleString() }} VNĐ
        </div>
        <div>
          Voucher: {{ voucher?.maVoucher }} - {{ voucher?.moTa }}
        </div>
        <div>
          Giảm giá: {{ discountAmount.toLocaleString() }} VNĐ
        </div>
        <div>
          Phí vận chuyển: {{ shippingFee.toLocaleString() }} VNĐ
        </div>
      </div>
    </div>

    <div class="order-actions">
      <div><strong>Tổng tiền:</strong> {{ order.tongTien.toLocaleString() }} VNĐ</div>
      <div>
        <button class="cancel-btn">HỦY ĐƠN</button>
        <button class="view-btn">🛒 XEM ĐƠN HÀNG</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeTab: "Chờ xác nhận",
      tabs: [
        "Chờ xác nhận",
        "Đã xác nhận",
        "Chờ giao hàng",
        "Đang vận chuyển",
        "Chờ thanh toán",
        "Đã thanh toán",
        "Hoàn thành",
        "Đã hủy",
      ],
      orders: [], // Lưu thông tin đơn hàng
      voucher: null, // Store voucher data
      discountAmount: 0, // Store discount value
      shippingFee: 0, // Store shipping fee
    };
  },
  computed: {
    filteredOrders() {
      return this.orders.filter(order => order.trangThai === this.activeTab);
    },
    
  },
  methods: {
    loadOrderData() {
      // Lấy thông tin đơn hàng từ localStorage khi component được mount
      const recentOrder = JSON.parse(localStorage.getItem("recentOrder"));
      if (recentOrder) {
        this.orders.push(recentOrder);  // Thêm đơn hàng vào mảng orders
        this.voucher = recentOrder.voucher || null; // Load voucher from the order
        this.discountAmount = recentOrder.discount || 0;
        this.shippingFee = recentOrder.chiPhiGiaoHang || 0; // Load shipping fee from the order
      }
      if (recentOrder.length) {
        this.orders = recentOrder;  // Lưu tất cả các đơn hàng vào mảng orders
      }
    },
  },
  mounted() {
    this.loadOrderData();  // Tải dữ liệu đơn hàng từ localStorage khi component được mount
  },
};
</script>

<style scoped>
.tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  font-weight: bold;
  text-transform: uppercase;
}

.tab {
  cursor: pointer;
  padding: 8px 16px;
  border-bottom: 2px solid transparent;
  transition: all 0.2s ease;
}

.tab.active {
  color: #1976d2;
  border-color: #1976d2;
}

.order-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  transition: transform 0.2s ease;
}

.order-card:hover {
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 16px;
}

.order-status {
  color: #1976d2;
  font-weight: bold;
}

.order-product {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.product-img {
  width: 110px;
  height: 110px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #eee;
}

.order-product-info {
  font-size: 15px;
  line-height: 1.6;
}

.order-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}

.order-actions button {
  padding: 10px 16px;
  margin-left: 10px;
  font-weight: bold;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.cancel-btn {
  background-color: #e53935;
  color: white;
}

.cancel-btn:hover {
  background-color: #c62828;
}

.view-btn {
  background-color: #f1f1f1;
  color: #333;
}

.view-btn:hover {
  background-color: #ddd;
}
</style>
