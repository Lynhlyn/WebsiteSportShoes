package com.example.baskend.SanPham.QuanLySanPham.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "san_pham_thuoc_tinh")
public class SanPhamThuocTinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_san_pham", referencedColumnName = "id", nullable = false)
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "id_chat_lieu", referencedColumnName = "id", nullable = false)
    private ChatLieu chatLieu;

    @ManyToOne
    @JoinColumn(name = "id_de_giay", referencedColumnName = "id", nullable = false)
    private DeGiay deGiay;

    @ManyToOne
    @JoinColumn(name = "id_danh_muc", referencedColumnName = "id", nullable = false)
    private DanhMuc danhMuc;

    @ManyToOne
    @JoinColumn(name = "id_thuong_hieu", referencedColumnName = "id", nullable = false)
    private ThuongHieu thuongHieu;
}
