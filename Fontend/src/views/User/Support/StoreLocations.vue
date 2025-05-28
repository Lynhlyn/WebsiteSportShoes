<script setup>
import '@/assets/main.css'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import Header from "@/components/User/Header.vue";
import Footer from "@/components/User/Footer.vue";

const stores = [
    {
        name: 'SportShoes Vincom Bà Triệu',
        address: '191 Bà Triệu, Hai Bà Trưng, Hà Nội',
        phone: '024 3974 xxxx',
        hours: '9:00 - 22:00',
        mapUrl: 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3723.843811161554!2d105.84965527485614!3d21.000520480639516!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ab9d1973cd2f%3A0x8c0338c8e7a7a2c7!2sVincom%20Center%20Ba%20Trieu!5e0!3m2!1sen!2s!4v1709557547943!5m2!1sen!2s'
    },
    {
        name: 'SportShoes Lotte Center',
        address: '54 Liễu Giai, Ba Đình, Hà Nội',
        phone: '024 3333 xxxx',
        hours: '9:00 - 22:00',
        mapUrl: 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3723.843811161554!2d105.84965527485614!3d21.000520480639516!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ab9d1973cd2f%3A0x8c0338c8e7a7a2c7!2sLotte%20Center%20Hanoi!5e0!3m2!1sen!2s!4v1709557547943!5m2!1sen!2s'
    },
    {
        name: 'SportShoes Aeon Mall Long Biên',
        address: '27 Cổ Linh, Long Biên, Hà Nội',
        phone: '024 3999 xxxx',
        hours: '9:00 - 22:00',
        mapUrl: 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3723.843811161554!2d105.84965527485614!3d21.000520480639516!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ab9d1973cd2f%3A0x8c0338c8e7a7a2c7!2sAeon%20Mall%20Long%20Bien!5e0!3m2!1sen!2s!4v1709557547943!5m2!1sen!2s'
    }
];

</script>

<template>
    <div class="container my-4">
        <!-- Navbar -->
        <Header />
        

        <div class="stores-content my-5">
            <h1 class="text-primary mb-4">Hệ thống cửa hàng</h1>

            <div class="row g-4">
                <div v-for="(store, index) in stores" :key="index" class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <div class="row">
                                <!-- Store Info -->
                                <div class="col-md-4">
                                    <h2 class="h4 text-primary mb-3">{{ store.name }}</h2>
                                    <div class="store-info">
                                        <p>
                                            <i class="fas fa-map-marker-alt text-danger me-2"></i>
                                            {{ store.address }}
                                        </p>
                                        <p>
                                            <i class="fas fa-phone text-success me-2"></i>
                                            {{ store.phone }}
                                        </p>
                                        <p>
                                            <i class="fas fa-clock text-primary me-2"></i>
                                            {{ store.hours }}
                                        </p>
                                    </div>
                                </div>

                                <!-- Store Map -->
                                <div class="col-md-8">
                                    <div class="ratio ratio-16x9">
                                        <iframe
                                            :src="store.mapUrl"
                                            width="600"
                                            height="450"
                                            style="border:0;"
                                            allowfullscreen=""
                                            loading="lazy"
                                            referrerpolicy="no-referrer-when-downgrade">
                                        </iframe>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <Footer />
    </div>
</template>

<style scoped>
.stores-content {
    line-height: 1.6;
}

.card {
    transition: transform 0.3s ease;
}

.card:hover {
    transform: translateY(-5px);
}

.store-info {
    color: #666;
}

.store-info p {
    margin-bottom: 0.5rem;
}

.ratio {
    border-radius: 0.5rem;
    overflow: hidden;
}
</style>