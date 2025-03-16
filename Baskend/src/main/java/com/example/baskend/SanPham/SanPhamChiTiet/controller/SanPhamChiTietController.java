package com.example.baskend.SanPham.SanPhamChiTiet.controller;

import com.example.baskend.SanPham.QuanLySanPham.entity.SanPham;
import com.example.baskend.SanPham.QuanLySanPham.repository.SanPhamRepo;
import com.example.baskend.SanPham.KhuyenMai.entity.KhuyenMai;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.MauSac;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.Size;
import com.example.baskend.SanPham.KhuyenMai.repository.KhuyenMaiRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.MauSacRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SanPhamChiTietRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SizeRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.response.SanPhamChiTietResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/san-pham-chi-tiet")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SanPhamChiTietController {
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final SanPhamRepo sanPhamRepo;
    private final MauSacRepo mauSacRepo;
    private final KhuyenMaiRepo khuyenMaiRepo;
    private final SizeRepo sizeRepo;

//    @GetMapping("")
//    public ResponseEntity<List<SanPhamChiTietResponse>> getAllSanPhamChiTiet(
//            @RequestParam(required = false, defaultValue = "") String keyword) {
//
//        List<SanPhamChiTiet> sanPhamChiTietList = keyword.trim().isEmpty()
//                ? sanPhamChiTietRepo.findAll()
//                : sanPhamChiTietRepo.searchByTenSanPham(keyword.trim());
//
//        List<SanPhamChiTietResponse> responseList = sanPhamChiTietList.stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(responseList);
//    }

    @GetMapping("")
    public ResponseEntity<List<SanPhamChiTietResponse>> getAllSanPhamChiTiet(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) Boolean status) {

        List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietRepo.findAll();

        // Lọc theo từ khóa tìm kiếm sản phẩm
        if (!keyword.trim().isEmpty()) {
            sanPhamChiTietList = sanPhamChiTietRepo.searchByTenSanPham(keyword.trim());
        }

        // Lọc theo trạng thái của sản phẩm chi tiết
        if (status != null) {
            sanPhamChiTietList = sanPhamChiTietList.stream()
                    .filter(spct -> spct.getTrangThai().equals(status))
                    .collect(Collectors.toList());
        }

        // Chuyển đổi kết quả sang dạng response
        List<SanPhamChiTietResponse> responseList = sanPhamChiTietList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }



    @GetMapping("/san-pham")
    public ResponseEntity<List<SanPham>> getSanPhamList() {
        return ResponseEntity.ok(sanPhamRepo.findAll());
    }

    @GetMapping("/mau-sac")
    public ResponseEntity<List<MauSac>> getMauSacList() {
        return ResponseEntity.ok(mauSacRepo.findAll());
    }

    @GetMapping("/khuyen-mai")
    public ResponseEntity<List<KhuyenMai>> getKhuyenMaiList() {
        return ResponseEntity.ok(khuyenMaiRepo.findAll());
    }

    @GetMapping("/size")
    public ResponseEntity<List<Size>> getSizeList() {
        return ResponseEntity.ok(sizeRepo.findAll());
    }

    // API lấy thông tin sản phẩm chi tiết (bao gồm dữ liệu liên quan)
    @GetMapping("/{id}")
    public ResponseEntity<?> getSanPhamChiTietById(@PathVariable Integer id) {
        Optional<SanPhamChiTiet> optionalSPCT = sanPhamChiTietRepo.findById(id);
        if (optionalSPCT.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SanPhamChiTiet spct = optionalSPCT.get();
        return ResponseEntity.ok(spct);
    }

    @PostMapping("/add-spct")
    public ResponseEntity<?> createSanPhamChiTiet(@Valid @RequestBody SanPhamChiTiet sanPhamChiTiet) {
        if (sanPhamChiTiet.getSanPham() == null || sanPhamChiTiet.getMauSac() == null ||
                sanPhamChiTiet.getSize() == null || sanPhamChiTiet.getMaSPCT() == null || sanPhamChiTiet.getMaSPCT().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Thiếu thông tin sản phẩm, màu sắc, size hoặc mã sản phẩm!");
        }

        Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(sanPhamChiTiet.getSanPham().getId());
        Optional<MauSac> mauSacOpt = mauSacRepo.findById(sanPhamChiTiet.getMauSac().getId());
        Optional<Size> sizeOpt = sizeRepo.findById(sanPhamChiTiet.getSize().getId());
        Optional<KhuyenMai> khuyenMaiOpt = (sanPhamChiTiet.getKhuyenMai() != null && sanPhamChiTiet.getKhuyenMai().getId() != null)
                ? khuyenMaiRepo.findById(sanPhamChiTiet.getKhuyenMai().getId()) : Optional.empty();

        if (sanPhamOpt.isEmpty() || mauSacOpt.isEmpty() || sizeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Sản phẩm, màu sắc hoặc size không hợp lệ!");
        }

        if (sanPhamChiTiet.getGiaBan() <= 0 || sanPhamChiTiet.getSoLuong() < 0) {
            return ResponseEntity.badRequest().body("Giá bán phải lớn hơn 0, số lượng không được âm!");
        }

        sanPhamChiTiet.setSanPham(sanPhamOpt.get());
        sanPhamChiTiet.setMauSac(mauSacOpt.get());
        sanPhamChiTiet.setSize(sizeOpt.get());
        sanPhamChiTiet.setKhuyenMai(khuyenMaiOpt.orElse(null));

        sanPhamChiTietRepo.save(sanPhamChiTiet);
        return ResponseEntity.ok("Thêm sản phẩm chi tiết thành công!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSanPhamChiTiet(@PathVariable Integer id, @RequestBody SanPhamChiTiet updatedSPCT) {
        Optional<SanPhamChiTiet> optionalSPCT = sanPhamChiTietRepo.findById(id);
        if (optionalSPCT.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SanPhamChiTiet spct = optionalSPCT.get();
        spct.setMaSPCT(updatedSPCT.getMaSPCT());
        spct.setSanPham(updatedSPCT.getSanPham());
        spct.setMauSac(updatedSPCT.getMauSac());
        spct.setKhuyenMai(updatedSPCT.getKhuyenMai());
        spct.setSize(updatedSPCT.getSize());
        spct.setGiaBan(updatedSPCT.getGiaBan());
        spct.setSoLuong(updatedSPCT.getSoLuong());
        spct.setTrangThai(updatedSPCT.getTrangThai());

        sanPhamChiTietRepo.save(spct);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    private SanPhamChiTietResponse mapToResponse(SanPhamChiTiet spct) {
        KhuyenMai khuyenMai = spct.getKhuyenMai();
        return new SanPhamChiTietResponse(
                spct.getId(),
                spct.getMaSPCT(),
                spct.getSanPham().getTenSanPham(),
                spct.getMauSac().getTenMau(),
                khuyenMai != null ? khuyenMai.getTenKhuyenMai() : "Không có khuyến mãi",
                khuyenMai != null ? khuyenMai.getPhanTramGiamGia() : 0F,
                khuyenMai != null ? khuyenMai.getNgayBatDau() : null,
                khuyenMai != null ? khuyenMai.getNgayKetThuc() : null,
                spct.getSize().getTenSize(),
                spct.getGiaBan(),
                spct.getSoLuong(),
                spct.getNgayTao(),
                spct.getNgaySua(),
                spct.getTrangThai()
        );
    }
}
