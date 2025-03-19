<template>
  <div class="container my-5">
    <div class="d-flex align-items-center mb-3">
      <button class="btn btn-light me-2" @click="$router.back()">
        <i class="bi bi-arrow-left"></i>
      </button>
      <h2 class="mb-0 fw-bold">Chỉnh Sửa Voucher</h2>
    </div>

    <div class="row">
      <!-- Form Voucher -->
      <div class="col-md-6">
        <form @submit.prevent="saveVoucher">
          <div class="mb-3">
            <label class="form-label">Mã voucher</label>
            <input v-model="voucher.maVoucher" type="text" class="form-control" disabled />
          </div>

          <div class="mb-3">
            <label class="form-label">Tên voucher</label>
            <input v-model="voucher.moTa" type="text" class="form-control" required />
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Giá trị giảm</label>
              <div class="input-group">
                <input v-model="voucher.giaTriGiam" type="number" class="form-control" required />
                <div class="ms-3 d-flex align-items-center gap-2">
                  <input type="radio" id="percent" :value="0" v-model.number="voucher.loaiVoucher" />
                  <label for="percent">%</label>

                  <input type="radio" id="currency" :value="1" v-model.number="voucher.loaiVoucher" />
                  <label for="currency">đ</label>
                </div>
              </div>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Giá trị tối thiểu</label>
              <input v-model="voucher.giaTriToiThieu" type="number" class="form-control" required />
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3" v-if="voucher.loaiVoucher === 0">
              <label class="form-label">Giá trị giảm tối đa</label>
              <input v-model="voucher.giaTriToiDa" type="number" class="form-control" required />
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Số lượng</label>
              <input v-model="voucher.soLuong" type="number" class="form-control" required />
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Từ ngày</label>
              <input v-model="voucher.ngayBatDau" type="datetime-local" class="form-control" required />
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Đến ngày</label>
              <input v-model="voucher.ngayKetThuc" type="datetime-local" class="form-control" required />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Kiểu Voucher</label>
            <div class="d-flex gap-3">
              <input type="radio" id="public" v-model="voucher.kieuVoucher" value="public"
                @change="onVoucherTypeChange" />
              <label for="public">Công khai</label>

              <input type="radio" id="private" v-model="voucher.kieuVoucher" value="private"
                @change="onVoucherTypeChange" />
              <label for="private">Cá nhân</label>
            </div>
          </div>

          <button type="submit" class="btn btn-primary w-100">Cập nhật Voucher</button>
        </form>
      </div>

      <!-- Danh sách khách hàng -->
      <div class="col-md-6" v-show="voucher.kieuVoucher === 'private'">
        <h5>Danh sách khách hàng</h5>
        <input v-model="searchQuery" type="text" class="form-control mb-2" placeholder="Tìm kiếm khách hàng..."
          @input="searchCustomers">

        <table class="table table-striped">
          <thead>
            <tr>
              <th><input type="checkbox" v-model="selectAll" @change="toggleSelectAll" /></th>
              <th>Tên</th>
              <th>Số điện thoại</th>
              <th>Email</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="customer in customers" :key="customer.id">
              <td><input type="checkbox" v-model="selectedCustomers" :value="customer.id" /></td>
              <td>{{ customer.hoTen }}</td>
              <td>{{ customer.soDienThoai }}</td>
              <td>{{ customer.email }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: ['id'],
  data() {
    return {
      voucher: {
        maVoucher: '',
        moTa: '',
        giaTriGiam: '',
        giaTriToiThieu: '',
        giaTriToiDa: '',
        ngayBatDau: '',
        ngayKetThuc: '',
        loaiVoucher: 0, // 1: Giảm số tiền, 2: Giảm phần trăm
        soLuong: '',
        trangThai: true,
        kieuVoucher: 'public'
      },
      existingVouchers: [],
      customers: [],
      allCustomers: [],
      selectedCustomers: [],
      searchQuery: '',
      selectAll: false
    };
  },
  
  async mounted() {
    await this.fetchVoucher();
    await this.fetchAllVouchers();
    await this.fetchAllCustomers();
  },

  watch: {
    'voucher.giaTriGiam': 'updateGiaTriToiDa',
    'voucher.giaTriToiThieu': 'updateGiaTriToiDa'
  },

  methods: {
    async fetchVoucher() {
      try {
        const response = await axios.get(`http://localhost:8080/admin/voucher/${this.id}`);
        this.voucher = {
          ...response.data,
          loaiVoucher: Number(response.data.loaiVoucher)
        };
      } catch (error) {
        console.error('Lỗi khi lấy voucher:', error);
      }
    },

    async fetchAllVouchers() {
      try {
        const response = await axios.get('http://localhost:8080/admin/voucher');
        this.existingVouchers = response.data.map(v => v.maVoucher);
      } catch (error) {
        console.error('Lỗi khi lấy danh sách voucher:', error);
      }
    },

    async fetchAllCustomers() {
      try {
        const response = await axios.get('http://localhost:8080/admin/customers');
        this.allCustomers = response.data;
        this.customers = [...this.allCustomers];
      } catch (error) {
        console.error('Lỗi khi lấy danh sách khách hàng:', error);
      }
    },

    validateVoucher() {
      const { giaTriGiam, giaTriToiThieu, loaiVoucher } = this.voucher;
      const giaTriGiamNum = Number(giaTriGiam);
      const giaTriToiThieuNum = Number(giaTriToiThieu);

      if (giaTriGiamNum > giaTriToiThieuNum) {
        alert("Giá trị giảm không được lớn hơn giá trị tối thiểu.");
        return false;
      }

      if (loaiVoucher === 2 && giaTriGiamNum > 100) {
        alert("Giá trị giảm phần trăm không được vượt quá 100%.");
        return false;
      }

      return true;
    },

    updateGiaTriToiDa() {
      if (!this.voucher.giaTriToiThieu || !this.voucher.giaTriGiam) return;

      const giaTriToiThieuNum = Number(this.voucher.giaTriToiThieu);
      const giaTriGiamNum = Number(this.voucher.giaTriGiam);

      if (this.voucher.loaiVoucher === 2) { // Giảm theo phần trăm
        this.voucher.giaTriToiDa = (giaTriToiThieuNum * (giaTriGiamNum / 100)).toFixed(2);
      } else {
        this.voucher.giaTriToiDa = giaTriGiamNum;
      }
    },

    async saveVoucher() {
      if (!this.validateVoucher()) return;
      
      if (new Date(this.voucher.ngayBatDau) > new Date(this.voucher.ngayKetThuc)) {
        alert("Cập nhật thất bại! Ngày bắt đầu không được lớn hơn ngày kết thúc.");
        return;
      }

      try {
        await axios.put(`http://localhost:8080/admin/voucher/${this.id}`, this.voucher);
        alert('Voucher đã được cập nhật!');
        this.$router.push('/admin/vouchers');
      } catch (error) {
        console.error('Lỗi khi lưu voucher:', error);
        alert('Cập nhật thất bại! Vui lòng thử lại.');
      }
    },

    searchCustomers() {
      if (!this.searchQuery.trim()) {
        this.customers = [...this.allCustomers];
        return;
      }

      let searchParams = {};
      if (!isNaN(this.searchQuery.trim())) {
        searchParams.soDienThoai = this.searchQuery.trim();
      } else if (this.searchQuery.includes("@")) {
        searchParams.email = this.searchQuery.trim();
      } else {
        searchParams.hoTen = this.searchQuery.trim();
      }

      axios.get("http://localhost:8080/admin/search-customers", { params: searchParams })
        .then(response => {
          this.customers = response.data?.content || [];
        })
        .catch(error => {
          console.error("Lỗi khi tìm kiếm khách hàng:", error);
        });
    },

    toggleSelectAll() {
      this.selectedCustomers = this.selectAll ? this.customers.map(c => c.id) : [];
    }
  }
};
</script>

