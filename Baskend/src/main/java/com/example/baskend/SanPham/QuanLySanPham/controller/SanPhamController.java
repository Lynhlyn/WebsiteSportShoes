package com.example.baskend.SanPham.QuanLySanPham.controller;

import com.example.baskend.SanPham.QuanLySanPham.entity.*;
import com.example.baskend.SanPham.QuanLySanPham.repository.*;
import com.example.baskend.SanPham.QuanLySanPham.response.SanPhamResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/check-name")
    public ResponseEntity<?> checkTenSanPham(@RequestParam String tenSanPham,
                                             @RequestParam(required = false) Integer id) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên sản phẩm không được để trống!");
        }

        boolean exists = sanPhamRepo.existsByTenSanPhamIgnoreCaseAndIdNot(tenSanPham, id);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
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
    @PostMapping("/import-batch")
    public ResponseEntity<?> importSanPham(@RequestPart("products") String productsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> productList = objectMapper.readValue(productsJson, new TypeReference<>() {});

            List<Map<String, String>> errors = new ArrayList<>();
            List<SanPham> savedProducts = new ArrayList<>();

            for (int i = 0; i < productList.size(); i++) {
                Map<String, Object> productData = productList.get(i);
                try {
                    // Extract data
                    String tenSanPham = (String) productData.get("tenSanPham");
                    String moTa = (String) productData.get("moTa");
                    Integer danhMucId = (Integer) productData.get("danhMucId");
                    Integer thuongHieuId = (Integer) productData.get("thuongHieuId");
                    Integer chatLieuId = (Integer) productData.get("chatLieuId");
                    Integer deGiayId = (Integer) productData.get("deGiayId");
                    Boolean trangThai = (Boolean) productData.get("trangThai");

                    // Validate required fields
                    if (tenSanPham == null || tenSanPham.trim().isEmpty()) {
                        errors.add(Map.of(
                                "row", String.valueOf(i + 2),
                                "message", "Tên sản phẩm không được để trống"
                        ));
                        continue;
                    }

                    if (moTa == null || moTa.trim().length() < 25) {
                        errors.add(Map.of(
                                "row", String.valueOf(i + 2),
                                "message", "Mô tả phải có ít nhất 25 ký tự"
                        ));
                        continue;
                    }

                    // Check for duplicate product name
                    if (sanPhamRepo.existsByTenSanPham(tenSanPham.trim())) {
                        errors.add(Map.of(
                                "row", String.valueOf(i + 2),
                                "message", "Tên sản phẩm đã tồn tại: " + tenSanPham
                        ));
                        continue;
                    }

                    // Fetch and validate references
                    Optional<DanhMuc> danhMuc = danhMucRepo.findById(danhMucId);
                    Optional<ThuongHieu> thuongHieu = thuongHieuRepo.findById(thuongHieuId);
                    Optional<ChatLieu> chatLieu = chatLieuRepo.findById(chatLieuId);
                    Optional<DeGiay> deGiay = deGiayRepo.findById(deGiayId);

                    if (danhMuc.isEmpty() || thuongHieu.isEmpty() || chatLieu.isEmpty() || deGiay.isEmpty()) {
                        errors.add(Map.of(
                                "row", String.valueOf(i + 2),
                                "message", "Một hoặc nhiều tham chiếu không hợp lệ"
                        ));
                        continue;
                    }

                    // Create and populate SanPham entity
                    SanPham sanPham = new SanPham();
                    sanPham.setTenSanPham(tenSanPham.trim());
                    sanPham.setMoTa(moTa.trim());
                    sanPham.setDanhMuc(danhMuc.get());
                    sanPham.setThuongHieu(thuongHieu.get());
                    sanPham.setChatLieu(chatLieu.get());
                    sanPham.setDeGiay(deGiay.get());
                    sanPham.setTrangThai(trangThai != null ? trangThai : true);

                    savedProducts.add(sanPham);
                } catch (Exception e) {
                    errors.add(Map.of(
                            "row", String.valueOf(i + 2),
                            "message", "Lỗi xử lý dữ liệu: " + e.getMessage()
                    ));
                }
            }

            // Return errors if any were found
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", 0,
                        "errors", errors
                ));
            }

            // Save all valid products
            sanPhamRepo.saveAll(savedProducts);

            return ResponseEntity.ok(Map.of(
                    "success", savedProducts.size(),
                    "errors", Collections.emptyList()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", 0,
                            "errors", List.of(Map.of(
                                    "row", "N/A",
                                    "message", "Lỗi hệ thống: " + e.getMessage()
                            ))
                    ));
        }
    }

    @PostMapping("/addSPWithFile")
    public ResponseEntity<?> createSanPham(
            @Valid @RequestPart("sanPham") SanPham sanPham,
            @RequestPart("files") MultipartFile[] files,
            HttpServletRequest request) {

        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("Phải tải lên ít nhất một hình ảnh!");
        }

        SanPham savedSanPham = sanPhamRepo.save(sanPham);
        List<String> danhSachAnh = new ArrayList<>();
        String uploadDir = "uploads/";

        // ✅ Automatically get the public-facing base URL
        String baseUrl = "http://localhost:8080" + "/images/";

        for (MultipartFile file : files) {
            try {
                if (!file.getContentType().startsWith("image/")) {
                    return ResponseEntity.badRequest().body("Tệp tải lên phải là ảnh!");
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // ✅ Use correct base URL
                String publicUrl = baseUrl + fileName;
                danhSachAnh.add(publicUrl);

                AnhSanPham anhSanPham = new AnhSanPham();
                anhSanPham.setSanPham(savedSanPham);
                anhSanPham.setAnhSP(publicUrl);
                anhSanPhamRepo.save(anhSanPham);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi khi tải lên ảnh: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(danhSachAnh);
    }

    @PutMapping("/updateSPWithFile/{id}")
    public ResponseEntity<?> updateSanPhamWithFile(
            @PathVariable Integer id,
            @Valid @RequestPart("sanPham") SanPham sanPham,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "oldImageUrls", required = false) List<String> oldImageUrls) {

        Optional<SanPham> existingSanPhamOpt = sanPhamRepo.findById(id);
        if (existingSanPhamOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn tại!");
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

        String uploadDir = "uploads/";
        String baseUrl = "http://localhost:8080" + "/images/";

        // Handle image removal
        List<AnhSanPham> existingImages = anhSanPhamRepo.findBySanPhamId(id);
        for (AnhSanPham anh : existingImages) {
            if (oldImageUrls == null || !oldImageUrls.contains(anh.getAnhSP())) {
                try {
                    // Delete the file from the file system
                    Path filePath = Paths.get(uploadDir + anh.getAnhSP().substring(baseUrl.length()));
                    Files.deleteIfExists(filePath);
                    // Remove from the database
                    anhSanPhamRepo.delete(anh);
                } catch (IOException e) {
                    // Log error and continue
                    System.err.println("Lỗi khi xóa ảnh cũ: " + e.getMessage());
                }
            }
        }

        List<String> danhSachAnh = new ArrayList<>();

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                try {
                    if (!file.getContentType().startsWith("image/")) {
                        return ResponseEntity.badRequest().body("Tệp tải lên phải là ảnh!");
                    }
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    String publicUrl = baseUrl + fileName;
                    danhSachAnh.add(publicUrl);

                    AnhSanPham anhSanPham = new AnhSanPham();
                    anhSanPham.setSanPham(existingSanPham);
                    anhSanPham.setAnhSP(publicUrl);
                    anhSanPhamRepo.save(anhSanPham);

                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Lỗi khi tải lên ảnh: " + e.getMessage());
                }
            }
        }

        return ResponseEntity.ok(danhSachAnh);
    }


    @PostMapping("/addSP")
    public ResponseEntity<?> createSanPham(@Valid @RequestBody SanPham sanPham, @RequestParam List<String> urlAnh) {
        // Kiểm tra danh sách ảnh hợp lệ
        for (String url : urlAnh) {
            if (!url.matches("^(http|https)://.*$")) {
                return ResponseEntity.badRequest()
                        .body("Ảnh sản phẩm phải là một URL hợp lệ bắt đầu bằng http hoặc https: " + url);
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
        return ResponseEntity.ok("https://supersports.com.vn/cdn/shop/files/3WF10042998-1.jpg"); // Ảnh mặc định nếu
        // không có ảnh
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
    public ResponseEntity<?> updateSanPham(@PathVariable Integer id, @Valid @RequestBody SanPham sanPham,
                                           @RequestParam List<String> urlAnh) {
        Optional<SanPham> existingSanPhamOpt = sanPhamRepo.findById(id);
        if (existingSanPhamOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn tại!");
        }

        // Kiểm tra URL ảnh có hợp lệ không
        for (String url : urlAnh) {
            if (!url.matches("^(http|https)://.*$")) {
                return ResponseEntity.badRequest()
                        .body("Ảnh sản phẩm phải là một URL hợp lệ bắt đầu bằng http hoặc https: " + url);
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
                    .filter(sp -> sp.getThuongHieu() != null
                            && sp.getThuongHieu().getTenThuongHieu().equalsIgnoreCase(tenThuongHieu))
                    .collect(Collectors.toList());
        }

        if (tenDanhMuc != null && !tenDanhMuc.isEmpty()) {
            sanPhams = sanPhams.stream()
                    .filter(sp -> sp.getDanhMuc() != null
                            && sp.getDanhMuc().getTenDanhMuc().equalsIgnoreCase(tenDanhMuc))
                    .collect(Collectors.toList());
        }

        if (tenChatLieu != null && !tenChatLieu.isEmpty()) {
            sanPhams = sanPhams.stream()
                    .filter(sp -> sp.getChatLieu() != null
                            && sp.getChatLieu().getTenChatLieu().equalsIgnoreCase(tenChatLieu))
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
