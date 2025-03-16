<template>
      <footer class="footer">
            <div class="container">
                  <div class="row">
                        <div class="col-sm-12 col-md-6 col-lg-3">
                              <img src="@/assets/logo/logo.png" alt="" width="100px" height="100px"
                                    class="footer-logo" />
                              <ul class="footer__list">
                                    <li class="footer__item" v-for="(item, index) in contactInfo" :key="index">
                                          <p>
                                                <i :class="item.icon" class="footer__item-icon"></i> {{ item.label }}:
                                                <a v-if="item.link" :href="item.link">{{ item.value }}</a>
                                                <span v-else>{{ item.value }}</span>
                                          </p>
                                    </li>
                                    <li class="social-icons">
                                          <a v-for="(icon, index) in socialIcons" :key="index" :href="icon.link"
                                                class="social-link">
                                                <i :class="icon.class"></i>
                                          </a>
                                    </li>
                              </ul>
                        </div>

                        <div class="col-sm-12 col-md-6 col-lg-3" v-for="(section, idx) in footerSections" :key="idx">
                              <div @click="toggleSection(idx)" class="footer-section">
                                    <h3 class="footer__heading">{{ section.title }}</h3>
                                    <i class="bi bi-chevron-down"></i>
                              </div>
                              <ul class="footer__list" v-show="section.expanded">
                                    <li class="footer__item" v-for="(link, i) in section.links" :key="i">
                                          <router-link :to="link.href" class="footer__item--link">{{ link.text
                                                }}</router-link>
                                    </li>
                              </ul>
                        </div>
                  </div>
            </div>
            <div class="footer__bottom">
                  <p class="footer__text">© Bản quyền thuộc về SportShoes</p>
            </div>
      </footer>
</template>

<script>
export default {
      data() {
            return {
                  contactInfo: [
                        { icon: 'bi bi-geo-alt', label: 'Địa chỉ', value: 'Ho Chi Minh, Viet Nam' },
                        { icon: 'bi bi-telephone', label: 'Phone', value: '0123456789', link: 'tel:0123456789' },
                        { icon: 'bi bi-envelope', label: 'Email', value: 'abc@gmail.com', link: 'mailto:abc@gmail.com' }
                  ],
                  socialIcons: [
                        { class: 'bi bi-facebook', link: '#' },
                        { class: 'bi bi-instagram', link: '#' },
                        { class: 'bi bi-youtube', link: '#' },
                        { class: 'bi bi-twitter', link: '#' }
                  ],
                  footerSections: [
                        {
                              title: 'Thông tin của chúng tôi',
                              expanded: true,
                              links: [
                                    { text: 'Cơ sở 1: 26 Đường D1, P12, Quận Tân Bình, TP.HCM', href: '/' },
                                    { text: 'Cơ sở 2: 86 Đinh Bộ Lĩnh, P10, Quận Bình Thanh, TP.HCM', href: '/' },
                                    { text: 'Lĩnh vực kinh doanh', href: '/' }
                              ]
                        },
                        {
                              title: 'Chính sách',
                              expanded: true,
                              links: [
                                    { text: 'Chính sách bảo hành', href: '/' },
                                    { text: 'Chính sách đổi trả', href: '/' },
                                    { text: 'Chính sách thanh toán', href: '/' },
                                    { text: 'Chính sách giao nhận hàng', href: '/' },
                                    { text: 'Chính sách bảo mật', href: '/' }
                              ]
                        },
                        {
                              title: 'Hỗ trợ chung',
                              expanded: true,
                              links: [
                                    { text: 'Trang chủ', href: '/' },
                                    { text: 'Giới thiệu', href: '/intro' },
                                    { text: 'Sản phẩm', href: '/product' },
                                    { text: 'Tin tức', href: '/news' },
                                    { text: 'Liên hệ', href: '/contact' }
                              ]
                        }
                  ]
            };
      },
      methods: {
            toggleSection(index) {
                  this.footerSections[index].expanded = !this.footerSections[index].expanded;
            }
      }
};
</script>

<style scoped>
.footer {
      background: #f8f9fa;
      padding: 20px 0;
}

.footer-logo {
      border-radius: 50%;
      border: 3px solid #000;
      margin-bottom: 20px;
}

.footer__bottom {
      text-align: center;
      margin-top: 10px;
      padding: 10px 0;
      background: #ddd;
}

.footer__heading {
      font-size: 18px;
      font-weight: bold;
}

.footer__list {
      list-style: none;
      padding: 0;
}

.footer__item {
      margin-bottom: 5px;
}

.footer__item--link {
      text-decoration: none;
      color: #333;
      transition: color 0.3s;
}

.footer__item--link:hover {
      color: #007bff;
}

.footer__item-icon {
      margin-right: 5px;
}

.footer-section {
      display: flex;
      justify-content: space-between;
      cursor: pointer;
      margin-bottom: 8px;
}

.social-icons {
      display: flex;
      gap: 10px;
      margin-top: 10px;
}

.social-link {
      font-size: 24px;
      transition: transform 0.3s;
}

.social-link:hover {
      transform: scale(1.1);
}
</style>