<script setup>
import { RouterLink, useRoute } from 'vue-router';
import { ref, reactive } from 'vue';

// Lấy dữ liệu đăng nhập từ localStorage
let userLogin = reactive({});
if (localStorage.getItem("userLogin")) {
    userLogin = JSON.parse(localStorage.getItem("userLogin"));
}

const route = useRoute();

// Xác định mục nào đang active dựa trên route hiện tại
const isActive = (path) => route.path.startsWith(path);

// Trạng thái mở/đóng của các menu con
const openMenus = reactive({
    sales: false,
    stats: false,
    products: false,
    voucher: false,
    staff: false,
    customers: false,
    orders: false,
    chat: false,
});

// Hàm toggle menu
const toggleMenu = (menu) => {
    openMenus[menu] = !openMenus[menu];
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
            <!-- Trang chủ -->
             <!-- Trang Chủ -->
            <RouterLink to="/" class="menu-item" :class="{ active: isActive('/') }">
                <i class="bi bi-house-door me-2"></i> Trang Chủ
            </RouterLink>
            <!-- Bán hàng -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('sales')">
                    <i class="bi bi-cart me-2"></i> Bán hàng
                </button>
                <div v-show="openMenus.sales">
                    <RouterLink to="/admin/sales/offline" class="menu-item" :class="{ active: isActive('/admin/sales/offline') }">
                        <i class="bi bi-shop me-2"></i> Bán hàng tại quầy
                    </RouterLink>
                    <RouterLink to="/admin/sales/online" class="menu-item" :class="{ active: isActive('/admin/sales/online') }">
                        <i class="bi bi-globe me-2"></i> Bán hàng online
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
                    <RouterLink to="/admin/products/manage" class="menu-item" :class="{ active: isActive('/admin/products/manage') }">
                        <i class="bi bi-grid me-2"></i> Quản lý sản phẩm
                    </RouterLink>
                    <RouterLink to="/admin/products/details" class="menu-item" :class="{ active: isActive('/admin/products/details') }">
                        <i class="bi bi-list-task me-2"></i> Sản phẩm chi tiết
                    </RouterLink>
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

            <!-- Nhân viên -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('staff')">
                    <i class="bi bi-person-badge me-2"></i> Nhân viên
                </button>
                <div v-show="openMenus.staff">
                    <RouterLink to="/admin/staff" class="menu-item" :class="{ active: isActive('/admin/staff') }">
                        <i class="bi bi-person-gear me-2"></i> Quản lý nhân viên
                    </RouterLink>
                </div>
            </div>

            <!-- Khách hàng -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('customers')">
                    <i class="bi bi-people me-2"></i> Khách hàng
                </button>
                <div v-show="openMenus.customers">
                    <RouterLink to="/admin/customers" class="menu-item" :class="{ active: isActive('/admin/customers') }">
                        <i class="bi bi-person-check me-2"></i> Quản lý khách hàng
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
                        <i class="bi bi-clipboard-check me-2"></i> Đơn hàng
                    </RouterLink>
                    <RouterLink to="/admin/order-details" class="menu-item" :class="{ active: isActive('/admin/order-details') }">
                        <i class="bi bi-card-list me-2"></i> Đơn hàng chi tiết
                    </RouterLink>
                </div>
            </div>

            <!-- Chat -->
            <div class="menu-group">
                <button class="menu-toggle" @click="toggleMenu('chat')">
                    <i class="bi bi-chat-dots me-2"></i> Chat
                </button>
                <div v-show="openMenus.chat">
                    <RouterLink to="/admin/chat" class="menu-item" :class="{ active: isActive('/admin/chat') }">
                        <i class="bi bi-chat-left-text me-2"></i> Chat hỗ trợ
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
