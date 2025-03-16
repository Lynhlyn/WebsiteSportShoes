package com.example.baskend.Vouchers.repository;


import com.example.baskend.Vouchers.entity.KhachHangEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachHangVCRepository extends JpaRepository<KhachHangEntity,Integer> {
    @Query("SELECT k FROM KhachHangEntity k WHERE " +
            "(:hoTen IS NOT NULL AND LOWER(k.hoTen) LIKE LOWER(CONCAT('%', :hoTen, '%'))) OR " +
            "(:soDienThoai IS NOT NULL AND k.soDienThoai LIKE CONCAT('%', :soDienThoai, '%')) OR " +
            "(:email IS NOT NULL AND LOWER(k.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<KhachHangEntity> searchCustomers(
            @Param("hoTen") String hoTen,
            @Param("soDienThoai") String soDienThoai,
            @Param("email") String email,
            Pageable pageable);

}
