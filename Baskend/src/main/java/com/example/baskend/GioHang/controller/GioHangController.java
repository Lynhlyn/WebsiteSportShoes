package com.example.baskend.GioHang.controller;

import com.example.baskend.GioHang.entity.GioHang;
import com.example.baskend.GioHang.repository.GioHangRepo;
import com.example.baskend.KhachHang.entity.KhachHang;
import com.example.baskend.KhachHang.repository.KhachHangRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SanPhamChiTietRepo;
import com.example.baskend.Vouchers.entity.Voucher;
import com.example.baskend.Vouchers.repository.VoucherRepository;
import com.example.baskend.Vouchers.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gio-hang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GioHangController {

    private final GioHangRepo gioHangRepo;

    private final VoucherRepository voucherRepository;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final KhachHangRepo khachHangRepo;

    // Lấy giỏ hàng của khách hàng
    @GetMapping("/{khachHangId}")
    public ResponseEntity<List<GioHang>> getGioHangByKhachHangId(@PathVariable Integer khachHangId) {
        List<GioHang> cartItems = gioHangRepo.findByKhachHangId(khachHangId);
        return ResponseEntity.ok(cartItems);
    }
    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestParam Integer khachHangId, @RequestParam Integer sanPhamChiTietId, @RequestParam Integer soLuong) {
        KhachHang khachHang = khachHangRepo.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.findById(sanPhamChiTietId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        GioHang gioHang = new GioHang();
        gioHang.setKhachHang(khachHang);
        gioHang.setSanPhamChiTiet(sanPhamChiTiet);
        gioHang.setSoLuong(soLuong);

        gioHangRepo.save(gioHang);
        return ResponseEntity.ok("Sản phẩm đã được thêm vào giỏ hàng");
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeFromCart(@PathVariable Integer id) {
        if (gioHangRepo.existsById(id)) {
            gioHangRepo.deleteById(id);
            return ResponseEntity.ok("Sản phẩm đã được xoá khỏi giỏ hàng");
        }
        return ResponseEntity.badRequest().body("Sản phẩm không tồn tại trong giỏ hàng");
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/update")
    public ResponseEntity<String> updateQuantity(@RequestParam Integer id, @RequestParam Integer soLuong) {
        GioHang gioHang = gioHangRepo.findById(id).orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
        gioHang.setSoLuong(soLuong);
        gioHangRepo.save(gioHang);
        return ResponseEntity.ok("Số lượng sản phẩm đã được cập nhật");
    }

    @PostMapping("/apply-voucher")
    public ResponseEntity<?> applyVoucher(@RequestParam Integer khachHangId, @RequestParam String maVoucher) {
        KhachHang khachHang = khachHangRepo.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        Voucher voucher = voucherRepository.findByMaVoucher(maVoucher)
                .orElseThrow(() -> new RuntimeException("Voucher không hợp lệ hoặc không tồn tại"));

        List<GioHang> gioHangList = gioHangRepo.findByKhachHangId(khachHangId);
        if (gioHangList.isEmpty()) {
            return ResponseEntity.badRequest().body("Giỏ hàng trống, không thể áp dụng voucher");
        }

        double tongTien = gioHangList.stream()
                .mapToDouble(item -> item.getSanPhamChiTiet().getGiaBan() * item.getSoLuong())
                .sum();

        if (tongTien < voucher.getGiaTriToiThieu()) {
            return ResponseEntity.badRequest().body("Tổng giá trị giỏ hàng chưa đạt mức tối thiểu để áp dụng voucher");
        }

        double giamGia;
        if (voucher.getLoaiVoucher() == 1) { // Giảm theo phần trăm
            giamGia = Math.min(voucher.getGiaTriToiDa(), voucher.getGiaTriToiThieu() * voucher.getGiaTriGiam() / 100);
        } else { // Giảm theo số tiền
            giamGia = Math.min(voucher.getGiaTriToiDa(), voucher.getGiaTriGiam());
        }

        double tongTienSauGiam = tongTien - giamGia;

        return ResponseEntity.ok("Tổng tiền trước giảm: " + tongTien + ", Giảm giá: " + giamGia + ", Tổng tiền sau giảm: " + tongTienSauGiam);
    }
}
