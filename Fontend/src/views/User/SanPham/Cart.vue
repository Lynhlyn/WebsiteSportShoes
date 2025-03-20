<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const gioHang = ref([]);

// Tải giỏ hàng từ localStorage
const loadGioHang = () => {
    gioHang.value = JSON.parse(localStorage.getItem("gioHang")) || [];
};

// Lưu giỏ hàng vào localStorage
const saveGioHang = () => {
    localStorage.setItem("gioHang", JSON.stringify(gioHang.value));
};

// Tăng số lượng sản phẩm
const tangSoLuong = (index) => {
    if (index >= 0 && index < gioHang.value.length) {
        gioHang.value[index].soLuong++;
        saveGioHang();
    }
};

// Giảm số lượng sản phẩm (không giảm dưới 1)
const giamSoLuong = (index) => {
    if (index >= 0 && index < gioHang.value.length && gioHang.value[index].soLuong > 1) {
        gioHang.value[index].soLuong--;
        saveGioHang();
    }
};

// Xóa sản phẩm khỏi giỏ hàng
const xoaSanPham = (index) => {
    if (index >= 0 && index < gioHang.value.length) {
        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này không?")) {
            gioHang.value.splice(index, 1);
            saveGioHang();
        }
    }
};

// Tính tổng tiền
const tongTienFormatted = computed(() => {
    const total = gioHang.value.reduce((acc, item) => acc + item.giaGiam * item.soLuong, 0);
    return new Intl.NumberFormat("vi-VN").format(total) + " đ";
});

onMounted(loadGioHang);
</script>

<template>
    <div class="container mt-5">
        <h2 class="text-center mb-4">🛒 Giỏ hàng của bạn</h2>
        <div v-if="gioHang.length === 0" class="alert alert-warning text-center">
            Giỏ hàng trống!
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
                        <td>{{ item.mauSac.tenMau }}</td>
                        <td>{{ item.size.tenSize }}</td>
                        <td>
                            <button class="btn btn-outline-secondary" @click="giamSoLuong(index)">-</button>
                            <span class="mx-2">{{ item.soLuong }}</span>
                            <button class="btn btn-outline-secondary" @click="tangSoLuong(index)">+</button>
                        </td>
                        <td>{{ new Intl.NumberFormat("vi-VN").format(item.giaGiam) }} đ</td>
                        <td>{{ new Intl.NumberFormat("vi-VN").format(item.giaGiam * item.soLuong) }} đ</td>
                        <td>
                            <button class="btn btn-danger" @click="xoaSanPham(index)">🗑</button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <div class="text-end mt-3">
                <h4 class="fw-bold">Tổng tiền: {{ tongTienFormatted }}</h4>
                <router-link to="/thanh-toan" class="btn btn-primary px-4 py-2 fs-5">🛒 Thanh toán</router-link>
            </div>
        </div>
    </div>
</template>

<style scoped>
img {
    object-fit: cover;
}
</style>
