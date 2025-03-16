package com.example.baskend.KhachHang.repository;

import com.example.baskend.KhachHang.entity.KhachHang;
import com.example.baskend.SanPham.KhuyenMai.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KhachHangRepo extends JpaRepository<KhachHang, Integer> {
    List<KhachHang> findByHoTenContainingIgnoreCaseOrSoDienThoaiContaining(String hoTen, String soDienThoai);
    boolean existsByEmail(String email);
    boolean existsByMaKhachHang(String maKhachHang);
    boolean existsBySoDienThoai(String soDienThoai);
    List<KhachHang> findByTrangThai(Boolean trangThai);

}
