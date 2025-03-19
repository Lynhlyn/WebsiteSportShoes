package com.example.baskend.DonHangChiTiet.entity;

import com.example.baskend.DonHang.entity.DonHang;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "don_hang_chi_tiet")
public class DonHangChiTiet {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_don_hang", nullable = false)
    private DonHang donHang;

    @ManyToOne
    @JoinColumn(name = "id_spct", nullable = false)
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", nullable = false)
    private Float donGia;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "ngay_sua", nullable = false)
    private LocalDateTime ngaySua = LocalDateTime.now();

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;
}
