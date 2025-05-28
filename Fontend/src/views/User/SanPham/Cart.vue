<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const gioHang = ref([]);
const voucherList = ref([]);
const selectedVoucher = ref(null);
const discountAmount = ref(0); // 🔥 Thêm biến này
const errorVoucher = ref('');

const loadGioHang = () => {
    try {
        const stored = localStorage.getItem("gioHang");
        gioHang.value = stored ? JSON.parse(stored) : [];
    } catch (e) {
        console.error("Không thể parse dữ liệu từ localStorage:", e);
        gioHang.value = [];
    }
};


const saveGioHang = () => {
    localStorage.setItem("gioHang", JSON.stringify(gioHang.value));
    window.dispatchEvent(new CustomEvent("gioHangUpdated"));
};

const soLuongTheoBienThe = computed(() => {
    if (!selectedMauSac.value || !selectedSize.value) return null;
    const bienThe = sanPhamChiTiet.value.bienThe.find(
        b => b.mauSac.id === selectedMauSac.value && b.size.id === selectedSize.value
    );
    return bienThe ? bienThe.soLuong : 0;
});

const tangSoLuong = (index) => {
    gioHang.value[index].soLuong++;
    saveGioHang();
};

const giamSoLuong = (index) => {
    if (gioHang.value[index].soLuong > 1) {
        gioHang.value[index].soLuong--;
        saveGioHang();
    }
};

const xoaSanPham = (index) => {
    if (confirm("Bạn có chắc muốn xoá sản phẩm này không?")) {
        gioHang.value.splice(index, 1);
        saveGioHang();
    }
};

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


// Tự động áp dụng giảm giá khi voucher thay đổi
watch(selectedVoucher, applyDiscount);

// Tính tổng tiền trước khi giảm giá
const totalAmountBeforeDiscount = computed(() => {
    return gioHang.value.reduce((total, item) => {
        const price = item?.giaBan || 0;
        return total + (price * item.soLuong);
    }, 0);
});

// Tính tổng tiền sau khi giảm giá
const totalAmountAfterDiscount = computed(() => {
    return totalAmountBeforeDiscount.value - discountAmount.value;
});


// Format tiền
const tongTienFormatted = computed(() => {
    return totalAmountBeforeDiscount.value.toLocaleString("vi-VN") + " đ";
});

onMounted(() => {
    loadGioHang();
    fetchVouchers();
    watch(gioHang, (newVal) => {
        localStorage.setItem("gioHang", JSON.stringify(newVal));
    }, { deep: true });
});

</script>

<template>
    <div class="container py-5">
        <h3 class="fw-bold mb-4">🛒 Giỏ hàng của bạn</h3>
        <div v-if="gioHang.length === 0" class="alert alert-warning text-center">
            Giỏ hàng trống!
        </div>
        <div v-else class="row">
            <!-- Chi tiết đơn hàng -->
            <div class="col-lg-8">
                <div class="card mb-4">
                    <div class="card-body">
                        <div v-for="(item, index) in gioHang" :key="index" class="row mb-4 border-bottom pb-3">
                            <div class="col-md-2">
                                <img :src="item.hinhAnh" class="img-fluid rounded" alt="Ảnh sản phẩm" />
                            </div>
                            <div class="col-md-6">
                                <h5 class="fw-semibold mb-1">{{ item.tenSanPham }}</h5>
                                <p class="mb-1">Mã SPCT: {{ item.maSPCT || item.id }}</p>
                                <p class="mb-1">Màu sắc: {{ item.mauSac.tenMau }}</p>
                                <p class="mb-1">Kích thước: {{ item.size.tenSize }}</p>
                                <p class="mb-1">Giá bán: {{ new Intl.NumberFormat("vi-VN").format(item.giaBan) }} đ</p>
                                <a href="#" class="text-decoration-underline text-primary"
                                    @click.prevent="xoaSanPham(index)">❌ Xoá</a>
                            </div>
                            <div class="col-md-4 text-md-end">
                                <div class="input-group w-75 mx-auto">
                                    <button class="btn btn-outline-secondary" @click="giamSoLuong(index)">-</button>
                                    <input type="text" class="form-control text-center" :value="item.soLuong"
                                        readonly />
                                    <button class="btn btn-outline-secondary" @click="tangSoLuong(index)">+</button>
                                </div>
                                <div class="mt-2 fw-bold">
                                    {{ new Intl.NumberFormat("vi-VN").format(item.giaBan * item.soLuong) }} đ
                                </div>

                            </div>
                        </div>
                        <router-link to="/trang-chu" class="btn btn-outline-primary mt-3">
                            ← Mua thêm
                        </router-link>
                    </div>
                </div>
            </div>

            <!-- Thông tin thanh toán -->
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="fw-bold mb-3">Thành tiền</h5>
                        <div class="d-flex justify-content-between mb-3">
                            <span>Tổng tiền:</span>
                            <span class="fw-bold">{{ tongTienFormatted }}</span>
                        </div>

                        <div class="mb-3">
                            <label class="block font-semibold">Mã Giảm Giá (Nếu Có)</label>
                            <div class="flex items-center space-x-2">
                                <!-- Dropdown để chọn voucher -->
                                <select v-model="selectedVoucher" class="form-control">
                                    <option value="" disabled>Chọn Voucher</option>
                                    <option v-for="voucher in voucherList" :key="voucher.id" :value="voucher">
                                        {{ voucher.maVoucher }} - {{ voucher.moTa }}
                                    </option>
                                </select>
                            </div>
                        </div>

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
                                <p class="font-bold text-danger">Tổng Tiền Sau Giảm:</p>
                                <p class="text-right text-danger">{{ totalAmountAfterDiscount.toLocaleString() }} đ</p>
                            </div>
                        </div>
                        <button class="btn btn-danger w-100 mt-3" @click="router.push('/thanh-toan')">
                            🧾 Thanh toán
                        </button>
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
</style>
