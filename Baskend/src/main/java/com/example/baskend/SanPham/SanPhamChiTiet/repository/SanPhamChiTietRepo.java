package com.example.baskend.SanPham.SanPhamChiTiet.repository;

import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamChiTietRepo extends JpaRepository<SanPhamChiTiet, Integer> {

    @Query("SELECT sp FROM SanPhamChiTiet sp WHERE sp.sanPham.tenSanPham IS NOT NULL AND LOWER(sp.sanPham.tenSanPham) LIKE LOWER(CONCAT('%', :tenSanPham, '%'))")
    List<SanPhamChiTiet> searchByTenSanPham(@Param("tenSanPham") String tenSanPham);
}

