<template>
  <div class="success-container">
    <v-container class="fill-height d-flex justify-center align-center">
      <v-card
        class="pa-10 text-center rounded-xl elevation-10"
        width="600"
        color="white"
      >
        <!-- Icon thành công -->
        <v-avatar size="100" class="mx-auto mb-6">
          <img
            src="@/assets/logo/thanhtoan.png"
            alt="Thanh toán thành công"
            class="success-image"
          />
        </v-avatar>

        <!-- Tiêu đề -->
        <h2 class="text-success text-h5 font-weight-bold mb-4">
          Đặt hàng thành công!
        </h2>

        <!-- Nội dung chính -->
        <p class="text-body-1 mb-2">
          Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi.
        </p>
        <p class="text-body-1 mb-6">
          Đơn hàng của bạn đã được xử lý thành công.
        </p>

        <!-- Đếm ngược -->
        <p class="text-caption grey--text mb-6">
          Tự động chuyển về trang chủ sau <strong>{{ countdown }}</strong> giây...
        </p>

        <!-- Nút hành động -->
        <v-btn
          color="success"
          dark
          large
          rounded
          elevation="2"
          @click="handleBackHome"
        >
          Quay về trang chủ ngay
        </v-btn>
      </v-card>
    </v-container>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'ThanhToanThanhCong',
  setup() {
    const router = useRouter();
    const countdown = ref(10);
    const intervalId = ref(null);

    const handleBackHome = () => {
      clearInterval(intervalId.value);
      router.push('/trang-chu');
    };

    const startCountdown = () => {
      intervalId.value = setInterval(() => {
        if (countdown.value > 0) {
          countdown.value--;
        } else {
          clearInterval(intervalId.value);
          router.push('/trang-chu');
        }
      }, 1000);
    };

    onMounted(() => {
      startCountdown();
    });

    return {
      countdown,
      handleBackHome,
    };
  },
});
</script>

<style scoped>
.success-container {
  background: linear-gradient(to bottom right, #e8fef4, #f4fff9);
  min-height: 50vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
}

.success-image {
  width: 50%;
  height: 50%;
  object-fit: contain;
}
</style>
