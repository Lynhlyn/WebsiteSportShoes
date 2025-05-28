package com.example.baskend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(
    basePackages = "com.example.baskend",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "com.example.baskend.NhanVien.configuration.*"
        )
    }
)
public class BaskendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaskendApplication.class, args);
    }
}
