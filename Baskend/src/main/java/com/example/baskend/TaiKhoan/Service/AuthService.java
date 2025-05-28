package com.example.baskend.TaiKhoan.Service;

import com.example.baskend.TaiKhoan.DTO.LoginRequest;
import com.example.baskend.TaiKhoan.DTO.LoginResponse;
import com.example.baskend.TaiKhoan.DTO.RegisterRequest;
import com.example.baskend.TaiKhoan.Entity.TaiKhoan;
import com.example.baskend.TaiKhoan.Repository.TaiKhoanRepo;
import com.example.baskend.VaiTro.Entity.VaiTro;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AuthService {

    @Autowired
    private TaiKhoanRepo taiKhoanRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        TaiKhoan taiKhoan = taiKhoanRepo.findByTenDangNhap(request.getTenDangNhap());

        if (taiKhoan == null) {
            throw new RuntimeException("Tài khoản không tồn tại");
        }

        if (!passwordEncoder.matches(request.getMatKhau(), taiKhoan.getMatKhau())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        if (!taiKhoan.getTrangThai()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        // Generate JWT token (you can implement this part based on your needs)
        String token = generateToken(taiKhoan);

        return new LoginResponse(
                taiKhoan.getId(),
                taiKhoan.getTenDangNhap(),
                taiKhoan.getVaiTro(),
                token
        );
    }

    private String generateToken(TaiKhoan taiKhoan) {
        // Implement your JWT token generation logic here
        return "dummy-token";
    }
    @PostConstruct
    public void encodeAllPlainPasswords() {
        List<TaiKhoan> accounts = taiKhoanRepo.findAll();
        for (TaiKhoan tk : accounts) {
            String password = tk.getMatKhau();
            // Nếu chưa mã hóa
            if (!password.startsWith("$2a$")) {
                tk.setMatKhau(passwordEncoder.encode(password));
                System.out.println("✅ Đã mã hóa mật khẩu cho user: " + tk.getTenDangNhap());
            }
        }
        taiKhoanRepo.saveAll(accounts);
    }
    public void register(RegisterRequest request) {
        if (taiKhoanRepo.findByTenDangNhap(request.getTenDangNhap()) != null) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        TaiKhoan newTaiKhoan = new TaiKhoan();
        newTaiKhoan.setTenDangNhap(request.getTenDangNhap());
        newTaiKhoan.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        newTaiKhoan.setTrangThai(true);

        // Gán mặc định vai trò là Khách hàng (id_vai_tro = 3)
        VaiTro role = new VaiTro();
        role.setId(3);
        newTaiKhoan.setVaiTro(role);

        taiKhoanRepo.save(newTaiKhoan);
    }

}