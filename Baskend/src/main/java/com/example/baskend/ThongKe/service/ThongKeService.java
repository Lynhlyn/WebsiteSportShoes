package com.example.baskend.ThongKe.service;

import com.example.baskend.ThongKe.repository.ThongKeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThongKeService {

    @Autowired
    private ThongKeRepository thongKeRepository;

    // Chuyển Object[] thành Map<String, Object> để tránh lặp code
    private List<Map<String, Object>> convertToMap(List<Object[]> data, String... keys) {
        return data.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < keys.length; i++) {
                        if (row[i] instanceof Number) {
                            map.put(keys[i], ((Number) row[i]).doubleValue());
                        } else {
                            map.put(keys[i], row[i].toString());
                        }
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

    // Thống kê doanh thu và đơn hàng theo ngày, tháng, năm
    public List<Map<String, Object>> thongKeDoanhThuVaDonHang() {
        return convertToMap(thongKeRepository.thongKeDoanhThuVaDonHang(),
                "ngay", "tongDonHang", "tongDoanhThu");
    }

    // Thống kê doanh thu và đơn hàng của tháng hiện tại
    public List<Map<String, Object>> thongKeDoanhThuVaDonHangTrongThang() {
        return convertToMap(thongKeRepository.thongKeDoanhThuVaDonHangTrongThang(),
                "nam", "thang", "tongDonHang", "tongDoanhThu");
    }

    public List<Map<String, Object>> thongKeDoanhThuVaDonHangTheoNam() {
        return convertToMap(thongKeRepository.thongKeDoanhThuVaDonHangTheoNam(),
                "nam", "tongDonHang", "tongDoanhThu");
    }


    public List<Object[]> getSanPhamBanChay() {
        return thongKeRepository.thongKeSanPhamBanChay();
    }
    // Thống kê sản phẩm bán chạy theo ngày
    public List<Map<String, Object>> getSanPhamBanChayHomNay() {
        List<Object[]> results = thongKeRepository.thongKeSanPhamBanChay();
        return convertToMap(results, "idSanPham", "tenSanPham", "tongSoLuong");
    }

    public List<Map<String, Object>> layThongKeTrangThaiDonHangTheoNgay() {
        List<Object[]> results = thongKeRepository.thongKeTrangThaiDonHangTheoNgay();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("trangThai", row[0]);
            map.put("tongDonHang", row[1]);
            response.add(map);
        }

        return response;
    }
    public List<Map<String, Object>> layThongKeTrangThaiDonHangTheoThang() {
        List<Object[]> results = thongKeRepository.thongKeTrangThaiDonHangTheoThang();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("trangThai", row[0]);
            map.put("tongDonHang", row[1]);
            response.add(map);
        }

        return response;
    }
    public List<Map<String, Object>> thongKeTrangThaiDonHangTheoNam() {
        return convertToMap(thongKeRepository.thongKeTrangThaiDonHangTheoNam(),
                "trangThaiDonHang", "tongDonHang");
    }
    public Map<String, Object> getSanPhamBanChayNhatHomNay() {
        Object[] result = thongKeRepository.findSanPhamBanChayNhatHomNay();
        if (result == null) {
            return null; // Không có sản phẩm bán chạy trong ngày
        }

        Map<String, Object> sanPham = new HashMap<>();
        sanPham.put("idSanPham", result[0]);
        sanPham.put("tenSanPham", result[1]);
        sanPham.put("tongSoLuong", result[2]);

        return sanPham;
    }

}
