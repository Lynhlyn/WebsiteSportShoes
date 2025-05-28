<template>
  <section class="bg-gray-100 py-12 text-center">
    <div class="max-w-4xl mx-auto">
      <h3 class="store-heading">
        HỆ THỐNG CỬA HÀNG SPORT SHOES <br />
        <span class="text-2xl font-semibold text-gray-700">TRÊN TOÀN QUỐC</span>
      </h3>
      <a href="#"
         class="mt-4 inline-block bg-white text-black px-8 py-3 rounded-full border-2 border-black hover:bg-gray-100 transition duration-300 font-medium">
        Hệ thống các showroom
      </a>
    </div>
  </section>

  <footer class="bg-black text-white py-12">
    <div class="container mx-auto px-4 grid grid-cols-1 md:grid-cols-5 gap-6">
      <div class="md:col-span-2">
        <h3 class="text-lg font-bold border-b border-gray-500 pb-2 mb-4">LIÊN HỆ</h3>
        <h4 class="text-lg font-semibold mb-2">Sport Shoes</h4>
        <p>Địa chỉ: 442 Đội Cấn - Ba Đình - Hà Nội</p>
        <p>Email: support@sapo.vn</p>
        <p>Số điện thoại: (04) 6674 2332</p>
        <div class="flex gap-2 mt-4">
          <img src="@/assets/img/payment-1.png" alt="" class="w-10 h-6 object-contain" />
          <img src="@/assets/img/payment-2.png" alt="" class="w-10 h-6 object-contain" />
          <img src="@/assets/img/payment-3.png" alt="" class="w-10 h-6 object-contain" />
          <img src="@/assets/img/payment-4.png" alt="" class="w-10 h-6 object-contain" />
        </div>
      </div>

      <div>
        <h4 class="text-lg font-semibold mb-2">KÊNH BÁN HÀNG</h4>
        <ul class="space-y-1">
          <li><a href="#" class="hover:text-gray-300">Shoppe</a></li>
          <li><a href="#" class="hover:text-gray-300">Sendo</a></li>
          <li><a href="#" class="hover:text-gray-300">Zalo</a></li>
          <li><a href="#" class="hover:text-gray-300">Lazada</a></li>
          <li><a href="#" class="hover:text-gray-300">Tiki</a></li>
        </ul>
      </div>

      <div>
        <h4 class="text-lg font-semibold mb-2">VỀ CHÚNG TÔI</h4>
        <ul class="space-y-1">
          <li><RouterLink to="/gioi-thieu"  class="hover:text-gray-300">Về Sport Shoes</RouterLink></li>
          <li><RouterLink to="/gioi-thieu" class="hover:text-gray-300">Chương trình khuyến mãi</RouterLink></li>
          <li><RouterLink to="/gioi-thieu" class="hover:text-gray-300">Hướng dẫn đặt hàng</RouterLink></li>
          <li><RouterLink to="/gioi-thieu" class="hover:text-gray-300">Hướng dẫn chọn size</RouterLink></li>
          <li><RouterLink to="/gioi-thieu" class="hover:text-gray-300">Tin tức</RouterLink></li>
        </ul>
      </div>

      <div>
        <h4 class="text-lg font-semibold mb-2">CHÍNH SÁCH</h4>
        <ul class="space-y-1">
          <li><RouterLink to="/gioi-thieu" class="hover:text-gray-300">Chính sách bán hàng</RouterLink></li>
          <li><RouterLink to="/chinh-sach-doi-tra" class="hover:text-gray-300">Chính sách hoàn hàng</RouterLink></li>
          <li><RouterLink to="/chinh-sach-van-chuyen" class="hover:text-gray-300">Chính sách vận chuyển</RouterLink></li>
          <li><RouterLink to="/nghia-vu-nguoi-ban-va-khach-hang" class="hover:text-gray-300">Chính sách cộng tác viên</RouterLink></li>
          <li><RouterLink to="/chinh-sach-kiem-hang" class="hover:text-gray-300">Chính sách bảo hành</RouterLink></li>
        </ul>
      </div>

      <div>
        <h4 class="text-lg font-semibold mb-2">SẢN PHẨM</h4>
        <ul class="space-y-1">
          <li><a href="#" class="hover:text-gray-300">Sản phẩm nổi bật</a></li>
          <li><a href="#" class="hover:text-gray-300">Bộ sưu tập mới</a></li>
          <li><a href="#" class="hover:text-gray-300">Bộ sưu tập 2025</a></li>
          <li><a href="#" class="hover:text-gray-300">Sản phẩm bán chạy</a></li>
          <li><a href="#" class="hover:text-gray-300">Sản phẩm khuyến mãi</a></li>
        </ul>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { RouterLink } from 'vue-router';
</script>

<style scoped>
footer {
  background-color: black;
  padding: 3rem 0;
}

.container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 1.5rem;
  gap: 2rem;
}

.container>div {
  flex: 1;
}

.container>div:first-child {
  flex: 2;
}

h3,
h4 {
  color: white;
  margin-bottom: 1rem;
  font-weight: 600;
}

ul {
  list-style: none;
  padding: 0;
}

ul li {
  margin-bottom: 0.5rem;
}

ul li a {
  color: #9ca3af;
  transition: color 0.3s ease;
}

ul li a:hover {
  color: white;
}

.flex.gap-2 {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }

  .container>div {
    width: 100%;
  }
}

section h3 {
  text-transform: uppercase;
  letter-spacing: 0.05em;
  line-height: 1.5;
}

section a {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

section a:hover {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.store-heading {
  font-size: 2.5rem;
  font-weight: 800;
  color: #1e3a8a;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 1.5rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  background: linear-gradient(45deg, #1e3a8a, #3b82f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  padding-bottom: 0.5rem;
}

.store-heading span {
  font-size: 1.875rem;
  background: none;
  -webkit-text-fill-color: #4b5563;
  text-shadow: none;
  display: block;
  margin-top: 0.5rem;
}
</style>