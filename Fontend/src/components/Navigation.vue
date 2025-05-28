<template>
  <!-- Add this to your navigation menu -->
  <button @click="handleLogout" v-if="isLoggedIn">Đăng xuất</button>
</template>

<script>
export default {
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem('user');
    }
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('user');
      this.$router.push('/login');
    }
  }
}
</script>