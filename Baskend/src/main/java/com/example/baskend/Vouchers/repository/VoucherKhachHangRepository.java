package com.example.baskend.Vouchers.repository;


import com.example.baskend.Vouchers.entity.VoucherKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherKhachHangRepository extends JpaRepository<VoucherKhachHang,Integer> {
}
