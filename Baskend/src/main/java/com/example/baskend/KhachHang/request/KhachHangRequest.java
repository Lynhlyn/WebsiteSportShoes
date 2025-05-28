package com.example.baskend.KhachHang.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KhachHangRequest {
    private String hoTen;
    private Boolean gioiTinh;
    private String email;
    private String soDienThoai;
    private String tenDangNhap;
    private String matKhau;

    // Địa chỉ
    private String diaChiChiTiet;
    private String phuong;
    private String quan;
    private String thanhPho;
    private String tenNguoiNhan;
}
