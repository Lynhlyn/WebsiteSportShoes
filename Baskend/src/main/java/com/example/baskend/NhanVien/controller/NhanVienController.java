package com.example.baskend.NhanVien.controller;

import com.example.baskend.NhanVien.entity.NhanVien;
import com.example.baskend.NhanVien.service.NhanVienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/nhan-vien")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService nhanVienService;

    @GetMapping()
    public List<NhanVien> getAllNhanVien() {
        return nhanVienService.getAllNhanVien();
    }

    @PostMapping()
    public ResponseEntity<?> createNhanVien(@RequestBody NhanVien nhanVien) {
        // Kiểm tra xem id_tai_khoan có null không
        if (nhanVien.getTaiKhoan().getId() == null) {
            return ResponseEntity.badRequest().body("ID tài khoản không thể null");
        }

        try {
            // Tiến hành lưu nhân viên
            NhanVien createdNhanVien = nhanVienService.createNhanVien(nhanVien);
            return ResponseEntity.ok(createdNhanVien);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo nhân viên: " + e.getMessage());
        }
    }




    @PutMapping("/{id}")
    public ResponseEntity<?> updateNhanVien(@PathVariable Integer id, @Valid @RequestBody NhanVien nhanVienDetails, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        NhanVien updatedNhanVien = nhanVienService.updateNhanVien(id, nhanVienDetails);
        return ResponseEntity.ok(updatedNhanVien);
    }


    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> getNhanVienById(@PathVariable Integer id) {
        Optional<NhanVien> nhanVien = nhanVienService.getNhanVienById(id);
        return nhanVien.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<NhanVien> updateNhanVien(@PathVariable Integer id, @Valid @RequestBody NhanVien nhanVienDetails) {
//        return ResponseEntity.ok(nhanVienService.updateNhanVien(id, nhanVienDetails));
//    }

    // Thêm API để cập nhật trạng thái nhân viên
    @PatchMapping("/{id}/status")
    public ResponseEntity<NhanVien> updateStatus(@PathVariable Integer id, @RequestParam("trangThai") Boolean trangThai) {
        Optional<NhanVien> nhanVienOptional = nhanVienService.getNhanVienById(id);
        if (nhanVienOptional.isPresent()) {
            NhanVien updatedNhanVien = nhanVienService.updateStatus(id, trangThai);
            return ResponseEntity.ok(updatedNhanVien);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
