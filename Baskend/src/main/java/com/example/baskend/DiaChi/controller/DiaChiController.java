package com.example.baskend.DiaChi.controller;

import com.example.baskend.DiaChi.entity.DiaChi;
import com.example.baskend.DiaChi.repository.DiaChiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/dia-chi")
public class DiaChiController {
    @Autowired
    private DiaChiRepository diaChiRepository;

    @GetMapping("/khach-hang/{idKhachHang}")
    public ResponseEntity<List<DiaChi>> getDiaChiByKhachHang(@PathVariable Integer idKhachHang) {
        List<DiaChi> danhSachDiaChi = diaChiRepository.findByKhachHangId(idKhachHang);
        return ResponseEntity.ok(danhSachDiaChi);
    }
}
