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

        <div class="about-content my-5">
            <h1 class="text-primary mb-4">Giới thiệu về SportShoes</h1>

            <div class="row g-4">
                <!-- Company Overview -->
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-3">Về chúng tôi</h2>
                            <p>SportShoes là thương hiệu bán lẻ giày thể thao hàng đầu tại Việt Nam, chuyên cung cấp các sản phẩm giày thể thao chính hãng từ các thương hiệu nổi tiếng như Nike, Adidas, Puma, Jordan và nhiều hãng khác.</p>
                            <p>Với hơn 10 năm kinh nghiệm trong ngành, chúng tôi tự hào mang đến cho khách hàng những sản phẩm chất lượng cao cùng dịch vụ chuyên nghiệp.</p>
                        </div>
                    </div>
                </div>

                <!-- Vision & Mission -->
                <div class="col-md-6">
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-3">Tầm nhìn</h2>
                            <p>Trở thành thương hiệu bán lẻ giày thể thao số 1 tại Việt Nam, mang đến cho khách hàng những sản phẩm chất lượng và trải nghiệm mua sắm tuyệt vời nhất.</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-3">Sứ mệnh</h2>
                            <p>Cung cấp các sản phẩm giày thể thao chính hãng với giá cả hợp lý, đồng thời mang đến dịch vụ khách hàng xuất sắc và trải nghiệm mua sắm thuận tiện.</p>
                        </div>
                    </div>
                </div>

                <!-- Store Location Map -->
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-3">Vị trí cửa hàng chính</h2>
                            <div class="ratio ratio-16x9">
                                <iframe
                                    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3723.843811161554!2d105.84965527485614!3d21.000520480639516!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ab9d1973cd2f%3A0x8c0338c8e7a7a2c7!2sVincom%20Center%20Ba%20Trieu!5e0!3m2!1sen!2s!4v1709557547943!5m2!1sen!2s"
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

                <!-- Company Values -->
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-4">Giá trị cốt lõi</h2>
                            <div class="row g-4">
                                <div class="col-md-3 col-6">
                                    <div class="text-center">
                                        <i class="fas fa-check-circle text-primary mb-3 fa-2x"></i>
                                        <h3 class="h5">Chất lượng</h3>
                                        <p class="small">Cam kết 100% hàng chính hãng</p>
                                    </div>
                                </div>
                                <div class="col-md-3 col-6">
                                    <div class="text-center">
                                        <i class="fas fa-heart text-primary mb-3 fa-2x"></i>
                                        <h3 class="h5">Tận tâm</h3>
                                        <p class="small">Phục vụ khách hàng tận tình</p>
                                    </div>
                                </div>
                                <div class="col-md-3 col-6">
                                    <div class="text-center">
                                        <i class="fas fa-sync text-primary mb-3 fa-2x"></i>
                                        <h3 class="h5">Đổi mới</h3>
                                        <p class="small">Liên tục cập nhật xu hướng</p>
                                    </div>
                                </div>
                                <div class="col-md-3 col-6">
                                    <div class="text-center">
                                        <i class="fas fa-shield-alt text-primary mb-3 fa-2x"></i>
                                        <h3 class="h5">Uy tín</h3>
                                        <p class="small">Xây dựng niềm tin với khách hàng</p>
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
.about-content {
    line-height: 1.6;
}

.card {
    transition: transform 0.3s ease;
}

.card:hover {
    transform: translateY(-5px);
}

.ratio {
    border-radius: 0.5rem;
    overflow: hidden;
}
</style>