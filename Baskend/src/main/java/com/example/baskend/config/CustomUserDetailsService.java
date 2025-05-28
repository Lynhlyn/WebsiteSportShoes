package com.example.baskend.config;

import com.example.baskend.TaiKhoan.Entity.TaiKhoan;
import com.example.baskend.TaiKhoan.Repository.TaiKhoanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TaiKhoanRepo taiKhoanRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(username);
        if (taiKhoan == null) {
            throw new UsernameNotFoundException("User not found");
        }

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + taiKhoan.getVaiTro().getTenVaiTro().toUpperCase())
        );

        return new org.springframework.security.core.userdetails.User(
                taiKhoan.getTenDangNhap(),
                taiKhoan.getMatKhau(),
                taiKhoan.getTrangThai(),
                true,
                true,
                true,
                authorities
        );
    }
}
