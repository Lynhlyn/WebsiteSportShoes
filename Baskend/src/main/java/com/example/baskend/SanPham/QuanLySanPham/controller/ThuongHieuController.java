package com.example.baskend.SanPham.QuanLySanPham.controller;

import com.example.baskend.SanPham.QuanLySanPham.entity.ThuongHieu;
import com.example.baskend.SanPham.QuanLySanPham.repository.ThuongHieuRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/thuong-hieu")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ThuongHieuController {
    private final ThuongHieuRepo thuongHieuRepo;

    /**
     * Get all ThuongHieu
     */
    @GetMapping("")
    public ResponseEntity<List<ThuongHieu>> getAllThuongHieu() {
        List<ThuongHieu> thuongHieuList = thuongHieuRepo.findAll();
        return ResponseEntity.ok(thuongHieuList);
    }

    /**
     * Get ThuongHieu by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ThuongHieu> getThuongHieuById(@PathVariable Integer id) {
        Optional<ThuongHieu> thuongHieu = thuongHieuRepo.findById(id);
        return thuongHieu.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new ThuongHieu
     */
    @PostMapping("/add")
    public ResponseEntity<String> createThuongHieu(@Valid @RequestBody ThuongHieu thuongHieu) {
        thuongHieuRepo.save(thuongHieu);
        return ResponseEntity.ok("Thêm thương hiệu thành công!");
    }

    /**
     * Update an existing ThuongHieu
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateThuongHieu(@PathVariable Integer id, @Valid @RequestBody ThuongHieu thuongHieuDetails) {
        Optional<ThuongHieu> existingThuongHieuOpt = thuongHieuRepo.findById(id);
        if (existingThuongHieuOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Thương hiệu không tồn tại!");
        }

        ThuongHieu existingThuongHieu = existingThuongHieuOpt.get();
        existingThuongHieu.setTenThuongHieu(thuongHieuDetails.getTenThuongHieu());
        existingThuongHieu.setTrangThai(thuongHieuDetails.getTrangThai());

        thuongHieuRepo.save(existingThuongHieu);
        return ResponseEntity.ok("Cập nhật thương hiệu thành công!");
    }

    /**
     * Delete a ThuongHieu
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteThuongHieu(@PathVariable Integer id) {
        if (!thuongHieuRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Thương hiệu không tồn tại!");
        }
        thuongHieuRepo.deleteById(id);
        return ResponseEntity.ok("Xoá thương hiệu thành công!");
    }
}
