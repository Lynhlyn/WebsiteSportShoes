package com.example.baskend.SanPham.QuanLySanPham.controller;

import com.example.baskend.SanPham.QuanLySanPham.entity.DeGiay;
import com.example.baskend.SanPham.QuanLySanPham.repository.DeGiayRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/de-giay")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeGiayController {
    private final DeGiayRepo deGiayRepo;

    /**
     * Get all DeGiay
     */
    @GetMapping("")
    public ResponseEntity<List<DeGiay>> getAllDeGiay() {
        List<DeGiay> deGiayList = deGiayRepo.findAll();
        return ResponseEntity.ok(deGiayList);
    }

    /**
     * Get DeGiay by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeGiay> getDeGiayById(@PathVariable Integer id) {
        Optional<DeGiay> deGiay = deGiayRepo.findById(id);
        return deGiay.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new DeGiay
     */
    @PostMapping("/add")
    public ResponseEntity<String> createDeGiay(@Valid @RequestBody DeGiay deGiay) {
        deGiayRepo.save(deGiay);
        return ResponseEntity.ok("Thêm đế giày thành công!");
    }

    /**
     * Update an existing DeGiay
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateDeGiay(@PathVariable Integer id, @Valid @RequestBody DeGiay deGiayDetails) {
        Optional<DeGiay> existingDeGiayOpt = deGiayRepo.findById(id);
        if (existingDeGiayOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Đế giày không tồn tại!");
        }

        DeGiay existingDeGiay = existingDeGiayOpt.get();
        existingDeGiay.setTenDeGiay(deGiayDetails.getTenDeGiay());
        existingDeGiay.setTrangThai(deGiayDetails.getTrangThai());

        deGiayRepo.save(existingDeGiay);
        return ResponseEntity.ok("Cập nhật đế giày thành công!");
    }

    /**
     * Delete a DeGiay
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDeGiay(@PathVariable Integer id) {
        if (!deGiayRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Đế giày không tồn tại!");
        }
        deGiayRepo.deleteById(id);
        return ResponseEntity.ok("Xoá đế giày thành công!");
    }
}
