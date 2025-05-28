package com.example.baskend.SanPham.SanPhamChiTiet.controller;

import com.example.baskend.SanPham.SanPhamChiTiet.entity.Size;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SizeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("size")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SizeController {
    @Autowired
    private final SizeRepo sizeRepo;

    @GetMapping
    public List<Size> getAllSizes() {
        return sizeRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Size> getSizeById(@PathVariable Integer id) {
        return sizeRepo.findById(id)
                .map(size -> ResponseEntity.ok().body(size))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<Size> createSize(@RequestBody Size size) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sizeRepo.save(size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSize(@PathVariable Integer id, @RequestBody Size updatedSize) {
        return sizeRepo.findById(id).map(size -> {
            size.setTenSize(updatedSize.getTenSize());
            size.setTrangThai(updatedSize.getTrangThai());
            sizeRepo.save(size);
            return ResponseEntity.ok().body(size);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSize(@PathVariable Integer id) {
        return sizeRepo.findById(id).map(size -> {
            sizeRepo.delete(size);
            return ResponseEntity.ok("Size deleted successfully");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Size not found"));
    }
}
