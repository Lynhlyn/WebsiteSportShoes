import { createRouter, createWebHistory } from 'vue-router';
import StaffLayout from '@/views/Admin/Staff/StaffLayout.vue';
import StaffManage from '@/views/Admin/Staff/StaffManage.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/Admin/HomePage.vue'),
      children: [
        // Bán hàng
        {
          path: 'sales/offline',
          name: 'sales-offline',
          component: () => import('../views/Admin/Sales/OfflineSales.vue'),
        },
        {
          path: 'sales/online',
          name: 'sales-online',
          component: () => import('../views/Admin/Sales/OnlineSales.vue'),
        },
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
          path: 'products/manage/view-sanpham/:id',
          name: 'admin-view-product',
          component: () => import('../views/Admin/SanPham/QuanLySanPham/ViewSanPham.vue'),
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
          path: 'products/details/view-spct/:id',
          name: 'view-products-details',
          component: () => import('../views/Admin/SanPham/SanPhamChiTiet/ViewSanPhamChiTiet.vue'),
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


        // Nhân viên
        // { path: 'staff', component: StaffLayout,
        //   children: [
        //     {path: '', component: StaffManage },
        //   ]
        // },

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
          path: 'order-details',
          name: 'order-details',
          component: () => import('../views/Admin/Orders/OrderDetails.vue'),
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
      name: 'user-home',
      component: () => import('../views/User/TrangChu/HomePage.vue'),
      children: [
        {
          path: '',
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
          path: 'thanh-toan',
          name: 'y',
          component: () => import('../views/User/SanPham/Checkout.vue'),
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

export default router;
