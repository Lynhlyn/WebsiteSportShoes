package com.example.baskend.SanPham.QuanLySanPham.controller;

import com.example.baskend.SanPham.QuanLySanPham.entity.*;
import com.example.baskend.SanPham.QuanLySanPham.repository.*;
import com.example.baskend.SanPham.QuanLySanPham.response.SanPhamResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamResponse> getSanPhamById(@PathVariable Integer id) {
        Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(id);
        if (sanPhamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SanPham sanPham = sanPhamOpt.get();
        return ResponseEntity.ok(mapSanPhamToResponse(sanPham));
    }

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

    @PostMapping("/addSP")
    public ResponseEntity<?> createSanPham(@Valid @RequestBody SanPham sanPham, @RequestParam List<String> urlAnh) {
        // Kiểm tra danh sách ảnh hợp lệ
        for (String url : urlAnh) {
            if (!url.matches("^(http|https)://.*$")) {
                return ResponseEntity.badRequest().body("Ảnh sản phẩm phải là một URL hợp lệ bắt đầu bằng http hoặc https: " + url);
            }
        }

        // Lưu sản phẩm vào database
        SanPham savedSanPham = sanPhamRepo.save(sanPham);

        // Lưu ảnh vào database
        List<AnhSanPham> anhSanPhamList = new ArrayList<>();
        for (String url : urlAnh) {
            AnhSanPham anhSanPham = new AnhSanPham();
            anhSanPham.setSanPham(savedSanPham);
            anhSanPham.setAnhSP(url);
            anhSanPhamRepo.save(anhSanPham);
            anhSanPhamList.add(anhSanPham);
        }

        // Tạo danh sách ảnh trả về
        List<String> danhSachAnh = anhSanPhamList.stream()
                .map(AnhSanPham::getAnhSP)
                .collect(Collectors.toList());

        // Lấy ảnh đầu tiên nếu có ảnh, ngược lại để giá trị null
        String anhDauTien = !danhSachAnh.isEmpty() ? danhSachAnh.get(0) : null;

        // Trả về SanPhamResponse
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
                anhDauTien // Truyền ảnh đầu tiên
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/anh-san-pham/{id}")
    public ResponseEntity<String> getAnhSanPham(@PathVariable Integer id) {
        List<AnhSanPham> anhSanPhamList = anhSanPhamRepo.findBySanPhamId(id);

        if (!anhSanPhamList.isEmpty()) {
            String urlAnh = anhSanPhamList.get(0).getAnhSP(); // Lấy ảnh đầu tiên
            if (urlAnh != null && !urlAnh.isEmpty()) {
                return ResponseEntity.ok(urlAnh);
            }
        }
        return ResponseEntity.ok("https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg"); // Ảnh mặc định nếu không có ảnh
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSanPham(@PathVariable Integer id) {
        if (!sanPhamRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Sản phẩm không tồn tại!");
        }

        // Xoá ảnh trước khi xoá sản phẩm
        anhSanPhamRepo.deleteBySanPhamId(id);

        try {
            sanPhamRepo.deleteById(id);
            return ResponseEntity.ok("Xoá sản phẩm thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xoá sản phẩm: " + e.getMessage());
        }
    }

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

    @GetMapping("/filter")
    public List<SanPhamResponse> filterSanPham(
            @RequestParam(required = false) String trangThai, // Nhận dưới dạng chuỗi
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

    private SanPhamResponse mapSanPhamToResponse(SanPham sanPham) {
        // Lấy danh sách ảnh từ bảng AnhSanPham
        List<AnhSanPham> anhSanPhamList = anhSanPhamRepo.findBySanPhamId(sanPham.getId());

        // Nếu có ảnh, lấy danh sách và ảnh đầu tiên, nếu không có thì đặt ảnh mặc định
        List<String> danhSachAnh = anhSanPhamList.isEmpty()
                ? List.of("https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg") // Ảnh mặc định
                : anhSanPhamList.stream().map(AnhSanPham::getAnhSP).toList(); // Lấy tất cả ảnh

        String anhDauTien = danhSachAnh.get(0); // Ảnh đầu tiên

        return new SanPhamResponse(
                sanPham.getId(),
                sanPham.getTenSanPham(),
                sanPham.getMoTa(),
                sanPham.getDanhMuc() != null ? sanPham.getDanhMuc().getTenDanhMuc() : "Không có",
                sanPham.getThuongHieu() != null ? sanPham.getThuongHieu().getTenThuongHieu() : "Không có",
                sanPham.getChatLieu() != null ? sanPham.getChatLieu().getTenChatLieu() : "Không có",
                sanPham.getDeGiay() != null ? sanPham.getDeGiay().getTenDeGiay() : "Không có",
                sanPham.getNgayTao(),
                sanPham.getNgaySua(),
                sanPham.getTrangThai(),
                danhSachAnh,
                anhDauTien // Thêm ảnh đầu tiên vào response
        );
    }

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
