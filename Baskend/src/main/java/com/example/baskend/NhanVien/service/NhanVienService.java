package com.example.baskend.NhanVien.service;

import com.example.baskend.NhanVien.entity.NhanVien;
import com.example.baskend.NhanVien.repository.NhanVienRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NhanVienService {

    private final NhanVienRepo nhanVienRepository;

    public NhanVien createNhanVien(NhanVien nhanVien) {
        nhanVien.setNgayTao(new Date());
        nhanVien.setNgaySua(new Date());
        return nhanVienRepository.save(nhanVien);
    }

    public List<NhanVien> getAllNhanVien() {
        return nhanVienRepository.findAll();
    }

    public Optional<NhanVien> getNhanVienById(Long id) {
        return nhanVienRepository.findById(id);
    }

    public NhanVien updateNhanVien(Long id, NhanVien nhanVienDetails) {
        return nhanVienRepository.findById(id).map(nhanVien -> {
            nhanVien.setTenDangNhap(nhanVienDetails.getTenDangNhap());
            nhanVien.setHoTen(nhanVienDetails.getHoTen());
            nhanVien.setGioiTinh(nhanVienDetails.getGioiTinh());
            nhanVien.setNamSinh(nhanVienDetails.getNamSinh());
            nhanVien.setSoDienThoai(nhanVienDetails.getSoDienThoai());
            nhanVien.setEmail(nhanVienDetails.getEmail());
            nhanVien.setDiaChi(nhanVienDetails.getDiaChi());
            nhanVien.setNgaySua(new Date());
            return nhanVienRepository.save(nhanVien);
        }).orElse(null);
    }

    public boolean deleteNhanVien(Long id) {
        return nhanVienRepository.findById(id).map(nhanVien -> {
            nhanVienRepository.delete(nhanVien);
            return true;
        }).orElse(false);
    }
}