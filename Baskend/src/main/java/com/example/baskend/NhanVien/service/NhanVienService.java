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
        try {
            // Tạo mã nhân viên tự động
            String maNhanVien = generateMaNhanVien();
            nhanVien.setMaNhanVien(maNhanVien);

            // Thiết lập thời gian tạo và sửa
            nhanVien.setNgayTao(new Date());
            nhanVien.setNgaySua(new Date());

            // Lưu nhân viên vào cơ sở dữ liệu
            return nhanVienRepository.save(nhanVien); // Lưu và trả về nhân viên đã lưu
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu nhân viên: " + e.getMessage());
        }
    }


    public List<NhanVien> getAllNhanVien() {
        return nhanVienRepository.findAll();
    }

    public Optional<NhanVien> getNhanVienById(Integer id) {
        return nhanVienRepository.findById(id);
    }

    public NhanVien updateNhanVien(Integer id, NhanVien nhanVienDetails) {
        return nhanVienRepository.findById(id).map(nhanVien -> {

            nhanVien.setHoTen(nhanVienDetails.getHoTen());
            nhanVien.setGioiTinh(nhanVienDetails.getGioiTinh());
            nhanVien.setNamSinh(nhanVienDetails.getNamSinh());
            nhanVien.setSoDienThoai(nhanVienDetails.getSoDienThoai());
            nhanVien.setEmail(nhanVienDetails.getEmail());
            nhanVien.setDiaChi(nhanVienDetails.getDiaChi());
            if (nhanVienDetails.getTaiKhoan() != null && nhanVienDetails.getTaiKhoan().getVaiTro() != null) {
                nhanVien.getTaiKhoan().setVaiTro(nhanVienDetails.getTaiKhoan().getVaiTro());
            }

            nhanVien.setNgaySua(new Date());
            return nhanVienRepository.save(nhanVien);
        }).orElse(null);
    }

    public NhanVien updateStatus(Integer id, Boolean trangThai) {
        Optional<NhanVien> nhanVienOptional = nhanVienRepository.findById(id);
        if (nhanVienOptional.isPresent()) {
            NhanVien nhanVien = nhanVienOptional.get();
            nhanVien.setTrangThai(trangThai);
            return nhanVienRepository.save(nhanVien);
        } else {
            throw new RuntimeException("Không tìm thấy nhân viên với ID: " + id);
        }
    }

    // Phương thức sinh mã nhân viên tự động
    private String generateMaNhanVien() {
        int nextId = nhanVienRepository.findAll().size() + 1; // Lấy số lượng nhân viên hiện tại
        return "NV" + String.format("%04d", nextId); // Tạo mã theo dạng NV0001, NV0002, ...
    }
}
