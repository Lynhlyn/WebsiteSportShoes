package com.example.baskend.SanPham.QuanLySanPham.controller;

import com.example.baskend.SanPham.QuanLySanPham.entity.ChatLieu;
import com.example.baskend.SanPham.QuanLySanPham.repository.ChatLieuRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chat-lieu")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatLieuController {
    private final ChatLieuRepo chatLieuRepo;

    /**
     * Get all ChatLieu
     */
    @GetMapping("")
    public ResponseEntity<List<ChatLieu>> getAllChatLieu() {
        List<ChatLieu> chatLieuList = chatLieuRepo.findAll();
        return ResponseEntity.ok(chatLieuList);
    }

    /**
     * Get ChatLieu by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChatLieu> getChatLieuById(@PathVariable Integer id) {
        Optional<ChatLieu> chatLieu = chatLieuRepo.findById(id);
        return chatLieu.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new ChatLieu
     */
    @PostMapping("/add")
    public ResponseEntity<String> createChatLieu(@Valid @RequestBody ChatLieu chatLieu) {
        chatLieuRepo.save(chatLieu);
        return ResponseEntity.ok("Thêm chất liệu thành công!");
    }

    /**
     * Update an existing ChatLieu
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateChatLieu(@PathVariable Integer id, @Valid @RequestBody ChatLieu chatLieuDetails) {
        Optional<ChatLieu> existingChatLieuOpt = chatLieuRepo.findById(id);
        if (existingChatLieuOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Chất liệu không tồn tại!");
        }

        ChatLieu existingChatLieu = existingChatLieuOpt.get();
        existingChatLieu.setTenChatLieu(chatLieuDetails.getTenChatLieu());
        existingChatLieu.setTrangThai(chatLieuDetails.getTrangThai());

        chatLieuRepo.save(existingChatLieu);
        return ResponseEntity.ok("Cập nhật chất liệu thành công!");
    }

    /**
     * Delete a ChatLieu
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChatLieu(@PathVariable Integer id) {
        if (!chatLieuRepo.existsById(id)) {
            return ResponseEntity.badRequest().body("Chất liệu không tồn tại!");
        }
        chatLieuRepo.deleteById(id);
        return ResponseEntity.ok("Xoá chất liệu thành công!");
    }
}
