package com.example.baskend.Vouchers.service;


import com.example.baskend.Vouchers.entity.KhachHangEntity;
import com.example.baskend.Vouchers.entity.Voucher;
import com.example.baskend.Vouchers.entity.VoucherKhachHang;
import com.example.baskend.Vouchers.repository.KhachHangVCRepository;
import com.example.baskend.Vouchers.repository.VoucherKhachHangRepository;
import com.example.baskend.Vouchers.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    KhachHangVCRepository khachHangRepository;
    @Autowired
    VoucherKhachHangRepository voucherKhachHangRepository;
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }
    public List<KhachHangEntity> getAllCustomers() {
        return khachHangRepository.findAll();
    }
    public Voucher saveVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }
    public List<Voucher> getVouchersByType(String kieuVoucher) {
        return voucherRepository.findByKieuVoucher(kieuVoucher);
    }

    public Voucher createVoucher(Voucher voucher, List<Integer> khachHangIds) {
        // Lưu voucher vào DB trước
        voucher = voucherRepository.save(voucher);

        // Kiểm tra nếu là "public" thì không lưu danh sách khách hàng
        if ("public".equals(voucher.getKieuVoucher())) {
            return voucher;
        }

        // Kiểm tra nếu danh sách khách hàng có dữ liệu
        if (khachHangIds != null && !khachHangIds.isEmpty()) {
            for (Integer khachHangId : khachHangIds) {
                Optional<KhachHangEntity> optionalKhachHang = khachHangRepository.findById(khachHangId);
                if (optionalKhachHang.isPresent()) {
                    KhachHangEntity kh = optionalKhachHang.get();
                    VoucherKhachHang vkh = new VoucherKhachHang();
                    vkh.setVoucher(voucher);
                    vkh.setKhachHang(kh);
                    voucherKhachHangRepository.save(vkh); // Lưu vào DB
                } else {
                    System.out.println("Khách hàng ID " + khachHangId + " không tồn tại!");
                }
            }
        }

        return voucher;
    }

    public boolean updateVoucherStatus(int id, boolean trangThai) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
        if (optionalVoucher.isPresent()) {
            Voucher voucher = optionalVoucher.get();
            voucher.setTrangThai(trangThai);
            voucherRepository.save(voucher);
            return true;
        }
        return false;
    }


    public List<Voucher> getVouchersByStatus(Boolean statusFilter) {
        List<Voucher> vouchers = voucherRepository.findAll();

        if (statusFilter != null) {
            // Nếu lọc theo trạng thái, trả về các voucher có trạng thái tương ứng
            return vouchers.stream()
                    .filter(v -> v.getTrangThai().equals(statusFilter))
                    .collect(Collectors.toList());
        }

        // Nếu không có trạng thái lọc, trả về tất cả các voucher
        return vouchers;
    }

}
