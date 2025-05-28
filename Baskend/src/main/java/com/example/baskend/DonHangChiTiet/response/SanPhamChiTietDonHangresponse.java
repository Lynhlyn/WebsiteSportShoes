package com.example.baskend.DonHangChiTiet.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTietDonHangresponse {
    private Integer id;
    private Integer idspct;
    private String maSPCT;
    private String tenSanPham;
    private String tenDeGiay;
    private String tenThuongHieu;
    private String tenChatLieu;
    private String tenDanhMuc;
    private String tenMau;

    private String tenSize;
    private Float giaBan;

    private Integer soLuong;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;

    private Boolean trangThaispct;

}
