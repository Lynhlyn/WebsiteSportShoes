package com.example.baskend.NhanVien.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "nhan_vien")
@AllArgsConstructor
@NoArgsConstructor
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_nhan_vien", unique = true, nullable = false)
    private String maNhanVien;

    @Column(name = "ten_dang_nhap", unique = true, nullable = false)
    private String tenDangNhap;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "ho_ten", nullable = true)
    private String hoTen;

    @Column(name = "gioi_tinh", nullable = false)
    private int gioiTinh;

    @Column(name = "nam_sinh", nullable = false)
    private int namSinh;

    @Column(name = "so_dien_thoai", unique = true, nullable = false)
    private String soDienThoai;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "dia_chi", nullable = false)
    private String diaChi;

    @Column(name = "vai_tro", columnDefinition = "int default 0")
    private int vaiTro;  // 1: ADMIN, 0: USER

    @Column(name = "ngay_tao", nullable = false, columnDefinition = "DATETIME2(3) default CURRENT_TIMESTAMP")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao;

    @Column(name = "ngay_sua", nullable = false, columnDefinition = "DATETIME2(3) default CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngaySua;

    @Column(name = "trang_thai", nullable = false, columnDefinition = "BIT default 1")
    private boolean trangThai;
}

