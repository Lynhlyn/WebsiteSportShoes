package com.example.baskend.SanPham.QuanLySanPham.controller;

import com.example.baskend.SanPham.QuanLySanPham.entity.AnhSanPham;
import com.example.baskend.SanPham.QuanLySanPham.projection.SanPhamChiTietProjection;
import com.example.baskend.SanPham.QuanLySanPham.repository.AnhSanPhamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/anh-san-pham")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnhSanPhamController {
    @Autowired
    private AnhSanPhamRepo anhSanPhamRepo;

    @GetMapping("")
    public ResponseEntity<List<SanPhamChiTietProjection>> getAllSanPham() {
        List<SanPhamChiTietProjection> sanPhamList = anhSanPhamRepo.findAllSanPhamWithImagesAndDiscount();
        return ResponseEntity.ok(sanPhamList);
    }
}
