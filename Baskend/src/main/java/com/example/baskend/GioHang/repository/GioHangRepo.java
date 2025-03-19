package com.example.baskend.GioHang.repository;

import com.example.baskend.GioHang.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GioHangRepo extends JpaRepository<GioHang, Integer> {
    List<GioHang> findByKhachHangId(Integer khachHangId);

}
