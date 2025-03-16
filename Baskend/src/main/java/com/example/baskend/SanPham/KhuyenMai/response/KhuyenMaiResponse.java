package com.example.baskend.SanPham.KhuyenMai.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class KhuyenMaiResponse {  // Đổi tên lớp thành KhuyenMaiResponse để đúng với bảng khuyến mãi
    private Integer id;
    private String maKhuyenMai; // Mã khuyến mãi
    private String tenKhuyenMai; // Tên khuyến mãi
    private LocalDateTime ngayBatDau; // Ngày bắt đầu
    private LocalDateTime ngayKetThuc; // Ngày kết thúc
    private Float phanTramGiamGia; // Phần trăm giảm giá
    private LocalDateTime ngayTao; // Ngày tạo
    private LocalDateTime ngaySua; // Ngày sửa
    private boolean trangThai; // Trạng thái (1: Đang diễn ra, 0: Đã kết thúc)

}
