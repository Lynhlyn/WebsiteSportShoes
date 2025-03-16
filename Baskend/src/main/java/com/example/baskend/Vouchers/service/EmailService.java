package com.example.baskend.Vouchers.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(List<String> recipients, String subject, String message) {
        try {
            for (String recipient : recipients) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setFrom("maiththu2004@gmail.com");
                helper.setTo(recipient);
                helper.setSubject(subject);
                helper.setText(message, true); // true để hỗ trợ HTML

                mailSender.send(mimeMessage);
            }
            System.out.println("✅ Email đã được gửi thành công đến: " + recipients);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
