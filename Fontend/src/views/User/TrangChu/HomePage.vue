<script setup>
import '@/assets/main.css'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import Header from "@/components/User/Header.vue";
import Footer from "@/components/User/Footer.vue";
import Chatbot from "@/components/User/Chatbot.vue"
import Banner from "@/components/User/Banner.vue"
</script>

<template>
      <div class="container my-4">
            <!-- <Chatbot /> -->
             <!-- <Chatbot /> -->
            <!-- Header Carousel -->
            <Header />

            <Banner />
            <!-- Main Content -->
            <router-view />

            <!-- Footer -->
            <Footer />
      </div>
</template>
