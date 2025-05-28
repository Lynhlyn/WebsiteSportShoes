<template>
  <div class="w-full bg-white text-gray-800">
    <!-- Header -->
    <header class="bg-white shadow-md sticky top-0 z-50">
      <div class="max-w-[1300px] mx-auto grid grid-cols-12 items-center py-4 px-6 gap-4">
        <!-- Logo: 3/12 -->
        <div class="col-span-3 flex items-center gap-3">
          <img src="/src/assets/img/logo2.jpg" alt="Logo" class="logo me-3 w-12 h-12 object-cover" width="80px" />
          <router-link to="/trang-chu" class="brand-name">
            <span class="text-lg font-semibold uppercase text-blue-900 tracking-wide">Sport Shoes</span>
          </router-link>
        </div>

        <!-- Search Bar: 3/12 -->
        <div class="col-span-3">
          <input type="text" v-model="searchQuery" placeholder="Tìm kiếm sản phẩm..."
            class="w-full px-4 py-2 border rounded-full shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition" />
        </div>

        <!-- User Icon: 2/12 -->
        <div class="col-span-2 flex items-center justify-end relative">
          <div class="flex items-center gap-2 relative">
            <i class="bi bi-person-circle" @click="toggleUserMenu"></i>
            <span v-if="loggedInUser" class="text-sm">{{ loggedInUser.tenDangNhap }}</span>
          </div>
        </div>

        <div class="col-span-2 flex items-center justify-end relative">
          <div class="flex items-center gap-2 relative">
            <i i class="bi bi-box-arrow-right" @click="logout"></i>
          </div>
        </div>

        <!-- Cart Icon: 2/12 -->
        <div class="col-span-2 flex justify-end relative">
          <router-link to="/giohang" class="relative">
            <i class="bi bi-cart-plus text-2xl cursor-pointer"></i>
            <span v-if="cartCount > 0"
              class="absolute -top-2 -right-3 bg-red-500 text-white text-xs w-5 h-5 flex items-center justify-center rounded-full">
              {{ cartCount }}
            </span>
          </router-link>
        </div>

        <div class="col-span-2 flex items-center justify-end relative">
          <div class="flex items-center gap-2 relative">
            <i class="bi bi-truck" @click="goToDonMua"></i>
          </div>
        </div>

      </div>

    </header>
  </div>

</template>
<script>
export default {
  name: 'Header',
  props: ['searchQuery'],
  emits: ['update:searchQuery'],
  data() {
    return {
      searchQuery: '',
      showUserMenu: false,
      cartCount: 0,
      // ✅ Đã xóa loggedInUser khỏi data
    };
  },
  computed: {
    loggedInUser() {
      const user = localStorage.getItem('user');
      return user ? JSON.parse(user) : null;
    }
  },
  mounted() {
    this.updateCartCount();
    document.addEventListener('click', this.handleClickOutside);
    window.addEventListener('storage', () => {
      this.updateCartCount();
    });
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside);
    window.removeEventListener('storage', this.updateCartCount);
  },
  methods: {
    logout() {
      const user = JSON.parse(localStorage.getItem('user'));
      if (user) {
        const cartKey = `cart_${user.id}`;
        const userCart = localStorage.getItem(cartKey);
        if (userCart) {
          localStorage.setItem('cart_guest', userCart); // ✅ sao chép sang guest
        }
      }

      localStorage.removeItem('user');
      this.$router.push('/login');
      this.$toast && this.$toast.success('Đăng xuất thành công!');
      this.updateCartCount(); // ✅ cập nhật lại số lượng giỏ hàng
    },
    toggleUserMenu() {
      this.showUserMenu = !this.showUserMenu;
    },
    handleClickOutside(event) {
      const dropdown = this.$el.querySelector('.icons-right');
      if (dropdown && !dropdown.contains(event.target)) {
        this.showUserMenu = false;
      }
    },
    updateCartCount() {
      const user = JSON.parse(localStorage.getItem('user'));
      const cartKey = user ? `cart_${user.id}` : 'cart_guest';
      const cart = JSON.parse(localStorage.getItem(cartKey)) || [];
      this.cartCount = cart.reduce((total, item) => total + item.quantity, 0);
    },
    goToDonMua() {
      this.$router.push('/don-mua');
    }
  },
};
</script>


<style scoped>
/* Header */
header {
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 50;
  transition: all 0.3s ease-in-out;
}

header>div {
  max-width: 1300px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  gap: 1rem;
}

/* Logo & Brand */
.logo {
  border-radius: 0.5rem;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.brand-name span {
  font-size: 1.5rem;
  font-weight: 700;
  text-transform: uppercase;
  background: linear-gradient(90deg, #1e3a8a, #3b82f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  transition: all 0.3s ease;
}

.brand-name:hover span {
  transform: scale(1.05);
  background: linear-gradient(90deg, #3b82f6, #1e3a8a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* Icon Group */
.icons-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  font-size: 1.5rem;
  color: #374151;
}

.icons-right i {
  cursor: pointer;
  transition: all 0.3s ease;
}

.icons-right i:hover {
  color: #1d4ed8;
  transform: scale(1.1);
}

/* Dropdown */
.group:hover .group-hover\\:block {
  display: block;
}

.group-hover\\:block {
  display: none;
}
</style>
