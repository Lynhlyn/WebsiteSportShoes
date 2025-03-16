package com.example.baskend.SanPham.QuanLySanPham.controller;

import com.example.baskend.SanPham.QuanLySanPham.entity.DanhMuc;
import com.example.baskend.SanPham.QuanLySanPham.repository.DanhMucRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/danh-muc")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DanhMucController {
    private final DanhMucRepo danhMucRepo;

    /**
     * Get all categories.
     */
    @GetMapping
    public ResponseEntity<List<DanhMuc>> getAllDanhMuc() {
        List<DanhMuc> danhMucs = danhMucRepo.findAll();
        return ResponseEntity.ok(danhMucs);
    }

    /**
     * Get category by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DanhMuc> getDanhMucById(@PathVariable Integer id) {
        Optional<DanhMuc> danhMuc = danhMucRepo.findById(id);
        return danhMuc.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new category.
     */
    @PostMapping("/add")
    public ResponseEntity<String> createDanhMuc(@Valid @RequestBody DanhMuc danhMuc) {
        danhMucRepo.save(danhMuc);
        return ResponseEntity.ok("Thêm danh mục thành công!");
    }

    /**
     * Update an existing category.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateDanhMuc(@PathVariable Integer id, @Valid @RequestBody DanhMuc danhMucDetails) {
        Optional<DanhMuc> existingDanhMucOpt = danhMucRepo.findById(id);
        if (existingDanhMucOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Danh mục không tồn tại!");
        }

        DanhMuc existingDanhMuc = existingDanhMucOpt.get();
        existingDanhMuc.setTenDanhMuc(danhMucDetails.getTenDanhMuc());
        danhMucRepo.save(existingDanhMuc);
        return ResponseEntity.ok("Cập nhật danh mục thành công!");
    }

    /**
     * Delete a category.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDanhMuc(@PathVariable Integer id) {
        if (!danhMucRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Danh mục không tồn tại!");
        }
        danhMucRepo.deleteById(id);
        return ResponseEntity.ok("Xoá danh mục thành công!");
    }
}
