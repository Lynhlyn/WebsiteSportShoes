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
                            <RouterLink to="/chinh-sach-kiem-hang" class="nav-link active">
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
                        <h1 class="text-primary mb-4">Chính sách kiểm hàng</h1>

                        <div class="policy-content">
                            <h2 class="h4 mb-3">1. Quy định kiểm hàng</h2>
                            <p>Khách hàng có quyền kiểm tra hàng trước khi nhận. Cụ thể:</p>
                            <ul>
                                <li>Kiểm tra tình trạng bên ngoài của gói hàng</li>
                                <li>Đối chiếu thông tin sản phẩm với đơn hàng</li>
                                <li>Kiểm tra số lượng và chất lượng sản phẩm</li>
                                <li>Thử size giày (nếu cần)</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">2. Thời gian kiểm hàng</h2>
                            <ul>
                                <li>Thời gian kiểm hàng tối đa: 5 phút</li>
                                <li>Có thể kéo dài thêm nếu cần thiết</li>
                                <li>Thông báo với nhân viên giao hàng nếu cần thêm thời gian</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">3. Quy trình kiểm hàng</h2>
                            <ol>
                                <li>Kiểm tra niêm phong gói hàng</li>
                                <li>Mở gói hàng cẩn thận</li>
                                <li>Đối chiếu sản phẩm với đơn hàng</li>
                                <li>Kiểm tra chất lượng sản phẩm</li>
                                <li>Xác nhận với nhân viên giao hàng</li>
                            </ol>

                            <h2 class="h4 mb-3 mt-4">4. Xử lý sự cố</h2>
                            <p>Trong trường hợp phát hiện vấn đề:</p>
                            <ul>
                                <li>Ghi chú vào biên bản giao hàng</li>
                                <li>Chụp ảnh làm bằng chứng</li>
                                <li>Liên hệ ngay với bộ phận CSKH</li>
                                <li>Từ chối nhận hàng nếu cần thiết</li>
                            </ul>

                            <h2 class="h4 mb-3 mt-4">5. Lưu ý quan trọng</h2>
                            <ul>
                                <li>Kiểm tra kỹ trước khi ký nhận</li>
                                <li>Giữ lại hóa đơn và biên bản giao hàng</li>
                                <li>Quay video quá trình kiểm hàng (nếu cần)</li>
                                <li>Không ký nhận nếu chưa kiểm tra kỹ</li>
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