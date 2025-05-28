package com.example.baskend.KhachHang.controller;

import com.example.baskend.KhachHang.entity.KhachHang;
import com.example.baskend.KhachHang.repository.KhachHangRepo;
import com.example.baskend.KhachHang.response.KhachHangResponse;
import com.example.baskend.TaiKhoan.Entity.TaiKhoan;
import com.example.baskend.TaiKhoan.Repository.TaiKhoanRepo;
import com.example.baskend.VaiTro.Entity.VaiTro;
import com.example.baskend.VaiTro.Repository.VaiTroRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/khach-hang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KhachHangController {
    private final TaiKhoanRepo taiKhoanRepo;
    private final KhachHangRepo khachHangRepo;
    private final VaiTroRepository vaiTroRepo;

    @GetMapping("")
    public ResponseEntity<List<KhachHangResponse>> getAllKhachHang(@RequestParam(required = false) String keyword,
                                                                   @RequestParam(required = false) Boolean trangThai) {
        List<KhachHang> khachHangs = khachHangRepo.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            khachHangs = khachHangs.stream()
                    .filter(km -> km.getHoTen().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Lọc theo trạng thái
        if (trangThai != null) {
            khachHangs = khachHangs.stream()
                    .filter(km -> km.getTrangThai().equals(trangThai))
                    .collect(Collectors.toList());
        }


        if (keyword != null && !keyword.trim().isEmpty()) {
            khachHangs = khachHangs.stream()
                    .filter(kh -> kh.getHoTen().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (trangThai != null) {
            khachHangs = khachHangs.stream()
                    .filter(kh -> kh.getTrangThai().equals(trangThai))
                    .collect(Collectors.toList());
        }
        List<KhachHangResponse> responseList = khachHangs.stream()
                .map(this::mapKhachHangToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }


    @GetMapping("/search")
    public List<KhachHangResponse> searchKhachHang(@RequestParam String keyword) {
        return khachHangRepo.findByHoTenContainingIgnoreCaseOrSoDienThoaiContaining(keyword, keyword).stream()
                .map(this::mapKhachHangToResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/addKH")
    public ResponseEntity<?> createKhachHang(@Valid @RequestBody KhachHang khachHang, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        // Kiểm tra email và số điện thoại đã tồn tại chưa
        if (khachHangRepo.existsByEmail(khachHang.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại");
        }

        if (khachHangRepo.existsBySoDienThoai(khachHang.getSoDienThoai())) {
            return ResponseEntity.badRequest().body("Số điện thoại đã tồn tại");
        }

        // Kiểm tra xem khách hàng có cung cấp thông tin tài khoản hay không
        if (khachHang.getTaiKhoan() == null ||
                khachHang.getTaiKhoan().getTenDangNhap() == null ||
                khachHang.getTaiKhoan().getMatKhau() == null) {
            return ResponseEntity.badRequest().body("Thông tin tài khoản không hợp lệ");
        }

        // Lấy vai trò "Khách hàng" từ VaiTroRepository (id = 3)
        VaiTro vaiTroKhachHang = vaiTroRepo.findById(3).orElseThrow(() -> new RuntimeException("Vai trò không tồn tại"));

        // Tạo tài khoản cho khách hàng với vai trò là "Khách hàng"
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(khachHang.getTaiKhoan().getTenDangNhap());
        taiKhoan.setMatKhau(khachHang.getTaiKhoan().getMatKhau()); // Mật khẩu sẽ được mã hóa nếu cần
        taiKhoan.setVaiTro(vaiTroKhachHang);
        taiKhoan.setTrangThai(true); // Mặc định là trạng thái "Hoạt động"

        // Lưu tài khoản vào cơ sở dữ liệu
        TaiKhoan savedTaiKhoan = taiKhoanRepo.save(taiKhoan);

        // Liên kết tài khoản với khách hàng
        khachHang.setTaiKhoan(savedTaiKhoan); // Liên kết tài khoản với khách hàng
        KhachHang savedKhachHang = khachHangRepo.save(khachHang);

        // Trả về thông tin khách hàng sau khi tạo thành công
        KhachHangResponse response = mapKhachHangToResponse(savedKhachHang);

        return ResponseEntity.ok(response); // Trả về đối tượng KhachHangResponse
    }




    @GetMapping("/{id}")
    public ResponseEntity<?> getKhachHangById(@PathVariable Integer id) {
        Optional<KhachHang> khachHang = khachHangRepo.findById(id);
        if (khachHang.isPresent()) {
            return ResponseEntity.ok(khachHang.get());
        } else {
            return ResponseEntity.status(404).body("Khách hàng không tồn tại!");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteKhachHang(@PathVariable Integer id) {
        if (!khachHangRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Khách hàng không tồn tại!");
        }
        khachHangRepo.deleteById(id);
        return ResponseEntity.ok("Xóa khách hàng thành công!");
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateKhachHang(@PathVariable Integer id, @Valid @RequestBody KhachHang khachHang, BindingResult result) {
        // Validate input data
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        // Check if the customer exists
        Optional<KhachHang> existingKhachHangOpt = khachHangRepo.findById(id);
        if (existingKhachHangOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Khách hàng không tồn tại!");
        }

        // Get the existing customer
        KhachHang existingKhachHang = existingKhachHangOpt.get();

        // Update customer details
        existingKhachHang.setHoTen(khachHang.getHoTen());
        existingKhachHang.setEmail(khachHang.getEmail());
        existingKhachHang.setSoDienThoai(khachHang.getSoDienThoai());
        existingKhachHang.setGioiTinh(khachHang.getGioiTinh());
        existingKhachHang.setTrangThai(khachHang.getTrangThai());

        // Optionally update the account details
        if (khachHang.getTaiKhoan() != null) {
            existingKhachHang.getTaiKhoan().setTenDangNhap(khachHang.getTaiKhoan().getTenDangNhap());
            existingKhachHang.getTaiKhoan().setMatKhau(khachHang.getTaiKhoan().getMatKhau());  // Ensure password is encrypted if needed
        }

        // Save the updated customer
        khachHangRepo.save(existingKhachHang);

        return ResponseEntity.ok("Cập nhật khách hàng thành công!");
    }

    private KhachHangResponse mapKhachHangToResponse(KhachHang khachHang) {
        return new KhachHangResponse(
                khachHang.getId(),
//                khachHang.getTenDangNhap(),
                khachHang.getTaiKhoan().getTenDangNhap(),
                khachHang.getTaiKhoan().getId(),
                khachHang.getMaKhachHang(),
                khachHang.getHoTen(),
                khachHang.getGioiTinh(),
                khachHang.getEmail(),
//                khachHang.getMatKhau(),
                khachHang.getSoDienThoai(),
                khachHang.getTrangThai(),
                khachHang.getNgayTao(),
                khachHang.getNgaySua()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleValidationException(Exception ex) {
        return ResponseEntity.badRequest().body("Lỗi khi xử lý request: " + ex.getMessage());
    }
    @GetMapping("/get-max-ma")
    public ResponseEntity<String> getMaxMaKhachHang() {
        String maxMaKhachHang = khachHangRepo.findMaxMaKhachHang();
        return ResponseEntity.ok(maxMaKhachHang != null ? maxMaKhachHang : "KH0");
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<KhachHang> updateKhachHangStatus(@PathVariable Integer id, @RequestParam("trangThai") Boolean trangThai) {
        Optional<KhachHang> khachHangOptional = khachHangRepo.findById(id);
        if (khachHangOptional.isPresent()) {
            KhachHang khachHang = khachHangOptional.get();
            khachHang.setTrangThai(trangThai);  // Cập nhật trạng thái khách hàng

            // Cập nhật trạng thái tài khoản liên kết với khách hàng
            if (khachHang.getTaiKhoan().getId() != null) {
                Optional<TaiKhoan> taiKhoanOptional = taiKhoanRepo.findById(khachHang.getTaiKhoan().getId());
                if (taiKhoanOptional.isPresent()) {
                    TaiKhoan taiKhoan = taiKhoanOptional.get();
                    taiKhoan.setTrangThai(trangThai);  // Cập nhật trạng thái tài khoản
                    taiKhoanRepo.save(taiKhoan);  // Lưu lại tài khoản đã cập nhật
                }
            }

            KhachHang updatedKhachHang = khachHangRepo.save(khachHang);
            return ResponseEntity.ok(updatedKhachHang);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/account/{taiKhoanId}")
    public ResponseEntity<?> getKhachHangByTaiKhoanId(@PathVariable Integer taiKhoanId) {
        Optional<KhachHang> khachHang = khachHangRepo.findByTaiKhoan_Id(taiKhoanId);  // Assuming relationship exists
        if (khachHang.isPresent()) {
            return ResponseEntity.ok(khachHang.get());
        } else {
            return ResponseEntity.status(404).body("Khách hàng không tồn tại!");
        }
    }

}
