package com.example.baskend.KhachHang.repository;

import com.example.baskend.KhachHang.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepo extends JpaRepository<KhachHang, Integer> {
    List<KhachHang> findByHoTenContainingIgnoreCaseOrSoDienThoaiContaining(String hoTen, String soDienThoai);
    boolean existsByEmail(String email);
    boolean existsByMaKhachHang(String maKhachHang);
    boolean existsBySoDienThoai(String soDienThoai);
    List<KhachHang> findByTrangThai(Boolean trangThai);

    Optional<KhachHang> findByTaiKhoan_Id(Integer taiKhoanId);

    Optional<KhachHang> findByTaiKhoan_TenDangNhap(String tenDangNhap);

    @Query("SELECT MAX(kh.maKhachHang) FROM KhachHang kh")
    String findMaxMaKhachHang();
  //  public Optional<KhachHang> findByMaKhachHang(String maKhachHang);

}
