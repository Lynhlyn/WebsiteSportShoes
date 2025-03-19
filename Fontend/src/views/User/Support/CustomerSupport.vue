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


        <div class="support-content my-5">
            <h1 class="text-primary mb-4">Hỗ trợ khách hàng</h1>

            <div class="row g-4">
                <!-- Hotline Support -->
                <div class="col-md-6">
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-3">
                                <i class="fas fa-phone-alt me-2"></i>Hotline
                            </h2>
                            <p class="mb-2">Gọi ngay cho chúng tôi:</p>
                            <h3 class="h2 text-danger">1900 xxxx</h3>
                            <p class="text-muted">Thời gian làm việc: 8:00 - 22:00 (Thứ 2 - Chủ nhật)</p>
                        </div>
                    </div>
                </div>

                <!-- Email Support -->
                <div class="col-md-6">
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-3">
                                <i class="fas fa-envelope me-2"></i>Email
                            </h2>
                            <p class="mb-2">Gửi email cho chúng tôi:</p>
                            <h3 class="h4">support@sportshoes.vn</h3>
                            <p class="text-muted">Phản hồi trong vòng 24h làm việc</p>
                        </div>
                    </div>
                </div>

                <!-- FAQ Section -->
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-4">Câu hỏi thường gặp</h2>
                            <div class="accordion" id="faqAccordion">
                                <div class="accordion-item">
                                    <h3 class="accordion-header">
                                        <button class="accordion-button" type="button" data-bs-toggle="collapse"
                                            data-bs-target="#faq1">
                                            Làm sao để đặt hàng?
                                        </button>
                                    </h3>
                                    <div id="faq1" class="accordion-collapse collapse show" data-bs-parent="#faqAccordion">
                                        <div class="accordion-body">
                                            Bạn có thể đặt hàng trực tuyến thông qua website hoặc gọi điện đến hotline. Chúng tôi sẽ hướng dẫn bạn chi tiết các bước đặt hàng.
                                        </div>
                                    </div>
                                </div>

                                <div class="accordion-item">
                                    <h3 class="accordion-header">
                                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                            data-bs-target="#faq2">
                                            Thời gian giao hàng là bao lâu?
                                        </button>
                                    </h3>
                                    <div id="faq2" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                        <div class="accordion-body">
                                            Thời gian giao hàng phụ thuộc vào khu vực của bạn:
                                            <ul>
                                                <li>Nội thành: 1-2 ngày</li>
                                                <li>Tỉnh thành khác: 3-5 ngày</li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>

                                <div class="accordion-item">
                                    <h3 class="accordion-header">
                                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                            data-bs-target="#faq3">
                                            Chính sách đổi trả như thế nào?
                                        </button>
                                    </h3>
                                    <div id="faq3" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                        <div class="accordion-body">
                                            Chúng tôi áp dụng chính sách đổi trả trong vòng 7 ngày kể từ ngày nhận hàng. Sản phẩm phải còn nguyên tem, nhãn và chưa qua sử dụng.
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Contact Form -->
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-4">Gửi yêu cầu hỗ trợ</h2>
                            <form>
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="name" class="form-label">Họ và tên</label>
                                            <input type="text" class="form-control" id="name" required>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="email" class="form-label">Email</label>
                                            <input type="email" class="form-control" id="email" required>
                                        </div>
                                    </div>
                                    <div class="col-12">
                                        <div class="form-group">
                                            <label for="subject" class="form-label">Tiêu đề</label>
                                            <input type="text" class="form-control" id="subject" required>
                                        </div>
                                    </div>
                                    <div class="col-12">
                                        <div class="form-group">
                                            <label for="message" class="form-label">Nội dung</label>
                                            <textarea class="form-control" id="message" rows="5" required></textarea>
                                        </div>
                                    </div>
                                    <div class="col-12">
                                        <button type="submit" class="btn btn-primary">Gửi yêu cầu</button>
                                    </div>
                                </div>
                            </form>
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
.support-content {
    line-height: 1.6;
}

.card {
    transition: transform 0.3s ease;
}

.card:hover {
    transform: translateY(-5px);
}

.accordion-button:not(.collapsed) {
    background-color: #e7f1ff;
    color: #0d6efd;
}

.accordion-button:focus {
    box-shadow: none;
    border-color: rgba(0, 0, 0, 0.125);
}

.form-control:focus {
    border-color: #86b7fe;
    box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}
</style>