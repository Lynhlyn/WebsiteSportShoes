package com.example.baskend.SanPham.SanPhamChiTiet.repository;

import com.example.baskend.SanPham.SanPhamChiTiet.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepo extends JpaRepository<Size, Integer> {
}
