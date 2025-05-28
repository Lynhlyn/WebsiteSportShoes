package com.example.baskend.TaiKhoan.Repository;

import com.example.baskend.TaiKhoan.Entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepo extends JpaRepository<TaiKhoan, Integer> {

    // Kiểm tra tồn tại
    boolean existsByTenDangNhap(String tenDangNhap);

    // Tìm tài khoản theo tên đăng nhập (nên giữ cái này)
    TaiKhoan findByTenDangNhap(String tenDangNhap);

}
