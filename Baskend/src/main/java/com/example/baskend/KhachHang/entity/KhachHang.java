package com.example.baskend.KhachHang.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "khach_hang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Mã khách hàng không được để trống")
    @Column(name = "ma_khach_hang", length = 10)
    private String maKhachHang;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Column(name = "ten_dang_nhap", unique = true, length = 50)
    private String tenDangNhap;

    @NotBlank(message = "Họ tên không được để trống")
    @Column(name = "ho_ten", length = 100)
    private String hoTen;

    @NotNull(message = "Giới tính không được để trống")
    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(name = "email", unique = true, length = 100)
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "mat_khau")
    private String matKhau;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Column(name = "so_dien_thoai", unique = true, length = 15)
    private String soDienThoai;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "ngay_tao")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngaySua;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        this.ngaySua = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngaySua = LocalDateTime.now();
    }
}



