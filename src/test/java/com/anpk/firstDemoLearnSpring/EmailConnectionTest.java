package com.anpk.firstDemoLearnSpring;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test kết nối email để kiểm tra App Password có đúng không
 * Chạy test này để debug lỗi "Authentication failed"
 */
@SpringBootTest
@ActiveProfiles("local")
class EmailConnectionTest {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Test gửi email đơn giản để kiểm tra cấu hình
     * Thay "your-test-email@gmail.com" bằng email của bạn để nhận email test
     */
    @Test
    void testEmailConnection() throws MessagingException {
        System.out.println("=== BẮT ĐẦU TEST KẾT NỐI EMAIL ===");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("flashcard2300@gmail.com");
            helper.setTo("flashcard2300@gmail.com"); // Gửi cho chính mình để test
            helper.setSubject("Test Connection - Spring Boot");
            helper.setText("✅ Email configuration is working correctly!", false);

            System.out.println("Đang gửi email test...");
            mailSender.send(message);
            System.out.println("✅ GỬI EMAIL THÀNH CÔNG!");
            System.out.println("✅ App Password hoạt động bình thường");
            System.out.println("✅ Cấu hình SMTP đúng");

        } catch (Exception e) {
            System.err.println("❌ LỖI KHI GỬI EMAIL:");
            System.err.println("Loại lỗi: " + e.getClass().getSimpleName());
            System.err.println("Chi tiết: " + e.getMessage());

            // Phân tích lỗi phổ biến
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                if (errorMsg.contains("Authentication failed") || errorMsg.contains("535")) {
                    System.err.println("\n🔧 NGUYÊN NHÂN:");
                    System.err.println("- App Password SAI hoặc đã hết hiệu lực");
                    System.err.println("- Chưa bật 2-Step Verification");
                    System.err.println("\n📝 GIẢI PHÁP:");
                    System.err.println("1. Vào https://myaccount.google.com/security");
                    System.err.println("2. Bật 2-Step Verification");
                    System.err.println("3. Vào https://myaccount.google.com/apppasswords");
                    System.err.println("4. Tạo App Password mới");
                    System.err.println("5. Copy 16 ký tự → Paste vào application-local.properties");
                    System.err.println("6. Restart app và chạy lại test này");

                } else if (errorMsg.contains("Connection timed out") || errorMsg.contains("timeout")) {
                    System.err.println("\n🔧 NGUYÊN NHÂN:");
                    System.err.println("- Firewall/Antivirus chặn port 587");
                    System.err.println("- Không có kết nối internet");
                    System.err.println("\n📝 GIẢI PHÁP:");
                    System.err.println("1. Tắt tạm Firewall/Antivirus");
                    System.err.println("2. Kiểm tra kết nối internet");
                    System.err.println("3. Test port: Test-NetConnection smtp.gmail.com -Port 587");

                } else if (errorMsg.contains("Unknown SMTP host")) {
                    System.err.println("\n🔧 NGUYÊN NHÂN:");
                    System.err.println("- Sai cấu hình spring.mail.host");
                    System.err.println("\n📝 GIẢI PHÁP:");
                    System.err.println("Kiểm tra application.properties:");
                    System.err.println("spring.mail.host=smtp.gmail.com");
                }
            }

            throw e; // Re-throw để test fail
        }

        System.out.println("=== KẾT THÚC TEST ===");
    }
}

