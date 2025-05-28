package com.example.baskend.SanPham.SanPhamChiTiet.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietResonse {
    private Integer id;
    private String maSPCT;
    private Integer sanPhamId; // ID của sản phẩm chính

    private String tenSanPham;
    private String tenDeGiay;
    private String tenThuongHieu;
    private String tenChatLieu;
    private String tenDanhMuc;
    private String tenMau;
    private String tenKhuyenMai;
    private Float phanTramGiamGia;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String tenSize;

    private Float giaBan;
    private Integer soLuong;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private Boolean trangThai;
}
