package com.example.baskend.SanPham.QuanLySanPham.repository;

import com.example.baskend.SanPham.QuanLySanPham.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThuongHieuRepo extends JpaRepository<ThuongHieu, Integer> {
}
