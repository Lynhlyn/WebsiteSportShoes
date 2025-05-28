package com.example.baskend.TaiKhoan.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanResponse {
    private Integer id;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private boolean trangThai;
}
