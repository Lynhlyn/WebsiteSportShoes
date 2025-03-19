package com.example.baskend.PhuongThucThanhToan.controller;

import com.example.baskend.PhuongThucThanhToan.entity.PhuongThucThanhToan;
import com.example.baskend.PhuongThucThanhToan.repository.PhuongThucThanhToanRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/phuong-thuc-thanh-toan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ThanhToanController {
    private final PhuongThucThanhToanRepo phuongThucThanhToanRepo;

    // Lấy tất cả phương thức thanh toán
    @GetMapping("/list")
    public List<PhuongThucThanhToan> getAllPhuongThucThanhToan() {
        return phuongThucThanhToanRepo.findAll();
    }

    // Lấy phương thức thanh toán theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PhuongThucThanhToan> getPhuongThucThanhToanById(@PathVariable Integer id) {
        Optional<PhuongThucThanhToan> phuongThucThanhToan = phuongThucThanhToanRepo.findById(id);
        return phuongThucThanhToan.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Thêm mới phương thức thanh toán
    @PostMapping("/create")
    public ResponseEntity<String> createPhuongThucThanhToan(@RequestBody PhuongThucThanhToan phuongThucThanhToan) {
        phuongThucThanhToanRepo.save(phuongThucThanhToan);
        return ResponseEntity.ok("Phương thức thanh toán đã được tạo thành công!");
    }

    // Cập nhật phương thức thanh toán
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePhuongThucThanhToan(@PathVariable Integer id, @RequestBody PhuongThucThanhToan phuongThucThanhToan) {
        Optional<PhuongThucThanhToan> existingPhuongThucThanhToan = phuongThucThanhToanRepo.findById(id);
        if (existingPhuongThucThanhToan.isEmpty()) {
            return ResponseEntity.badRequest().body("Phương thức thanh toán không tồn tại!");
        }

        PhuongThucThanhToan updatedPhuongThucThanhToan = existingPhuongThucThanhToan.get();
        updatedPhuongThucThanhToan.setTenPhuongThuc(phuongThucThanhToan.getTenPhuongThuc());
        updatedPhuongThucThanhToan.setTrangThai(phuongThucThanhToan.getTrangThai());

        phuongThucThanhToanRepo.save(updatedPhuongThucThanhToan);
        return ResponseEntity.ok("Phương thức thanh toán đã được cập nhật!");
    }

    // Xóa phương thức thanh toán
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePhuongThucThanhToan(@PathVariable Integer id) {
        if (!phuongThucThanhToanRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Phương thức thanh toán không tồn tại!");
        }
        phuongThucThanhToanRepo.deleteById(id);
        return ResponseEntity.ok("Phương thức thanh toán đã được xóa!");
    }
}
