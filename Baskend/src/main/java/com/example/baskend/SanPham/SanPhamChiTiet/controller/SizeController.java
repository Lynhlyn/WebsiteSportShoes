package com.example.baskend.SanPham.SanPhamChiTiet.controller;

import com.example.baskend.SanPham.SanPhamChiTiet.entity.Size;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SizeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("size")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SizeController {
   @Autowired
   SizeRepo sizeRepo;

    @GetMapping
    public List<Size> getAllThuongHieu() {
        return sizeRepo.findAll();
    }
}
