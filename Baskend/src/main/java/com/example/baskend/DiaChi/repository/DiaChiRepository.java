package com.example.baskend.DiaChi.repository;

import com.example.baskend.DiaChi.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi,Integer> {
    List<DiaChi> findByKhachHangId(Integer idKhachHang);

    Optional<DiaChi> findFirstByKhachHang_IdOrderByIdDesc(Integer khachHangId);
}
