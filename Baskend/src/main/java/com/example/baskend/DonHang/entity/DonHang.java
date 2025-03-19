package com.example.baskend.DonHang.entity;

import com.example.baskend.KhachHang.entity.KhachHang;
import com.example.baskend.NhanVien.entity.NhanVien;
import com.example.baskend.PhuongThucThanhToan.entity.PhuongThucThanhToan;
import com.example.baskend.Vouchers.entity.Voucher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "don_hang")

public class DonHang {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;
   @Column(name = "ma_don_hang")
   private String maDonHang; // Mã đơn hàng

   @ManyToOne
   @JoinColumn(name = "id_nhan_vien", nullable = true)
   private NhanVien nhanVien; // Liên kết với nhân viên

   @ManyToOne
   @JoinColumn(name = "id_voucher", nullable = true)
   private Voucher voucher; // Liên kết với voucher

   @ManyToOne
   @JoinColumn(name = "id_phuong_thuc_thanh_toan", nullable = true)
   private PhuongThucThanhToan phuongThucThanhToan; // Phương thức thanh toán

   @ManyToOne
   @JoinColumn(name = "id_khach_hang", nullable = true)
   private KhachHang khachHang; // Liên kết với khách hàng
   @Column(name = "loai_don_hang", nullable = true)
   private Boolean loaiDonHang; // Online (1), Offline (0)
   @Column(name = "trang_thai_don_hang", nullable = true)
   private String trangThaiDonHang; // Trạng thái đơn hàng (Ví dụ: 'Đang giao', 'Đã giao')
   @Column(name = "tong_tien", nullable = true)
   private Double tongTien; // Tổng tiền thanh toán (bao gồm chi phí giao hàng, khuyến mãi)
   @Column(name = "chi_phi_giao_hang", nullable = true)
   private Double chiPhiGiaoHang; // Chi phí giao hàng
   @Column(name = "ngay_tao")
   private LocalDateTime ngayTao; // Ngày tạo đơn hàng
   @Column(name = "ngay_sua")
   private LocalDateTime ngaySua; // Ngày cập nhật
//
//    private LocalDateTime ngayThanhToan; // Ngày thanh toán

//    private String trangThaiThanhToan; // Trạng thái thanh toán (Ví dụ: 'Chưa thanh toán', 'Đã thanh toán')

//    @PrePersist
//    protected void onCreate() {
//        this.ngayTao = LocalDateTime.now();
//        this.ngaySua = LocalDateTime.now();
//        this.trangThaiThanhToan = "Chưa thanh toán"; // Mặc định trạng thái thanh toán là 'Chưa thanh toán'
//    }

   @PreUpdate
   protected void onUpdate() {
      this.ngaySua = LocalDateTime.now();
   }
}
