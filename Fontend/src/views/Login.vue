<template>
  <div class="login-container">
    <div class="login-box">
      <h2>Đăng nhập</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="username">Tên đăng nhập:</label>
          <input type="text" id="username" v-model="username" required />
        </div>
        <div class="input-group">
          <label for="password">Mật khẩu:</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <button type="submit">Đăng nhập</button>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      </form>

      <!-- Nút Đăng ký -->
      <p class="register-link">
        Bạn chưa có tài khoản?
        <a @click="goToRegister">Đăng ký</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const username = ref("");
const password = ref("");
const errorMessage = ref("");

// Xử lý đăng nhập
const handleLogin = () => {
  if (username.value === "admin" && password.value === "123456") {
    localStorage.setItem("isLoggedIn", "true");
    router.push("/admin");
  } else {
    errorMessage.value = "Tên đăng nhập hoặc mật khẩu không đúng!";
  }
};

// Chuyển sang trang đăng ký
const goToRegister = () => {
  router.push("/register");
};
</script>

<style scoped>
/* Màu nền pink gradient */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #ff9a9e, #fad0c4);
}

/* Hộp đăng nhập */
.login-box {
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

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
}

/* Nút đăng nhập */
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

.error {
  color: red;
  margin-top: 10px;
}

/* Định dạng cho link đăng ký */
.register-link {
  margin-top: 15px;
  font-size: 14px;
}

.register-link a {
  color: #ff4081;
  font-weight: bold;
  cursor: pointer;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
