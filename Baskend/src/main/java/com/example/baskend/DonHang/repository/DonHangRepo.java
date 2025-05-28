package com.example.baskend.DonHang.repository;

import com.example.baskend.DonHang.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangRepo extends JpaRepository<DonHang,Integer> {
    DonHang findByMaDonHang(String maDonHang);
    DonHang findByKhachHangId(Integer  maDonHang);
    DonHang findByTrangThaiDonHang(String trangThai);

    List<DonHang> findByLoaiDonHang(Boolean loaiDonHang);

    // Tìm kiếm theo mã đơn hàng (chính xác)
    List<DonHang> findByMaDonHangContaining(String maDonHang);

    // Tìm kiếm theo tên khách hàng (sử dụng JOIN với bảng KhachHang)
    @Query("SELECT d FROM DonHang d JOIN d.khachHang k WHERE LOWER(k.hoTen) LIKE LOWER(CONCAT('%', :tenKhachHang, '%'))")
    List<DonHang> findByTenKhachHang(@Param("tenKhachHang") String tenKhachHang);
}
