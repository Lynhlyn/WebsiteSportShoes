package com.example.baskend.SanPham.QuanLySanPham.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnhSanPhamResponse {
    private Integer id;
    private String anhSP;
    private String tenSanPham;
    private Double giaBan;
}
