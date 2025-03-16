package com.example.baskend.ThongKe.entity;

import com.example.baskend.KhachHang.entity.KhachHang;
import com.example.baskend.Vouchers.entity.Voucher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "don_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_don_hang", unique = true, nullable = false, length = 255)
    private String maDonHang;

//    @ManyToOne
//    @JoinColumn(name = "id_nhan_vien")
//    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "id_voucher")
    private Voucher voucher;

//    @ManyToOne
//    @JoinColumn(name = "id_phuong_thuc_thanh_toan")
//    private PhuongThucThanhToan phuongThucThanhToan;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @Column(name = "loai_don_hang", nullable = false)
    private Boolean loaiDonHang;

    @Column(name = "trang_thai_don_hang", nullable = false, length = 255)
    private String trangThaiDonHang;

    @Column(name = "tong_tien", nullable = false, precision = 10, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "chi_phi_giao_hang", nullable = false, precision = 10, scale = 2)
    private BigDecimal chiPhiGiaoHang;

    @Column(name = "ngay_tao", nullable = false, columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "ngay_sua", nullable = false, columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime ngaySua = LocalDateTime.now();
}
