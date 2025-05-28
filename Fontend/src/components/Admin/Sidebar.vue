<script setup>
import { RouterLink, useRoute } from 'vue-router';
import { ref, onMounted } from 'vue';

// Lấy thông tin người dùng từ localStorage
const userLogin = ref({ name: '' });

// Khi component được mount, lấy tên người dùng từ localStorage
onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (user) {
    userLogin.value.name = user.tenDangNhap || 'Admin'; // Gán tên đăng nhập hoặc mặc định là 'Admin'
  } else {
    userLogin.value.name = 'Admin'; // Nếu không có thông tin người dùng, gán mặc định là 'Admin'
  }
});

const route = useRoute();

// Xác định mục nào đang active dựa trên route hiện tại
const isActive = (path) => route.path.startsWith(path);

// Trạng thái mở/đóng của các menu con
const openMenus = ref({
  sales: false,
  stats: false,
  products: false,
  voucher: false,
  accounts: false,
  customers: false,
  orders: false,
  chat: false,
});

// Hàm toggle menu
const toggleMenu = (menu) => {
  openMenus.value[menu] = !openMenus.value[menu];
};
</script>


<template>
    <div class="sidebar bg-dark text-light position-fixed top-0 start-0" style="width: 250px; height: 100vh;">
        <!-- Header -->
        <div class="sidebar-header text-center py-4">
            <h5 class="m-0">SPORTSHOES</h5>
            <small>Xin chào, {{ userLogin.name || 'Admin' }}!</small>
        </div>

        <hr class="bg-secondary">

        <!-- Sidebar menu -->
        <div class="list-group">
            <!-- Bán hàng -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('sales')">
                    <i class="bi bi-cart me-2"></i> Bán hàng
                </button>
                <div v-show="openMenus.sales">
                    <RouterLink to="/admin/sales/offline" class="menu-item"
                        :class="{ active: isActive('/admin/sales/offline') }">
                        <i class="bi bi-shop me-2"></i> Bán hàng tại quầy
                    </RouterLink>

                </div>
            </div>

            <!-- Thống kê -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('stats')">
                    <i class="bi bi-bar-chart me-2"></i> Thống kê
                </button>
                <div v-show="openMenus.stats">
                    <RouterLink to="/admin/stats" class="menu-item" :class="{ active: isActive('/admin/stats') }">
                        <i class="bi bi-graph-up me-2"></i> Xem thống kê
                    </RouterLink>
                </div>
            </div>

            <!-- Sản phẩm -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('products')">
                    <i class="bi bi-box me-2"></i> Sản phẩm
                </button>
                <div v-show="openMenus.products">
                    <RouterLink to="/admin/products/thuoc_tinh" class="menu-item"
                        :class="{ active: isActive('/admin/products/thuoc_tinh') }">
                        <i class="bi bi-box-seam me-2"></i> Quản lý sản phẩm
                    </RouterLink>
                    <!-- Quản lý thuộc tính -->
                    <button class="menu-toggle sub-menu-toggle" @click="toggleMenu('productAttributes')">
                        <i class="bi bi-list me-2"></i> Quản lý thuộc tính
                    </button>
                    <div v-show="openMenus.productAttributes">
                        <RouterLink to="/admin/products/thuong_hieu" class="menu-item"
                            :class="{ active: isActive('/admin/products/thuong_hieu') }">
                            <i class="bi bi-star me-2"></i> Thương hiệu
                        </RouterLink>
                        <RouterLink to="/admin/products/categories" class="menu-item"
                            :class="{ active: isActive('/admin/products/categories') }">
                            <i class="bi bi-folder me-2"></i> Danh mục
                        </RouterLink>
                        <RouterLink to="/admin/products/chat_lieu" class="menu-item"
                            :class="{ active: isActive('/admin/products/chat_lieu') }">
                            <i class="bi bi-brush me-2"></i> Chất liệu
                        </RouterLink>
                        <RouterLink to="/admin/products/de_giay" class="menu-item"
                            :class="{ active: isActive('/admin/products/de_giay') }">
                            <i class="bi bi-shield me-2"></i> Đế giày
                        </RouterLink>
                        <RouterLink to="/admin/products/size" class="menu-item"
                            :class="{ active: isActive('/admin/products/size') }">
                            <i class="bi bi-aspect-ratio me-2"></i> Size
                        </RouterLink>

                        <RouterLink to="/admin/products/mau_sac" class="menu-item"
                            :class="{ active: isActive('/admin/products/mau_sac') }">
                            <i class="bi bi-palette me-2"></i> Màu sắc
                        </RouterLink>

                    </div>
                </div>

            </div>

            <!-- Voucher -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('voucher')">
                    <i class="bi bi-tag me-2"></i> Voucher
                </button>
                <div v-show="openMenus.voucher">
                    <RouterLink to="/admin/vouchers" class="menu-item" :class="{ active: isActive('/admin/vouchers') }">
                        <i class="bi bi-ticket me-2"></i> Quản lý voucher
                    </RouterLink>
                </div>
            </div>

            <!-- Quản lý tài khoản -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('accounts')">
                    <i class="bi bi-people-fill me-2"></i> Quản lý tài khoản
                </button>
                <div v-show="openMenus.accounts">
                    <RouterLink to="/admin/accounts" class="menu-item" :class="{ active: isActive('/admin/accounts') }">
                        <i class="bi bi-person-vcard me-2"></i> Tài khoản
                    </RouterLink>
                    <RouterLink to="/admin/staff" class="menu-item" :class="{ active: isActive('/admin/staff') }">
                        <i class="bi bi-person-gear me-2"></i> Nhân viên
                    </RouterLink>
                    <RouterLink to="/admin/customers" class="menu-item"
                        :class="{ active: isActive('/admin/customers') }">
                        <i class="bi bi-person-check me-2"></i> Khách hàng
                    </RouterLink>
                </div>
            </div>

            <!-- Đơn hàng -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('orders')">
                    <i class="bi bi-basket me-2"></i> Quản lý đơn hàng
                </button>
                <div v-show="openMenus.orders">
                    <RouterLink to="/admin/orders" class="menu-item" :class="{ active: isActive('/admin/orders') }">
                        <i class="bi bi-clipboard-check me-2"></i> Đơn hàng offline
                    </RouterLink>
                    <RouterLink to="/admin/orders-online" class="menu-item" :class="{ active: isActive('/admin/orders-online') }">
                        <i class="bi bi-clipboard-check me-2"></i> Đơn hàng online
                    </RouterLink>
                </div>
            </div>

        </div>
    </div>
</template>

<style scoped>
.menu-toggle {
    background: none;
    border: none;
    color: #fff;
    width: 100%;
    text-align: left;
    padding: 8px 15px;
    font-size: 16px;
    cursor: pointer;
    transition: background 0.3s;
}

.menu-toggle:hover {
    background: rgba(255, 255, 255, 0.1);
}

.menu-item {
    display: flex;
    align-items: center;
    padding: 8px 20px;
    color: #ccc;
    text-decoration: none;
    transition: all 0.3s;
}

.menu-item:hover {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
}
</style>
