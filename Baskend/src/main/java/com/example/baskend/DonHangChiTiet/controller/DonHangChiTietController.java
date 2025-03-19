package com.example.baskend.DonHangChiTiet.controller;

import com.example.baskend.DonHang.entity.DonHang;
import com.example.baskend.DonHang.repository.DonHangRepo;
import com.example.baskend.DonHangChiTiet.entity.DonHangChiTiet;
import com.example.baskend.DonHangChiTiet.repository.DonHangChiTietRepo;
import com.example.baskend.DonHangChiTiet.response.DonHangChiTietResponse;
import com.example.baskend.SanPham.SanPhamChiTiet.entity.SanPhamChiTiet;
import com.example.baskend.SanPham.SanPhamChiTiet.repository.SanPhamChiTietRepo;
import com.example.baskend.SanPham.SanPhamChiTiet.response.SanPhamChiTietResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/don-hang-chi-tiet")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DonHangChiTietController {
    @Autowired
    private DonHangRepo donHangRepo;
    @Autowired
    private DonHangChiTietRepo donHangChiTietRepo;

    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;

    // Lấy tất cả chi tiết đơn hàng
//    @GetMapping
//    public List<DonHangChiTiet> getAllDonHangChiTiet() {
//        return donHangChiTietRepo.findAll();
//    }

    // Lấy chi tiết đơn hàng theo id
//    @GetMapping("/{id}")
//    public ResponseEntity<DonHangChiTiet> getDonHangChiTietById(@PathVariable Integer id) {
//        Optional<DonHangChiTiet> donHangChiTiet = donHangChiTietRepo.findById(id);
//        return donHangChiTiet.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }


    @GetMapping("/{id}")
    public ResponseEntity<DonHangChiTietResponse> getDonHangChiTietById(@PathVariable Integer id) {
        Optional<DonHang> donHangOpt = donHangRepo.findById(id);

        if (donHangOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DonHang donHang = donHangOpt.get();

        // Fetching all DonHangChiTiet entries for the given order
        List<DonHangChiTiet> donHangChiTietList = donHangChiTietRepo.findByDonHangId(id);

        // Mapping DonHangChiTiet to DonHangChiTietResponse
        List<SanPhamChiTietResponse> sanPhamChiTietResponses = donHangChiTietList.stream()
                .map(donHangChiTiet -> {
                    // Map each DonHangChiTiet's SanPhamChiTiet to SanPhamChiTietResponse
                    SanPhamChiTietResponse sanPhamResponse = new SanPhamChiTietResponse(
                            donHangChiTiet.getSanPhamChiTiet().getId(),
                            donHangChiTiet.getSanPhamChiTiet().getMaSPCT(),
                            donHangChiTiet.getSanPhamChiTiet().getSanPham().getTenSanPham(),
                            donHangChiTiet.getSanPhamChiTiet().getSanPham().getDeGiay().getTenDeGiay(),
                            donHangChiTiet.getSanPhamChiTiet().getSanPham().getThuongHieu().getTenThuongHieu(),
                            donHangChiTiet.getSanPhamChiTiet().getSanPham().getChatLieu().getTenChatLieu(),
                            donHangChiTiet.getSanPhamChiTiet().getSanPham().getDanhMuc().getTenDanhMuc(),
                            donHangChiTiet.getSanPhamChiTiet().getMauSac().getTenMau(),
                            donHangChiTiet.getSanPhamChiTiet().getKhuyenMai() != null ? donHangChiTiet.getSanPhamChiTiet().getKhuyenMai().getTenKhuyenMai() : null,
                            donHangChiTiet.getSanPhamChiTiet().getKhuyenMai() != null ? donHangChiTiet.getSanPhamChiTiet().getKhuyenMai().getPhanTramGiamGia() : null,
                            donHangChiTiet.getSanPhamChiTiet().getKhuyenMai().getNgayBatDau(),
                            donHangChiTiet.getSanPhamChiTiet().getKhuyenMai().getNgayKetThuc(),
                            donHangChiTiet.getSanPhamChiTiet().getSize().getTenSize(),
                            donHangChiTiet.getSanPhamChiTiet().getGiaBan(),
                            donHangChiTiet.getSoLuong(),
                            donHangChiTiet.getSanPhamChiTiet().getNgayTao(),
                            donHangChiTiet.getSanPhamChiTiet().getNgaySua(),
                            donHangChiTiet.getTrangThai()
                    );
                    return sanPhamResponse;
                }).collect(Collectors.toList());

        // Construct the response with order details and list of products
        DonHangChiTietResponse response = new DonHangChiTietResponse(
                donHang.getId(),
                donHang,
                sanPhamChiTietResponses, // List of products
                donHangChiTietList.stream().mapToInt(DonHangChiTiet::getSoLuong).sum(),
                (float) donHangChiTietList.stream().mapToDouble(d -> d.getDonGia() * d.getSoLuong()).sum(),
                donHangChiTietList.get(0).getNgayTao().toString(),
                donHangChiTietList.get(0).getNgaySua().toString(),
                donHangChiTietList.get(0).getTrangThai()
        );

        return ResponseEntity.ok(response);
    }


    // Thêm chi tiết đơn hàng mới
//    @PostMapping
//    public ResponseEntity<DonHangChiTiet> createDonHangChiTiet(@RequestBody DonHangChiTiet donHangChiTiet) {
//        // Kiểm tra xem sản phẩm có tồn tại trong cơ sở dữ liệu không
//        Optional<SanPhamChiTiet> sanPhamChiTietOpt = sanPhamChiTietRepo.findById(donHangChiTiet.getSanPhamChiTiet().getId());
//        if (sanPhamChiTietOpt.isEmpty()) {
//            return ResponseEntity.badRequest().body(null); // Trả về lỗi nếu sản phẩm không tồn tại
//        }
//
//        // Lưu chi tiết đơn hàng vào cơ sở dữ liệu
//        DonHangChiTiet savedDonHangChiTiet = donHangChiTietRepo.save(donHangChiTiet);
//        return ResponseEntity.ok(savedDonHangChiTiet);
//    }
    @GetMapping
    public ResponseEntity<Page<DonHangChiTiet>> getAllDonHangChiTiet(Pageable pageable) {
        Page<DonHangChiTiet> donHangChiTietPage = donHangChiTietRepo.findAll(pageable);
        return ResponseEntity.ok(donHangChiTietPage);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> createOrUpdateDonHangChiTiet(@RequestBody DonHangChiTiet donHangChiTiet) {
        if (donHangChiTiet.getDonHang() == null || donHangChiTiet.getSanPhamChiTiet() == null) {
            return ResponseEntity.badRequest().body("❌ Thiếu thông tin hóa đơn hoặc sản phẩm.");
        }

        Optional<DonHang> donHangOpt = donHangRepo.findById(donHangChiTiet.getDonHang().getId());
        Optional<SanPhamChiTiet> sanPhamChiTietOpt = sanPhamChiTietRepo.findById(donHangChiTiet.getSanPhamChiTiet().getId());

        if (donHangOpt.isEmpty() || sanPhamChiTietOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Hóa đơn hoặc sản phẩm không tồn tại.");
        }

        DonHang donHang = donHangOpt.get();
        SanPhamChiTiet spct = sanPhamChiTietOpt.get();

        Optional<DonHangChiTiet> existingDonHangChiTiet = donHangChiTietRepo.findByDonHangAndSanPhamChiTiet(donHang, spct);

        if (existingDonHangChiTiet.isPresent()) {
            DonHangChiTiet updatedDonHangChiTiet = existingDonHangChiTiet.get();
            updatedDonHangChiTiet.setSoLuong(updatedDonHangChiTiet.getSoLuong() + donHangChiTiet.getSoLuong());
            updatedDonHangChiTiet.setDonGia(spct.getGiaBan()); // Cập nhật giá sản phẩm
            updatedDonHangChiTiet.setNgaySua(LocalDateTime.now());
            DonHangChiTiet savedDonHangChiTiet = donHangChiTietRepo.save(updatedDonHangChiTiet);
            return ResponseEntity.ok(savedDonHangChiTiet);
        } else {
            donHangChiTiet.setDonHang(donHang);
            donHangChiTiet.setSanPhamChiTiet(spct);
            donHangChiTiet.setDonGia(spct.getGiaBan());
            donHangChiTiet.setNgayTao(LocalDateTime.now());
            DonHangChiTiet savedDonHangChiTiet = donHangChiTietRepo.save(donHangChiTiet);
            return ResponseEntity.ok(savedDonHangChiTiet);
        }
    }


    // Cập nhật chi tiết đơn hàng
//    @PutMapping("/{id}")
//    public ResponseEntity<DonHangChiTiet> updateDonHangChiTiet(@PathVariable Integer id, @RequestBody DonHangChiTiet donHangChiTiet) {
//        Optional<DonHangChiTiet> existingDonHangChiTiet = donHangChiTietRepo.findById(id);
//        if (existingDonHangChiTiet.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // Cập nhật thông tin chi tiết đơn hàng
//        DonHangChiTiet updatedDonHangChiTiet = existingDonHangChiTiet.get();
//        updatedDonHangChiTiet.setSoLuong(donHangChiTiet.getSoLuong());
//        updatedDonHangChiTiet.setDonGia(donHangChiTiet.getDonGia());
//        updatedDonHangChiTiet.setTrangThai(donHangChiTiet.getTrangThai());
//        updatedDonHangChiTiet.setNgaySua(LocalDateTime.now());
//
//        DonHangChiTiet savedDonHangChiTiet = donHangChiTietRepo.save(updatedDonHangChiTiet);
//        return ResponseEntity.ok(savedDonHangChiTiet);
//    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDonHangChiTietQuantity(@PathVariable Integer id, @RequestBody DonHangChiTiet updatedItem) {
        try {
            // Lấy chi tiết đơn hàng cũ
            Optional<DonHangChiTiet> existingItem = donHangChiTietRepo.findById(id);

            if (existingItem.isEmpty()) {
                return ResponseEntity.notFound().build();  // Trả về 404 nếu không tìm thấy
            }

            // Cập nhật số lượng của đơn hàng chi tiết
            DonHangChiTiet itemToUpdate = existingItem.get();
            itemToUpdate.setSoLuong(updatedItem.getSoLuong());  // Cập nhật số lượng của chi tiết đơn hàng
            donHangChiTietRepo.save(itemToUpdate);  // Lưu lại item đã cập nhật

            // Trả về item đã cập nhật
            return ResponseEntity.ok(itemToUpdate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi cập nhật số lượng.");
        }
    }


    // Xóa chi tiết đơn hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDonHangChiTiet(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("Invalid ID provided.");
        }
        if (donHangChiTietRepo.existsById(id)) {
            donHangChiTietRepo.deleteById(id);
            return ResponseEntity.ok("Chi tiết đơn hàng đã được xóa.");
        }
        return ResponseEntity.notFound().build();
    }


}
