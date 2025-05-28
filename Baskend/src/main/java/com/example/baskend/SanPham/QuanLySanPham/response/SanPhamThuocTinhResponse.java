package com.example.baskend.SanPham.QuanLySanPham.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SanPhamThuocTinhResponse {
    private Integer id;
    private String chatLieu;
    private String deGiay;
    private String danhMuc;
    private String thuongHieu;
    private Integer sanPhamId;
    private String tenSanPham;

    public SanPhamThuocTinhResponse(Integer id, String chatLieu, String deGiay, String danhMuc, String thuongHieu, Integer sanPhamId, String tenSanPham) {
        this.id = id;
        this.chatLieu = chatLieu;
        this.deGiay = deGiay;
        this.danhMuc = danhMuc;
        this.thuongHieu = thuongHieu;
        this.sanPhamId = sanPhamId;
        this.tenSanPham = tenSanPham;
    }
}
