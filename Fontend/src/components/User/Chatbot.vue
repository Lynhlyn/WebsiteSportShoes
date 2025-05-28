<script setup>
import { onMounted } from 'vue';

onMounted(() => {
  // Xóa các dữ liệu đã lưu trong sessionStorage hoặc localStorage trước khi khởi tạo chatbot
  sessionStorage.clear(); // Xóa sessionStorage để xóa dữ liệu phiên hiện tại
  localStorage.clear(); // Xóa localStorage nếu bạn lưu trữ dữ liệu ở đó

  // Tạo một phần tử script để tải chatbot
  const script = document.createElement('script');
  script.src = 'https://app.tudongchat.com/js/chatbox.js';
  script.async = true;
  document.body.appendChild(script);

  script.onload = () => {
    const tudong_chatbox = new TuDongChat('wppRdp3fQjEZJsWjvMUZK');
    tudong_chatbox.initial(); // Khởi tạo chatbot sau khi script được load
  };
});
</script>

<template>
  <!-- Bạn có thể thêm một khối chứa ChatBot ở đây nếu cần -->
  <div id="chatbot"></div>
</template>

<style scoped>
#chatbot {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 9999;
  animation: flash 1s ease-in-out infinite;
}

@keyframes flash {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
  100% {
    opacity: 1;
  }
}
</style>
