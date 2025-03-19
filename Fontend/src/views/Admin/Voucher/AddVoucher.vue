<template>
  <div class="container my-5">
    <div class="d-flex align-items-center mb-3">
      <button class="btn btn-light me-2" @click="$router.back()">
        <i class="bi bi-arrow-left"></i>
      </button>
    </div>
    <h2 class="mb-4">Thêm Voucher</h2>

    <div class="row">
      <!-- Form Thêm Voucher -->
      <div class="col-md-6">
        <form @submit.prevent="saveVoucher">
          <div class="mb-3">
            <label class="form-label">Mã voucher</label>
            <input v-model="voucher.maVoucher" type="text" class="form-control" required />
          </div>

          <div class="mb-3">
            <label class="form-label">Tên voucher</label>
            <input v-model="voucher.tenVoucher" type="text" class="form-control" required />
          </div>

          <div class="row">
            <div class="col-md-6">
              <label class="form-label">Giá trị giảm</label>
              <div class="input-group">
                <input v-model="voucher.giaTriGiam" type="number" class="form-control" required
                  @input="validateGiaTriGiam" />
                <div class="ms-3 d-flex align-items-center gap-2">
                  <input type="radio" id="percent" :value="0" v-model.number="voucher.loaiVoucher" />
                  <label for="percent">%</label>

                  <input type="radio" id="currency" :value="1" v-model.number="voucher.loaiVoucher" />
                  <label for="currency">đ</label>
                </div>
              </div>
            </div>

            <div class="col-md-6">
              <label class="form-label">Giá trị giảm tối đa</label>
              <input v-model="voucher.giamToiDa" type="number" class="form-control"
                :disabled="voucher.loaiVoucher === 1" required />
            </div>
          </div>

          <div class="row">
            <div class="col-md-6">
              <label class="form-label">Giá trị tối thiểu</label>
              <input v-model="voucher.giaTriToiThieu" type="number" class="form-control" required />
            </div>
            <div class="col-md-6">
              <label class="form-label">Số lượng</label>
              <input v-model="voucher.soLuong" type="number" class="form-control"
                :readonly="voucher.kieuVoucher === 'private'" required />
            </div>
          </div>

          <div class="row">
            <div class="col-md-6">
              <label class="form-label">Từ ngày</label>
              <input v-model="voucher.ngayBatDau" type="datetime-local" class="form-control" required />
            </div>
            <div class="col-md-6">
              <label class="form-label">Đến ngày</label>
              <input v-model="voucher.ngayKetThuc" type="datetime-local" class="form-control" required />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Kiểu</label>
            <div>
              <input type="radio" v-model="voucher.kieuVoucher" value="public" @change="updateSoLuong" /> Công khai
              <input type="radio" v-model="voucher.kieuVoucher" value="private" class="ms-3" @change="updateSoLuong" />
              Cá nhân
            </div>
          </div>

          <button type="submit" class="btn btn-primary">Thêm Voucher</button>
        </form>
      </div>

      <!-- Danh sách khách hàng -->
      <div class="col-md-6" v-show="voucher.kieuVoucher === 'private'">
        <h5>Danh sách khách hàng</h5>
        <input v-model="searchQuery" type="text" class="form-control mb-2" placeholder="Tìm kiếm khách hàng..."
          @input="searchCustomers">
        <div class="table-responsive">
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
                <td><input type="checkbox" v-model="selectedCustomers" :value="customer.id" @change="updateSoLuong" />
                </td>
                <td>{{ customer.hoTen }}</td>
                <td>{{ customer.soDienThoai }}</td>
                <td>{{ customer.email }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      voucher: {
        maVoucher: '',
        tenVoucher: '',
        giaTriGiam: '',
        giamToiDa: '',
        giaTriToiThieu: '',
        ngayBatDau: '',
        ngayKetThuc: '',
        loaiVoucher: 0,
        soLuong: 1,
        trangThai: true,
        kieuVoucher: 'public'
      },
      customers: [],
      allCustomers: [],
      selectedCustomers: [],
      searchQuery: '',
      selectAll: false
    };
  },
  async mounted() {
    await this.fetchAllCustomers();
  },
  watch: {
    selectedCustomers() {
      this.updateSoLuong();
    },
    "voucher.kieuVoucher"() {
      this.updateSoLuong();
    },
    "voucher.giaTriGiam"(newValue) {
      this.updateGiamToiDa();
    },
    "voucher.giaTriToiThieu"(newValue) {
      this.updateGiamToiDa();
    }
  },
  methods: {
    async fetchAllCustomers() {
      try {
        const response = await axios.get('http://localhost:8080/admin/customers');
        this.allCustomers = response.data;
        this.customers = [...this.allCustomers];
      } catch (error) {
        console.error('Lỗi khi lấy danh sách khách hàng:', error);
      }
    },

    updateSoLuong() {
      if (this.voucher.kieuVoucher === 'private') {
        this.voucher.soLuong = this.selectedCustomers.length;
      }
    },

    async searchCustomers() {
      if (!this.searchQuery.trim()) {
        this.customers = [...this.allCustomers];
        return;
      }

      try {
        let searchParams = {};
        if (!isNaN(this.searchQuery.trim())) {
          searchParams.soDienThoai = this.searchQuery.trim();
        } else if (this.searchQuery.includes("@")) {
          searchParams.email = this.searchQuery.trim();
        } else {
          searchParams.hoTen = this.searchQuery.trim();
        }

        const response = await axios.get("http://localhost:8080/admin/search-customers", {
          params: searchParams,
        });

        this.customers = response.data.content.length > 0 ? [...response.data.content] : [];
      } catch (error) {
        console.error("❌ Lỗi khi tìm kiếm khách hàng:", error);
      }
    },

    validateGiaTriGiam() {
      if (this.voucher.loaiVoucher === 0 && this.voucher.giaTriGiam > 100) {
        this.voucher.giaTriGiam = 100;
      } else if (this.voucher.giaTriGiam < 0) {
        this.voucher.giaTriGiam = 0;
      }
    },

    updateGiamToiDa() {
      if (this.voucher.loaiVoucher === 0 && this.voucher.giaTriToiThieu && this.voucher.giaTriGiam) {
        this.voucher.giamToiDa = (this.voucher.giaTriToiThieu * this.voucher.giaTriGiam) / 100;
      }
    },

    async saveVoucher() {
      this.validateGiaTriGiam(); // Kiểm tra trước khi gửi

      try {
        const formattedVoucher = {
          maVoucher: this.voucher.maVoucher,
          moTa: this.voucher.tenVoucher,
          giaTriGiam: parseFloat(this.voucher.giaTriGiam),
          giamToiDa: parseFloat(this.voucher.giamToiDa),
          giaTriToiThieu: parseFloat(this.voucher.giaTriToiThieu),
          ngayBatDau: new Date(this.voucher.ngayBatDau).toISOString(),
          ngayKetThuc: new Date(this.voucher.ngayKetThuc).toISOString(),
          loaiVoucher: Number(this.voucher.loaiVoucher),
          soLuong: parseInt(this.voucher.soLuong),
          trangThai: this.voucher.trangThai,
          kieuVoucher: this.voucher.kieuVoucher,
          customers: this.voucher.kieuVoucher === 'private' ? this.selectedCustomers : []
        };

        const response = await axios.post('http://localhost:8080/admin/add-voucher', formattedVoucher);

        if (response.status === 200) {
          alert('Voucher đã được thêm thành công!');
          if (this.voucher.kieuVoucher === 'private') {
            alert('Email thông báo đã được gửi đến khách hàng!');
          }
          this.$router.push('/admin/vouchers');
        }
      } catch (error) {
        console.error('Lỗi khi thêm voucher:', error);
        alert('Thêm voucher thất bại!');
      }
    }
  }
};

</script>
