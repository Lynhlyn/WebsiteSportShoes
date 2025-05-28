package com.example.baskend.SanPham.SanPhamChiTiet.controller;

import com.example.baskend.SanPham.KhuyenMai.entity.KhuyenMai;
import com.example.baskend.SanPham.KhuyenMai.repository.KhuyenMaiRepo;
import com.example.baskend.SanPham.QuanLySanPham.entity.AnhSanPham;
import com.example.baskend.SanPham.QuanLySanPham.entity.SanPham;
import com.example.baskend.SanPham.QuanLySanPham.repository.AnhSanPhamRepo;
import com.example.baskend.SanPham.QuanLySanPham.repository.SanPhamRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.MauSac;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.Size;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.MauSacRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SanPhamChiTietRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SizeRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.response.ChiTietResonse;
import com.example.baskend.SanPham.SanPhamChiTiet.response.SanPhamChiTietResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final AnhSanPhamRepo anhSanPhamRepo;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getSanPhamChiTietByIds(@PathVariable Integer id) {
        Optional<SanPhamChiTiet> optionalSPCT = sanPhamChiTietRepo.findById(id);
        if (optionalSPCT.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SanPhamChiTiet spct = optionalSPCT.get();
        return ResponseEntity.ok(spct);
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

    @GetMapping("/by-san-pham/{sanPhamId}")
    public ResponseEntity<List<SanPhamChiTietResponse>> getSanPhamChiTietBySanPhamId(@PathVariable Integer sanPhamId) {
        List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietRepo.findBySanPhamId(sanPhamId);

        if (sanPhamChiTietList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<SanPhamChiTietResponse> responseList = sanPhamChiTietList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
    //    @GetMapping("/by-san-pham/{sanPhamId}")
//    public ResponseEntity<List<ChiTietResonse>> getSanPhamChiTietBySanPhamId(@PathVariable Integer sanPhamId) {
//        List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietRepo.findBySanPhamId(sanPhamId);
//
//        if (sanPhamChiTietList.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // Chuyển đổi thành ChiTietResponse để trả về
//        List<ChiTietResonse> responseList = sanPhamChiTietList.stream()
//                .map(spct -> {
//                    ChiTietResonse response = mapToChiTietResponse(spct);
//                    // Lấy thông tin sản phẩm chính của SPCT
//                    response.setSanPhamId(spct.getSanPham().getId()); // Set sanPhamId trong response
//
//                    return response;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(responseList);
//    }
//    private ChiTietResonse mapToChiTietResponse(SanPhamChiTiet spct) {
//        ChiTietResonse response = new ChiTietResonse();
//        response.setId(spct.getId());
//        response.setMaSPCT(spct.getMaSPCT());
//        response.setSanPhamId(spct.getSanPham().getId()); // Set sanPhamId
//
//
//        // Set các trường khác
//        response.setTenMau(spct.getMauSac().getTenMau());
//        response.setTenSize(spct.getSize().getTenSize());
//        response.setTenKhuyenMai(spct.getKhuyenMai() != null ? spct.getKhuyenMai().getTenKhuyenMai() : null);
//        response.setGiaBan(spct.getGiaBan());
//        response.setSoLuong(spct.getSoLuong());
//        response.setTenDanhMuc(spct.getSanPham().getDanhMuc().getTenDanhMuc());
//        response.setTenDeGiay(spct.getSanPham().getDeGiay().getTenDeGiay());
//        response.setTenChatLieu(spct.getSanPham().getChatLieu().getTenChatLieu());
//        response.setTenThuongHieu(spct.getSanPham().getThuongHieu().getTenThuongHieu());
//        response.setTrangThai(spct.getTrangThai());
//
//        return response;
//    }
    @GetMapping("/check-code")
    public ResponseEntity<?> checkMaSPCT(@RequestParam String maSPCT, @RequestParam(required = false) Integer id) {
        boolean exists;
        if (id != null) {
            exists = sanPhamChiTietRepo.existsByMaSPCTAndIdNot(maSPCT, id);
        } else {
            exists = sanPhamChiTietRepo.existsByMaSPCT(maSPCT);
        }
        return ResponseEntity.ok().body(Map.of("exists", exists));
    }
    // API lấy thông tin sản phẩm chi tiết (bao gồm dữ liệu liên quan)
    @GetMapping("/san-pham/{id}")
    public ResponseEntity<?> getSanPhamChiTietById(@PathVariable Integer id) {
        List<SanPhamChiTiet> listSPCT = sanPhamChiTietRepo.findBySanPhamId(id);
        if (listSPCT.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Map<String, Object>> danhSachBienThe = listSPCT.stream().map(spct -> {
            Map<String, Object> bienThe = new HashMap<>();
            bienThe.put("maSPCT", spct.getMaSPCT());
            bienThe.put("giaBan", spct.getGiaBan());
            bienThe.put("soLuong", spct.getSoLuong());
            bienThe.put("mauSac", spct.getMauSac());
            bienThe.put("size", spct.getSize());
            return bienThe;
        }).collect(Collectors.toList());

        List<String> danhSachHinhAnh = anhSanPhamRepo.findBySanPhamId(id)
                .stream().map(AnhSanPham::getAnhSP).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("sanPham", listSPCT.get(0).getSanPham());
        response.put("bienThe", danhSachBienThe);
        response.put("danhSachHinhAnh", danhSachHinhAnh);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-spct")
    public ResponseEntity<?> createSanPhamChiTiet(@Valid @RequestBody SanPhamChiTiet sanPhamChiTiet) {
        // Kiểm tra thông tin đầu vào
        if (sanPhamChiTiet.getSanPham() == null || sanPhamChiTiet.getMauSac() == null ||
                sanPhamChiTiet.getSize() == null || sanPhamChiTiet.getGiaBan() <= 0 || sanPhamChiTiet.getSoLuong() < 0) {
            return ResponseEntity.badRequest().body("Thiếu thông tin sản phẩm, màu sắc, size hoặc giá bán, số lượng không hợp lệ!");
        }

        // Kiểm tra xem sản phẩm, màu sắc và size có hợp lệ không
        Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(sanPhamChiTiet.getSanPham().getId());
        Optional<MauSac> mauSacOpt = mauSacRepo.findById(sanPhamChiTiet.getMauSac().getId());
        Optional<Size> sizeOpt = sizeRepo.findById(sanPhamChiTiet.getSize().getId());

        if (sanPhamOpt.isEmpty() || mauSacOpt.isEmpty() || sizeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Sản phẩm, màu sắc hoặc size không hợp lệ!");
        }

        // Sử dụng mã SPCT đã gửi từ frontend
        String maSPCT = sanPhamChiTiet.getMaSPCT();  // Mã này sẽ được gửi từ frontend
        if (maSPCT == null || maSPCT.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mã sản phẩm chi tiết không hợp lệ!");
        }

        // Cập nhật thông tin cho sanPhamChiTiet
        sanPhamChiTiet.setSanPham(sanPhamOpt.get());
        sanPhamChiTiet.setMauSac(mauSacOpt.get());
        sanPhamChiTiet.setSize(sizeOpt.get());

        // Lưu sản phẩm chi tiết vào cơ sở dữ liệu
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
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSanPhamChiTiets(@PathVariable Integer id, @RequestBody SanPhamChiTiet updatedSPCT) {
        Optional<SanPhamChiTiet> optionalSPCT = sanPhamChiTietRepo.findById(id);
        if (optionalSPCT.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SanPhamChiTiet spct = optionalSPCT.get();

        // Kiểm tra và cập nhật các trường, chỉ thay đổi nếu có giá trị mới
        if (updatedSPCT.getSoLuong() != null) {
            int updatedQuantity = spct.getSoLuong() - updatedSPCT.getSoLuong(); // Trừ đi số lượng thanh toán
            if (updatedQuantity < 0) {
                return ResponseEntity.badRequest().body("Số lượng sản phẩm không đủ.");
            }
            spct.setSoLuong(updatedQuantity);

            // Nếu số lượng bằng 0, cập nhật trạng thái thành false
            if (updatedQuantity == 0) {
                spct.setTrangThai(false);  // Sản phẩm đã hết hàng
            }
        }

        // Cập nhật các thông tin khác của sản phẩm
        if (updatedSPCT.getMaSPCT() != null) spct.setMaSPCT(updatedSPCT.getMaSPCT());
        if (updatedSPCT.getSanPham() != null) spct.setSanPham(updatedSPCT.getSanPham());
        if (updatedSPCT.getMauSac() != null) spct.setMauSac(updatedSPCT.getMauSac());
        if (updatedSPCT.getKhuyenMai() != null) spct.setKhuyenMai(updatedSPCT.getKhuyenMai());
        if (updatedSPCT.getSize() != null) spct.setSize(updatedSPCT.getSize());
        if (updatedSPCT.getGiaBan() != null) spct.setGiaBan(updatedSPCT.getGiaBan());
//    if (updatedSPCT.getTrangThai() != null) spct.setTrangThai(updatedSPCT.getTrangThai());

        sanPhamChiTietRepo.save(spct);
        return ResponseEntity.ok("Cập nhật sản phẩm chi tiết thành công!");
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
                sanPham.getId(),
                tenSanPham,
                tenDeGiay,     // Đế giày
                tenThuongHieu, // Thương hiệu
                tenChatLieu,   // Chất liệu
                tenDanhMuc,    // Danh mục
                tenMau,
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
    @PatchMapping("/{id}/status")
    public ResponseEntity<SanPhamChiTiet> updateProductStatus(@PathVariable Integer id, @RequestParam("trangThai") Boolean trangThai) {
        Optional<SanPhamChiTiet> sanPhamChiTietOptional = sanPhamChiTietRepo.findById(id);

        if (sanPhamChiTietOptional.isPresent()) {
            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietOptional.get();

            // Cập nhật trạng thái của sản phẩm
            sanPhamChiTiet.setTrangThai(trangThai);

            // Lưu lại sản phẩm đã cập nhật trạng thái
            SanPhamChiTiet updatedSanPham = sanPhamChiTietRepo.save(sanPhamChiTiet);

            // Trả về sản phẩm đã cập nhật
            return ResponseEntity.ok(updatedSanPham);
        } else {
            // Trả về lỗi nếu không tìm thấy sản phẩm
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicateSPCT(
            @RequestParam Integer sanPhamId,
            @RequestParam Integer mauSacId,
            @RequestParam Integer sizeId,
            @RequestParam(required = false) Integer excludeId
    ) {
        boolean exists;

        if (excludeId != null) {
            exists = sanPhamChiTietRepo.existsBySanPhamIdAndMauSacIdAndSizeIdAndIdNot(sanPhamId, mauSacId, sizeId, excludeId);
        } else {
            exists = sanPhamChiTietRepo.existsBySanPhamIdAndMauSacIdAndSizeId(sanPhamId, mauSacId, sizeId);
        }

        return ResponseEntity.ok(Map.of("exists", exists));
    }
}