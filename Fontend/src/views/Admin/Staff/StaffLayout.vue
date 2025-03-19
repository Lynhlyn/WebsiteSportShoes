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
    // Bán hàng
      titleHeader.value = 'Bán hàng - Sale';
      document.title = 'Bán hàng Sale';
});
</script>

<template>
  <div class="wrapper">
    <div class="sale-layout">
      <p>Staff layout</p>
      <RouterView />
    </div>
  </div>
</template>

<style scoped>
/* Bố cục chính */

</style>