package com.example.baskend.DiaChi.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiaChiResponse {
    private String tenNguoiNhan;
    private String soDienThoai;
    private String diaChiDayDu; // ghép: chiTiet + phường + quận + TP
}
