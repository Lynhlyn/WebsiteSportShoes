<script setup>
import { RouterLink } from 'vue-router';
import '@/assets/main.css'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import Header from "@/components/User/Header.vue";
import Footer from "@/components/User/Footer.vue";
</script>

<template>
    <div class="container my-4">
        <!-- Navbar -->
        <Header />
        

        <div class="row my-5">
            <!-- Policy Navigation Sidebar -->
            <div class="col-md-3 mb-4">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title text-primary mb-3">Chính sách</h5>
                        <div class="nav flex-column nav-pills">
                            <RouterLink to="/chinh-sach-van-chuyen" class="nav-link">
                                Chính sách vận chuyển
                            </RouterLink>
                            <RouterLink to="/chinh-sach-doi-tra" class="nav-link">
                                Chính sách đổi trả
                            </RouterLink>
                            <RouterLink to="/chinh-sach-bao-mat" class="nav-link">
                                Chính sách bảo mật
                            </RouterLink>
                            <RouterLink to="/chinh-sach-kiem-hang" class="nav-link">
                                Chính sách kiểm hàng
                            </RouterLink>
                            <RouterLink to="/nghia-vu-nguoi-ban-va-khach-hang" class="nav-link active">
                                Nghĩa vụ của người bán và khách hàng
                            </RouterLink>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Policy Content -->
            <div class="col-md-9">
                <div class="card shadow-sm">
                    <div class="card-body policy-content">
                        <h1 class="text-primary mb-4">Nghĩa vụ của người bán và khách hàng</h1>

                        <div class="policy-content">
                            <h2 class="h4 mb-3">1. Nghĩa vụ của người bán</h2>
                            <h3 class="h5 mb-2">Về sản phẩm:</h3>
                            <ul>
                                <li>Cung cấp sản phẩm chính hãng 100%</li>
                                <li>Đảm bảo chất lượng sản phẩm</li>
                                <li>Cung cấp thông tin chính xác về sản phẩm</li>
                                <li>Đóng gói sản phẩm an toàn</li>
                            </ul>

                            <h3 class="h5 mb-2 mt-3">Về dịch vụ:</h3>
                            <ul>
                                <li>Tư vấn nhiệt tình, chuyên nghiệp</li>
                                <li>Giao hàng đúng hẹn</li>
                                <li>Hỗ trợ đổi trả theo quy định</li>
                                <li>Bảo hành theo chính sách</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">2. Nghĩa vụ của khách hàng</h2>
                            <h3 class="h5 mb-2">Khi đặt hàng:</h3>
                            <ul>
                                <li>Cung cấp thông tin chính xác</li>
                                <li>Thanh toán đúng hạn</li>
                                <li>Kiểm tra kỹ thông tin đơn hàng</li>
                                <li>Đọc kỹ các chính sách và điều khoản</li>
                            </ul>

                            <h3 class="h5 mb-2 mt-3">Khi nhận hàng:</h3>
                            <ul>
                                <li>Kiểm tra hàng trước khi nhận</li>
                                <li>Ký xác nhận đầy đủ</li>
                                <li>Giữ lại hóa đơn và packaging</li>
                                <li>Thông báo ngay nếu có vấn đề</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">3. Quyền lợi của các bên</h2>
                            <h3 class="h5 mb-2">Người bán:</h3>
                            <ul>
                                <li>Từ chối các yêu cầu không hợp lý</li>
                                <li>Yêu cầu thanh toán đúng hạn</li>
                                <li>Điều chỉnh giá và chính sách</li>
                                <li>Ngừng cung cấp dịch vụ nếu vi phạm</li>
                            </ul>

                            <h3 class="h5 mb-2 mt-3">Khách hàng:</h3>
                            <ul>
                                <li>Được đổi trả theo quy định</li>
                                <li>Được bảo hành theo chính sách</li>
                                <li>Được tư vấn và hỗ trợ</li>
                                <li>Được bảo vệ thông tin cá nhân</li>
                            </ul>
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
.policy-content {
    line-height: 1.6;
}

.policy-content h2 {
    color: #0056b3;
}

.policy-content h3 {
    color: #28a745;
}

.policy-content ul, .policy-content ol {
    padding-left: 20px;
}

.policy-content li {
    margin-bottom: 10px;
}

.nav-link {
    color: #666;
    padding: 0.8rem 1rem;
    border-radius: 4px;
    margin-bottom: 0.5rem;
    transition: all 0.3s ease;
}

.nav-link:hover {
    background-color: #e9ecef;
    color: #007bff;
    transform: translateX(5px);
}

.nav-link.active {
    background-color: #007bff;
    color: white;
    font-weight: 500;
}
</style>