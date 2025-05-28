<template>
  <div class="register-container">
    <div class="register-form">
      <h2>Đăng ký</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>Họ tên <span style="color: red">*</span></label>
          <input type="text" v-model="hoTen" />
          <p class="error" v-if="errors.hoTen">{{ errors.hoTen }}</p>
        </div>

        <div class="form-group">
          <label>Tên đăng nhập <span style="color: red">*</span></label>
          <input type="text" v-model="tenDangNhap" />
          <p class="error" v-if="errors.tenDangNhap">{{ errors.tenDangNhap }}</p>
        </div>

        <div class="form-group">
          <label>Mật khẩu <span style="color: red">*</span></label>
          <input type="password" v-model="matKhau" />
          <p class="error" v-if="errors.matKhau">{{ errors.matKhau }}</p>
        </div>

        <div class="form-group">
          <label>Xác nhận mật khẩu <span style="color: red">*</span></label>
          <input type="password" v-model="xacNhanMatKhau" />
          <p class="error" v-if="errors.xacNhanMatKhau">{{ errors.xacNhanMatKhau }}</p>
        </div>

        <div class="form-group">
          <label>Giới tính:</label>
          <label><input type="radio" value="Nam" v-model="gioiTinh" /> Nam</label>
          <label><input type="radio" value="Nữ" v-model="gioiTinh" /> Nữ</label>
        </div>

        <div class="form-group">
          <label>Email:</label>
          <input type="email" v-model="email" />
          <p class="error" v-if="errors.email">{{ errors.email }}</p>
        </div>

        <div class="form-group">
          <label>Địa chỉ:</label>
          <textarea v-model="diaChi" rows="3"></textarea>
          <p class="error" v-if="errors.diaChi">{{ errors.diaChi }}</p>
        </div>

        <div class="form-group">
          <label>Thông Tin Điều Khoản</label>
          <button type="button" @click="docDieuKhoan">Đọc điều khoản</button>
        </div>

        <div class="form-group">
          <label>
            <input type="checkbox" v-model="chapNhanDieuKhoan" />
            Tôi chấp nhận điều khoản.
          </label>
          <p class="error" v-if="errors.chapNhanDieuKhoan">{{ errors.chapNhanDieuKhoan }}</p>
        </div>

        <button type="submit">Đăng ký</button>
        <button type="button" @click="handleCancel">Hủy</button>

        <p class="error" v-if="error">{{ error }}</p>
        <p class="success" v-if="successMessage">{{ successMessage }}</p>

        <p>
          Đã có tài khoản?
          <router-link to="/login">Đăng nhập</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      hoTen: '',
      tenDangNhap: '',
      matKhau: '',
      xacNhanMatKhau: '',
      gioiTinh: 'Nam',
      email: '',
      diaChi: '',
      chapNhanDieuKhoan: false,
      error: null,
      successMessage: null,
      errors: {} // Store field-specific error messages
    };
  },
  methods: {
    async handleRegister() {
      this.error = null;
      this.successMessage = null;
      this.errors = {}; // Reset errors

      // Gọi hàm kiểm tra nhập liệu
      if (!this.validateFields()) {
        return;
      }

      try {
        await axios.post('http://localhost:8080/api/auth/register', {
          hoTen: this.hoTen.trim(),
          tenDangNhap: this.tenDangNhap.trim(),
          matKhau: this.matKhau,
          gioiTinh: this.gioiTinh,
          email: this.email.trim(),
          diaChi: this.diaChi.trim()
        });

        this.successMessage = '🎉 Đăng ký thành công! Đang chuyển hướng đến trang đăng nhập...';
        setTimeout(() => {
          this.$router.push('/login');
        }, 2000);
      } catch (err) {
        this.error = err.response?.data?.message || 'Đăng ký thất bại. Vui lòng thử lại.';
      }
    },

    validateFields() {
      let valid = true;

      if (!this.hoTen || !this.hoTen.trim()) {
        this.errors.hoTen = 'Vui lòng nhập họ tên.';
        valid = false;
      }

      if (!this.tenDangNhap || !this.tenDangNhap.trim()) {
        this.errors.tenDangNhap = 'Vui lòng nhập tên đăng nhập.';
        valid = false;
      } else if (this.tenDangNhap.length < 5) {
        this.errors.tenDangNhap = 'Tên đăng nhập phải có ít nhất 5 ký tự.';
        valid = false;
      }

      if (!this.matKhau) {
        this.errors.matKhau = 'Vui lòng nhập mật khẩu.';
        valid = false;
      } else if (this.matKhau.length < 6) {
        this.errors.matKhau = 'Mật khẩu phải có ít nhất 6 ký tự.';
        valid = false;
      }

      if (!this.xacNhanMatKhau) {
        this.errors.xacNhanMatKhau = 'Vui lòng xác nhận mật khẩu.';
        valid = false;
      } else if (this.matKhau !== this.xacNhanMatKhau) {
        this.errors.xacNhanMatKhau = 'Mật khẩu xác nhận không khớp.';
        valid = false;
      }

      if (this.email && !/^\S+@\S+\.\S+$/.test(this.email)) {
        this.errors.email = 'Email không hợp lệ.';
        valid = false;
      }

      if (this.diaChi && this.diaChi.length > 255) {
        this.errors.diaChi = 'Địa chỉ không được vượt quá 255 ký tự.';
        valid = false;
      }

      if (!this.chapNhanDieuKhoan) {
        this.errors.chapNhanDieuKhoan = 'Bạn cần chấp nhận điều khoản để đăng ký.';
        valid = false;
      }

      return valid;
    },

    handleCancel() {
      this.hoTen = '';
      this.tenDangNhap = '';
      this.matKhau = '';
      this.xacNhanMatKhau = '';
      this.gioiTinh = 'Nam';
      this.email = '';
      this.diaChi = '';
      this.chapNhanDieuKhoan = false;
      this.errors = {};
      this.error = null;
      this.successMessage = null;
    },

    docDieuKhoan() {
      alert("Đây là nội dung điều khoản.");
    }
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(to right, #dfe9f3, #ffffff);
  padding: 20px;
}

.register-form {
  background-color: #fff;
  padding: 30px 40px;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
}

h2 {
  text-align: center;
  margin-bottom: 25px;
  color: #333;
}

.form-group {
  margin-bottom: 18px;
}

label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
  color: #444;
}

input[type="text"],
input[type="password"],
input[type="email"],
textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #ccc;
  border-radius: 8px;
  outline: none;
  transition: border-color 0.3s;
}

input[type="text"]:focus,
input[type="password"]:focus,
input[type="email"]:focus,
textarea:focus {
  border-color: #007BFF;
}

textarea {
  resize: vertical;
}

.form-group input[type="radio"] {
  margin-right: 6px;
  margin-left: 10px;
}

button {
  padding: 10px 16px;
  margin-right: 10px;
  border: none;
  border-radius: 8px;
  background-color: #007BFF;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;
}

button[type="button"]:last-of-type {
  background-color: #6c757d;
}

button:hover {
  background-color: #0056b3;
}

.error {
  color: red;
  margin-top: 12px;
  text-align: center;
  font-weight: bold;
}

.success {
  color: green;
  margin-top: 12px;
  text-align: center;
  font-weight: bold;
}

p {
  margin-top: 20px;
  text-align: center;
}

router-link {
  color: #007BFF;
  text-decoration: none;
}

router-link:hover {
  text-decoration: underline;
}
</style>
