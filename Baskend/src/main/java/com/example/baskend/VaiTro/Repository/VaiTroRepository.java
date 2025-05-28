package com.example.baskend.VaiTro.Repository;

import com.example.baskend.VaiTro.Entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VaiTroRepository extends JpaRepository<VaiTro, Integer> {
     Optional<VaiTro> findByTenVaiTro(String tenVaiTro);


}
