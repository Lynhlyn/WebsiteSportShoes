<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

// Router
const router = useRouter();

const selectedVoucher = ref(null);
const danhSachVoucher = ref([]);
const giamGia = ref(0);
const phiVanChuyen = ref(0);

// Giỏ hàng
const gioHang = ref([]);
const loadGioHang = () => {
    gioHang.value = JSON.parse(localStorage.getItem("gioHang")) || [];
};

// Lấy danh sách mã voucher từ API
const layDanhSachVoucher = async () => {
    try {
        const response = await axios.get("http://localhost:8080/admin/voucher");
        danhSachVoucher.value = response.data.filter(voucher => {
            const now = new Date();
            return new Date(voucher.ngayBatDau) <= now && new Date(voucher.ngayKetThuc) >= now;
        }).map(v => ({
            id: v.id,
            maVoucher: v.maVoucher,
            moTa: v.moTa,
            giaTriGiam: v.giaTriGiam || 0, // Giảm giá theo % hoặc giá trị cố định
            giaTriToiThieu: v.giaTriToiThieu || 0, // Điều kiện tối thiểu để áp dụng voucher
            loaiVoucher: v.loaiVoucher || 1 // 1: Giảm theo số tiền, 0: Giảm theo phần trăm
        }));
    } catch (error) {
        console.error("Lỗi khi tải danh sách voucher:", error);
    }
};

// Tính tổng tiền trước giảm giá
const tongTienTruocGiam = computed(() => {
    return gioHang.value.reduce((acc, item) => acc + item.giaGiam * item.soLuong, 0);
});

// Cập nhật tổng tiền sau khi chọn voucher
const apDungVoucher = () => {
    giamGia.value = 0; // Reset giảm giá
    if (!selectedVoucher.value) return;

    const voucher = danhSachVoucher.value.find(v => v.id === selectedVoucher.value);
    if (!voucher || tongTienTruocGiam.value < voucher.giaTriToiThieu) return;

    if (voucher.loaiVoucher === 0) { // Giảm theo phần trăm
        giamGia.value = Math.min(voucher.giaTriToiThieu * (voucher.giaTriGiam / 100), tongTienTruocGiam.value);
    } else if (voucher.loaiVoucher === 1) { // Giảm theo số tiền
        giamGia.value = Math.min(voucher.giaTriGiam, tongTienTruocGiam.value);
    }
};

// Tính tổng tiền sau giảm
const tongTienSauGiam = computed(() => {
    return Math.max(tongTienTruocGiam.value - giamGia.value, 0);
});

// Tính phí vận chuyển
const tinhPhiVanChuyen = computed(() => {
    return tongTienSauGiam.value >= 1000000 ? 0 : 30000;
});

// Tổng tiền thanh toán cuối cùng
const tongTienThanhToan = computed(() => {
    return tongTienSauGiam.value + tinhPhiVanChuyen.value;
});

// Format tiền VND
const formatVND = (value) => {
    return new Intl.NumberFormat("vi-VN").format(value) + " đ";
};

// Thông tin người nhận hàng
const hoTen = ref('');
const sdt = ref('');
const diaChi = ref('');
const tinh = ref('');
const quan = ref('');
const xa = ref('');

// Danh sách địa phương từ API
const danhSachTinh = ref([]);
const danhSachQuan = ref([]);
const danhSachXa = ref([]);
const loading = ref(false);

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

// Xóa sản phẩm khỏi giỏ hàng
const xoaSanPham = (index) => {
    if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này không?")) {
        gioHang.value.splice(index, 1);
        localStorage.setItem("gioHang", JSON.stringify(gioHang.value));
    }
};

// Đặt hàng
const datHang = () => {
    if (!hoTen.value || !sdt.value || !diaChi.value || !tinh.value || !quan.value || !xa.value) {
        alert("Vui lòng nhập đầy đủ thông tin!");
        return;
    }

    alert("Đặt hàng thành công!");
    localStorage.removeItem("gioHang"); // Xóa giỏ hàng sau khi đặt hàng
    router.push("/"); // Chuyển hướng về trang chủ hoặc trang xác nhận đơn hàng
};

// Khi trang được load
onMounted(() => {
    loadGioHang();
    layDanhSachTinh();
    layDanhSachVoucher();
});
</script>

<template>
    <div class="container mt-5">
        <h2 class="text-center mb-4">🛍 Xác nhận đơn hàng</h2>

        <!-- Giỏ hàng -->
        <div v-if="gioHang.length === 0" class="alert alert-warning text-center">
            Giỏ hàng trống! <router-link to="/">Quay lại mua sắm</router-link>
        </div>
        <div v-else>
            <table class="table table-bordered text-center">
                <thead class="table-dark">
                    <tr>
                        <th>Hình ảnh</th>
                        <th>Tên sản phẩm</th>
                        <th>Màu sắc</th>
                        <th>Kích thước</th>
                        <th>Số lượng</th>
                        <th>Giá bán</th>
                        <th>Thành tiền</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(item, index) in gioHang" :key="index">
                        <td>
                            <img :src="item.hinhAnh" class="img-thumbnail" width="80" height="80">
                        </td>
                        <td>{{ item.tenSanPham }}</td>
                        <td>{{ item.mauSac.tenMau }}</td> <!-- Hiển thị màu sắc -->
                        <td>{{ item.size.tenSize }}</td> <!-- Hiển thị kích thước -->
                        <td>{{ item.soLuong }}</td>
                        <td>{{ formatVND(item.giaGiam) }}</td>
                        <td>{{ formatVND(item.giaGiam * item.soLuong) }}</td>
                        <td>
                            <button class="btn btn-danger" @click="xoaSanPham(index)">🗑</button>
                        </td>
                    </tr>
                </tbody>
            </table>

            <div class="mt-3">
                <label class="fw-bold">🎟 Chọn mã giảm giá:</label>
                <select class="form-control" v-model="selectedVoucher" @change="apDungVoucher">
                    <option value="">-- Chọn voucher --</option>
                    <option v-for="voucher in danhSachVoucher" :key="voucher.id" :value="voucher.id">
                        {{ voucher.maVoucher }} -
                        <span v-if="voucher.loaiVoucher === 1">
                            Giảm {{ formatVND(voucher.giaTriGiam) }} (Đơn hàng từ {{ formatVND(voucher.giaTriToiThieu)
                            }})
                        </span>
                        <span v-else>
                            Giảm {{ voucher.giaTriGiam }}% (Đơn hàng từ {{ formatVND(voucher.giaTriToiThieu) }})
                            - Giảm tối đa {{ formatVND(voucher.giaTriToiThieu * voucher.giaTriGiam / 100) }}
                        </span>
                    </option>
                </select>
            </div>

            <!-- Hiển thị tổng tiền -->
            <div class="text-end mt-3">
                <h5>Tổng tiền: {{ formatVND(tongTienTruocGiam) }}</h5>
                <h5>Giảm giá: -{{ formatVND(giamGia) }}</h5>
                <h5>Phí vận chuyển: {{ formatVND(tinhPhiVanChuyen) }}</h5>
                <h4 class="fw-bold text-danger">Tổng thanh toán: {{ formatVND(tongTienThanhToan) }}</h4>
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
            <textarea class="form-control mb-3" rows="3" placeholder="Địa chỉ nhận hàng" v-model="diaChi"
                required></textarea>

            <!-- Chọn địa chỉ -->
            <div class="row">
                <div class="col-md-4">
                    <select class="form-control mb-3" v-model="tinh" @change="capNhatQuan">
                        <option value="">-- Chọn tỉnh --</option>
                        <option v-for="item in danhSachTinh" :key="item.code" :value="item.code">{{ item.name }}
                        </option>
                    </select>
                </div>
                <div class="col-md-4">
                    <select class="form-control mb-3" v-model="quan" @change="capNhatXa" :disabled="!tinh">
                        <option value="">-- Chọn Quận/Huyện --</option>
                        <option v-for="item in danhSachQuan" :key="item.code" :value="item.code">{{ item.name }}
                        </option>
                    </select>
                </div>
                <div class="col-md-4">
                    <select class="form-control mb-3" v-model="xa" :disabled="!quan">
                        <option value="">-- Chọn Xã/Phường --</option>
                        <option v-for="item in danhSachXa" :key="item.code" :value="item.code">{{ item.name }}</option>
                    </select>
                </div>

                <div class="text-end">
                    <button class="btn btn-success px-4 py-2 fs-5" @click="datHang">✅ Đặt Hàng</button>
                </div>
            </div>
        </div>
    </div>
</template>
