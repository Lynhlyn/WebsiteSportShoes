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
                            <RouterLink to="/chinh-sach-van-chuyen" class="nav-link active">
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
                        <h1 class="text-primary mb-4">Chính sách vận chuyển</h1>
                        
                        <div class="policy-content">
                            <h2 class="h4 mb-3">1. Phạm vi vận chuyển</h2>
                            <p>SportShoes Việt Nam cung cấp dịch vụ vận chuyển trên toàn quốc thông qua các đối tác vận chuyển uy tín.</p>

                            <h2 class="h4 mb-3 mt-4">2. Thời gian vận chuyển</h2>
                            <ul class="list-unstyled">
                                <li>✓ Nội thành Hồ Chí Minh: 1-2 ngày làm việc</li>
                                <li>✓ Các tỉnh miền Nam: 2-3 ngày làm việc</li>
                                <li>✓ Các tỉnh miền Trung và miền Bắc: 3-5 ngày làm việc</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">3. Phí vận chuyển</h2>
                            <p>Phí vận chuyển được tính dựa trên:</p>
                            <ul>
                                <li>Khoảng cách vận chuyển</li>
                                <li>Trọng lượng và kích thước sản phẩm</li>
                                <li>Khu vực giao hàng</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">4. Miễn phí vận chuyển</h2>
                            <p>Áp dụng miễn phí vận chuyển cho:</p>
                            <ul>
                                <li>Đơn hàng từ 2.000.000đ trở lên</li>
                                <li>Khách hàng thành viên VIP</li>
                                <li>Các chương trình khuyến mãi đặc biệt</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">5. Quy trình vận chuyển</h2>
                            <ol>
                                <li>Xác nhận đơn hàng</li>
                                <li>Đóng gói sản phẩm</li>
                                <li>Bàn giao cho đơn vị vận chuyển</li>
                                <li>Theo dõi đơn hàng</li>
                                <li>Giao hàng và xác nhận</li>
                            </ol>
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