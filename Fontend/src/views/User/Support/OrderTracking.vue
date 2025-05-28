<script setup>
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
        

        <div class="tracking-content my-5">
            <h1 class="text-primary mb-4">Kiểm tra đơn hàng</h1>

            <div class="row g-4">
                <!-- Order Tracking Form -->
                <div class="col-md-6 mx-auto">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <form>
                                <div class="mb-4">
                                    <label for="orderNumber" class="form-label">Mã đơn hàng</label>
                                    <input type="text" class="form-control" id="orderNumber" 
                                           placeholder="Nhập mã đơn hàng của bạn" required>
                                </div>
                                <div class="mb-4">
                                    <label for="phone" class="form-label">Số điện thoại</label>
                                    <input type="tel" class="form-control" id="phone" 
                                           placeholder="Nhập số điện thoại đặt hàng" required>
                                </div>
                                <button type="submit" class="btn btn-primary w-100">Kiểm tra</button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Tracking Instructions -->
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-4">Hướng dẫn kiểm tra đơn hàng</h2>
                            <div class="row g-4">
                                <div class="col-md-4">
                                    <div class="text-center">
                                        <i class="fas fa-search text-primary mb-3 fa-2x"></i>
                                        <h3 class="h5">Bước 1</h3>
                                        <p>Nhập mã đơn hàng và số điện thoại đặt hàng</p>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="text-center">
                                        <i class="fas fa-mouse-pointer text-primary mb-3 fa-2x"></i>
                                        <h3 class="h5">Bước 2</h3>
                                        <p>Nhấn nút "Kiểm tra" để xem thông tin</p>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="text-center">
                                        <i class="fas fa-truck text-primary mb-3 fa-2x"></i>
                                        <h3 class="h5">Bước 3</h3>
                                        <p>Theo dõi trạng thái đơn hàng của bạn</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Order Status Example -->
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h2 class="h4 text-primary mb-4">Trạng thái đơn hàng</h2>
                            <div class="tracking-status">
                                <div class="status-item completed">
                                    <div class="status-icon">
                                        <i class="fas fa-check-circle"></i>
                                    </div>
                                    <div class="status-content">
                                        <h3 class="h6">Đặt hàng thành công</h3>
                                        <p class="small text-muted">Đơn hàng đã được xác nhận</p>
                                    </div>
                                </div>
                                <div class="status-item completed">
                                    <div class="status-icon">
                                        <i class="fas fa-box"></i>
                                    </div>
                                    <div class="status-content">
                                        <h3 class="h6">Đóng gói</h3>
                                        <p class="small text-muted">Đơn hàng đang được đóng gói</p>
                                    </div>
                                </div>
                                <div class="status-item active">
                                    <div class="status-icon">
                                        <i class="fas fa-shipping-fast"></i>
                                    </div>
                                    <div class="status-content">
                                        <h3 class="h6">Đang giao hàng</h3>
                                        <p class="small text-muted">Đơn hàng đang trên đường giao</p>
                                    </div>
                                </div>
                                <div class="status-item">
                                    <div class="status-icon">
                                        <i class="fas fa-home"></i>
                                    </div>
                                    <div class="status-content">
                                        <h3 class="h6">Giao hàng thành công</h3>
                                        <p class="small text-muted">Đơn hàng đã được giao</p>
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
.tracking-content {
    line-height: 1.6;
}

.card {
    transition: transform 0.3s ease;
}

.card:hover {
    transform: translateY(-5px);
}

.form-control:focus {
    border-color: #86b7fe;
    box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.tracking-status {
    position: relative;
    padding: 20px 0;
}

.status-item {
    display: flex;
    align-items: center;
    margin-bottom: 30px;
    position: relative;
}

.status-item:not(:last-child)::after {
    content: '';
    position: absolute;
    left: 25px;
    top: 40px;
    height: calc(100% - 10px);
    width: 2px;
    background-color: #dee2e6;
}

.status-icon {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background-color: #f8f9fa;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20px;
    border: 2px solid #dee2e6;
    z-index: 1;
}

.status-item.completed .status-icon {
    background-color: #28a745;
    border-color: #28a745;
    color: white;
}

.status-item.active .status-icon {
    background-color: #007bff;
    border-color: #007bff;
    color: white;
}

.status-content {
    flex: 1;
}

.status-content h3 {
    margin: 0;
    color: #333;
}

.status-item.completed .status-content h3,
.status-item.active .status-content h3 {
    color: #007bff;
}
</style>