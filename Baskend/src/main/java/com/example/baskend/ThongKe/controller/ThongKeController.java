package com.example.baskend.ThongKe.controller;


import com.example.baskend.ThongKe.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/thong-ke")
@CrossOrigin(origins = "*")
public class ThongKeController {
    @Autowired
    private ThongKeService thongKeService;

    // Thống kê doanh thu và đơn hàng theo ngày
    @GetMapping("/doanh-thu/don-hang/ngay")
    public ResponseEntity<List<Map<String, Object>>> getDoanhThuVaDonHangTheoNgay() {
        return ResponseEntity.ok(thongKeService.thongKeDoanhThuVaDonHang());
    }

    // Thống kê doanh thu và đơn hàng theo tháng (có thể lọc theo năm)
    @GetMapping("/doanh-thu/don-hang/thang-nay")
    public ResponseEntity<List<Map<String, Object>>> getDoanhThuVaDonHangTrongThang() {
        return ResponseEntity.ok(thongKeService.thongKeDoanhThuVaDonHangTrongThang());
    }

    @GetMapping("/doanh-thu/don-hang/nam")
    public ResponseEntity<List<Map<String, Object>>> getDoanhThuVaDonHangTheoNam() {
        return ResponseEntity.ok(thongKeService.thongKeDoanhThuVaDonHangTheoNam());
    }

    @GetMapping("/san-pham-ban-chay")
    public List<Object[]> getSanPhamBanChay() {
        return thongKeService.getSanPhamBanChay();
    }
    //
    @GetMapping("/san-pham-ban-chay/ngay")
    public ResponseEntity<List<Map<String, Object>>> getSanPhamBanChayHomNay() {
        return ResponseEntity.ok(thongKeService.getSanPhamBanChayHomNay());
    }
    //
    @GetMapping("/san-pham-ban-chay/thang")
    public ResponseEntity<List<Map<String, Object>>> getSanPhamBanChayTrongThang() {
        return ResponseEntity.ok(thongKeService.getSanPhamBanChayTrongThang());
    }
    //
    @GetMapping("/san-pham-ban-chay/nam")
    public ResponseEntity<List<Map<String, Object>>> getSanPhamBanChayTheoNam() {
        return ResponseEntity.ok(thongKeService.getSanPhamBanChayTheoNam());
    }



    @GetMapping("/trang-thai-don-hang/ngay")
    public ResponseEntity<List<Map<String, Object>>> thongKeTrangThaiDonHangTheoNgay() {
        return ResponseEntity.ok(thongKeService.layThongKeTrangThaiDonHangTheoNgay());
    }
    @GetMapping("/trang-thai-don-hang/thang")
    public ResponseEntity<List<Map<String, Object>>> thongKeTrangThaiDonHangTheoThang() {
        return ResponseEntity.ok(thongKeService.layThongKeTrangThaiDonHangTheoThang());
    }
    @GetMapping("/trang-thai-don-hang/nam")
    public ResponseEntity<List<Map<String, Object>>> getTrangThaiDonHangTheoNam() {
        return ResponseEntity.ok(thongKeService.thongKeTrangThaiDonHangTheoNam());
    }
    @GetMapping("/san-pham-sap-het-hang")
    public ResponseEntity<List<Map<String, Object>>> getSanPhamSapHetHang() {
        return ResponseEntity.ok(thongKeService.getSanPhamSapHetHang());
    }
}
