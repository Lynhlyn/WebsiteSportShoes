package com.example.baskend.DonHangChiTiet.repository;

import com.example.baskend.DonHang.entity.DonHang;
import com.example.baskend.DonHangChiTiet.entity.DonHangChiTiet;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonHangChiTietRepo extends JpaRepository<DonHangChiTiet, Integer> {
    // Bạn có thể thêm các phương thức tìm kiếm theo yêu cầu nếu cần


    Optional<DonHangChiTiet> findById( Integer id );

    // Query để lấy tất cả chi tiết đơn hàng của một đơn hàng
    @Query("SELECT dhc FROM DonHangChiTiet dhc JOIN FETCH dhc.sanPhamChiTiet WHERE dhc.donHang.id = :donHangId")
    List<DonHangChiTiet> findByDonHangId(@Param("donHangId") Integer donHangId);

    @Query("SELECT dhc FROM DonHangChiTiet dhc WHERE dhc.donHang = :donHang AND dhc.sanPhamChiTiet = :sanPhamChiTiet")
    Optional<DonHangChiTiet> findByDonHangAndSanPhamChiTiet(@Param("donHang") DonHang donHang, @Param("sanPhamChiTiet") SanPhamChiTiet sanPhamChiTiet);

}
