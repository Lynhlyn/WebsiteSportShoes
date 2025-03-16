package com.example.baskend.SanPham.KhuyenMai.controller;

import com.example.baskend.SanPham.KhuyenMai.entity.KhuyenMai;
import com.example.baskend.SanPham.KhuyenMai.repository.KhuyenMaiRepo;
import com.example.baskend.SanPham.KhuyenMai.response.KhuyenMaiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/khuyen-mai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KhuyenMaiController {
    private final KhuyenMaiRepo khuyenMaiRepo; // Repository quản lý khuyến mãi

    // Định dạng ngày tháng năm: dd/MM/yyyy
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Lấy khuyến mãi theo ID
    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMaiResponse> getKhuyenMaiById(@PathVariable Integer id) {
        Optional<KhuyenMai> khuyenMai = khuyenMaiRepo.findById(id);
        if (khuyenMai.isPresent()) {
            return ResponseEntity.ok(mapKhuyenMaiToResponse(khuyenMai.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Lấy tất cả khuyến mãi hoặc tìm kiếm theo mã hoặc tên, và lọc theo ngày
    // @GetMapping("")
    // public ResponseEntity<List<KhuyenMaiResponse>> getAllKhuyenMai(@RequestParam(required = false) String keyword) {
    //     List<KhuyenMai> khuyenMais;
    //     if (keyword != null && !keyword.trim().isEmpty()) {
    //         khuyenMais = khuyenMaiRepo.findByTenKhuyenMaiContainingIgnoreCase(keyword);
    //     } else {
    //         khuyenMais = khuyenMaiRepo.findAll();
    //     }
    //     List<KhuyenMaiResponse> responseList = khuyenMais.stream()
    //             .map(this::mapKhuyenMaiToResponse)
    //             .collect(Collectors.toList());
    //     return ResponseEntity.ok(responseList);
    // }
// Lấy tất cả khuyến mãi hoặc tìm kiếm theo mã hoặc tên, và lọc theo ngày
@GetMapping("")
public ResponseEntity<List<KhuyenMaiResponse>> getAllKhuyenMai(@RequestParam(required = false) String keyword,
                                                                @RequestParam(required = false) String startDate,
                                                                @RequestParam(required = false) String endDate,
                                                                @RequestParam(required = false) Boolean trangThai) {
    List<KhuyenMai> khuyenMais = khuyenMaiRepo.findAll();

    if (keyword != null && !keyword.trim().isEmpty()) {
        khuyenMais = khuyenMais.stream()
                .filter(km -> km.getTenKhuyenMai().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    if (trangThai != null) {
        khuyenMais = khuyenMais.stream()
                .filter(km -> km.getTrangThai().equals(trangThai))
                .collect(Collectors.toList());
    }

    if (startDate != null && !startDate.trim().isEmpty()) {
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        khuyenMais = khuyenMais.stream()
                .filter(km -> !km.getNgayBatDau().isBefore(startLocalDate.atStartOfDay()))
                .collect(Collectors.toList());
    }

    if (endDate != null && !endDate.trim().isEmpty()) {
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        khuyenMais = khuyenMais.stream()
                .filter(km -> !km.getNgayKetThuc().isAfter(endLocalDate.atStartOfDay()))
                .collect(Collectors.toList());
    }

    List<KhuyenMaiResponse> responseList = khuyenMais.stream()
            .map(this::mapKhuyenMaiToResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(responseList);
}

    // Tìm kiếm khuyến mãi theo tên
    @GetMapping("/search")
    public ResponseEntity<List<KhuyenMaiResponse>> searchKhuyenMai(@RequestParam String keyword) {
        List<KhuyenMai> khuyenMais = khuyenMaiRepo.findByTenKhuyenMaiContainingIgnoreCase(keyword);
        if (khuyenMais.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<KhuyenMaiResponse> responseList = khuyenMais.stream()
                .map(this::mapKhuyenMaiToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // Thêm mới khuyến mãi
    @PostMapping("/addKM")
    public ResponseEntity<String> createKhuyenMai(@Valid @RequestBody KhuyenMai khuyenMai) {
        // Thiết lập ngày tạo khi thêm mới khuyến mãi
        khuyenMai.setNgayTao(LocalDateTime.now());
        khuyenMaiRepo.save(khuyenMai);
        return ResponseEntity.ok("Thêm khuyến mãi thành công!");
    }

    // Xoá khuyến mãi
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteKhuyenMai(@PathVariable Integer id) {
        if (!khuyenMaiRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Khuyến mãi không tồn tại!");
        }
        khuyenMaiRepo.deleteById(id);
        return ResponseEntity.ok("Xoá khuyến mãi thành công!");
    }

    // Cập nhật thông tin khuyến mãi
    @PutMapping("/{id}")
    public ResponseEntity<String> updateKhuyenMai(@PathVariable Integer id, @Valid @RequestBody KhuyenMai khuyenMai) {
        Optional<KhuyenMai> existingKhuyenMaiOpt = khuyenMaiRepo.findById(id);
        if (existingKhuyenMaiOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Khuyến mãi không tồn tại!");
        }

        KhuyenMai existingKhuyenMai = existingKhuyenMaiOpt.get();
        existingKhuyenMai.setMaKhuyenMai(khuyenMai.getMaKhuyenMai());
        existingKhuyenMai.setTenKhuyenMai(khuyenMai.getTenKhuyenMai());
        existingKhuyenMai.setNgayBatDau(khuyenMai.getNgayBatDau());
        existingKhuyenMai.setNgayKetThuc(khuyenMai.getNgayKetThuc());
        existingKhuyenMai.setPhanTramGiamGia(khuyenMai.getPhanTramGiamGia());
        existingKhuyenMai.setTrangThai(khuyenMai.getTrangThai());
        existingKhuyenMai.setNgaySua(LocalDateTime.now());

        khuyenMaiRepo.save(existingKhuyenMai);
        return ResponseEntity.ok("Sửa khuyến mãi thành công!");
    }

    // Chuyển đổi từ KhuyenMai Entity sang KhuyenMaiResponse với định dạng ngày tháng năm
    private KhuyenMaiResponse mapKhuyenMaiToResponse(KhuyenMai khuyenMai) {
        return new KhuyenMaiResponse(
                khuyenMai.getId(),
                khuyenMai.getMaKhuyenMai(),
                khuyenMai.getTenKhuyenMai(),
                khuyenMai.getNgayBatDau(),
                khuyenMai.getNgayKetThuc(),
                khuyenMai.getPhanTramGiamGia(),
                khuyenMai.getNgayTao(),
                khuyenMai.getNgaySua(),
                khuyenMai.getTrangThai()
        );
    }


}
