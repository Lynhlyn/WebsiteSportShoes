package com.example.baskend.SanPham.QuanLySanPham.repository;

import com.example.baskend.SanPham.QuanLySanPham.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucRepo extends JpaRepository<DanhMuc, Integer> {
}
