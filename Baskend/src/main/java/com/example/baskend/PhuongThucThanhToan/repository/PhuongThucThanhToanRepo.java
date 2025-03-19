package com.example.baskend.PhuongThucThanhToan.repository;

import com.example.baskend.PhuongThucThanhToan.entity.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhuongThucThanhToanRepo extends JpaRepository<PhuongThucThanhToan,Integer> {
}
