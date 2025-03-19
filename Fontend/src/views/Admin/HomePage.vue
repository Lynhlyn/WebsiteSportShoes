<script setup>
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import { RouterView, useRoute } from 'vue-router';
import { ref, watchEffect } from 'vue';
import Sidebar from '@/components/Admin/Sidebar.vue';
import Header from '@/components/Admin/Header.vue';
import Footer from '@/components/Admin/Footer.vue';

// Reactive title header
const route = useRoute();
const titleHeader = ref("Admin");

// Update title dynamically
watchEffect(() => {
  switch (route.name) {
    // Bán hàng
    case 'sales-online':
      titleHeader.value = 'Bán hàng - Online';
      document.title = 'Bán hàng Online';
      break;
    case 'sales-offline':
      titleHeader.value = 'Bán hàng - Offline';
      document.title = 'Bán hàng Offline';
      break;

    // Thống kê
    case 'stats':
      titleHeader.value = 'Thống kê';
      document.title = 'Xem thống kê';
      break;

    // Sản phẩm
    case 'products-manage':
      titleHeader.value = 'Quản lý sản phẩm';
      document.title = 'Quản lý sản phẩm';
      break;
    case 'products-details':
      titleHeader.value = 'Sản phẩm chi tiết';
      document.title = 'Quản lý sản phẩm chi tiết';
      break;
    case 'products-attributes':
      titleHeader.value = 'Quản lý thuộc tính';
      document.title = 'Quản lý thuộc tính sản phẩm';
      break;
    case 'products-promotions':
      titleHeader.value = 'Quản lý khuyến mại sản phẩm';
      document.title = 'Quản lý khuyến mại sản phẩm';
      break;

    case 'vouchers':
      titleHeader.value = 'Voucher';
      document.title = 'Quản lý voucher';
      break;
    case 'staff':
      titleHeader.value = 'Nhân viên';
      document.title = 'Quản lý nhân viên';
      break;
    case 'customers':
      titleHeader.value = 'Khách hàng';
      document.title = 'Quản lý khách hàng';
      break;
    case 'orders':
      titleHeader.value = 'Quản lý đơn hàng';
      document.title = 'Đơn hàng';
      break;
    case 'chat':
      titleHeader.value = 'Chat';
      document.title = 'Chat hỗ trợ';
      break;

    default:
      titleHeader.value = 'SportShoes';
      document.title = 'SportShoes';
  }
});
</script>

<template>
  <div class="wrapper">
    <!-- Sidebar -->
    <Sidebar />

    <div class="main-content">
      <!-- Header -->
      <Header :titleHeader="titleHeader" />

      <!-- Content -->
      <div class="content">
        <RouterView />
      </div>

      <!-- Footer -->
      <Footer />
    </div>
  </div>
</template>

<style scoped>
/* Bố cục chính */
.wrapper {
  display: flex;
  height: 100vh;
  background: #f4f6f9;
  padding: 10px;
}

/* Sidebar */
.sidebar {
  width: 250px;
  background: #343a40;
  color: white;
  height: 100vh;
  position: fixed;
  overflow-y: auto;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 15px;
}

/* Main content */
.main-content {
  flex: 1;
  margin-left: 270px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  border-radius: 15px;
  background: white;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

/* Nội dung chính */
.content {
  flex-grow: 1;
  padding: 20px;
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.05);
}

/* Footer */
footer {
  text-align: center;
  padding: 15px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  margin-top: 10px;
}
</style>