<script setup></script>

<template>
  <div class="footer-container">
    <!-- Logo -->
    <img src="/src/assets/logo/logo4.png" alt="Logo" class="logo me-3" />

    <!-- Chữ cạnh logo -->
    <div class="footer-text-container">
      <span class="footer-text">Website bán giày thể thao SportShoes</span>
      <span class="footer-subtext">Hệ thống bán giày thể thao số 1 Việt Nam</span>
    </div>
  </div>
</template>

<style scoped>
.footer-container {
  width: 100%;
  background-color: #f8d7da;
  /* Màu hồng nhạt */
  border-top: 3px solid #f1aeb5;
  /* Viền đậm hơn */
  color: #721c24;
  /* Màu chữ đậm hơn */
  display: flex;
  align-items: center;
  justify-content: start;
  padding: 10px;
  border-radius: 10px;
  /* Bo góc */
}

/* Logo */
.logo {
  height: 80px;
  width: auto;
  object-fit: contain;
}

.footer-text-container {
  display: flex;
  flex-direction: column; /* Sắp xếp theo cột để xuống dòng */
}

.footer-text {
  font-size: 1.2rem; /* Tăng kích thước chữ */
  font-weight: 600;
}

.footer-subtext {
  font-size: 0.8rem;
  font-weight: 500;
}
</style>
