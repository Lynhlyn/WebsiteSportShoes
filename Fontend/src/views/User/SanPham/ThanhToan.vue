<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const gioHang = ref([]);
const voucherList = ref([]);
const selectedVoucher = ref(null);
const discountAmount = ref(0); // 🔥 Thêm biến này
const hoTen = ref('');
const sdt = ref('');
const diaChi = ref('');
const tinh = ref('');
const quan = ref('');
const xa = ref('');
const selectedPaymentMethod = ref('');
const showImage = ref(false); // Controls the display of QR code

/// Danh sách địa phương từ API
const danhSachTinh = ref([]);
const danhSachQuan = ref([]);
const danhSachXa = ref([]);
const loading = ref(false);
const shippingFee = ref(0); // Phí vận chuyển


// Tải giỏ hàng
const loadGioHang = () => {
    try {
        const stored = localStorage.getItem("gioHang");
        gioHang.value = stored ? JSON.parse(stored) : [];
    } catch (e) {
        console.error("Không thể parse dữ liệu từ localStorage:", e);
        gioHang.value = [];
    }
};

// Lưu giỏ hàng vào localStorage
const saveGioHang = () => {
    localStorage.setItem("gioHang", JSON.stringify(gioHang.value));
    window.dispatchEvent(new CustomEvent("gioHangUpdated"));
};

// Lấy mã giảm giá
const fetchVouchers = async () => {
    try {
        const response = await axios.get("http://localhost:8080/admin/voucher");
        const now = new Date();
        voucherList.value = response.data.filter(voucher => {
            return new Date(voucher.ngayBatDau) <= now && new Date(voucher.ngayKetThuc) >= now;
        });
    } catch (error) {
        console.error("Error fetching vouchers:", error);
    }
};

// Áp dụng giảm giá
const applyDiscount = () => {
    const voucher = selectedVoucher.value;

    if (!voucher) {
        discountAmount.value = 0;
        return;
    }

    const total = totalAmountBeforeDiscount.value;

    if (isNaN(total) || total <= 0) {
        discountAmount.value = 0;
        return;
    }

    if (total < voucher.giaTriToiThieu) {
        alert(`Giảm giá chỉ áp dụng cho đơn hàng từ ${voucher.giaTriToiThieu.toLocaleString()} VNĐ.`);
        discountAmount.value = 0;
        selectedVoucher.value = null;
        return;
    }

    if (voucher.loaiVoucher === 0) {
        discountAmount.value = (total * voucher.giaTriGiam) / 100;
    } else {
        discountAmount.value = voucher.giaTriGiam;
    }
};

watch(selectedVoucher, applyDiscount);

// Tính tổng tiền trước khi giảm giá
const totalAmountBeforeDiscount = computed(() => {
    return gioHang.value.reduce((total, item) => {
        const price = item?.giaBan || 0;
        return total + (price * item.soLuong);
    }, 0);
});

// Tổng tiền sau khi giảm giá
const totalAmountAfterDiscount = computed(() => {
    return Math.max(totalAmountBeforeDiscount.value - discountAmount.value + shippingFee.value, 0);
});

// Format tiền
const tongTienFormatted = computed(() => {
    return totalAmountBeforeDiscount.value.toLocaleString("vi-VN") + " đ";
});

// Lấy danh sách tỉnh/thành phố từ API
const layDanhSachTinh = async () => {
    loading.value = true;
    try {
        const response = await axios.get('https://provinces.open-api.vn/api/?depth=1');
        danhSachTinh.value = response.data.map(t => ({ code: t.code, name: t.name }));
    } catch (error) {
        console.error("Lỗi tải tỉnh/thành phố:", error);
    } finally {
        loading.value = false;
    }
};

// Cập nhật quận/huyện theo tỉnh đã chọn
const capNhatQuan = async () => {
    if (!tinh.value) {
        danhSachQuan.value = [];
        danhSachXa.value = [];
        quan.value = "";
        xa.value = "";
        return;
    }

    loading.value = true;
    try {
        const response = await axios.get(`https://provinces.open-api.vn/api/p/${tinh.value}?depth=2`);
        danhSachQuan.value = response.data.districts.map(q => ({ code: q.code, name: q.name }));
    } catch (error) {
        console.error("Lỗi tải quận/huyện:", error);
    } finally {
        loading.value = false;
    }

    quan.value = "";
    xa.value = "";
};

// Cập nhật xã/phường theo quận đã chọn
const capNhatXa = async () => {
    if (!quan.value) {
        danhSachXa.value = [];
        xa.value = "";
        return;
    }

    loading.value = true;
    try {
        const response = await axios.get(`https://provinces.open-api.vn/api/d/${quan.value}?depth=2`);
        danhSachXa.value = response.data.wards.map(x => ({ code: x.code, name: x.name }));
    } catch (error) {
        console.error("Lỗi tải xã/phường:", error);
    } finally {
        loading.value = false;
    }

    xa.value = "";
};

// Tính phí vận chuyển (ví dụ sử dụng giao hàng tiết kiệm)
const tinhPhiVanChuyen = () => {
    if (tinh && quan && xa) {
        // Giả định phí vận chuyển cố định, có thể thay đổi theo API
        shippingFee.value = 30000; // 30,000 VND
    }
};

const layThongTinNguoiDung = async () => {
    try {
        // Lấy thông tin người dùng từ localStorage
        const user = JSON.parse(localStorage.getItem("user"));
        if (user && user.tenDangNhap) {
            // Gọi API lấy thông tin khách hàng từ backend dựa trên user.id
            const response = await axios.get(`http://localhost:8080/khach-hang/account/${user.id}`);
            const khachHang = response.data;
            
            // Điền dữ liệu vào các trường thông tin
            hoTen.value = khachHang.hoTen || '';  // Điền Họ tên
            sdt.value = khachHang.soDienThoai || '';  // Điền Số điện thoại
            diaChi.value = khachHang.diaChiChiTiet || '';  // Optional
            tinh.value = khachHang.diaChi?.tinh || '';  // Optional
            quan.value = khachHang.diaChi?.quan || '';  // Optional
            xa.value = khachHang.diaChi?.xa || '';  // Optional
        } else {
            console.error("Không có thông tin người dùng.");
        }
    } catch (error) {
        console.error("Không thể lấy thông tin người dùng:", error);
    }
};

// Kiểm tra thông tin khách hàng trước khi thanh toán
const handlePayment = async () => {
    // Kiểm tra các trường thông tin
    if (!hoTen.value || !sdt.value || !diaChi.value || !tinh.value || !quan.value || !xa.value) {
        alert("Vui lòng nhập đầy đủ thông tin khách hàng trước khi thanh toán!");
        return;
    }

    // Kiểm tra phương thức thanh toán
    if (!selectedPaymentMethod.value) {
        alert("Vui lòng chọn phương thức thanh toán!");
        return;
    }

    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || !user.tenDangNhap) {
        alert("⚠️ Cần có thông tin tài khoản đăng nhập!");
        return;
    }

    // Tạo đối tượng khách hàng với ID lấy từ backend
    const khachHang = {
        id: user.id,  // Lấy id khách hàng từ thông tin đăng nhập
        diaChiChiTiet: diaChi.value,
        diaChi: {
            tinh: tinh.value,
            quan: quan.value,
            xa: xa.value
        },
        taiKhoan: {
            tenDangNhap: user?.tenDangNhap || null
        }
    };

    // Tạo đối tượng phương thức thanh toán với ID hợp lệ
    const phuongThucThanhToan = {
        id: 1,  // Đảm bảo phuongThucThanhToan có id hợp lệ
        tenPhuongThuc: selectedPaymentMethod.value
    };

    // Tạo đối tượng voucher nếu có (có thể là null hoặc một voucher cụ thể)
    const voucher = selectedVoucher.value ? {
        id: selectedVoucher.value.id,  // Sử dụng ID voucher đã chọn
        maVoucher: selectedVoucher.value.maVoucher,
        moTa: selectedVoucher.value.moTa,
        giaTriGiam: selectedVoucher.value.giaTriGiam
    } : null;

    // const recentOrder = JSON.parse(localStorage.getItem("recentOrder")) || [];

    // Tạo đối tượng đơn hàng từ các thông tin cần thiết
    const recentOrder = {
        maDonHang: "HD" + Math.floor(Math.random() * 1000000),  // Tạo mã đơn hàng ngẫu nhiên
        khachHang: khachHang,  // Đảm bảo khachHang không null
        phuongThucThanhToan: phuongThucThanhToan,  // Đảm bảo phương thức thanh toán có giá trị
        voucher: voucher,  // Thêm voucher nếu có
        loaiDonHang: true,  // Đặt loại đơn hàng là Online
        tongTien: totalAmountAfterDiscount.value,  // Tổng tiền sau khi giảm giá
        items: JSON.parse(JSON.stringify(gioHang.value)),  // Danh sách các sản phẩm trong giỏ hàng
        discount: discountAmount.value,  // Giá trị giảm giá
        chiPhiGiaoHang: shippingFee.value,  // Phí giao hàng (có thể được tính toán thêm)
        trangThai: selectedPaymentMethod.value === 'bank-transfer' ? 'Đang chờ thanh toán' : 'Chờ xác nhận',  // Trạng thái đơn hàng
        ngayTao: new Date().toISOString(),  // Ngày tạo đơn hàng
        ngaySua: new Date().toISOString(),  // Ngày sửa đơn hàng
    };

    localStorage.setItem("selectedVoucher", JSON.stringify(selectedVoucher.value));
    localStorage.setItem("discount", discountAmount.value);
    localStorage.setItem("shippingFee", shippingFee.value);

    const savedOrders = JSON.parse(localStorage.getItem("orders")) || [];
    savedOrders.push(recentOrder);
    localStorage.setItem("orders", JSON.stringify(savedOrders));

    try {
        const order = {
            ...recentOrder
        }
        // Gửi yêu cầu lưu đơn hàng vào backend
        console.log("Dữ liệu gửi lên:", order);
        const response = await axios.post("http://localhost:8080/don-hang/create-online", order);

        // localStorage.setItem("recentOrder", JSON.stringify(order)); 

        if (response.status === 200) {
            alert("Đơn hàng đã được lưu thành công!");

            // Lưu đơn hàng vào localStorage hoặc trạng thái toàn cục
            // localStorage.setItem("recentOrder", JSON.stringify(order));

            // Xử lý thanh toán thành công
            // if (selectedPaymentMethod.value === 'cod') {
            //     alert('Thanh toán khi nhận hàng thành công!');
            //     router.push('/thanh-toan-thanh-cong');
            // } else if (selectedPaymentMethod.value === 'bank-transfer') {
            //     showImage.value = true; // Hiện mã QR để người dùng quét
            // }
        } else {
            alert("Đã xảy ra lỗi khi lưu đơn hàng!");
        }
    } catch (error) {
        console.error("Lỗi khi lưu đơn hàng:", error);
        alert("Có lỗi khi lưu đơn hàng, vui lòng thử lại.");
    }
};

// Cập nhật trạng thái đơn hàng trong `DonMua.vue`
const updateOrderStatus = (orderId, newStatus) => {
  // Giả sử bạn có phương thức để tìm và cập nhật đơn hàng trong `DonMua.vue`
  const order = this.orders.find(o => o.maDonHang === orderId);
  if (order) {
    order.trangThai = newStatus;
  }
};

onMounted(() => {
    loadGioHang();
    fetchVouchers();
    layDanhSachTinh();
    tinhPhiVanChuyen();
    layThongTinNguoiDung();
});

</script>

<template>
    <div class="container py-5">
        <h3 class="fw-bold mb-4">🛒 Thanh toán</h3>
        <div v-if="gioHang.length === 0" class="alert alert-warning text-center">
            Giỏ hàng trống!
        </div>
        <div v-else class="row">
            <!-- Chi tiết đơn hàng -->
            <div class="col-lg-8">
                <div class="card mb-4">
                    <div class="card-body">
                        <!-- Hiển thị chi tiết các sản phẩm trong giỏ -->
                        <div v-for="(item, index) in gioHang" :key="index" class="row mb-4 border-bottom pb-3">
                            <div class="col-md-2">
                                <img :src="item.hinhAnh" class="img-fluid rounded" alt="Ảnh sản phẩm" />
                            </div>
                            <div class="col-md-6">
                                <h5 class="fw-semibold mb-1">{{ item.tenSanPham }}</h5>
                                <p class="mb-1">Mã SPCT: {{ item.maSPCT || item.id }}</p>
                                <p class="mb-1">Số lượng: {{ item.soLuong }}</p>
                                <p class="mb-1">Màu sắc: {{ item.mauSac.tenMau }}</p>
                                <p class="mb-1">Kích thước: {{ item.size.tenSize }}</p>
                                <p class="mb-1">Giá bán: {{ new Intl.NumberFormat("vi-VN").format(item.giaBan) }} đ</p>
                            </div>
                            <div class="col-md-4 text-md-end">
                                <div class="mt-2 fw-bold">
                                    {{ new Intl.NumberFormat("vi-VN").format(item.giaBan * item.soLuong) }} đ
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Thông tin thanh toán -->
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="fw-bold mb-3">Thanh toán</h5>
                        <div class="d-flex justify-content-between mb-3">
                            <span>Tổng tiền:</span>
                            <span class="fw-bold">{{ tongTienFormatted }}</span>
                        </div>

                        <!-- Mã giảm giá -->
                        <div class="mb-3">
                            <label class="block font-semibold">Mã Giảm Giá (Nếu Có)</label>
                            <div class="flex items-center space-x-2">
                                <select v-model="selectedVoucher" class="form-control">
                                    <option value="" disabled>Chọn Voucher</option>
                                    <option v-for="voucher in voucherList" :key="voucher.id" :value="voucher">
                                        {{ voucher.maVoucher }} - {{ voucher.moTa }}
                                    </option>
                                </select>
                            </div>
                        </div>

                        <!-- Phí vận chuyển và tổng tiền -->
                        <div class="mb-4 mt-8">
                            <div class="d-flex justify-content-between">
                                <p class="font-bold">Tổng Tiền (Trước Giảm): </p>
                                <p class="text-right">{{ totalAmountBeforeDiscount.toLocaleString() }} đ</p>
                            </div>
                            <div class="d-flex justify-content-between">
                                <p class="font-bold">Giảm Giá:</p>
                                <p class="text-right text-danger">-{{ discountAmount.toLocaleString() }} đ</p>
                            </div>
                            <div class="d-flex justify-content-between">
                                <p class="font-bold">Phí vận chuyển:</p>
                                <p class="text-right text-danger">+{{ shippingFee.toLocaleString() }} đ</p>
                            </div>
                            <div class="d-flex justify-content-between">
                                <p class="font-bold text-danger">Tổng Tiền Sau Giảm:</p>
                                <p class="text-right text-danger">{{ totalAmountAfterDiscount.toLocaleString() }} đ</p>
                            </div>
                        </div>

                        <!-- Thông tin vận chuyển -->
                        <h4 class="mt-4 text-danger">📦 Thông tin vận chuyển</h4>
                        <div class="row">
                            <div class="col-md-6">
                                <input type="text" class="form-control mb-3" placeholder="Họ tên" v-model="hoTen" required />
                            </div>
                            <div class="col-md-6">
                                <input type="text" class="form-control mb-3" placeholder="Số điện thoại" v-model="sdt" required />
                            </div>
                        </div>

                        <textarea class="form-control mb-3" placeholder="Địa chỉ chi tiết" v-model="diaChi" required></textarea>

                        <!-- Address Inputs -->
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <select class="form-control" v-model="tinh" @change="capNhatQuan">
                                    <option value="">-- Chọn tỉnh/thành phố --</option>
                                    <option v-for="t in danhSachTinh" :key="t.code" :value="t.code">{{ t.name }}</option>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <select class="form-control" v-model="quan" @change="capNhatXa">
                                    <option value="">-- Chọn quận/huyện --</option>
                                    <option v-for="q in danhSachQuan" :key="q.code" :value="q.code">{{ q.name }}</option>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <select class="form-control" v-model="xa">
                                    <option value="">-- Chọn xã/phường --</option>
                                    <option v-for="x in danhSachXa" :key="x.code" :value="x.code">{{ x.name }}</option>
                                </select>
                            </div>
                        </div>

                        <!-- Payment Method Selection -->
                        <h4 class="mt-4 text-danger">💳 Phương thức thanh toán</h4>
                        <div class="d-flex gap-3 mb-4">
                            <div>
                                <input type="radio" id="cod" value="cod" v-model="selectedPaymentMethod" />
                                <label for="cod" class="btn btn-outline-primary">🚚 Thanh toán khi nhận hàng</label>
                            </div>
                        </div>

                        <!-- Payment Button -->
                        <button class="btn btn-danger w-100 mt-3" @click="handlePayment">🧾 Thanh toán</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>


<style scoped>
img {
    object-fit: cover;
    width: 100%;
    height: 100%;
}

.card {
    border-radius: 15px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

input[readonly] {
    background-color: #fff;
}

#qr-image-container {
    position: relative;
    text-align: center;
}

#closeQRCode {
    position: absolute;
    top: 0;
    right: 0;
    font-size: 16px;
    background-color: transparent;
    border: none;
    color: red;
    cursor: pointer;
}
</style>
