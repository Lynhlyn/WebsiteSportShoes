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
                            <RouterLink to="/chinh-sach-doi-tra" class="nav-link active">
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
                        <h1 class="text-primary mb-4">Chính sách đổi trả</h1>

                        <div class="policy-content">
                            <h2 class="h4 mb-3">1. Điều kiện đổi trả</h2>
                            <ul>
                                <li>Sản phẩm còn nguyên tem, nhãn và chưa qua sử dụng</li>
                                <li>Thời gian đổi trả trong vòng 7 ngày kể từ ngày nhận hàng</li>
                                <li>Sản phẩm không bị hư hỏng, bẩn, biến dạng</li>
                                <li>Có đầy đủ hóa đơn mua hàng</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">2. Các trường hợp được đổi trả</h2>
                            <ul>
                                <li>Sản phẩm bị lỗi từ nhà sản xuất</li>
                                <li>Sản phẩm không đúng size, màu sắc như đã đặt</li>
                                <li>Sản phẩm giao không đúng mẫu</li>
                                <li>Chất lượng sản phẩm không đúng như mô tả</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">3. Quy trình đổi trả</h2>
                            <ol>
                                <li>Liên hệ với bộ phận CSKH qua hotline hoặc email</li>
                                <li>Cung cấp thông tin đơn hàng và lý do đổi trả</li>
                                <li>Nhận mã đổi trả và hướng dẫn đóng gói</li>
                                <li>Gửi sản phẩm về địa chỉ công ty</li>
                                <li>Nhận sản phẩm mới hoặc hoàn tiền</li>
                            </ol>

                            <h2 class="h4 mb-3 mt-4">4. Hình thức hoàn tiền</h2>
                            <ul>
                                <li>Chuyển khoản ngân hàng</li>
                                <li>Tiền mặt tại cửa hàng</li>
                                <li>Voucher mua hàng</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">5. Thời gian xử lý</h2>
                            <p>Thời gian xử lý đổi trả và hoàn tiền:</p>
                            <ul>
                                <li>Xác nhận yêu cầu: 24h làm việc</li>
                                <li>Kiểm tra sản phẩm: 1-2 ngày làm việc</li>
                                <li>Hoàn tiền: 3-5 ngày làm việc</li>
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