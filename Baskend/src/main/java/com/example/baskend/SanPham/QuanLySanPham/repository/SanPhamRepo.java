package com.example.baskend.SanPham.QuanLySanPham.repository;

import com.example.baskend.SanPham.QuanLySanPham.entity.AnhSanPham;
import com.example.baskend.SanPham.QuanLySanPham.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepo extends JpaRepository<SanPham, Integer> {
    List<SanPham> findByTenSanPhamContainingIgnoreCase(String keyword);

    @Query("SELECT sp FROM SanPham sp " +
            "WHERE (:trangThai IS NULL OR sp.trangThai = :trangThai) " +
            "AND (:tenThuongHieu IS NULL OR sp.thuongHieu.tenThuongHieu = :tenThuongHieu) " +
            "AND (:tenDanhMuc IS NULL OR sp.danhMuc.tenDanhMuc = :tenDanhMuc) " +
            "AND (:tenChatLieu IS NULL OR sp.chatLieu.tenChatLieu = :tenChatLieu) " +
            "AND (:tenDeGiay IS NULL OR sp.deGiay.tenDeGiay = :tenDeGiay)")
    List<SanPham> filterSanPham(
            @Param("trangThai") Boolean trangThai,
            @Param("tenThuongHieu") String tenThuongHieu,
            @Param("tenDanhMuc") String tenDanhMuc,
            @Param("tenChatLieu") String tenChatLieu,
            @Param("tenDeGiay") String tenDeGiay);

    @Query("SELECT a FROM AnhSanPham a WHERE a.sanPham.id = :sanPhamId")
    List<AnhSanPham> findBySanPhamId(@Param("sanPhamId") Integer sanPhamId);
    boolean existsByTenSanPham(String tenSanPham);
    boolean existsByTenSanPhamIgnoreCaseAndIdNot(String tenSanPham, Integer id);
}
