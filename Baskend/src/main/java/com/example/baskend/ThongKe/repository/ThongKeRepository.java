package com.example.baskend.ThongKe.repository;


import com.example.baskend.DonHang.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ThongKeRepository extends JpaRepository<DonHang,Integer> {
    // Tổng doanh thu theo ngày
    @Query(value = """
    SELECT 
        CAST(ngay_tao AS DATE) AS ngay, 
        COUNT(id) AS tongDonHang, 
        SUM(tong_tien + chi_phi_giao_hang) AS tongDoanhThu
    FROM don_hang
    WHERE trang_thai_don_hang IN ('Hoàn thành', 'Đã thanh toán') 
    AND CAST(ngay_tao AS DATE) = CAST(GETDATE() AS DATE) -- Lọc theo ngày hiện tại
    GROUP BY CAST(ngay_tao AS DATE)
    ORDER BY ngay DESC
    """, nativeQuery = true)
    List<Object[]> thongKeDoanhThuVaDonHang();


    // Tổng doanh thu theo tháng
    @Query(value = """
    SELECT 
        YEAR(ngay_tao) AS nam, 
        MONTH(ngay_tao) AS thang, 
        COUNT(id) AS tongDonHang, 
        SUM(tong_tien + chi_phi_giao_hang) AS tongDoanhThu
    FROM don_hang
    WHERE MONTH(ngay_tao) = MONTH(GETDATE()) 
          AND YEAR(ngay_tao) = YEAR(GETDATE()) 
          AND trang_thai_don_hang IN ('Hoàn thành', 'Đã thanh toán') 
    GROUP BY YEAR(ngay_tao), MONTH(ngay_tao)
    ORDER BY nam DESC, thang DESC
    """, nativeQuery = true)
    List<Object[]> thongKeDoanhThuVaDonHangTrongThang();


    @Query(value = """
    SELECT 
        YEAR(ngay_tao) AS nam, 
        COUNT(id) AS tongDonHang, 
        SUM(tong_tien + chi_phi_giao_hang) AS tongDoanhThu
    FROM don_hang
    WHERE trang_thai_don_hang IN ('Hoàn thành', 'Đã thanh toán') 
    GROUP BY YEAR(ngay_tao)
    ORDER BY nam DESC
    """, nativeQuery = true)
    List<Object[]> thongKeDoanhThuVaDonHangTheoNam();

    // Thống kê sản phẩm bán chạy
    @Query(value = """
    SELECT TOP 5 sp.id, sp.ten_san_pham AS tenSanPham, SUM(dhct.so_luong) AS soLuongBan
    FROM don_hang_chi_tiet dhct
    JOIN san_pham_chi_tiet spct ON dhct.id_spct = spct.id
    JOIN san_pham sp ON spct.id_san_pham = sp.id
    GROUP BY sp.id, sp.ten_san_pham
    ORDER BY soLuongBan DESC
    """, nativeQuery = true)
    List<Object[]> thongKeSanPhamBanChay();

    @Query(value = """
        SELECT TOP 5 sp.id AS idSanPham, sp.ten_san_pham AS tenSanPham, SUM(dhct.so_luong) AS tongSoLuong
        FROM don_hang_chi_tiet dhct
        JOIN san_pham_chi_tiet spct ON dhct.id_spct = spct.id
        JOIN san_pham sp ON spct.id_san_pham = sp.id
        WHERE CAST(dhct.ngay_tao AS DATE) = CAST(GETDATE() AS DATE)
        GROUP BY sp.id, sp.ten_san_pham
        ORDER BY tongSoLuong DESC
    """, nativeQuery = true)
    Object[] findSanPhamBanChayNhatHomNay();

    @Query(value = """
    SELECT trang_thai_don_hang, COUNT(id) AS tong_don_hang
    FROM don_hang
    WHERE CAST(ngay_tao AS DATE) = CAST(GETDATE() AS DATE)
    GROUP BY trang_thai_don_hang
    ORDER BY trang_thai_don_hang
    """, nativeQuery = true)
    List<Object[]> thongKeTrangThaiDonHangTheoNgay();
    @Query(value = """
    SELECT trang_thai_don_hang, COUNT(id) AS tong_don_hang
    FROM don_hang
    WHERE YEAR(ngay_tao) = YEAR(GETDATE()) AND MONTH(ngay_tao) = MONTH(GETDATE())
    GROUP BY trang_thai_don_hang
    ORDER BY trang_thai_don_hang
    """, nativeQuery = true)
    List<Object[]> thongKeTrangThaiDonHangTheoThang();
    @Query(value = """
    SELECT trang_thai_don_hang, COUNT(id) AS tong_don_hang
    FROM don_hang
    WHERE YEAR(ngay_tao) = YEAR(GETDATE()) 
    GROUP BY trang_thai_don_hang
    ORDER BY trang_thai_don_hang
    """, nativeQuery = true)
    List<Object[]> thongKeTrangThaiDonHangTheoNam();

    @Query(value = "SELECT COALESCE(SUM(so_luong), 0) " +
            "FROM don_hang_chi_tiet " +
            "WHERE CAST(ngay_tao AS DATE) = CAST(GETDATE() AS DATE)",
            nativeQuery = true)
    int tongSanPhamBanDuocHomNay();
    //
    @Query(value = """
        SELECT sp.id, sp.ten_san_pham, spct.so_luong
        FROM san_pham_chi_tiet spct
        JOIN san_pham sp ON spct.id_san_pham = sp.id
        WHERE spct.so_luong <= :nguong
        ORDER BY spct.so_luong ASC
    """, nativeQuery = true)
    List<Object[]> findSanPhamSapHetHang(@Param("nguong") int nguong);
}
