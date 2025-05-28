package com.example.baskend.TaiKhoan.DTO;

public class RegisterRequest {
    private String tenDangNhap;
    private String matKhau;

    // Getters & Setters
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}
