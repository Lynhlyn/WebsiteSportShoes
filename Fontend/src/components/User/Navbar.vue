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
    <nav class="navbar navbar-expand-lg navbar-light shadow-sm navbar-custom">
        <div class="container">
            <!-- Logo -->
            <RouterLink class="navbar-brand" to="/">
                <img src="@/assets/logo/logo.png" alt="Logo" height="70" />
            </RouterLink>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarContent">
                <!-- Menu -->
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <RouterLink class="nav-link" to="/">Trang chủ</RouterLink>
                    </li>
                    <li class="nav-item">
                        <RouterLink class="nav-link" to="/about">Giới thiệu</RouterLink>
                    </li>
                    <li class="nav-item">
                        <RouterLink class="nav-link" to="/stores">Hệ thống cửa hàng</RouterLink>
                    </li>
                    <li class="nav-item">
                        <RouterLink class="nav-link" to="/new-arrivals">Hàng mới</RouterLink>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="brandDropdown" role="button"
                            data-bs-toggle="dropdown">Thương hiệu</a>
                        <ul class="dropdown-menu">
                            <li><RouterLink class="dropdown-item" to="/brands/adidas">Giày ADIDAS</RouterLink></li>
                            <li><RouterLink class="dropdown-item" to="/brands/converse">Giày CONVERSE</RouterLink></li>
                            <li><RouterLink class="dropdown-item" to="/brands/jordan">Giày JORDAN</RouterLink></li>
                            <li><RouterLink class="dropdown-item" to="/brands/nike">Giày NIKE</RouterLink></li>
                            <li><RouterLink class="dropdown-item" to="/brands/on-running">Giày ON RUNNING</RouterLink></li>
                            <li><RouterLink class="dropdown-item" to="/brands/puma">Giày PUMA</RouterLink></li>
                        </ul>
                    </li>
                </ul>

                <!-- Thanh tìm kiếm -->
                <form class="d-flex me-3 search-bar">
                    <input class="form-control" type="search" placeholder="Bạn muốn tìm gì?" />
                    <button class="btn btn-primary ms-2" type="submit"><i class="bi bi-search"></i></button>
                </form>

                <!-- Tài khoản -->
                <div class="dropdown me-3">
                    <button class="btn btn-light dropdown-toggle account-btn" type="button" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle"></i>
                    </button>
                    <ul class="dropdown-menu">
                        <li v-if="!loggedInUser">
                            <RouterLink class="dropdown-item" to="/login">
                                <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
                            </RouterLink>
                        </li>
                        <li v-if="!loggedInUser">
                            <RouterLink class="dropdown-item" to="/register">
                                <i class="bi bi-person-plus"></i> Đăng ký
                            </RouterLink>
                        </li>
                        <li v-if="loggedInUser">
                            <button class="dropdown-item" @click="handleLogout">
                                <i class="bi bi-box-arrow-right"></i> Đăng xuất
                            </button>
                        </li>
                    </ul>
                </div>

                <!-- Giỏ hàng -->
                <RouterLink class="btn btn-outline-primary cart-btn" to="/cart">
                    <i class="bi bi-cart"></i>
                </RouterLink>
            </div>
        </div>
    </nav>
</template>

<style scoped>
/* Navbar */
.navbar-custom {
  background-color: #f8dee0 !important; /* Màu hồng nhạt */
  border-radius: 15px; /* Bo góc */
  padding: 5px;
}

/* Menu Item */
.navbar-nav .nav-link {
    font-size: 17px;
    font-weight: 500;
    transition: color 0.3s;
}

.navbar-nav .nav-link:hover {
    color: #007bff;
}

/* Dropdown */
.dropdown-menu {
    border-radius: 8px;
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

.dropdown-item {
    transition: background 0.3s;
}

.dropdown-item:hover {
    background: #f8f9fa;
}

/* Tìm kiếm */
.search-bar input {
    border-radius: 8px;
}

.search-bar button {
    border-radius: 8px;
}

/* Tài khoản */
.account-btn {
    font-size: 20px;
    padding: 6px 8px;
    border-radius: 50%;
    transition: background 0.3s;
}

.account-btn:hover {
    background: rgba(0, 0, 0, 0.1);
}

/* Giỏ hàng */
.cart-btn {
    font-size: 20px;
    padding: 6px 10px;
    border-radius: 50%;
}
</style>
