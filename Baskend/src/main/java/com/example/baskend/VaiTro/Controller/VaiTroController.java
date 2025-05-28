package com.example.baskend.VaiTro.Controller;

import com.example.baskend.VaiTro.Entity.VaiTro;
import com.example.baskend.VaiTro.Repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/vai-tro")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VaiTroController {
    private final VaiTroRepository vaiTroRepo;
    @GetMapping()
    public ResponseEntity<?> getAllVaiTro() {
        return ResponseEntity.ok(vaiTroRepo.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getVaiTroById(@PathVariable Integer id) {
        Optional<VaiTro> vaiTro = vaiTroRepo.findById(id);
        if (vaiTro.isPresent()) {
            return ResponseEntity.ok(vaiTro.get());
        } else {
            return ResponseEntity.status(404).body("Vai trò không tồn tại!");
        }
    }

}
