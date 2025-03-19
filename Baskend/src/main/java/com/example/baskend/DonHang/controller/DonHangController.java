package com.example.baskend.DonHang.controller;

import com.example.baskend.DonHang.entity.DonHang;
import com.example.baskend.DonHang.repository.DonHangRepo;
import com.example.baskend.GioHang.entity.GioHang;
import com.example.baskend.GioHang.repository.GioHangRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SanPhamChiTietRepo;
import com.example.baskend.KhachHang.repository.KhachHangRepo;
import com.example.baskend.PhuongThucThanhToan.repository.PhuongThucThanhToanRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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



    // Lấy danh sách đơn hàng của một khách hàng
    // Lấy danh sách đơn hàng của một khách hàng
    //    @GetMapping("/khach-hang/{khachHangId}")
    //    public ResponseEntity<List<DonHang>> getDonHangByKhachHangId(@PathVariable Integer khachHangId) {
    //        List<DonHang> donHangList = donHangRepo.findByKhachHangId(khachHangId);  // Lấy danh sách đơn hàng của khách hàng
    //        if (donHangList.isEmpty()) {
    //            return ResponseEntity.noContent().build();  // Trả về HTTP 204 nếu không có đơn hàng
    //        }
    //        return ResponseEntity.ok(donHangList);  // Trả về danh sách đơn hàng
    //    }





    @PostMapping("/create")
    public ResponseEntity<String> createDonHang(@RequestBody DonHang donHang) {
        if (donHang.getNhanVien() == null) {
            return ResponseEntity.badRequest().body("⚠️ Cần có thông tin nhân viên!");
        }

        donHang.setNgayTao(LocalDateTime.now());
        donHang.setTrangThaiDonHang("Chưa thanh toán");

        // Nếu không có khách hàng, set NULL
        if (donHang.getKhachHang() == null) {
            donHang.setKhachHang(null);
        }

        donHangRepo.save(donHang);
        return ResponseEntity.ok("Đơn hàng đã được tạo thành công.");
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

        // Kiểm tra số tiền thanh toán
        if (amountPaid >= donHang.getTongTien()) {
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



}
