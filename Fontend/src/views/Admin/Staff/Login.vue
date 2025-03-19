<template>
  <div class="login-container">
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
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const username = ref("");
const password = ref("");
const errorMessage = ref("");

const handleLogin = () => {
  if (username.value === "admin" && password.value === "123456") {
    router.push("/dashboard");
  } else {
    errorMessage.value = "Tên đăng nhập hoặc mật khẩu không đúng!";
  }
};
</script>

<style scoped>
.login-container {
  width: 300px;
  margin: 100px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
.input-group {
  margin-bottom: 15px;
}
label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}
input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
button:hover {
  background-color: #0056b3;
}
.error {
  color: red;
  margin-top: 10px;
}
</style>
