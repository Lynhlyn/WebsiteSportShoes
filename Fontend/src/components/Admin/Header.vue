<script setup>
import { useRouter } from 'vue-router';

const props = defineProps({
  titleHeader: {
    type: String,
    required: true,
    default: 'SportShoes',
  },
});

const router = useRouter();

// Handle logout and redirect to the login page
const handleLogout = () => {
  localStorage.removeItem("userLogin");
  router.push('/login');
};
</script>

<template>
  <div class="header-container">
    <!-- Logo & Title -->
    <div class="d-flex align-items-center">
      <img src="/src/assets/logo/logo.png" alt="Logo" class="logo me-3" />
      <h3 class="title mb-0">{{ props.titleHeader }}</h3>
    </div>

    <!-- Đăng xuất -->
    <button @click="handleLogout" class="btn btn-outline-dark fw-bold">Đăng xuất</button>
  </div>
</template>

<style scoped>
.header-container {
  width: 100%;
  background-color: #f8d7da; /* Màu hồng nhạt */
  border-bottom: 3px solid #f1aeb5; /* Viền dưới */
  color: #721c24; /* Màu chữ */
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  border-radius: 10px; /* Bo góc */
}

/* Logo */
.logo {
  height: 80px;
  width: auto;
  object-fit: contain;
}

/* Tiêu đề */
.title {
  color: #721c24; /* Màu chữ đậm hơn để dễ đọc */
  font-weight: bold;
}
</style>
