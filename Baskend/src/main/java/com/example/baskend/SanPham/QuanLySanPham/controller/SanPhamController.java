package com.example.baskend.SanPham.QuanLySanPham.controller;
import com.example.baskend.SanPham.QuanLySanPham.entity.*;
import com.example.baskend.SanPham.QuanLySanPham.repository.*;
import com.example.baskend.SanPham.QuanLySanPham.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/san-pham")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SanPhamController {
    private final SanPhamRepo sanPhamRepo;
    private final DanhMucRepo danhMucRepo;
    private final ThuongHieuRepo thuongHieuRepo;
    private final ChatLieuRepo chatLieuRepo;
    private final DeGiayRepo deGiayRepo;
    private final AnhSanPhamRepo anhSanPhamRepo;

    // Get the details of a product by ID
    @GetMapping("/{id}")
    public ResponseEntity<SanPhamResponse> getSanPhamById(@PathVariable Integer id) {
        Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(id);
        if (sanPhamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        SanPham sanPham = sanPhamOpt.get();
        return ResponseEntity.ok(mapSanPhamToResponse(sanPham));
    }

    // Get all products (optional search functionality)
    @GetMapping("")
    public List<SanPhamResponse> getAllSanPham(@RequestParam(required = false) String keyword) {
        List<SanPham> sanPhams;
        if (keyword != null && !keyword.trim().isEmpty()) {
            sanPhams = sanPhamRepo.findByTenSanPhamContainingIgnoreCase(keyword);
        } else {
            sanPhams = sanPhamRepo.findAll();
        }
        return sanPhams.stream()
                .map(this::mapSanPhamToResponse)
                .collect(Collectors.toList());
    }

    // Search for products based on a keyword
    @GetMapping("/search")
    public ResponseEntity<List<SanPhamResponse>> searchSanPham(@RequestParam(required = false) String keyword) {
        List<SanPhamResponse> result;

        if (keyword == null || keyword.trim().isEmpty()) {
            result = sanPhamRepo.findAll().stream()
                    .map(this::mapSanPhamToResponse)
                    .collect(Collectors.toList());
        } else {
            result = sanPhamRepo.findByTenSanPhamContainingIgnoreCase(keyword).stream()
                    .map(this::mapSanPhamToResponse)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(result);
    }

    // Add a new product
    @PostMapping("/addSP")
    public ResponseEntity<?> createSanPham(@Valid @RequestBody SanPham sanPham, @RequestParam List<String> urlAnh) {
        if (urlAnh == null || urlAnh.isEmpty()) {
            return ResponseEntity.badRequest().body("Danh sách ảnh không được để trống!");
        }

        // Check if URLs are valid
        for (String url : urlAnh) {
            if (!url.matches("^(http|https)://.*$")) {
                return ResponseEntity.badRequest().body("Ảnh sản phẩm phải là một URL hợp lệ: " + url);
            }
        }

        try {
            // Save the product
            SanPham savedSanPham = sanPhamRepo.save(sanPham);

            // Save images for the product
            List<AnhSanPham> anhSanPhamList = new ArrayList<>();
            for (String url : urlAnh) {
                AnhSanPham anhSanPham = new AnhSanPham();
                anhSanPham.setSanPham(savedSanPham);
                anhSanPham.setAnhSP(url);
                anhSanPhamRepo.save(anhSanPham);
                anhSanPhamList.add(anhSanPham);
            }

            // Get list of image URLs
            List<String> danhSachAnh = anhSanPhamList.stream()
                    .map(AnhSanPham::getAnhSP)
                    .collect(Collectors.toList());

            // Set the first image as the main image
            String anhDauTien = !danhSachAnh.isEmpty() ? danhSachAnh.get(0) : null;

            // Return product response
            SanPhamResponse response = new SanPhamResponse(
                    savedSanPham.getId(),
                    savedSanPham.getTenSanPham(),
                    savedSanPham.getMoTa(),
                    savedSanPham.getDanhMuc().getTenDanhMuc(),
                    savedSanPham.getThuongHieu().getTenThuongHieu(),
                    savedSanPham.getChatLieu().getTenChatLieu(),
                    savedSanPham.getDeGiay().getTenDeGiay(),
                    savedSanPham.getNgayTao(),
                    savedSanPham.getNgaySua(),
                    savedSanPham.getTrangThai(),
                    danhSachAnh,
                    anhDauTien
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
    }

    // Update an existing product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSanPham(@PathVariable Integer id, @Valid @RequestBody SanPham sanPham, @RequestParam List<String> urlAnh) {
        Optional<SanPham> existingSanPhamOpt = sanPhamRepo.findById(id);
        if (existingSanPhamOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn tại!");
        }

        // Kiểm tra URL ảnh có hợp lệ không
        for (String url : urlAnh) {
            if (!url.matches("^(http|https)://.*$")) {
                return ResponseEntity.badRequest().body("Ảnh sản phẩm phải là một URL hợp lệ bắt đầu bằng http hoặc https: " + url);
            }
        }

        SanPham existingSanPham = existingSanPhamOpt.get();
        existingSanPham.setTenSanPham(sanPham.getTenSanPham());
        existingSanPham.setMoTa(sanPham.getMoTa());
        existingSanPham.setDanhMuc(sanPham.getDanhMuc());
        existingSanPham.setThuongHieu(sanPham.getThuongHieu());
        existingSanPham.setChatLieu(sanPham.getChatLieu());
        existingSanPham.setDeGiay(sanPham.getDeGiay());
        existingSanPham.setTrangThai(sanPham.getTrangThai());

        sanPhamRepo.save(existingSanPham);

        // Xoá ảnh cũ
        anhSanPhamRepo.deleteBySanPhamId(id);

        // Lưu ảnh mới
        for (String url : urlAnh) {
            AnhSanPham anhSanPham = new AnhSanPham();
            anhSanPham.setSanPham(existingSanPham);
            anhSanPham.setAnhSP(url);
            anhSanPhamRepo.save(anhSanPham);
        }

        return ResponseEntity.ok("Cập nhật sản phẩm thành công!");
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSanPham(@PathVariable Integer id) {
        if (!sanPhamRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Sản phẩm không tồn tại!");
        }

        // Delete related images before deleting the product
        anhSanPhamRepo.deleteBySanPhamId(id);

        try {
            sanPhamRepo.deleteById(id);
            return ResponseEntity.ok("Xoá sản phẩm thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xoá sản phẩm: " + e.getMessage());
        }
    }

    // Filter products based on various criteria
    @GetMapping("/filter")
    public List<SanPhamResponse> filterSanPham(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String tenThuongHieu,
            @RequestParam(required = false) String tenDanhMuc,
            @RequestParam(required = false) String tenChatLieu,
            @RequestParam(required = false) String tenDeGiay) {

        List<SanPham> sanPhams = sanPhamRepo.findAll();

        if (trangThai != null && !trangThai.isEmpty()) {
            Boolean parsedTrangThai = Boolean.parseBoolean(trangThai);
            sanPhams = sanPhams.stream()
                    .filter(sp -> sp.getTrangThai().equals(parsedTrangThai))
                    .collect(Collectors.toList());
        }

        if (tenThuongHieu != null && !tenThuongHieu.isEmpty()) {
            sanPhams = sanPhams.stream()
                    .filter(sp -> sp.getThuongHieu() != null && sp.getThuongHieu().getTenThuongHieu().equalsIgnoreCase(tenThuongHieu))
                    .collect(Collectors.toList());
        }

        if (tenDanhMuc != null && !tenDanhMuc.isEmpty()) {
            sanPhams = sanPhams.stream()
                    .filter(sp -> sp.getDanhMuc() != null && sp.getDanhMuc().getTenDanhMuc().equalsIgnoreCase(tenDanhMuc))
                    .collect(Collectors.toList());
        }

        if (tenChatLieu != null && !tenChatLieu.isEmpty()) {
            sanPhams = sanPhams.stream()
                    .filter(sp -> sp.getChatLieu() != null && sp.getChatLieu().getTenChatLieu().equalsIgnoreCase(tenChatLieu))
                    .collect(Collectors.toList());
        }

        if (tenDeGiay != null && !tenDeGiay.isEmpty()) {
            sanPhams = sanPhams.stream()
                    .filter(sp -> sp.getDeGiay() != null && sp.getDeGiay().getTenDeGiay().equalsIgnoreCase(tenDeGiay))
                    .collect(Collectors.toList());
        }

        return sanPhams.stream()
                .map(this::mapSanPhamToResponse)
                .collect(Collectors.toList());
    }

    // Update the product response mapping
    private SanPhamResponse mapSanPhamToResponse(SanPham sanPham) {
        // Get the list of images for the product
        List<AnhSanPham> anhSanPhamList = anhSanPhamRepo.findBySanPhamId(sanPham.getId());
        List<String> danhSachAnh = anhSanPhamList.isEmpty()
                ? List.of("https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg") // Default image
                : anhSanPhamList.stream().map(AnhSanPham::getAnhSP).collect(Collectors.toList());
        String anhDauTien = danhSachAnh.get(0); // First image as the main image

        // Fetch related entities directly from the SanPham entity
        String danhMuc = sanPham.getDanhMuc() != null ? sanPham.getDanhMuc().getTenDanhMuc() : "N/A";
        String thuongHieu = sanPham.getThuongHieu() != null ? sanPham.getThuongHieu().getTenThuongHieu() : "N/A";
        String chatLieu = sanPham.getChatLieu() != null ? sanPham.getChatLieu().getTenChatLieu() : "N/A";
        String deGiay = sanPham.getDeGiay() != null ? sanPham.getDeGiay().getTenDeGiay() : "N/A";

        // Create and return the SanPhamResponse object
        return new SanPhamResponse(
                sanPham.getId(),
                sanPham.getTenSanPham(),
                sanPham.getMoTa(),
                danhMuc,  // Use the actual category name
                thuongHieu,  // Use the actual brand name
                chatLieu,  // Use the actual material name
                deGiay,  // Use the actual sole name
                sanPham.getNgayTao(),
                sanPham.getNgaySua(),
                sanPham.getTrangThai(),
                danhSachAnh,
                anhDauTien
        );
    }





    // Get the list of categories, brands, materials, and soles
    @GetMapping("/danh-muc")
    public List<DanhMuc> getAllDanhMuc() {
        return danhMucRepo.findAll();
    }

    @GetMapping("/thuong-hieu")
    public List<ThuongHieu> getAllThuongHieu() {
        return thuongHieuRepo.findAll();
    }

    @GetMapping("/chat-lieu")
    public List<ChatLieu> getAllChatLieu() {
        return chatLieuRepo.findAll();
    }

    @GetMapping("/de-giay")
    public List<DeGiay> getAllDeGiay() {
        return deGiayRepo.findAll();
    }
}
