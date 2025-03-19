<template>
  <div class="register-container">
    <div class="register-box">
      <h2>Đăng ký</h2>
      <form @submit.prevent="handleRegister">
        <!-- Họ tên -->
        <div class="input-group">
          <label for="fullName">Họ tên:</label>
          <input type="text" id="fullName" v-model="fullName" required />
          <p v-if="errors.fullName" class="error">{{ errors.fullName }}</p>
        </div>

        <!-- Email -->
        <div class="input-group">
          <label for="email">Email:</label>
          <input type="email" id="email" v-model="email" required />
          <p v-if="errors.email" class="error">{{ errors.email }}</p>
        </div>

        <!-- Tên đăng nhập -->
        <div class="input-group">
          <label for="username">Tên đăng nhập:</label>
          <input type="text" id="username" v-model="username" required />
          <p v-if="errors.username" class="error">{{ errors.username }}</p>
        </div>

        <!-- Mật khẩu -->
        <div class="input-group">
          <label for="password">Mật khẩu:</label>
          <input type="password" id="password" v-model="password" required />
          <p v-if="errors.password" class="error">{{ errors.password }}</p>
        </div>

        <!-- Nhập lại mật khẩu -->
        <div class="input-group">
          <label for="confirmPassword">Nhập lại mật khẩu:</label>
          <input type="password" id="confirmPassword" v-model="confirmPassword" required />
          <p v-if="errors.confirmPassword" class="error">{{ errors.confirmPassword }}</p>
        </div>

        <!-- Chọn vai trò bằng Dropdown -->
        <div class="input-group">
          <label for="role">Vai trò:</label>
          <select id="role" v-model="role" required>
            <option value="" disabled>Chọn vai trò</option>
            <option value="Admin">Admin</option>
            <option value="User">User</option>
          </select>
          <p v-if="errors.role" class="error">{{ errors.role }}</p>
        </div>

        <button type="submit">Đăng ký</button>
      </form>
      <p class="login-link">
        Đã có tài khoản? <a @click="goToLogin">Đăng nhập</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

// Dữ liệu người dùng nhập vào
const fullName = ref("");
const email = ref("");
const username = ref("");
const password = ref("");
const confirmPassword = ref("");
const role = ref(""); // Vai trò: Admin hoặc User

// Lưu lỗi validate
const errors = ref({
  fullName: "",
  email: "",
  username: "",
  password: "",
  confirmPassword: "",
  role: "",
});

// Kiểm tra dữ liệu nhập
const validateForm = () => {
  errors.value = {}; // Reset lỗi

  if (!fullName.value.trim()) {
    errors.value.fullName = "Họ tên không được để trống";
  }

  if (!email.value.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
    errors.value.email = "Email không hợp lệ";
  }

  if (username.value.length < 5) {
    errors.value.username = "Tên đăng nhập phải có ít nhất 5 ký tự";
  }

  if (password.value.length < 6) {
    errors.value.password = "Mật khẩu phải có ít nhất 6 ký tự";
  }

  if (password.value !== confirmPassword.value) {
    errors.value.confirmPassword = "Mật khẩu nhập lại không khớp";
  }

  if (!role.value) {
    errors.value.role = "Bạn phải chọn một vai trò";
  }

  return Object.keys(errors.value).length === 0;
};

const handleRegister = () => {
  if (!validateForm()) return;

  alert(`Đăng ký thành công!\nVai trò: ${role.value}`);
  router.push("/admin"); // Chuyển đến trang admin sau khi đăng ký
};

const goToLogin = () => {
  router.push("/login");
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #ff9a9e, #fad0c4);
}

.register-box {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  width: 350px;
  text-align: center;
}

h2 {
  margin-bottom: 20px;
  color: #d63384;
  font-size: 24px;
  font-weight: bold;
}

.input-group {
  margin-bottom: 15px;
  text-align: left;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
  color: #d63384;
}

input, select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
}

select {
  appearance: none;
  background-color: white;
  cursor: pointer;
}

/* Tạo hiệu ứng mũi tên xuống */
select {
  background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='black'%3E%3Cpath d='M5 8l5 5 5-5H5z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 12px;
}

.error {
  color: red;
  font-size: 13px;
  margin-top: 5px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #ff4081;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: background 0.3s ease;
}

button:hover {
  background-color: #e91e63;
}

.login-link {
  margin-top: 15px;
  font-size: 14px;
}

.login-link a {
  color: #ff4081;
  font-weight: bold;
  cursor: pointer;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
