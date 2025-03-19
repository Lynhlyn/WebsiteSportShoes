package com.example.baskend.SanPham.QuanLySanPham.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamResponse {
    private Integer id;
    private String tenSanPham;
    private String moTa;
    private String danhMuc;
    private String thuongHieu;
    private String chatLieu;
    private String deGiay;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private boolean trangThai;
    private List<String> danhSachAnh; // Đổi tên trường để rõ ràng hơn
    private String anhDauTien;


    }


