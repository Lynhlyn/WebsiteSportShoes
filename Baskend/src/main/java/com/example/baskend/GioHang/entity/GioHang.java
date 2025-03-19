package com.example.baskend.GioHang.entity;

import com.example.baskend.KhachHang.entity.KhachHang;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gio_hang_chi_tiet")

public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "id_spct", referencedColumnName = "id")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "trang_thai")
    private Boolean trangThai = true;
}
