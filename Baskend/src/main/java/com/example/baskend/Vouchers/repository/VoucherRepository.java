package com.example.baskend.Vouchers.repository;

import com.example.baskend.Vouchers.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher,Integer> {
    List<Voucher> findByTrangThai(Boolean trangThai);

    List<Voucher> findByKieuVoucher(String kieuVoucher);
    List<Voucher> findByNgayBatDauBetween(LocalDateTime start, LocalDateTime end);
    List<Voucher> findByNgayKetThucBefore(LocalDateTime now);
    @Query("SELECT v FROM Voucher v WHERE " +
            "(:maVoucher IS NULL OR LOWER(v.maVoucher) LIKE LOWER(CONCAT('%', :maVoucher, '%'))) OR " + // Tìm theo mã voucher
            "(:moTa IS NULL OR LOWER(v.moTa) LIKE LOWER(CONCAT('%', :moTa, '%')))") // Tìm theo mô tả voucher
    Page<Voucher> searchByMaAndMoTa(
            @Param("maVoucher") String maVoucher,
            @Param("moTa") String moTa,
            Pageable pageable);


    // Tìm kiếm theo các trường khác như kiểu voucher, trạng thái, ngày bắt đầu, ngày kết thúc
    @Query("SELECT v FROM Voucher v WHERE " +
            "(:tuNgay IS NULL OR v.ngayBatDau >= :tuNgay) AND " +
            "(:denNgay IS NULL OR v.ngayKetThuc <= :denNgay) AND " +
            "(:loaiVoucher IS NULL OR v.loaiVoucher = :loaiVoucher) AND " +
            "(:kieuVoucher IS NULL OR v.kieuVoucher = :kieuVoucher) AND " +
            "(:trangThai IS NULL OR v.trangThai = :trangThai)")
    Page<Voucher> searchOtherFields(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            @Param("loaiVoucher") Boolean loaiVoucher,
            @Param("kieuVoucher") String kieuVoucher,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable);

}
    // Lấy danh sách voucher đã kết thúc (đã hết hạn)

