<template>
  <div class="tabs">
    <div v-for="tab in tabs" :key="tab" class="tab" :class="{ active: activeTab === tab }" @click="activeTab = tab">
      {{ tab }}
    </div>
  </div>

  <div v-if="filteredOrders.length === 0" class="no-orders">
    Không có đơn hàng nào với trạng thái "{{ activeTab }}"
  </div>

  <div class="order-card" v-for="order in filteredOrders" :key="order.maDonHang">
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
        <div v-if="order.voucher">
          Voucher: {{ order.voucher.maVoucher }} - {{ order.voucher.moTa }}
        </div>
        <div v-if="order.discount > 0">
          Giảm giá: {{ order.discount.toLocaleString() }} VNĐ
        </div>
        <div>
          Phí vận chuyển: {{ (order.chiPhiGiaoHang || 0).toLocaleString() }} VNĐ
        </div>
      </div>
    </div>

    <div class="order-actions">
      <div><strong>Tổng tiền:</strong> {{ order.tongTien.toLocaleString() }} VNĐ</div>
      <div>
        <button class="cancel-btn" v-if="canCancelOrder(order.trangThai)">HỦY ĐƠN</button>
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
    };
  },
  computed: {
    filteredOrders() {
      console.log('All orders:', this.orders); // Debug log
      console.log('Active tab:', this.activeTab); // Debug log
      const filtered = this.orders.filter(order => order.trangThai === this.activeTab);
      console.log('Filtered orders:', filtered); // Debug log
      return filtered;
    },
  },
  methods: {
    loadOrderData() {
      try {
        const storedData = localStorage.getItem("recentOrder");
        
        if (!storedData) {
          console.log('Không có dữ liệu đơn hàng trong localStorage');
          return;
        }

        const recentOrder = JSON.parse(storedData);
        console.log('Loaded data from localStorage:', recentOrder);

        // Kiểm tra xem dữ liệu là array hay object
        if (Array.isArray(recentOrder)) {
          // Nếu là array, gán trực tiếp
          this.orders = recentOrder;
        } else if (recentOrder && typeof recentOrder === 'object') {
          // Nếu là object đơn lẻ, chuyển thành array
          this.orders = [recentOrder];
        }

        console.log('Orders after loading:', this.orders);
        
      } catch (error) {
        console.error('Lỗi khi load dữ liệu đơn hàng:', error);
      }
    },

    canCancelOrder(status) {
      // Chỉ cho phép hủy đơn ở một số trạng thái nhất định
      const cancelableStatuses = ['Chờ xác nhận', 'Chờ thanh toán'];
      return cancelableStatuses.includes(status);
    },

    // Method để debug - có thể xóa sau khi fix xong
    debugOrders() {
      console.log('Current orders:', this.orders);
      console.log('Active tab:', this.activeTab);
      console.log('Filtered orders:', this.filteredOrders);
    }
  },
  mounted() {
    this.loadOrderData();
    
    // Debug - có thể xóa sau
    this.$nextTick(() => {
      this.debugOrders();
    });
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
  flex-wrap: wrap;
}

.tab {
  cursor: pointer;
  padding: 8px 16px;
  border-bottom: 2px solid transparent;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.tab.active {
  color: #1976d2;
  border-color: #1976d2;
}

.no-orders {
  text-align: center;
  padding: 40px;
  color: #666;
  font-style: italic;
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
  flex: 1;
}

.order-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.order-actions button {
  padding: 10px 16px;
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