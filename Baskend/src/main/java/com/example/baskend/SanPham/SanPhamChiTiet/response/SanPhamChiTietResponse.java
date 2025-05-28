package com.example.baskend.SanPham.SanPhamChiTiet.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTietResponse {
    private Integer id;
    private String maSPCT;
    private Integer sanPhamId;
    private String tenSanPham;
    private String tenDeGiay;
    private String tenThuongHieu;
    private String tenChatLieu;
    private String tenDanhMuc;
    private String tenMau;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String tenSize;

    private Float giaBan;
    private Integer soLuong;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private Boolean trangThai;
}
