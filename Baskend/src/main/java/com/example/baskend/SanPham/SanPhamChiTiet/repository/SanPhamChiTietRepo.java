package com.example.baskend.SanPham.SanPhamChiTiet.repository;

import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamChiTietRepo extends JpaRepository<SanPhamChiTiet, Integer> {

        Optional<SanPhamChiTiet> findById(Integer id);  // Lấy sản phẩm chi tiết theo ID



    @Query("SELECT sp FROM SanPhamChiTiet sp WHERE sp.sanPham.tenSanPham IS NOT NULL AND LOWER(sp.sanPham.tenSanPham) LIKE LOWER(CONCAT('%', :tenSanPham, '%'))")
    List<SanPhamChiTiet> searchByTenSanPham(@Param("tenSanPham") String tenSanPham);


    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN FETCH spct.size WHERE spct.sanPham.id = :id")
    List<SanPhamChiTiet> findBySanPhamId(@Param("id") Integer id);

    ////////////////////////////////////////////////////////
    @Query("SELECT  spct FROM SanPhamChiTiet  spct " +

            "WHERE (:tenMau IS NULL OR spct.mauSac.tenMau = :tenMau) " +
            "AND (:tenSize IS NULL OR spct.size.tenSize = :tenSize)")
    List<SanPhamChiTiet> filterSanPhamByMauSacAndSize(
            @Param("tenMau") String tenMau,
            @Param("tenSize") String tenSize);

//     @Query("SELECT sp FROM SanPhamChiTiet sp " +
//     "JOIN sp.khuyenMai km " +
//     "JOIN sp.sanPham sp1 " +
//     "WHERE (:keyword IS NULL OR LOWER(sp1.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
//     "AND (:startDate IS NULL OR km.ngayBatDau >= :startDate) " +
//     "AND (:endDate IS NULL OR km.ngayKetThuc <= :endDate) " +
//     "AND (:sizeId IS NULL OR sp.size.id = :sizeId) " +
//     "AND (:colorId IS NULL OR sp.mauSac.id = :colorId) " +
//     "AND (:status IS NULL OR sp.trangThai = :status)")
// List<SanPhamChiTiet> filterSanPhamChiTiet(
// @Param("keyword") String keyword,
// @Param("startDate") LocalDateTime startDate,
// @Param("endDate") LocalDateTime endDate,
// @Param("sizeId") Integer sizeId,
// @Param("colorId") Integer colorId,
// @Param("status") Boolean status);
@Query("SELECT sp FROM SanPhamChiTiet sp " +
        "JOIN FETCH sp.khuyenMai km " +
        "JOIN FETCH sp.sanPham sp1 " +
        "LEFT JOIN FETCH sp1.deGiay " +
        "LEFT JOIN FETCH sp1.thuongHieu " +
        "LEFT JOIN FETCH sp1.chatLieu " +
        "LEFT JOIN FETCH sp1.danhMuc " +
        "WHERE (COALESCE(:keyword, '') = '' OR LOWER(sp1.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
        "AND (:startDate IS NULL OR km.ngayBatDau >= :startDate) " +
        "AND (:endDate IS NULL OR km.ngayKetThuc <= :endDate) " +
        "AND (:sizeId IS NULL OR sp.size.id = :sizeId) " +
        "AND (:colorId IS NULL OR sp.mauSac.id = :colorId) " +
        "AND (:status IS NULL OR sp.trangThai = :status)")
List<SanPhamChiTiet> filterSanPhamChiTiet(
        @Param("keyword") String keyword,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("sizeId") Integer sizeId,
        @Param("colorId") Integer colorId,
        @Param("status") Boolean status);
}

