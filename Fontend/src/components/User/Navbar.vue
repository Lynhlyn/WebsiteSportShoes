<script setup>
import { RouterLink, useRouter } from 'vue-router';
import { ref } from 'vue';

const router = useRouter();
const loggedInUser = ref(localStorage.getItem('loggedInUser'));

const handleLogout = () => {
    localStorage.removeItem('loggedInUser');
    loggedInUser.value = null;
    router.push('/login');
};
</script>

<template>
    <div class="container py-3">
        <div class="top-link d-none d-md-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center">
                <span class="fw-bold text-dark">Theo dõi:</span>
                <a href="#" class="social-icon"><i class="bi bi-facebook"></i></a>
                <a href="#" class="social-icon"><i class="bi bi-instagram"></i></a>
                <a href="#" class="social-icon"><i class="bi bi-youtube"></i></a>
                <a href="#" class="social-icon"><i class="bi bi-twitter"></i></a>
            </div>
            <div>
                <div v-if="!loggedInUser" class="auth-links">
                    <RouterLink to="/login" class="auth-link">Đăng nhập</RouterLink>
                    <RouterLink to="/register" class="auth-link">Đăng ký</RouterLink>
                </div>
                <div v-else class="user-menu dropdown">
                    <a href="#" class="dropdown-toggle" data-bs-toggle="dropdown">
                        <img src="@/assets/logo/image.png" alt="User" class="user-avatar">
                        <span>{{ loggedInUser }}</span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li>
                            <RouterLink to="/profile" class="dropdown-item">Tài khoản</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/orders" class="dropdown-item">Đơn mua</RouterLink>
                        </li>
                        <li>
                            <hr class="dropdown-divider">
                        </li>
                        <li><a href="#" class="dropdown-item text-danger" @click="handleLogout">Đăng xuất</a></li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="main-header rounded p-3 d-flex align-items-center justify-content-between">
            <img src="@/assets/logo/logo.png" alt="Logo" class="logo-img">
            <header class="bg-light py-2">
                <div class="container-fluid">
                    <!-- Thanh tìm kiếm nằm trên -->
                    <!-- Điều chỉnh chiều rộng cột tìm kiếm -->
                    <div class="row justify-content-center">
                        <div class="col-md-12">
                            <form class="search-form d-flex">
                                <input type="text" placeholder="Tìm kiếm..." class="form-control me-2">
                                <button class="btn btn-outline-primary"><i class="bi bi-search"></i></button>
                            </form>
                        </div>
                    </div>

                    <!-- Điều chỉnh navbar -->
                    <nav class="navbar mt-3">
                        <ul class="nav-list d-flex justify-content-center w-100">
                            <li>
                                <RouterLink to="/" class="nav-link">Trang chủ</RouterLink>
                            </li>
                            <li>
                                <RouterLink to="/intro" class="nav-link">Giới thiệu</RouterLink>
                            </li>
                            <li>
                                <RouterLink to="/products" class="nav-link">Sản phẩm</RouterLink>
                            </li>
                            <li>
                                <RouterLink to="/news" class="nav-link">Tin tức</RouterLink>
                            </li>
                            <li>
                                <RouterLink to="/contact" class="nav-link">Liên hệ</RouterLink>
                            </li>
                        </ul>
                    </nav>

                </div>
            </header>
            <div class="cart-icons d-flex">
                <RouterLink to="/wishlist" class="icon-btn"><i class="bi bi-heart"></i></RouterLink>
                <RouterLink to="/cart" class="icon-btn"><i class="bi bi-cart"></i></RouterLink>
            </div>
        </div>


    </div>
</template>

<style scoped>
.container {
    background-color: #fff;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

.social-icon {
    font-size: 20px;
    margin: 0 10px;
    color: #555;
    transition: all 0.3s;
}

.social-icon:hover {
    color: #e6007e;
    transform: scale(1.1);
}

.auth-link {
    margin-right: 10px;
    text-decoration: none;
    font-weight: 500;
    color: #333;
    transition: color 0.3s;
}

.auth-link:hover {
    color: #e6007e;
}

.user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    border: 2px solid #ddd;
}

.logo-img {
    width: 100px;
}

/* Mở rộng thanh tìm kiếm */
.search-form {
    flex-grow: 1;
    max-width: 100%;
    width: 100%;
    display: flex;
}

.search-form input {
    flex-grow: 1;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px 0 0 8px;
}

.search-form button {
    padding: 12px 16px;
    border-radius: 0 8px 8px 0;
}

/* Mở rộng menu */
.navbar {
    width: 100%;
    background-color: #f8f9fa;
    padding: 10px 0;
    border-radius: 8px;
}

.nav-list {
    display: flex;
    justify-content: center;
    width: 100%;
    gap: 25px;
    list-style: none;
    padding: 0;
    margin: 0;
}

.nav-list a {
    padding: 10px 15px;
    font-size: 18px;
    font-weight: bold;
    text-decoration: none;
    color: #333;
    transition: all 0.3s;
}

.nav-list a:hover {
    color: #e6007e;
    background: rgba(230, 0, 126, 0.1);
    border-radius: 5px;
}

.cart-icons {
    display: flex;
    gap: 15px;
}

.icon-btn {
    font-size: 24px;
    color: #333;
    transition: color 0.3s;
}

.icon-btn:hover {
    color: #e6007e;
}

</style>