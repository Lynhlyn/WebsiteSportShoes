package com.example.baskend.Vouchers.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_voucher")
    private String maVoucher;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "loai_voucher")
    private Integer loaiVoucher;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "gia_tri_giam")
    private Double giaTriGiam;

    @Column(name = "gia_tri_toi_thieu")
    private Double giaTriToiThieu;


    @Column(name = "gia_tri_toi_da")
    private Double giaTriToiDa;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    private LocalDateTime ngaySua;

    @Column(name = "kieu_voucher")
    private String kieuVoucher;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL)
    private List<VoucherKhachHang> danhSachKhachHang;

    public Integer getId() {
        return id;
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public String getMoTa() {
        return moTa;
    }

    public LocalDateTime getNgayBatDau() {
        return ngayBatDau;
    }

    public Integer getLoaiVoucher() {
        return loaiVoucher;
    }

    public void setLoaiVoucher(Integer loaiVoucher) {
        this.loaiVoucher = loaiVoucher;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public Double getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(Double giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public Double getGiaTriToiThieu() {
        return giaTriToiThieu;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public LocalDateTime getNgaySua() {
        return ngaySua;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public void setNgayBatDau(LocalDateTime ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public void setNgayKetThuc(LocalDateTime ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public LocalDateTime getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }


    public void setGiaTriToiThieu(Double giaTriToiThieu) {
        this.giaTriToiThieu = giaTriToiThieu;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public void setNgaySua(LocalDateTime ngaySua) {
        this.ngaySua = ngaySua;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

    public String getKieuVoucher() {
        return kieuVoucher;
    }

    public void setKieuVoucher(String kieuVoucher) {
        this.kieuVoucher = kieuVoucher;
    }

    public Double getGiaTriToiDa() {
        return giaTriToiDa;
    }

    public void setGiaTriToiDa(Double giaTriToiDa) {
        this.giaTriToiDa = giaTriToiDa;
    }
}
