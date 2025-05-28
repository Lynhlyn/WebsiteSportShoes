import { createRouter, createWebHistory } from 'vue-router';
import StaffLayout from '@/views/Admin/Staff/StaffLayout.vue';
import StaffManage from '@/views/Admin/Staff/StaffManage.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Add authentication routes
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Auth/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Auth/Register.vue')
    },
    // Modify admin route to include authentication
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/Admin/HomePage.vue'),
      meta: { requiresAuth: true },
      children: [
        // Bán hàng
        {
          path: 'sales/offline',
          name: 'sales-offline',
          component: () => import('../views/Admin/Sales/OfflineSales.vue'),
        },

        // {
        //   path: 'sales/online',
        //   name: 'sales-online',
        //   component: () => import('../views/Admin/Sales/OnlineSales.vue'),
        // },
        // Thống kê
        {
          path: 'stats',
          name: 'ThongKe',
          component: () => import('../views/Admin/ThongKe/ThongKe.vue'),
        },
        // Sản phẩm
        {
          path: 'products/manage',
          name: 'admin-products',
          component: () => import('../views/Admin/SanPham/QuanLySanPham/SanPham.vue'),
        },
        {
          path: 'products/manage/add-sanpham',
          name: 'admin-add-product',
          component: () => import('../views/Admin/SanPham/QuanLySanPham/AddSanPham.vue'),
        },
        {
          path: 'products/manage/update-sanpham/:id',
          name: 'admin-update-product',
          component: () => import('../views/Admin/SanPham/QuanLySanPham/UpdateSanPham.vue'),
          props: true,
        },
        {
          path: 'products/details',
          name: 'products-details',
          component: () => import('../views/Admin/SanPham/SanPhamChiTiet/SanPhamChiTiet.vue'),
        },
        {
          path: 'products/details/add-spct',
          name: 'add-products-details',
          component: () => import('../views/Admin/SanPham/SanPhamChiTiet/AddSPCT.vue'),
        },
        {
          path: 'products/details/update-spct/:id',
          name: 'update-products-details',
          component: () => import('../views/Admin/SanPham/SanPhamChiTiet/UpdateSPCT.vue'),
        },
        {
          path: 'products/promotions',
          name: 'products-promotions',
          component: () => import('../views/Admin/SanPham/KhuyenMai/KhuyenMai.vue'),
        },

        {
          path: 'khuyen-mai/manage/add-khuyenmai',
          name: 'khuyen-mai-manage-add-khuyenmai',
          component: () => import('../views/Admin/SanPham/KhuyenMai/AddKhuyenMai.vue'),
        },
        {
          path: 'khuyen-mai/manage/update-khuyenmai/:id',
          name: 'khuyen-mai-manage-update-khuyenmai',
          component: () => import('../views/Admin/SanPham/KhuyenMai/UpdateKhuyenMai.vue'),
        },
        {
          path: 'products/thuoc_tinh',
          name: 'admin-products-thuoc_tinh',
          component: () => import('../views/Admin/SanPham/SanPhamThuocTinh/SanPhamThuocTinh.vue'),
        },
        {
          path: 'products/de_giay',
          name: 'admin-products-de_giay',
          component: () => import('../views/Admin/SanPham/DeGiay/DeGiay.vue'),
        },
        {
          path: 'products/thuong_hieu',
          name: 'admin-products-thuong_hieu',
          component: () => import('../views/Admin/SanPham/ThuongHieu/ThuongHieu.vue'),
        },
        {
          path: 'products/chat_lieu',
          name: 'admin-products-chat_lieu',
          component: () => import('../views/Admin/SanPham/ChatLieu/ChatLieu.vue'),
        },
        {
          path: 'products/categories',
          name: 'admin-products-categories',
          component: () => import('../views/Admin/SanPham/DanhMuc/DanhMuc.vue'),
        },
        {
          path: 'products/size',
          name: 'admin-products-size',
          component: () => import('../views/Admin/SanPham/Size/Size.vue'),
        },

        {
          path: 'products/mau_sac',
          name: 'admin-products-mau_sac',
          component: () => import('../views/Admin/SanPham/MauSac/MauSac.vue'),
        },


        // Voucher
        {
          path: 'vouchers',  // Đường dẫn con: /admin/vouchers
          name: 'Voucher',
          component: () => import('../views/Admin/Voucher/Voucher.vue')  // Component Voucher
        },
        {
          path: 'vouchers/add',  // Đường dẫn cho thêm mới voucher
          name: 'AddVoucher',
          component: () => import('../views/Admin/Voucher/AddVoucher.vue')
        },
        {
          path: 'vouchers/edit/:id',  // Đường dẫn cho chỉnh sửa voucher, nhận id voucher
          name: 'EditVoucher',
          component: () => import('../views/Admin/Voucher/EditVoucher.vue'),  // Component chỉnh sửa voucher
          props: true  // Truyền id voucher dưới dạng props
        },
        {
          path: 'accounts',  // Đường dẫn con: /admin/vouchers
          name: 'Account',
          component: () => import('../views/Admin/Account/accounts.vue')  // Component Voucher
        },


        // Nhân viên
        // { path: 'staff', component: StaffLayout,
        //   children: [
        //     {path: '', component: StaffManage },
        //   ]
        // },
        {
          path: 'staff',  // Đường dẫn con: /admin/vouchers
          name: 'staff',
          component: () => import('../views/Admin/Staff/NhanVien.vue')  // Component Voucher
        },

        //  Khach hang

        {
          path: "/admin/customers",
          name: "customers",
          component: () => import("../views/Admin/KhachHang/KhachHang.vue")
        },
        {
          path: 'customers/manage/add-khachhang',
          name: 'admin-add-customer',
          component: () => import('../views/Admin/KhachHang/AddKhachHang.vue'),
        },
        {
          path: 'customers/manage/update-khachhang/:id',
          name: 'admin-update-customer',
          component: () => import('../views/Admin/KhachHang/UpdateKhachHang.vue'),
        },
        // Đơn hàng
        {
          path: 'orders',
          name: 'orders',
          component: () => import('../views/Admin/Orders/OrderManage.vue'),
        },
        {
          path: '/xac-nhan',
          name: 'xac-nhan',
          component: () => import('../views/Admin/Orders/XacNhanDonHang.vue'),
          props:true
        },
        {
          path: 'orders-online',
          name: 'orders-online',
          component: () => import('../views/Admin/Orders/OrderOnline.vue'),
        },

        // Chat hỗ trợ
        {
          path: 'chat',
          name: 'chat-support',
          component: () => import('../views/Admin/Chat/ChatSupport.vue'),
        },
      ],
    },
    {
  path: '/',
  redirect: '/login'
},
    {
      path: '/home',
      name: 'user-home',
      component: () => import('../views/User/TrangChu/HomePage.vue'),
      children: [
        {
          path: '/trang-chu',
          name: 'trangchu',
          component: () => import('../views/User/TrangChu/TrangChu.vue'),
        },

        {
          path: '/san-pham/:id',
          name: 'san-pham-chi-tiet',
          component: () => import('../views/User/SanPham/SanPhamChiTiet.vue'),
          props:true
        },
        {
          path: '/giohang',
          name: 'giohang',
          component: () => import('../views/User/SanPham/Cart.vue'),
        },
        {
          path: '/thanh-toan',
          name: 'thanh-toan',
          component: () => import('../views/User/SanPham/ThanhToan.vue'),
          props:true
        },
        {
          path: '/thanh-toan-thanh-cong',
          name: 'thanh-toan-thanh-cong',
          component: () => import('../views/User/SanPham/ThanhToanThanhCong.vue'),
          props:true
        },
        {
          path: '/don-mua',
          name: 'don-mua',
          component: () => import('../views/User/SanPham/DonMua.vue'),
          props:true
        },
      ]
    },
    {
      path: '/chinh-sach-van-chuyen',
      name: 'shipping-policy',
      component: () => import('../views/User/Policies/ShippingPolicy.vue'),
    },
    {
      path: '/chinh-sach-doi-tra',
      name: 'return-policy',
      component: () => import('../views/User/Policies/ReturnPolicy.vue'),
    },
    {
      path: '/chinh-sach-bao-mat',
      name: 'privacy-policy',
      component: () => import('../views/User/Policies/PrivacyPolicy.vue'),
    },
    {
      path: '/chinh-sach-kiem-hang',
      name: 'inspection-policy',
      component: () => import('../views/User/Policies/InspectionPolicy.vue'),
    },
    {
      path: '/nghia-vu-nguoi-ban-va-khach-hang',
      name: 'obligation-policy',
      component: () => import('../views/User/Policies/ObligationPolicy.vue'),
    },
    {
      path: '/ho-tro-khach-hang',
      name: 'customer-support',
      component: () => import('../views/User/Support/CustomerSupport.vue'),
    },
    {
      path: '/gioi-thieu',
      name: 'about',
      component: () => import('../views/User/Support/About.vue'),
    },
    {
      path: '/he-thong-cua-hang',
      name: 'store-locations',
      component: () => import('../views/User/Support/StoreLocations.vue'),
    },
    {
      path: '/kiem-tra-don-hang',
      name: 'order-tracking',
      component: () => import('../views/User/Support/OrderTracking.vue'),
    }
  ],
});

// Navigation guard
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const user = JSON.parse(localStorage.getItem('user'));

  if (requiresAuth && !user) {
    next('/login');
  } else if (requiresAuth && user) {
    const role = user.vaiTro.tenVaiTro;
    
    if (to.path.startsWith('/admin')) {
      if (role === 'ADMIN') {
        next();
      } else if (role === 'Nhân viên') {
        if (to.path.includes('/admin/stats') || 
            to.path.includes('/admin/vouchers') || 
            to.path.includes('/admin/staff') ||
            to.path.includes('/admin/accounts')) {
          next('/');
        } else {
          next();
        }
      } else {
        next('/');
      }
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
