package com.example.baskend.NhanVien.entity;

import com.example.baskend.TaiKhoan.Entity.TaiKhoan;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "nhan_vien")
@AllArgsConstructor
@NoArgsConstructor
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_nhan_vien", unique = true, nullable = false)
    private String maNhanVien;

    @ManyToOne
    @JoinColumn(name = "id_tai_khoan",referencedColumnName = "id")
    private TaiKhoan taiKhoan;


    @Column(name = "ho_ten", nullable = true)
    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @Column(name = "gioi_tinh", nullable = false)
    @Min(value = 0, message = "Giới tính không hợp lệ")
    private int gioiTinh;

    @Column(name = "nam_sinh", nullable = false)
    @Min(value = 1900, message = "Năm sinh không hợp lệ")
    private int namSinh;

    @Column(name = "so_dien_thoai", unique = true, nullable = false)
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String soDienThoai;

    @Column(name = "email", nullable = false)
    @Email(message = "Email không hợp lệ")
    private String email;

    @Column(name = "dia_chi", nullable = false)
    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;


    @Column(name = "ngay_tao", nullable = false, columnDefinition = "DATETIME2(3) default CURRENT_TIMESTAMP")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao;

    @Column(name = "ngay_sua", nullable = false, columnDefinition = "DATETIME2(3) default CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngaySua;

    @Column(name = "trang_thai", nullable = false, columnDefinition = "BIT default 1")
    private boolean trangThai;
}
