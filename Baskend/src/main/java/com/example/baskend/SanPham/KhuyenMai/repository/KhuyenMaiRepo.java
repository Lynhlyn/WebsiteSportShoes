package com.example.baskend.SanPham.KhuyenMai.repository;

import com.example.baskend.SanPham.KhuyenMai.entity.KhuyenMai;
import com.example.baskend.SanPham.QuanLySanPham.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KhuyenMaiRepo extends JpaRepository<KhuyenMai, Integer> {
    List<KhuyenMai> findByTrangThai(Boolean trangThai);

    List<KhuyenMai> findByTenKhuyenMaiContainingIgnoreCase(String keyword);

    List<KhuyenMai> findByNgayBatDauBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<KhuyenMai> findByTrangThaiAndNgayBatDauBetween(Boolean trangThai, LocalDateTime startDate, LocalDateTime endDate);

}
