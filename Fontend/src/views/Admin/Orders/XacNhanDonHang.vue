<template>
  <div class="container">
    <!-- Thanh trạng thái đơn hàng -->
    <div class="status-bar">
      <template v-for="(step, index) in steps" :key="index">
        <div class="status-step" :class="{ active: index <= currentStep, inactive: index > currentStep }">
          {{ step }}
        </div>
        <div v-if="index < steps.length - 1" class="status-line"
          :class="{ active: index < currentStep, inactive: index >= currentStep }"></div>
      </template>
    </div>

    <!-- Các nút hành động -->
    <div class="button-group">
      <button class="btn undo">&#8592; HOÀN TÁC</button>
      <button class="btn confirm" @click="moModalLyDo">XÁC NHẬN &#8594;</button>

      <button class="btn cancel" disabled>&#128465; HỦY HÓA ĐƠN</button>
      <!-- <button class="btn history">🕘 LỊCH SỬ HÓA ĐƠN</button> -->
    </div>

    <!-- Thông tin hóa đơn -->
    <div class="info-grid">
      <!-- Trái -->
      <div class="info-box">
        <h2 class="title">Thông tin hóa đơn <span class="bold">{{ donHang?.maDonHang }}</span></h2>
        <p><strong>Trạng thái:</strong> <span class="label paid">{{ donHang?.trangThaiDonHang }}</span></p>
        <p><strong>Loại hóa đơn:</strong>
          <span class="label counter">
            Online
          </span>
        </p>
        <p><strong>Ghi chú:</strong> {{ donHang?.ghiChu || 'Không có' }}</p>
      </div>

      <!-- Phải -->
      <div class="info-box">
        <h2 class="title orange">Thông tin nhận hàng</h2>
        <p><strong>Tên người nhận:</strong> {{ donHang?.khachHang?.hoTen }}</p>
        <p><strong>SDT người nhận:</strong> {{ donHang?.khachHang?.soDienThoai }}</p>
        <p><strong>Địa chỉ người nhận:</strong> {{ donHang?.khachHang?.diaChi || 'Chưa có' }}</p>
      </div>
    </div>
  </div>
  <!-- Modal nhập lý do -->


</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

// Lấy id đơn hàng từ URL
const route = useRoute()
// Sửa lại dòng này
const id = route.query.id


// Biến lưu thông tin hóa đơn
const donHang = ref(null)
const currentStep = ref(0)
const showModal = ref(false)
const lyDo = ref('')

const moModalLyDo = () => {
  showModal.value = true
}

const xacNhanThayDoiTrangThai = () => {
  if (!lyDo.value.trim()) {
    alert('Vui lòng nhập lý do!')
    return
  }

  // Gửi request cập nhật trạng thái ở đây
  console.log("Lý do cập nhật:", lyDo.value)

  showModal.value = false
}
const steps = [
  'Chờ xác nhận',
  'Đã xác nhận',
  'Chờ giao hàng',
  'Đang vận chuyển',
  'Đã giao hàng',
  'Chờ thanh toán',
  "Đã thanh toán",
  "Hoàn thành"
]

// Mapping trạng thái sang số step
const trangThaiMap = {
  'CHỜ XÁC NHẬN': 0,
  'ĐÃ XÁC NHẬN': 1,
  'CHỜ GIAO HÀNG': 2,
  'ĐANG VẬN CHUYỂN': 3,
  'ĐÃ GIAO HÀNG': 4,
  'CHỜ THANH TOÁN': 5,
  'ĐÃ THANH TOÁN': 6,
  'HOÀN THÀNH': 7
}

// Gọi API khi component mounted
onMounted(async () => {
  try {
    const response = await axios.get(`http://localhost:8080/don-hang/${id}`)
    donHang.value = response.data

    // Gán step theo trạng thái
    const trangThai = donHang.value.trangThai?.toUpperCase()
    currentStep.value = trangThaiMap[trangThai] ?? 0
  } catch (error) {
    console.error('Lỗi khi lấy hóa đơn:', error)
  }
})
</script>


<style>
/* .container {
  padding: 24px;
  background-color: #f9fafb;
  min-height: 100vh;
  font-family: Arial, sans-serif;
} */
.status-bar {
  display: flex;
  align-items: center;
  overflow-x: auto;
  margin-bottom: 24px;
}

.status-step {
  padding: 6px 16px;
  border-radius: 9999px;
  font-weight: bold;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  font-size: 14px;
}

.status-step.active {
  background-color: #2563eb;
  color: white;
}

.status-step.inactive {
  background-color: #e5e7eb;
  color: #6b7280;
}

.status-line {
  width: 20px;
  height: 4px;
  border-radius: 2px;
  margin: 0 6px;
}

.status-line.active {
  background-color: #2563eb;
}

.status-line.inactive {
  background-color: #d1d5db;
}

.button-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}

.btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: bold;
  border: none;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: background 0.3s;
}

.undo {
  background-color: #16a34a;
  color: white;
}

.undo:hover {
  background-color: #15803d;
}

.confirm {
  background-color: #2563eb;
  color: white;
}

.confirm:hover {
  background-color: #1e40af;
}

.cancel {
  background-color: #e5e7eb;
  color: #6b7280;
  cursor: not-allowed;
}

.history {
  background-color: #ede9fe;
  color: #6b21a8;
  border: 1px solid #c4b5fd;
}

.history:hover {
  background-color: #ddd6fe;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

@media (min-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr 1fr;
  }
}

.info-box {
  background: white;
  padding: 24px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #2563eb;
}

.title.orange {
  color: #ea580c;
}

.bold {
  color: black;
}

.label {
  padding: 4px 8px;
  border-radius: 9999px;
  font-size: 13px;
  font-weight: 500;
}

.label.paid {
  background-color: #dcfce7;
  color: #166534;
}

.label.counter {
  background-color: #fce7f3;
  color: #9d174d;
}

.note {
  display: inline-block;
  font-size: 13px;
  background-color: #dbeafe;
  color: #1e40af;
  padding: 4px 12px;
  border-radius: 9999px;
  font-weight: 500;
  margin-top: 8px;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 999;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal {
  background-color: white;
  padding: 20px;
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.modal textarea {
  width: 100%;
  height: 100px;
  margin-top: 10px;
  margin-bottom: 20px;
  padding: 10px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-actions button {
  padding: 6px 12px;
  cursor: pointer;
}


</style>