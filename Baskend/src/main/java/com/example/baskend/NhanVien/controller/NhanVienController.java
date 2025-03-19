package com.example.baskend.NhanVien.controller;

import com.example.baskend.NhanVien.entity.NhanVien;
import com.example.baskend.NhanVien.service.NhanVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("nhanvien")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService nhanVienService;

    @GetMapping()
    public List<NhanVien> getAllNhanVien() {
        return nhanVienService.getAllNhanVien();
    }

    @PostMapping()
    public ResponseEntity<NhanVien> createNhanVien(@RequestBody NhanVien nhanVien) {
        return ResponseEntity.ok(nhanVienService.createNhanVien(nhanVien));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> getNhanVienById(@PathVariable Long id) {
        Optional<NhanVien> nhanVien = nhanVienService.getNhanVienById(id);
        return nhanVien.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhanVien> updateNhanVien(@PathVariable Long id, @RequestBody NhanVien nhanVienDetails) {
        return ResponseEntity.ok(nhanVienService.updateNhanVien(id, nhanVienDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNhanVien(@PathVariable Long id) {
        boolean deleted = nhanVienService.deleteNhanVien(id);
        return deleted ? ResponseEntity.ok("Xóa nhân viên thành công!")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nhân viên không tồn tại!");
    }
}
