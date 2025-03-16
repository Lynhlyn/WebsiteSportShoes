<script setup>
import '@/assets/main.css'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import Header from "@/components/User/Header.vue";
import Navbar from "@/components/User/Navbar.vue";
import Footer from "@/components/User/Footer.vue";
</script>

<template>
      <div class="container my-4">
            <!-- Navbar -->
            <Navbar />
            
            <!-- Header Carousel -->
            <Header />

            <!-- Main Content -->
            <router-view />

            <!-- Footer -->
            <Footer />
      </div>
</template>
