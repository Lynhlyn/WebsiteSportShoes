package com.example.baskend.SanPham.SanPhamChiTiet.controller;

import com.example.baskend.SanPham.SanPhamChiTiet.entity.MauSac;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.MauSacRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mau-sac")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MauSacController {

    @Autowired
    private MauSacRepo mauSacRepo;

    @GetMapping
    public List<MauSac> getAllMauSac() {
        return mauSacRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MauSac> getMauSacById(@PathVariable Integer id) {
        Optional<MauSac> mauSac = mauSacRepo.findById(id);
        return mauSac.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<MauSac> createMauSac(@RequestBody MauSac mauSac) {
        MauSac createdMauSac = mauSacRepo.save(mauSac);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMauSac);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMauSac(@PathVariable Integer id, @RequestBody MauSac mauSac) {
        Optional<MauSac> existingMauSac = mauSacRepo.findById(id);
        if (existingMauSac.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Màu sắc không tồn tại!");
        }

        MauSac updatedMauSac = existingMauSac.get();
        updatedMauSac.setTenMau(mauSac.getTenMau());
        updatedMauSac.setTrangThai(mauSac.getTrangThai());
        mauSacRepo.save(updatedMauSac);

        return ResponseEntity.ok(updatedMauSac);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMauSac(@PathVariable Integer id) {
        try {
            mauSacRepo.deleteById(id);
            return ResponseEntity.ok("Màu sắc đã được xóa thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể xóa màu sắc!");
        }
    }
}
