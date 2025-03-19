package com.example.baskend.DonHang.repository;

import com.example.baskend.DonHang.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonHangRepo extends JpaRepository<DonHang,Integer> {
    DonHang findByMaDonHang(String maDonHang);
    DonHang findByKhachHangId(Integer  maDonHang);
    DonHang findByTrangThaiDonHang(String trangThai);
}
