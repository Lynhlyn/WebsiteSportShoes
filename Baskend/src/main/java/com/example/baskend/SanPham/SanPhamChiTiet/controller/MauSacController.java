package com.example.baskend.SanPham.SanPhamChiTiet.controller;

import com.example.baskend.SanPham.SanPhamChiTiet.entity.MauSac;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.MauSacRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("mau-sac")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MauSacController {
    @Autowired
    MauSacRepo mauSacRepo;

    @GetMapping
    public List<MauSac> getAllThuongHieu() {
        return mauSacRepo.findAll();
    }
}
