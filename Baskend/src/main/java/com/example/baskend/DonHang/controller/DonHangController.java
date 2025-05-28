package com.example.baskend.DonHang.controller;

import com.example.baskend.DonHang.entity.DonHang;
import com.example.baskend.DonHang.repository.DonHangRepo;
import com.example.baskend.GioHang.entity.GioHang;
import com.example.baskend.GioHang.repository.GioHangRepo;
import com.example.baskend.KhachHang.entity.KhachHang;
import com.example.baskend.KhachHang.repository.KhachHangRepo;
import com.example.baskend.PhuongThucThanhToan.entity.PhuongThucThanhToan;
import com.example.baskend.PhuongThucThanhToan.repository.PhuongThucThanhToanRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SanPhamChiTietRepo;
import com.example.baskend.Vouchers.entity.Voucher;
import com.example.baskend.Vouchers.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/don-hang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DonHangController {

    private final DonHangRepo donHangRepo;
    private final GioHangRepo gioHangRepo;  // Repository giỏ hàng
    private final SanPhamChiTietRepo sanPhamChiTietRepo;  // Repository sản phẩm chi tiết
    private final KhachHangRepo khachHangRepo;
    private final PhuongThucThanhToanRepo phuongThucThanhToanRepo;
    private final VoucherRepository voucherRepo;
    // Lấy thông tin đơn hàng theo id
    // Lấy thông tin đơn hàng theo id
    @GetMapping("")
    public ResponseEntity<?> getAllDonHang() {
        try {
            List<DonHang> donHangList = donHangRepo.findAll(); // Lấy tất cả đơn hàng
            if (donHangList.isEmpty()) {
                return ResponseEntity.noContent().build();  // Trả về HTTP 204 nếu không có đơn hàng
            }
            return ResponseEntity.ok(donHangList);  // Trả về danh sách đơn hàng dưới dạng JSON
        } catch (Exception e) {
            // Log lỗi chi tiết
            e.printStackTrace();
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi truy vấn dữ liệu đơn hàng: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonHang> getDonHangById(@PathVariable Integer id) {
        Optional<DonHang> donHangOpt = donHangRepo.findById(id);  // Tìm đơn hàng theo ID
        if (donHangOpt.isEmpty()) {
            return ResponseEntity.notFound().build();  // Trả về 404 nếu không tìm thấy đơn hàng
        }
        return ResponseEntity.ok(donHangOpt.get());  // Trả về đơn hàng nếu tìm thấy
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDonHang(@RequestBody DonHang donHang) {
        if (donHang.getNhanVien() == null) {
            return ResponseEntity.badRequest().body("⚠️ Cần có thông tin nhân viên!");
        }

        donHang.setNgayTao(LocalDateTime.now());
        donHang.setTrangThaiDonHang("Chờ thanh toán");
        donHang.setLoaiDonHang(false);

        // Nếu không có khách hàng, set NULL
        if (donHang.getKhachHang() == null) {
            donHang.setKhachHang(null);
        }

        // Lưu đơn hàng vào cơ sở dữ liệu
        donHangRepo.save(donHang);
        return ResponseEntity.ok("Đơn hàng đã được tạo thành công.");
    }



    @PostMapping("/create-online")
    public ResponseEntity<String> createDonHangOnline(@RequestBody DonHang donHang) {
        try {
            // Kiểm tra sự tồn tại của khách hàng và tài khoản
            if (donHang.getKhachHang() == null ||
                    donHang.getKhachHang().getId() == null ||
                    donHang.getKhachHang().getTaiKhoan() == null ||
                    donHang.getKhachHang().getTaiKhoan().getTenDangNhap() == null) {
                return ResponseEntity.badRequest().body("⚠️ Cần có thông tin tài khoản đăng nhập!");
            }

            // Kiểm tra sự tồn tại của khách hàng trong cơ sở dữ liệu
            Optional<KhachHang> khachHangOpt = khachHangRepo.findById(donHang.getKhachHang().getId());
            if (khachHangOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("⚠️ Khách hàng không tồn tại trong cơ sở dữ liệu!");
            }

            // Kiểm tra sự tồn tại của phương thức thanh toán
            if (donHang.getPhuongThucThanhToan() == null || donHang.getPhuongThucThanhToan().getId() == null) {
                return ResponseEntity.badRequest().body("⚠️ Cần có phương thức thanh toán hợp lệ!");
            }

            // Kiểm tra giá trị của tong_tien trước khi lưu
            if (donHang.getTongTien() == null || donHang.getTongTien().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("⚠️ Tổng tiền không hợp lệ!");
            }

            donHang.setTrangThaiDonHang("Chờ xác nhận");

            // Lưu đơn hàng vào cơ sở dữ liệu
            donHang.setNgayTao(LocalDateTime.now());
            donHang.setLoaiDonHang(true);  // Đặt loại đơn hàng là Online

            donHangRepo.save(donHang);  // Lưu đơn hàng

            return ResponseEntity.ok("Đơn hàng online đã được tạo thành công.");
        } catch (Exception e) {
            e.printStackTrace();  // hoặc dùng logger để ghi lỗi chi tiết
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<DonHang>> getPendingInvoices() {
        List<DonHang> donHangs = (List<DonHang>) donHangRepo.findByTrangThaiDonHang("Chờ thêm sản phẩm");
        return ResponseEntity.ok(donHangs);
    }

    @PostMapping("/{id}/add-product")
    public ResponseEntity<?> addProductToInvoice(@PathVariable Integer id, @RequestBody GioHang gioHang) {
        // Tìm hóa đơn
        Optional<DonHang> donHangOpt = donHangRepo.findById(id);
        if (donHangOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Hóa đơn không tồn tại.");
        }

        // Kiểm tra sản phẩm chi tiết
        Optional<SanPhamChiTiet> sanPhamOpt = sanPhamChiTietRepo.findById(gioHang.getSanPhamChiTiet().getId());
        if (sanPhamOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Sản phẩm không tồn tại.");
        }

        // Tạo giỏ hàng mới
        GioHang newCartItem = new GioHang();

        newCartItem.setSanPhamChiTiet(sanPhamOpt.get());
        newCartItem.setSoLuong(gioHang.getSoLuong());

        // Lưu vào database
        gioHangRepo.save(newCartItem);

        return ResponseEntity.ok("Sản phẩm đã thêm vào hóa đơn.");
    }

    // Cập nhật trạng thái thanh toán khi khách thanh toán
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payInvoice(@PathVariable Integer id, @RequestParam Double amountPaid) {
        Optional<DonHang> donHangOpt = donHangRepo.findById(id);
        if (donHangOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Hóa đơn không tồn tại.");
        }

        DonHang donHang = donHangOpt.get();
        BigDecimal paid = BigDecimal.valueOf(amountPaid);
        BigDecimal tongTien = donHang.getTongTien();

        // Kiểm tra số tiền thanh toán
        if (paid.compareTo(tongTien) >= 0) {
            donHang.setTrangThaiDonHang("Đã thanh toán");
            donHangRepo.save(donHang);
            return ResponseEntity.ok("Hóa đơn đã thanh toán thành công.");
        } else {
            return ResponseEntity.badRequest().body("Số tiền không đủ.");
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDonHang(@PathVariable Integer id) {
        Optional<DonHang> donHangOpt = donHangRepo.findById(id);
        if (donHangOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ Hóa đơn không tồn tại.");
        }

        donHangRepo.deleteById(id);
        return ResponseEntity.ok("✅ Hóa đơn đã được xóa thành công.");
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonHang(@PathVariable Integer id, @RequestBody DonHang updatedDonHang) {
        Optional<DonHang> donHangOpt = donHangRepo.findById(id);
        if (donHangOpt.isEmpty()) {
            return ResponseEntity.notFound().build();  // Trả về HTTP 404 nếu không tìm thấy đơn hàng
        }

        DonHang donHang = donHangOpt.get();
        // Cập nhật phương thức thanh toán nếu có
        if (updatedDonHang.getPhuongThucThanhToan() != null) {
            Optional<PhuongThucThanhToan> paymentMethodOpt = phuongThucThanhToanRepo.findById(updatedDonHang.getPhuongThucThanhToan().getId());
            if (paymentMethodOpt.isPresent()) {
                donHang.setPhuongThucThanhToan(paymentMethodOpt.get());  // Cập nhật phương thức thanh toán vào đơn hàng
            } else {
                return ResponseEntity.badRequest().body("Phương thức thanh toán không hợp lệ.");
            }
        }
        if (updatedDonHang.getKhachHang() != null && updatedDonHang.getKhachHang().getId() != null) {
            // Kiểm tra xem khách hàng có tồn tại trong cơ sở dữ liệu hay không
            Optional<KhachHang> khachHangOpt = khachHangRepo.findById(updatedDonHang.getKhachHang().getId());
            if (khachHangOpt.isPresent()) {
                donHang.setKhachHang(khachHangOpt.get());  // Cập nhật khách hàng nếu có
            } else {
                return ResponseEntity.badRequest().body("Khách hàng không tồn tại.");
            }
        }

        // Cập nhật voucher nếu có
        if (updatedDonHang.getVoucher() != null && updatedDonHang.getVoucher().getId() != null) {
            // Kiểm tra xem voucher có tồn tại trong cơ sở dữ liệu không
            Optional<Voucher> voucherOpt = voucherRepo.findById(updatedDonHang.getVoucher().getId());
            if (voucherOpt.isPresent()) {
                donHang.setVoucher(voucherOpt.get());  // Cập nhật voucher vào đơn hàng
            } else {
                return ResponseEntity.badRequest().body("Voucher không tồn tại.");
            }
        }

        // Cập nhật các thông tin cần thiết trong đơn hàng
        if (updatedDonHang.getTrangThaiDonHang() != null) {
            donHang.setTrangThaiDonHang(updatedDonHang.getTrangThaiDonHang());  // Cập nhật trạng thái đơn hàng
        }
        if (updatedDonHang.getTongTien() != null) {
            donHang.setTongTien(updatedDonHang.getTongTien());  // Cập nhật tổng tiền nếu có thay đổi
        }

        // Lưu đơn hàng đã được cập nhật
        donHangRepo.save(donHang);
        return ResponseEntity.ok("Cập nhật đơn hàng thành công!");
    }

    // Cập nhật trạng thái đơn hàng thành "Đã thanh toán"
    @PutMapping("/{id}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable Integer id) {
        Optional<DonHang> donHangOpt = donHangRepo.findById(id);
        if (donHangOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ Hóa đơn không tồn tại.");
        }

        DonHang donHang = donHangOpt.get();

        // Kiểm tra trạng thái đơn hàng trước khi xác nhận
        if (donHang.getTrangThaiDonHang().equals("Đã thanh toán")) {
            return ResponseEntity.status(400).body("⚠️ Hóa đơn đã được thanh toán.");
        }

        // Cập nhật trạng thái đơn hàng thành "Đã thanh toán"
        donHang.setTrangThaiDonHang("Đã thanh toán");
        donHangRepo.save(donHang);
        return ResponseEntity.ok("✅ Hóa đơn đã được xác nhận.");
    }
    // Cập nhật trạng thái đơn hàng thành "Đã hủy"
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Integer id) {
        Optional<DonHang> donHangOpt = donHangRepo.findById(id);
        if (donHangOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ Hóa đơn không tồn tại.");
        }

        DonHang donHang = donHangOpt.get();

        // Kiểm tra trạng thái đơn hàng trước khi hủy
        if (donHang.getTrangThaiDonHang().equals("Đã hủy")) {
            return ResponseEntity.status(400).body("⚠️ Hóa đơn đã bị hủy.");
        }

        // Cập nhật trạng thái đơn hàng thành "Đã hủy"
        donHang.setTrangThaiDonHang("Đã hủy");
        donHangRepo.save(donHang);
        return ResponseEntity.ok("✅ Hóa đơn đã được hủy.");
    }


}
