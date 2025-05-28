package com.example.baskend.SanPham.QuanLySanPham.repository;

import com.example.baskend.SanPham.QuanLySanPham.entity.SanPhamThuocTinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamThuocTinhRepo extends JpaRepository<SanPhamThuocTinh, Integer> {

    // Lấy danh sách thuộc tính theo ID sản phẩm
    List<SanPhamThuocTinh> findBySanPhamId(Integer sanPhamId);

}
