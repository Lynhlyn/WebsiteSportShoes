package com.example.baskend.SanPham.QuanLySanPham.repository;

import com.example.baskend.SanPham.QuanLySanPham.entity.AnhSanPham;
import com.example.baskend.SanPham.QuanLySanPham.projection.SanPhamChiTietProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AnhSanPhamRepo extends JpaRepository<AnhSanPham, Integer> {
    List<AnhSanPham> findBySanPhamId(Integer sanPhamId);

    @Query("""
        SELECT sp.tenSanPham AS tenSanPham, 
               spct.giaBan AS giaBan, 
               km.phanTramGiamGia AS phanTramGiamGia,
               (spct.giaBan - (spct.giaBan * km.phanTramGiamGia / 100)) AS giaSauGiam,
               asp.anhSP AS anhSP
        FROM AnhSanPham asp
        JOIN asp.sanPham sp
        JOIN SanPhamChiTiet spct ON sp.id = spct.sanPham.id
        JOIN KhuyenMai km ON spct.khuyenMai.id = km.id
        WHERE asp.trangThai = true AND spct.trangThai = true AND km.trangThai = true
    """)
    List<SanPhamChiTietProjection> findAllSanPhamWithImagesAndDiscount();

    @Modifying
    @Transactional
    @Query("DELETE FROM AnhSanPham a WHERE a.sanPham.id = :sanPhamId")
    void deleteBySanPhamId(@Param("sanPhamId") Integer sanPhamId);
}
