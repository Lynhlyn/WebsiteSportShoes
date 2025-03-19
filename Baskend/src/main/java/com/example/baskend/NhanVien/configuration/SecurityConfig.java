package com.example.baskend.NhanVien.configuration;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Cho phép tất cả truy cập
                .csrf(csrf -> csrf.disable()) // Tắt CSRF
                .cors(cors -> cors.disable()) // Tắt CORS
                .formLogin(Customizer.withDefaults()) // Giữ login nếu cần, có thể disable
                .httpBasic(Customizer.withDefaults()); // Giữ HTTP Basic nếu cần, có thể disable

        return http.build();
    }
}
