package com.example.baskend.DonHangChiTiet.response;

import com.example.baskend.DonHang.entity.DonHang;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonHangChiTietResponse {
    private Integer id;
    private DonHang donHang;  // Thông tin đơn hàng
    private List<SanPhamChiTietDonHangresponse> sanPhamChiTiet; // Danh sách sản phẩm chi tiết
    private Integer soLuong;
    private Float donGia;
    private String ngayTao;
    private String ngaySua;
    private Boolean trangThai;
}
