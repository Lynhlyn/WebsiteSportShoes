package com.example.baskend.KhachHang.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class KhachHangResponse {
    private Integer id;
    private String taiKhoan;
    private Integer idTk;
    private String maKhachHang;
    private String hoTen;
    private Boolean gioiTinh;
    private String email;
    private String soDienThoai;
    private Boolean trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
}