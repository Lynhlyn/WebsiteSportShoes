package com.example.baskend.NhanVien.repository;

import com.example.baskend.NhanVien.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepo extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findByHoTen(String tenDangNhap);
}
