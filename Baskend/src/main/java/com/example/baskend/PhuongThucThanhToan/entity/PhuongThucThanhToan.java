package com.example.baskend.PhuongThucThanhToan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phuong_thuc_thanh_toan")
public class PhuongThucThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_phuong_thuc", nullable = false)
    private String tenPhuongThuc;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @PrePersist
    protected void onCreate() {
        this.ngayThanhToan = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngayThanhToan = LocalDateTime.now();
    }
}
