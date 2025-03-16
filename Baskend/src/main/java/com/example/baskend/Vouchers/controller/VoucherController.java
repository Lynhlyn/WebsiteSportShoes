package com.example.baskend.Vouchers.controller;

import com.example.baskend.Vouchers.entity.KhachHangEntity;
import com.example.baskend.Vouchers.entity.Voucher;
import com.example.baskend.Vouchers.repository.KhachHangVCRepository;
import com.example.baskend.Vouchers.repository.VoucherRepository;
import com.example.baskend.Vouchers.service.EmailService;
import com.example.baskend.Vouchers.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")

public class VoucherController {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    KhachHangVCRepository khachHangRepository;
    @Autowired
    private EmailService emailService;
    @GetMapping("/voucher")
    public List<Voucher> getAllVouchers() {
        return voucherService.getAllVouchers();
    }
    @GetMapping("/voucher/status")
    public ResponseEntity<List<Voucher>> getVouchersByStatus(@RequestParam Boolean status) {
        List<Voucher> vouchers = voucherService.getVouchersByStatus(status);
        return ResponseEntity.ok(vouchers);
    }
    @GetMapping("/customers")
    public ResponseEntity<List<KhachHangEntity>> getAllCustomers() {
        List<KhachHangEntity> customers = voucherService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search-customers")
    public Page<KhachHangEntity> searchCustomers(
            @RequestParam(value = "hoTen", required = false) String ten,
            @RequestParam(value = "soDienThoai", required = false) String sdt,
            @RequestParam(value = "email", required = false) String email,
            Pageable pageable) {

        // Nếu cả 3 tham số đều rỗng, trả về danh sách rỗng
        if ((ten == null || ten.trim().isEmpty()) &&
                (sdt == null || sdt.trim().isEmpty()) &&
                (email == null || email.trim().isEmpty())) {
            return Page.empty();
        }

        return khachHangRepository.searchCustomers(ten, sdt, email, pageable);
    }


    @PostMapping("/add-voucher")
    public ResponseEntity<?> createVoucher(@RequestBody Map<String, Object> requestBody) {
        try {
            Voucher voucher = new Voucher();
            voucher.setMaVoucher(Objects.toString(requestBody.get("maVoucher"), ""));
            voucher.setMoTa(Objects.toString(requestBody.get("moTa"), ""));

            String ngayBatDauStr = Objects.toString(requestBody.get("ngayBatDau"), null);
            String ngayKetThucStr = Objects.toString(requestBody.get("ngayKetThuc"), null);
            String giaTriGiam = Objects.toString(requestBody.get("giaTriGiam"), "0");
            String giamToiDa = Objects.toString(requestBody.get("giamToiDa"), "0"); // Giá trị mặc định là 0
            String giaTriToiThieu = Objects.toString(requestBody.get("giaTriToiThieu"), "0");
            String loaiVoucher = Objects.toString(requestBody.get("loaiVoucher"), "0");

            if (ngayBatDauStr != null && ngayKetThucStr != null) {
                Instant ngayBatDauInstant = Instant.parse(ngayBatDauStr);
                Instant ngayKetThucInstant = Instant.parse(ngayKetThucStr);
                voucher.setNgayBatDau(LocalDateTime.ofInstant(ngayBatDauInstant, ZoneId.systemDefault()));
                voucher.setNgayKetThuc(LocalDateTime.ofInstant(ngayKetThucInstant, ZoneId.systemDefault()));
            } else {
                return ResponseEntity.badRequest().body("Ngày bắt đầu và ngày kết thúc không được để trống.");
            }

            voucher.setGiaTriGiam(Double.valueOf(giaTriGiam));
            voucher.setGiaTriToiDa(Double.valueOf(giamToiDa));
            voucher.setGiaTriToiThieu(Double.valueOf(giaTriToiThieu));
            voucher.setLoaiVoucher(Integer.valueOf(loaiVoucher));
            voucher.setNgayTao(LocalDateTime.now());
            voucher.setNgaySua(LocalDateTime.now());
            voucher.setTrangThai(true);
            voucher.setSoLuong(Integer.parseInt(requestBody.get("soLuong").toString()));
            voucher.setKieuVoucher(requestBody.get("kieuVoucher").toString());

            Voucher savedVoucher = voucherService.saveVoucher(voucher);

            if ("private".equals(voucher.getKieuVoucher())) {
                List<Integer> selectedCustomerIds = (List<Integer>) requestBody.get("customers");
                if (selectedCustomerIds != null && !selectedCustomerIds.isEmpty()) {
                    voucherService.createVoucher(savedVoucher, selectedCustomerIds);

                    // Gửi email
                    List<String> recipientEmails = khachHangRepository.findAllById(selectedCustomerIds)
                            .stream()
                            .map(KhachHangEntity::getEmail)
                            .collect(Collectors.toList());

                    String subject = "Bạn vừa nhận được một voucher mới!";
                    String message = "<h3>Xin chào,</h3>" +
                            "<p>Bạn đã được cấp một voucher với mã: <b>" + voucher.getMaVoucher() + "</b></p>" +
                            "<p>Giá trị giảm: <b>" + voucher.getGiaTriGiam() + "</b></p>" +
                            "<p>Áp dụng từ: " + voucher.getNgayBatDau() +
                            " đến " + voucher.getNgayKetThuc() + "</p>" +
                            "<p><b>Chúc bạn mua sắm vui vẻ!</b></p>";

                    emailService.sendEmail(recipientEmails, subject, message);
                } else {
                    return ResponseEntity.badRequest().body("Vui lòng chọn ít nhất một khách hàng!");
                }
            }

            return ResponseEntity.ok(savedVoucher);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi khi thêm voucher: " + e.getMessage());
        }
    }

    @PutMapping("/voucher/{id}/status")
    public ResponseEntity<?> updateVoucherStatus(@PathVariable int id, @RequestBody Map<String, Boolean> requestBody) {
        Boolean trangThai = requestBody.get("trangThai");

        if (trangThai == null) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ!");
        }

        boolean isUpdated = voucherService.updateVoucherStatus(id, trangThai);
        if (isUpdated) {
            // Trả về thông tin voucher mới để frontend cập nhật
            Optional<Voucher> updatedVoucher = voucherRepository.findById(id);
            return ResponseEntity.ok(updatedVoucher.orElse(null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher không tồn tại!");
        }
    }

    // Lấy thông tin voucher theo id
    @GetMapping("/voucher/{id}")
    public ResponseEntity<Voucher> getVoucherById(@PathVariable int id) {
        Optional<Voucher> voucher = voucherRepository.findById(id);
        if (voucher.isPresent()) {
            return ResponseEntity.ok(voucher.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Cập nhật voucher
    @PutMapping("/voucher/{id}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable int id, @RequestBody Voucher voucherDetails) {
        Optional<Voucher> existingVoucher = voucherRepository.findById(id);
        if (existingVoucher.isPresent()) {
            Voucher voucher = existingVoucher.get();

            // Cập nhật tất cả các trường của voucher
            voucher.setMaVoucher(voucherDetails.getMaVoucher());
            voucher.setMoTa(voucherDetails.getMoTa());
            voucher.setGiaTriGiam(voucherDetails.getGiaTriGiam());
            voucher.setGiaTriToiThieu(voucherDetails.getGiaTriToiThieu());
            voucher.setNgayBatDau(voucherDetails.getNgayBatDau());
            voucher.setNgayKetThuc(voucherDetails.getNgayKetThuc());
            voucher.setLoaiVoucher(voucherDetails.getLoaiVoucher());
            voucher.setSoLuong(voucherDetails.getSoLuong());
            voucher.setKieuVoucher(voucherDetails.getKieuVoucher());
            voucher.setTrangThai(voucherDetails.getTrangThai());
            voucher.setGiaTriToiDa(voucher.getGiaTriToiDa());
            // Lưu voucher đã được cập nhật
            voucherRepository.save(voucher);

            return ResponseEntity.ok(voucher);  // Trả về voucher đã được cập nhật
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Nếu không tìm thấy voucher
        }
    }

    @GetMapping("/searchByMaAndMoTa")
    public Page<Voucher> searchByMaAndMoTa(
            @RequestParam(value = "maVoucher", required = false) String maVoucher,
            @RequestParam(value = "moTa", required = false) String moTa,
            Pageable pageable) {

        return voucherRepository.searchByMaAndMoTa(maVoucher, moTa, pageable);
    }

    // Tìm kiếm theo các trường còn lại (kiểu voucher, trạng thái, ngày bắt đầu, ngày kết thúc)
    @GetMapping("/searchOtherFields")
    public Page<Voucher> searchOtherFields(
            @RequestParam(value = "tuNgay", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tuNgay,
            @RequestParam(value = "denNgay", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate denNgay,
            @RequestParam(value = "loaiVoucher", required = false) Boolean loaiVoucher,
            @RequestParam(value = "kieuVoucher", required = false) String kieuVoucher,
            @RequestParam(value = "trangThai", required = false) Boolean trangThai,
            Pageable pageable) {

        // Chuyển LocalDate thành LocalDateTime
        LocalDateTime tuNgayTime = (tuNgay != null) ? tuNgay.atStartOfDay() : null;
        LocalDateTime denNgayTime = (denNgay != null) ? denNgay.atTime(23, 59, 59) : null;

        return voucherRepository.searchOtherFields(tuNgayTime, denNgayTime, loaiVoucher, kieuVoucher, trangThai, pageable);
    }

    @DeleteMapping("/voucher/{id}")
    public ResponseEntity<?> deleteVoucher(@PathVariable int id) {
        Optional<Voucher> voucherOptional = voucherRepository.findById(id);

        if (voucherOptional.isPresent()) {
            voucherRepository.deleteById(id);
            return ResponseEntity.ok("Xóa voucher thành công!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher không tồn tại!");
        }
    }


}
