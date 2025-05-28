<template>
  <div class="login-container">
    <div v-if="showLoginForm" class="login-form">
      <h2>Đăng nhập</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Tên đăng nhập:</label>
          <input type="text" v-model="tenDangNhap" required />
        </div>
        <div class="form-group">
          <label>Mật khẩu:</label>
          <input type="password" v-model="matKhau" required />
        </div>
        <button type="submit">Đăng nhập</button>
        <p class="error" v-if="error">{{ error }}</p>

        <!-- Success message -->
        <p class="success" v-if="successMessage">{{ successMessage }}</p>

        <p>
          Chưa có tài khoản? 
          <router-link to="/register">Đăng ký</router-link>
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
      tenDangNhap: '',
      matKhau: '',
      error: null,
      successMessage: null, // To display the success message with the user's role
      showLoginForm: true, // Controls whether to show the login form
    };
  },
  methods: {
    async handleLogin() {
      this.error = null;
      this.successMessage = null;

      try {
        const response = await axios.post('http://localhost:8080/api/auth/login', {
          tenDangNhap: this.tenDangNhap,
          matKhau: this.matKhau
        });

        localStorage.setItem('user', JSON.stringify(response.data));

        // Get the role and set the success message
        const role = response.data.vaiTro.tenVaiTro;

        // Show success message
        this.successMessage = `Đăng nhập thành công với vai trò: ${role}`;
        
        // Display the appropriate alert based on the role
        if (role === 'ADMIN') {
          alert('Bạn đã đăng nhập thành công với vai trò admin');
        } else if (role === 'Khách hàng') {
          alert('Bạn đã đăng nhập thành công với vai trò Khách hàng');
        } else {
          alert('Bạn đã đăng nhập thành công với vai trò nhân viên');
        }

        // Hide login form after successful login
        this.showLoginForm = false;

        // Redirect based on role
        setTimeout(() => {
          if (role === 'ADMIN') {
            this.$router.push('/admin');
          } else if (role === 'Khách hàng') {
            this.$router.push('/trang-chu');
          } else {
            this.$router.push('/admin/products/thuoc_tinh');
          }
        }, 2000); // Delay redirection for a brief moment to show the success message

      } catch (err) {
        this.error = 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.';
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
}

input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 0.75rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

.error {
  color: red;
  margin-top: 1rem;
}

.success {
  color: green;
  margin-top: 1rem;
  font-weight: bold;
  text-align: center;
}
</style>
