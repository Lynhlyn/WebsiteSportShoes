package com.example.baskend.TaiKhoan.Controller;

import com.example.baskend.TaiKhoan.Entity.TaiKhoan;
import com.example.baskend.TaiKhoan.Repository.TaiKhoanRepo;
import com.example.baskend.VaiTro.Entity.VaiTro;
import com.example.baskend.VaiTro.Repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tai-khoan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaiKhoanController {
    private final TaiKhoanRepo taiKhoanRepo;
    private final VaiTroRepository vaiTroRepo;

    @GetMapping("")
    public ResponseEntity<?> getAllTaiKhoan() {
        return ResponseEntity.ok(taiKhoanRepo.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> createTaiKhoan(@RequestBody TaiKhoan taiKhoan) {
        // Kiểm tra các trường bắt buộc
        if (taiKhoan.getTenDangNhap() == null || taiKhoan.getMatKhau() == null || taiKhoan.getVaiTro() == null) {
            return ResponseEntity.badRequest().body("Tên đăng nhập, mật khẩu và vai trò là bắt buộc!");
        }

        // Kiểm tra trùng lặp tài khoản
        if (taiKhoanRepo.existsByTenDangNhap(taiKhoan.getTenDangNhap())) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại!");
        }

        // Kiểm tra vai trò hợp lệ (chắc chắn vai trò có id hợp lệ)
        if (taiKhoan.getVaiTro().getId() == null || (taiKhoan.getVaiTro().getId() != 1 && taiKhoan.getVaiTro().getId() != 2)) {
            return ResponseEntity.badRequest().body("Vai trò không hợp lệ!");
        }

        // Lưu tài khoản vào cơ sở dữ liệu
        taiKhoan.setTrangThai(true);
        TaiKhoan createdTaiKhoan = taiKhoanRepo.save(taiKhoan);

        return ResponseEntity.ok(createdTaiKhoan);
    }
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = taiKhoanRepo.existsByTenDangNhap(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists); // Trả về true nếu tên đăng nhập tồn tại
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addKH")
    public ResponseEntity<?> createTaiKhoanKH(@RequestBody TaiKhoan taiKhoan) {
        // Tìm vai trò "Khách hàng" từ cơ sở dữ liệu
        VaiTro vaiTroKhachHang = vaiTroRepo.findByTenVaiTro("Khách hàng")
                .orElseThrow(() -> new RuntimeException("Vai trò 'Khách hàng' không tồn tại"));

        // Kiểm tra xem tài khoản đã có tên đăng nhập chưa
        if (taiKhoanRepo.existsByTenDangNhap(taiKhoan.getTenDangNhap())) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại");
        }

        taiKhoan.setVaiTro(vaiTroKhachHang);  // Gán vai trò "Khách hàng"
        taiKhoan.setTrangThai(true);  // Mặc định tài khoản là "Hoạt động"
        taiKhoan.setNgayTao(LocalDateTime.now());
        taiKhoan.setNgaySua(LocalDateTime.now());

        TaiKhoan savedTaiKhoan = taiKhoanRepo.save(taiKhoan);   // Lưu tài khoản vào cơ sở dữ liệu

        return ResponseEntity.ok(savedTaiKhoan);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaiKhoan(@PathVariable Integer id, @RequestBody TaiKhoan taiKhoanDetails) {
        // Kiểm tra xem tài khoản có tồn tại không
        Optional<TaiKhoan> existingTaiKhoanOpt = taiKhoanRepo.findById(id);
        if (existingTaiKhoanOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Tài khoản không tồn tại!");
        }

        TaiKhoan taiKhoan = existingTaiKhoanOpt.get();

        // Cập nhật thông tin tài khoản
        taiKhoan.setTenDangNhap(taiKhoanDetails.getTenDangNhap());
        taiKhoan.setMatKhau(taiKhoanDetails.getMatKhau());
        taiKhoan.setTrangThai(taiKhoanDetails.getTrangThai());

        // Cập nhật vai trò nếu có
        if (taiKhoanDetails.getVaiTro() != null) {
            VaiTro vaiTro = vaiTroRepo.findById(taiKhoanDetails.getVaiTro().getId())
                    .orElseThrow(() -> new RuntimeException("Vai trò không tồn tại"));
            taiKhoan.setVaiTro(vaiTro);
        }

        // Cập nhật thời gian sửa
        //taiKhoan.setNgaySua(new Date());

        // Lưu tài khoản đã cập nhật
        TaiKhoan updatedTaiKhoan = taiKhoanRepo.save(taiKhoan);

        return ResponseEntity.ok(updatedTaiKhoan);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaiKhoan> updateAccountStatus(@PathVariable Integer id, @RequestParam("trangThai") Boolean trangThai) {
        Optional<TaiKhoan> taiKhoanOptional = taiKhoanRepo.findById(id);  // Sử dụng TaiKhoanRepo để tìm tài khoản
        if (taiKhoanOptional.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOptional.get();
            taiKhoan.setTrangThai(trangThai);  // Cập nhật trạng thái tài khoản

            TaiKhoan updatedTaiKhoan = taiKhoanRepo.save(taiKhoan);  // Lưu lại tài khoản đã cập nhật
            return ResponseEntity.ok(updatedTaiKhoan);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Trả về 404 nếu không tìm thấy tài khoản
        }
    }


}
