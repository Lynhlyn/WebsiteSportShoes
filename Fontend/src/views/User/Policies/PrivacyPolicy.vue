<script setup>
import { RouterLink } from 'vue-router';
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
                            <RouterLink to="/chinh-sach-bao-mat" class="nav-link active">
                                Chính sách bảo mật
                            </RouterLink>
                            <RouterLink to="/chinh-sach-kiem-hang" class="nav-link">
                                Chính sách kiểm hàng
                            </RouterLink>
                            <RouterLink to="/nghia-vu-nguoi-ban-va-khach-hang" class="nav-link">
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
                        <h1 class="text-primary mb-4">Chính sách bảo mật</h1>

                        <div class="policy-content">
                            <h2 class="h4 mb-3">1. Thông tin thu thập</h2>
                            <p>Chúng tôi thu thập các thông tin sau:</p>
                            <ul>
                                <li>Thông tin cá nhân (tên, địa chỉ, số điện thoại, email)</li>
                                <li>Thông tin đơn hàng</li>
                                <li>Lịch sử mua hàng</li>
                                <li>Thông tin thanh toán</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">2. Mục đích sử dụng thông tin</h2>
                            <ul>
                                <li>Xử lý đơn hàng và giao hàng</li>
                                <li>Cung cấp dịch vụ khách hàng</li>
                                <li>Gửi thông tin khuyến mãi (nếu được cho phép)</li>
                                <li>Phân tích và cải thiện dịch vụ</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">3. Bảo mật thông tin</h2>
                            <p>Chúng tôi cam kết:</p>
                            <ul>
                                <li>Mã hóa thông tin thanh toán</li>
                                <li>Không chia sẻ thông tin với bên thứ ba</li>
                                <li>Bảo vệ dữ liệu khách hàng</li>
                                <li>Tuân thủ quy định pháp luật về bảo mật</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">4. Quyền của khách hàng</h2>
                            <p>Khách hàng có quyền:</p>
                            <ul>
                                <li>Yêu cầu xem thông tin cá nhân</li>
                                <li>Yêu cầu chỉnh sửa thông tin</li>
                                <li>Yêu cầu xóa thông tin</li>
                                <li>Từ chối nhận email marketing</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">5. Liên hệ</h2>
                            <p>Mọi thắc mắc về chính sách bảo mật, vui lòng liên hệ:</p>
                            <ul>
                                <li>Email: privacy@sportshoes.vn</li>
                                <li>Hotline: 1900 xxxx</li>
                                <li>Địa chỉ: Tầng 6, Tòa Friendship, Số 3, Đường Lê Duẩn, Hồ Chí Minh</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
            <!-- Footer -->
            <Footer />
</template>

<style scoped>
.policy-content {
    line-height: 1.6;
}

.policy-content h2 {
    color: #0056b3;
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