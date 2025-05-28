<template>
  <div class="w-full">
    <img src="@/assets/img/slide1.jpg" alt="Banner" class="w-full h-[600px] object-cover rounded-none" width="100%" />
  </div>

  <!-- Services section -->
  <section class="bg-gradient-to-b from-gray-100 to-white py-8">
    <div class="w-full max-w-[1300px] mx-auto flex flex-nowrap justify-between items-center gap-4 flex-wrap">
      <div v-for="(service, index) in services" :key="index"
           class="flex-1 bg-white rounded-lg shadow-sm p-3 flex items-center gap-3 hover:shadow-lg transition duration-300 transform hover:-translate-y-1 min-w-[180px]">
        <img :src="getImgUrl(service.icon)" alt="service-icon" class="w-10 h-10" />
        <div class="flex-1 min-w-0">
          <span class="font-semibold text-sm text-gray-800 block truncate">{{ service.title }}</span>
          <p class="text-xs text-gray-500 truncate">{{ service.desc }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
const serviceImages = import.meta.glob('@/assets/img/icon_s_*.png', {
  eager: true,
  import: 'default',
});

export default {
  name: 'Banner',
  data() {
    return {
      services: [
        { icon: 'icon_s_1.png', title: '10000+ sản phẩm', desc: '400 thương hiệu' },
        { icon: 'icon_s_2.png', title: '2 Triệu khách hàng', desc: 'Tin tưởng mua sắm' },
        { icon: 'icon_s_3.png', title: 'Hàng chính hãng', desc: '100% chính hãng' },
        { icon: 'icon_s_4.png', title: 'Giao hàng miễn phí', desc: 'Miễn phí nội thành' },
        { icon: 'icon_s_5.png', title: 'Đổi hàng 07 ngày', desc: 'Hoàn toàn miễn phí' },
        { icon: 'icon_s_6.png', title: 'Khách VIP', desc: 'Ưu đãi hấp dẫn' },
      ],
    };
  },
  methods: {
    getImgUrl(filename) {
      return serviceImages[`/src/assets/img/${filename}`];
    }
  }
};
</script>

<style scoped>
img[alt="Banner"] {
  object-fit: cover;
  transition: transform 0.4s ease;
}

img[alt="Banner"]:hover {
  transform: scale(1.01);
}

section img {
  width: 40px;
  height: 40px;
}

section span {
  font-weight: 600;
  font-size: 0.95rem;
  color: #1f2937;
}

section p {
  font-size: 0.8rem;
  color: #6b7280;
  margin-top: 0.25rem;
}

@media (max-width: 768px) {
  section>div>div {
    flex: 0 0 48%;
    margin-bottom: 1rem;
  }
}
</style>

<style scoped>
/* Banner */
img[alt="Banner"] {
  border-radius: 0;
  object-fit: cover;
  transition: transform 0.4s ease;
}

img[alt="Banner"]:hover {
  transform: scale(1.01);
}

/* Services Section */
section {
  background: linear-gradient(to bottom, #f9fafb 0%, #ffffff 100%);
  padding: 2rem 1.5rem;
}

section>div {
  max-width: 1300px;
  margin: auto;
  display: flex;
  gap: 1rem;
}

section>div>div {
  flex: 1;
  background-color: white;
  border-radius: 0.75rem;
  padding: 1rem;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

section>div>div:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}

section img {
  width: 40px;
  height: 40px;
}

section span {
  font-weight: 600;
  font-size: 0.95rem;
  color: #1f2937;
}

section p {
  font-size: 0.8rem;
  color: #6b7280;
  margin-top: 0.25rem;
}

/* Search bar - Tailwind handles it well, but enhance focus effect */
input:focus {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3);
}

/* Responsive Fixes */
@media (max-width: 768px) {
  .icons-right {
    gap: 1rem;
    font-size: 1.25rem;
  }

  .icon-lg {
    font-size: 2rem;
    /* tương đương text-3xl */
  }


  section>div {
    flex-wrap: wrap;
  }

  section>div>div {
    flex: 0 0 48%;
    margin-bottom: 1rem;
  }
}
</style>