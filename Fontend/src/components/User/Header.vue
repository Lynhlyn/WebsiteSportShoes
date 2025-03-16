<script setup>
import header1 from "@/assets/header/header1.webp";
import header2 from "@/assets/header/header2.webp";
import header3 from "@/assets/header/header3.webp";
import header4 from "@/assets/header/header4.webp";
import header5 from "@/assets/header/header5.webp";
</script>
<template>
      <div id="carouselExampleCaptions" class="carousel slide mb-4" data-bs-ride="carousel">
            <div class="carousel-indicators">
                  <button type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide-to="0" class="active"
                        aria-label="Slide 1"></button>
                  <button type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide-to="1"
                        aria-label="Slide 2"></button>
                  <button type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide-to="2"
                        aria-label="Slide 3"></button>
                  <button type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide-to="3"
                        aria-label="Slide 4"></button>
                  <button type="button" data-bs-target="#carouselExampleCaptions" data-bs-slide-to="4"
                        aria-label="Slide 5"></button>
            </div>
            <div class="carousel-inner">
                  <div class="carousel-item active">
                        <img :src="header1" class="d-block w-100" alt="Header 1" />
                  </div>
                  <div class="carousel-item">
                        <img :src="header2" class="d-block w-100" alt="Header 2" />
                  </div>
                  <div class="carousel-item">
                        <img :src="header3" class="d-block w-100" alt="Header 3" />
                  </div>
                  <div class="carousel-item">
                        <img :src="header4" class="d-block w-100" alt="Header 4" />
                  </div>
                  <div class="carousel-item">
                        <img :src="header5" class="d-block w-100" alt="Header 5" />
                  </div>
            </div>
            <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleCaptions"
                  data-bs-slide="prev">
                  <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                  <span class="visually-hidden">Previous</span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleCaptions"
                  data-bs-slide="next">
                  <span class="carousel-control-next-icon" aria-hidden="true"></span>
                  <span class="visually-hidden">Next</span>
            </button>
      </div>
</template>