package com.example.baskend.SanPham.SanPhamChiTiet.controller;

import com.example.baskend.SanPham.KhuyenMai.entity.KhuyenMai;
import com.example.baskend.SanPham.KhuyenMai.repository.KhuyenMaiRepo;
import com.example.baskend.SanPham.QuanLySanPham.entity.SanPham;
import com.example.baskend.SanPham.QuanLySanPham.repository.SanPhamRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.MauSac;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.Size;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.MauSacRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SanPhamChiTietRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SizeRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.response.SanPhamChiTietResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    // @GetMapping("")
    // public ResponseEntity<List<SanPhamChiTietResponse>> getAllSanPhamChiTiet(
    //         @RequestParam(required = false, defaultValue = "") String keyword) {

    //     List<SanPhamChiTiet> sanPhamChiTietList = keyword.trim().isEmpty()
    //             ? sanPhamChiTietRepo.findAll()
    //             : sanPhamChiTietRepo.searchByTenSanPham(keyword.trim());

    //     List<SanPhamChiTietResponse> responseList = sanPhamChiTietList.stream()
    //             .map(this::mapToResponse)
    //             .collect(Collectors.toList());

    //     return ResponseEntity.ok(responseList);
    // }
    @GetMapping("spct")
    public ResponseEntity<List<SanPhamChiTiet>> findAllSPCT(){
        return ResponseEntity.ok(sanPhamChiTietRepo.findAll());
    }
    @GetMapping("")
    public ResponseEntity<List<SanPhamChiTietResponse>> getAllSanPhamChiTiet(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer sizeId,
            @RequestParam(required = false) Integer colorId,
            @RequestParam(required = false) Boolean status) {

        System.out.println("✅ Trạng thái nhận từ request: " + status);

        // Chuyển đổi startDate và endDate từ String sang LocalDateTime nếu có
        LocalDateTime startLocalDateTime = null;
        LocalDateTime endLocalDateTime = null;

        if (startDate != null && !startDate.isEmpty()) {
            startLocalDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
        }
        if (endDate != null && !endDate.isEmpty()) {
            endLocalDateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
        }

        // Truy vấn sản phẩm chi tiết theo các điều kiện lọc
        List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietRepo.findAll();

        // Lọc theo từ khóa tìm kiếm sản phẩm
        if (!keyword.trim().isEmpty()) {
            sanPhamChiTietList = sanPhamChiTietRepo.searchByTenSanPham(keyword.trim());
        }

        // Lọc theo ngày tạo
        if (startLocalDateTime != null || endLocalDateTime != null) {
            LocalDateTime finalStartLocalDateTime = startLocalDateTime;
            LocalDateTime finalEndLocalDateTime = endLocalDateTime;
            sanPhamChiTietList = sanPhamChiTietList.stream()
                    .filter(spct -> {
                        LocalDateTime ngayTao = spct.getNgayTao();
                        return (finalStartLocalDateTime == null || !ngayTao.isBefore(finalStartLocalDateTime)) &&
                                (finalEndLocalDateTime == null || !ngayTao.isAfter(finalEndLocalDateTime));
                    })
                    .collect(Collectors.toList());
        }

        // Lọc theo kích thước
        if (sizeId != null) {
            sanPhamChiTietList = sanPhamChiTietList.stream()
                    .filter(spct -> spct.getSize() != null && spct.getSize().getId().equals(sizeId))
                    .collect(Collectors.toList());
        }

        // Lọc theo màu sắc
        if (colorId != null) {
            sanPhamChiTietList = sanPhamChiTietList.stream()
                    .filter(spct -> spct.getMauSac() != null && spct.getMauSac().getId().equals(colorId))
                    .collect(Collectors.toList());
        }

        // Lọc theo trạng thái
        if (status != null) {
            sanPhamChiTietList = sanPhamChiTietList.stream()
                    .filter(spct -> spct.getTrangThai().equals(status))
                    .collect(Collectors.toList());
        }

        // Chuyển đổi kết quả sang danh sách response
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
    @GetMapping("/san-pham/{id}")
    public ResponseEntity<?> getSanPhamChiTietById(@PathVariable Integer id) {
        List<SanPhamChiTiet> listSPCT = sanPhamChiTietRepo.findBySanPhamId(id);
        if (listSPCT.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SanPhamChiTiet spct = listSPCT.get(0);
        Float giaGiam = spct.getGiaBan();

        // Kiểm tra nếu sản phẩm có khuyến mãi
        if (spct.getKhuyenMai() != null) {
            Float giamGia = spct.getKhuyenMai().getPhanTramGiamGia(); // Lấy % giảm giá
            giaGiam = spct.getGiaBan() * (1 - giamGia / 100);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("maSPCT", spct.getMaSPCT()); // Thêm mã sản phẩm chi tiết
        response.put("sanPham", spct.getSanPham());
        response.put("giaBan", spct.getGiaBan());
        response.put("giaGiam", giaGiam);
        response.put("soLuong", spct.getSoLuong());
        response.put("danhSachMauSac", listSPCT.stream().map(SanPhamChiTiet::getMauSac).collect(Collectors.toSet()));
        response.put("danhSachSize", listSPCT.stream().map(SanPhamChiTiet::getSize).filter(Objects::nonNull).collect(Collectors.toSet()));

        return ResponseEntity.ok(response);
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
        // Đảm bảo các đối tượng không bị null
        String tenSanPham = spct.getSanPham() != null ? spct.getSanPham().getTenSanPham() : "Không có tên sản phẩm";
        String tenMau = spct.getMauSac() != null ? spct.getMauSac().getTenMau() : "Không có màu sắc";
        String tenSize = spct.getSize() != null ? spct.getSize().getTenSize() : "Không có kích thước";

        // Lấy thông tin Khuyến mãi
        KhuyenMai khuyenMai = spct.getKhuyenMai();
        String tenKhuyenMai = khuyenMai != null ? khuyenMai.getTenKhuyenMai() : "Không có khuyến mãi";
        Float phanTramGiamGia = khuyenMai != null ? khuyenMai.getPhanTramGiamGia() : 0F;
        LocalDateTime ngayBatDau = khuyenMai != null ? khuyenMai.getNgayBatDau() : null;
        LocalDateTime ngayKetThuc = khuyenMai != null ? khuyenMai.getNgayKetThuc() : null;

        // Lấy thông tin từ SanPham
        SanPham sanPham = spct.getSanPham();
        String tenDeGiay = sanPham != null && sanPham.getDeGiay() != null ? sanPham.getDeGiay().getTenDeGiay() : "Không có";
        String tenThuongHieu = sanPham != null && sanPham.getThuongHieu() != null ? sanPham.getThuongHieu().getTenThuongHieu() : "Không có";
        String tenChatLieu = sanPham != null && sanPham.getChatLieu() != null ? sanPham.getChatLieu().getTenChatLieu() : "Không có";
        String tenDanhMuc = sanPham != null && sanPham.getDanhMuc() != null ? sanPham.getDanhMuc().getTenDanhMuc() : "Không có";

        // Trả về đối tượng Response đúng thứ tự
        return new SanPhamChiTietResponse(
                spct.getId(),
                spct.getMaSPCT(),
                tenSanPham,
                tenDeGiay,     // Đế giày
                tenThuongHieu, // Thương hiệu
                tenChatLieu,   // Chất liệu
                tenDanhMuc,    // Danh mục
                tenMau,
                tenKhuyenMai,
                phanTramGiamGia,
                ngayBatDau,
                ngayKetThuc,
                tenSize,
                spct.getGiaBan(),
                spct.getSoLuong(),
                spct.getNgayTao(),
                spct.getNgaySua(),
                spct.getTrangThai()
        );
    }
    @GetMapping("/filter")
    public ResponseEntity<List<SanPhamChiTiet>> filterSanPhamByMauSacAndSize(
            @RequestParam(required = false) String tenMau,
            @RequestParam(required = false) String tenSize) {

        List<SanPhamChiTiet> result = sanPhamChiTietRepo.filterSanPhamByMauSacAndSize(tenMau, tenSize);
        return ResponseEntity.ok(result);
    }
}
