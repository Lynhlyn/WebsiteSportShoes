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
        CAST(dh.ngay_tao AS DATE) AS ngay,
        COUNT(DISTINCT dh.id) AS tongDonHang,
       
        -- Doanh thu: tổng tiền + phí giao hàng, chỉ tính 1 lần duy nhất cho mỗi đơn hàng
        SUM(DISTINCT (dh.tong_tien + dh.chi_phi_giao_hang)) AS tongDoanhThu,
    
        -- Tổng số lượng bán từ bảng chi tiết
        SUM(dhct.so_luong) AS tongSoLuongBan
    
    FROM don_hang dh
    JOIN don_hang_chi_tiet dhct ON dh.id = dhct.id_don_hang
    JOIN san_pham_chi_tiet spct ON dhct.id_spct = spct.id
    
    WHERE dh.trang_thai_don_hang IN (N'Hoàn thành', N'Đã thanh toán')
      AND CAST(dh.ngay_tao AS DATE) = CAST(GETDATE() AS DATE)
    
    GROUP BY CAST(dh.ngay_tao AS DATE)
    ORDER BY ngay DESC;
    
    
    """, nativeQuery = true)
    List<Object[]> thongKeDoanhThuVaDonHang();


    // Tổng doanh thu theo tháng
    @Query(value = """
  SELECT \s
      YEAR(dh.ngay_tao) AS nam,
      MONTH(dh.ngay_tao) AS thang,
  
      COUNT(DISTINCT dh.id) AS tongDonHang,
  
      -- Doanh thu chỉ tính mỗi đơn 1 lần
      SUM(DISTINCT (dh.tong_tien + dh.chi_phi_giao_hang)) AS tongDoanhThu,
  
      -- Tổng số lượng bán là tổng từ chi tiết đơn hàng
      SUM(dhct.so_luong) AS tongSoLuongBan
  
  FROM don_hang dh
  JOIN don_hang_chi_tiet dhct ON dh.id = dhct.id_don_hang
  
  WHERE MONTH(dh.ngay_tao) = MONTH(GETDATE())
    AND YEAR(dh.ngay_tao) = YEAR(GETDATE())
    AND dh.trang_thai_don_hang IN (N'Hoàn thành', N'Đã thanh toán')
  
  GROUP BY YEAR(dh.ngay_tao), MONTH(dh.ngay_tao)
  ORDER BY nam DESC, thang DESC;
    """, nativeQuery = true)
    List<Object[]> thongKeDoanhThuVaDonHangTrongThang();


    @Query(value = """
     SELECT
         YEAR(dh.ngay_tao) AS nam,
         COUNT(DISTINCT dh.id) AS tongDonHang,
         SUM(DISTINCT (dh.tong_tien + dh.chi_phi_giao_hang)) AS tongDoanhThu,
         SUM(dhct.so_luong) AS tongSoLuongBan
     FROM don_hang dh
     JOIN don_hang_chi_tiet dhct ON dh.id = dhct.id_don_hang
     WHERE dh.trang_thai_don_hang IN (N'Hoàn thành', N'Đã thanh toán')
       AND YEAR(dh.ngay_tao) = YEAR(GETDATE())
     GROUP BY YEAR(dh.ngay_tao)
     ORDER BY nam DESC;
     
    
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
    //SP bán chạy theo ngày
    @Query(value = """
   SELECT TOP 5
       sp.id,
       sp.ten_san_pham AS tenSanPham,
       SUM(dhct.so_luong) AS soLuongBan,
       sz.ten_size AS size,
       ms.ten_mau AS mauSac,
       dg.ten_de_giay AS deGiay,
       th.ten_thuong_hieu AS thuongHieu
   FROM don_hang_chi_tiet dhct
   JOIN san_pham_chi_tiet spct ON dhct.id_spct = spct.id
   JOIN san_pham sp ON spct.id_san_pham = sp.id
   JOIN size sz ON spct.id_size = sz.id
   JOIN mau_sac ms ON spct.id_mau_sac = ms.id
   JOIN de_giay dg ON sp.id_de_giay = dg.id
   JOIN thuong_hieu th ON sp.id_thuong_hieu = th.id
   JOIN don_hang dh ON dhct.id_don_hang = dh.id
   WHERE CONVERT(DATE, dh.ngay_tao) = CONVERT(DATE, GETDATE()) 
    AND dh.trang_thai_don_hang IN (N'Hoàn thành', N'Đã thanh toán') -- Lọc theo ngày hiện tại
   GROUP BY sp.id, sp.ten_san_pham, sz.ten_size, ms.ten_mau, dg.ten_de_giay, th.ten_thuong_hieu
   ORDER BY soLuongBan DESC;
    """, nativeQuery = true)
    List<Object[]> thongKeSanPhamBanTheoNgay();
    //SP bán chay theo thang
    @Query(value = """
      SELECT TOP 5
            sp.id,
            sp.ten_san_pham AS tenSanPham,
            SUM(dhct.so_luong) AS soLuongBan,
            sz.ten_size AS size,
            ms.ten_mau AS mauSac,
            th.ten_thuong_hieu AS thuongHieu,
            ds.ten_de_giay AS deGiay
        FROM don_hang_chi_tiet dhct
        JOIN san_pham_chi_tiet spct ON dhct.id_spct = spct.id
        JOIN san_pham sp ON spct.id_san_pham = sp.id
        JOIN don_hang dh ON dhct.id_don_hang = dh.id
         JOIN size sz ON spct.id_size = sz.id
        JOIN mau_sac ms ON spct.id_mau_sac = ms.id  -- sửa tên bảng nếu cần
       JOIN thuong_hieu th ON sp.id_thuong_hieu = th.id
       JOIN de_giay ds ON sp.id_de_giay = ds.id
        WHERE MONTH(dh.ngay_tao) = MONTH(GETDATE())
          AND YEAR(dh.ngay_tao) = YEAR(GETDATE())
            AND dh.trang_thai_don_hang IN (N'Hoàn thành', N'Đã thanh toán') 
        GROUP BY sp.id, sp.ten_san_pham, sz.ten_size, ms.ten_mau, th.ten_thuong_hieu, ds.ten_de_giay
        ORDER BY soLuongBan DESC
    """, nativeQuery = true)
    List<Object[]> thongKeSanPhamBanChayTheoThang();
    //  SP bán chạy theo năm
    @Query(value = """
     SELECT TOP 5
            sp.id,
            sp.ten_san_pham AS tenSanPham,
            SUM(dhct.so_luong) AS soLuongBan,
            sz.ten_size AS size,
            ms.ten_mau AS mauSac,
            th.ten_thuong_hieu AS thuongHieu,
            ds.ten_de_giay AS deGiay
        FROM don_hang_chi_tiet dhct
        JOIN san_pham_chi_tiet spct ON dhct.id_spct = spct.id
        JOIN san_pham sp ON spct.id_san_pham = sp.id
        JOIN don_hang dh ON dhct.id_don_hang = dh.id
         JOIN size sz ON spct.id_size = sz.id
       JOIN mau_sac ms ON spct.id_mau_sac = ms.id  -- sửa tên bảng nếu cần
        JOIN thuong_hieu th ON sp.id_thuong_hieu = th.id
        JOIN de_giay ds ON sp.id_de_giay = ds.id
        WHERE YEAR(dh.ngay_tao) = YEAR(GETDATE())
          AND dh.trang_thai_don_hang IN (N'Hoàn thành', N'Đã thanh toán') 
        GROUP BY sp.id, sp.ten_san_pham, sz.ten_size, ms.ten_mau, th.ten_thuong_hieu, ds.ten_de_giay
        ORDER BY soLuongBan DESC
    """, nativeQuery = true)
    List<Object[]> thongKeSanPhamBanChayTheoNam();
    //


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


    //
    @Query(value = """
    SELECT
        sp.id AS idSanPham,
        sp.ten_san_pham AS tenSanPham,
        spct.so_luong AS soLuong,
        sz.ten_size AS size,
        ms.ten_mau AS mauSac,
        dg.ten_de_giay AS deGiay,
        th.ten_thuong_hieu AS thuongHieu
    FROM san_pham_chi_tiet spct
    JOIN san_pham sp ON spct.id_san_pham = sp.id
    JOIN size sz ON spct.id_size = sz.id
    JOIN mau_sac ms ON spct.id_mau_sac = ms.id
    JOIN de_giay dg ON sp.id_de_giay = dg.id
    JOIN thuong_hieu th ON sp.id_thuong_hieu = th.id
    WHERE spct.so_luong < 100
    ORDER BY spct.so_luong ASC;
""", nativeQuery = true)
    List<Object[]> thongKeSanPhamSapHetHang();
}
