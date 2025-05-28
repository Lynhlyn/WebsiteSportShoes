package com.example.baskend.TaiKhoan.DTO;

import com.example.baskend.VaiTro.Entity.VaiTro;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer id;
    private String tenDangNhap;
    private VaiTro vaiTro;
    private String token;
    private String tenKhachHang;
}