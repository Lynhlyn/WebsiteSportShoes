package com.example.baskend.SanPham.QuanLySanPham.controller;

import com.example.baskend.SanPham.QuanLySanPham.response.SanPhamThuocTinhResponse;
import com.example.baskend.SanPham.QuanLySanPham.entity.*;
import com.example.baskend.SanPham.QuanLySanPham.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/san-pham-thuoc-tinh")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SanPhamThuocTinhController {
    private final SanPhamThuocTinhRepo sanPhamThuocTinhRepo;
    private final SanPhamRepo sanPhamRepo;
    private final ChatLieuRepo chatLieuRepo;
    private final DeGiayRepo deGiayRepo;
    private final DanhMucRepo danhMucRepo;
    private final ThuongHieuRepo thuongHieuRepo;

    // Thêm thuộc tính vào sản phẩm
    @PostMapping("/add")
    public ResponseEntity<?> addThuocTinh(
            @RequestParam Integer sanPhamId,
            @RequestParam Integer chatLieuId,
            @RequestParam Integer deGiayId,
            @RequestParam Integer danhMucId,
            @RequestParam Integer thuongHieuId) {

        Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(sanPhamId);
        Optional<ChatLieu> chatLieuOpt = chatLieuRepo.findById(chatLieuId);
        Optional<DeGiay> deGiayOpt = deGiayRepo.findById(deGiayId);
        Optional<DanhMuc> danhMucOpt = danhMucRepo.findById(danhMucId);
        Optional<ThuongHieu> thuongHieuOpt = thuongHieuRepo.findById(thuongHieuId);

        if (sanPhamOpt.isEmpty() || chatLieuOpt.isEmpty() || deGiayOpt.isEmpty() || danhMucOpt.isEmpty()
                || thuongHieuOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Sản phẩm hoặc thuộc tính không hợp lệ!");
        }

        SanPhamThuocTinh thuocTinh = new SanPhamThuocTinh();
        thuocTinh.setSanPham(sanPhamOpt.get());
        thuocTinh.setChatLieu(chatLieuOpt.get());
        thuocTinh.setDeGiay(deGiayOpt.get());
        thuocTinh.setDanhMuc(danhMucOpt.get());
        thuocTinh.setThuongHieu(thuongHieuOpt.get());

        SanPhamThuocTinh savedThuocTinh = sanPhamThuocTinhRepo.save(thuocTinh);
        return ResponseEntity.ok(convertToResponse(savedThuocTinh));
    }

    // Lấy danh sách thuộc tính của sản phẩm
    @GetMapping("/{sanPhamId}")
    public ResponseEntity<List<SanPhamThuocTinhResponse>> getThuocTinhBySanPham(@PathVariable Integer sanPhamId) {
        List<SanPhamThuocTinh> thuocTinhs = sanPhamThuocTinhRepo.findBySanPhamId(sanPhamId);
        List<SanPhamThuocTinhResponse> responseList = thuocTinhs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // Cập nhật thuộc tính của sản phẩm
    @PutMapping("/update/{thuocTinhId}")
    public ResponseEntity<?> updateThuocTinh(
            @PathVariable Integer thuocTinhId,
            @RequestParam Integer chatLieuId,
            @RequestParam Integer deGiayId,
            @RequestParam Integer danhMucId,
            @RequestParam Integer thuongHieuId) {

        Optional<SanPhamThuocTinh> thuocTinhOpt = sanPhamThuocTinhRepo.findById(thuocTinhId);
        if (thuocTinhOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Thuộc tính sản phẩm không tồn tại!");
        }

        Optional<ChatLieu> chatLieuOpt = chatLieuRepo.findById(chatLieuId);
        Optional<DeGiay> deGiayOpt = deGiayRepo.findById(deGiayId);
        Optional<DanhMuc> danhMucOpt = danhMucRepo.findById(danhMucId);
        Optional<ThuongHieu> thuongHieuOpt = thuongHieuRepo.findById(thuongHieuId);

        if (chatLieuOpt.isEmpty() || deGiayOpt.isEmpty() || danhMucOpt.isEmpty() || thuongHieuOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Dữ liệu thuộc tính không hợp lệ!");
        }

        SanPhamThuocTinh thuocTinh = thuocTinhOpt.get();
        thuocTinh.setChatLieu(chatLieuOpt.get());
        thuocTinh.setDeGiay(deGiayOpt.get());
        thuocTinh.setDanhMuc(danhMucOpt.get());
        thuocTinh.setThuongHieu(thuongHieuOpt.get());

        SanPhamThuocTinh updatedThuocTinh = sanPhamThuocTinhRepo.save(thuocTinh);
        return ResponseEntity.ok(convertToResponse(updatedThuocTinh));
    }

    // Xóa thuộc tính của sản phẩm
    @DeleteMapping("/delete/{thuocTinhId}")
    public ResponseEntity<String> deleteThuocTinh(@PathVariable Integer thuocTinhId) {
        if (!sanPhamThuocTinhRepo.existsById(thuocTinhId)) {
            return ResponseEntity.badRequest().body("Thuộc tính không tồn tại!");
        }
        sanPhamThuocTinhRepo.deleteById(thuocTinhId);
        return ResponseEntity.ok("Xóa thuộc tính thành công!");
    }

    // Chuyển đổi entity sang DTO
    private SanPhamThuocTinhResponse convertToResponse(SanPhamThuocTinh thuocTinh) {
        return new SanPhamThuocTinhResponse(
                thuocTinh.getId(),
                thuocTinh.getChatLieu().getTenChatLieu(),
                thuocTinh.getDeGiay().getTenDeGiay(),
                thuocTinh.getDanhMuc().getTenDanhMuc(),
                thuocTinh.getThuongHieu().getTenThuongHieu(),
                thuocTinh.getSanPham().getId(),
                thuocTinh.getSanPham().getTenSanPham());
    }
}
