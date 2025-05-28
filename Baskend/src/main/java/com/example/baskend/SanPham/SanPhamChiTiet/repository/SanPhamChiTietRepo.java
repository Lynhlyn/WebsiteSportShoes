package com.example.baskend.SanPham.SanPhamChiTiet.repository;

import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamChiTietRepo extends JpaRepository<SanPhamChiTiet, Integer> {

    Optional<SanPhamChiTiet> findById(Integer id);  // Lấy sản phẩm chi tiết theo ID

    @Query("SELECT MAX(sct.maSPCT) FROM SanPhamChiTiet sct WHERE sct.sanPham.id = :sanPhamId")
    String findMaxMaSPCTBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    @Query("SELECT sp FROM SanPhamChiTiet sp WHERE sp.sanPham.tenSanPham IS NOT NULL AND LOWER(sp.sanPham.tenSanPham) LIKE LOWER(CONCAT('%', :tenSanPham, '%'))")
    List<SanPhamChiTiet> searchByTenSanPham(@Param("tenSanPham") String tenSanPham);


    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN FETCH spct.size WHERE spct.sanPham.id = :id")
    List<SanPhamChiTiet> findBySanPhamId(@Param("id") Integer id);

    boolean existsByMaSPCT(String maSPCT);
    boolean existsByMaSPCTAndIdNot(String maSPCT, Integer id);

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
    ////////////////////////////
    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :idSanPham")
    List<SanPhamChiTiet> findAllBySanPhamId(@Param("idSanPham") Integer idSanPham);

    @Query("SELECT sp FROM SanPhamChiTiet sp " +
            "WHERE (:idSanPham IS NULL OR sp.sanPham.id = :idSanPham) " +
            "AND (:idSize IS NULL OR sp.size.id = :idSize) " +
            "AND (:idMauSac IS NULL OR sp.mauSac.id = :idMauSac OR sp.mauSac.id IS NULL) " +
            "AND (:trangThai IS NULL OR sp.trangThai = :trangThai) " +
            "AND (:ngayBatDau IS NULL OR sp.khuyenMai IS NOT NULL AND sp.khuyenMai.ngayBatDau >= :ngayBatDau) " +
            "AND (:ngayKetThuc IS NULL OR sp.khuyenMai IS NOT NULL AND sp.khuyenMai.ngayKetThuc <= :ngayKetThuc)")
    List<SanPhamChiTiet> filterSanPhamChiTiet(
            @Param("idSanPham") Integer idSanPham,
            @Param("idSize") Integer idSize,
            @Param("idMauSac") Integer idMauSac,
            @Param("trangThai") Boolean trangThai,
            @Param("ngayBatDau") LocalDate ngayBatDau,
            @Param("ngayKetThuc") LocalDate ngayKetThuc);

    // Kiểm tra trùng khi thêm mới
    boolean existsBySanPhamIdAndMauSacIdAndSizeId(Integer sanPhamId, Integer mauSacId, Integer sizeId);

    // Kiểm tra trùng khi cập nhật (loại trừ chính nó)
    boolean existsBySanPhamIdAndMauSacIdAndSizeIdAndIdNot(Integer sanPhamId, Integer mauSacId, Integer sizeId, Integer excludeId);


}

