package com.anpk.firstDemoLearnSpring.Services.MailService;

import com.anpk.firstDemoLearnSpring.infrastructure.config.models.MailProperties;
import com.anpk.firstDemoLearnSpring.infrastructure.templates.RegisterEmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationMailService {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    // Hàm gửi email xác thực cho người dùng mới đăng ký
    public void sendVerificationEmail(String recipientEmail, String recipientName, String token) {
        Assert.hasText(recipientEmail, "recipientEmail must not be blank");
        Assert.hasText(token, "token must not be blank");

        String verificationUrl = buildVerificationUrl(token);
        String fromAddress = mailProperties.getFrom();
        Assert.hasText(fromAddress, "Missing mail sender address (app.mail.from)");

        String body = RegisterEmailTemplate.render(recipientName, verificationUrl);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setFrom(fromAddress);
            helper.setTo(recipientEmail);
            helper.setSubject("Xác thực tài khoản");
            helper.setText(body, true);
            mailSender.send(mimeMessage);
            log.info("Email xác thực đã được gửi đến: {}", recipientEmail);
        } catch (MessagingException | MailException ex) {
            log.error("Lỗi khi gửi email xác thực đến {}: {}", recipientEmail, ex.getMessage(), ex);
            throw new IllegalStateException("Không thể gửi email xác thực: " + ex.getMessage(), ex);
        }
    }
    // Hàm xây dựng URL xác thực dựa trên token để cho vào email gửi cho người dùng xác thực.
    private String buildVerificationUrl(String token) {
        String baseUrl = mailProperties.getBaseUrl();
        Assert.hasText(baseUrl, "Missing base URL (app.mail.base-url)");
        // Đường dẫn xác thực tài khoản
        String verifyPath = "/api/auth/verify";
        String url = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        url += verifyPath;
        return url + "?token=" + token;
    }

    // Hàm gửi email đặt lại mật khẩu cho người dùng khi họ yêu cầu reset password
    public void sendResetPasswordEmail(String recipientEmail, String recipientName, String token) {
        Assert.hasText(recipientEmail, "recipientEmail must not be blank");
        Assert.hasText(token, "token must not be blank");

        String resetUrl = buildResetPasswordUrl(token);
        String fromAddress = mailProperties.getFrom();
        Assert.hasText(fromAddress, "Missing mail sender address (app.mail.from)");

        // Sử dụng template ResetPasswordEmailTemplate
        String body = com.anpk.firstDemoLearnSpring.infrastructure.templates.ResetPasswordEmailTemplate.render(recipientName, resetUrl);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setFrom(fromAddress);
            helper.setTo(recipientEmail);
            helper.setSubject("Đặt lại mật khẩu");
            helper.setText(body, true);
            mailSender.send(mimeMessage);
            log.info("Email reset password đã được gửi đến: {}", recipientEmail);
        } catch (MessagingException | MailException ex) {
            log.error("Lỗi khi gửi email reset password đến {}: {}", recipientEmail, ex.getMessage(), ex);
            throw new IllegalStateException("Không thể gửi email reset password: " + ex.getMessage(), ex);
        }
}

// Hàm xây dựng URL đặt lại mật khẩu dựa trên token để cho vào email gửi cho người dùng khi họ yêu cầu reset password.
private String buildResetPasswordUrl(String token) {
    String baseUrl = mailProperties.getBaseUrl();
    Assert.hasText(baseUrl, "Missing base URL (app.mail.base-url)");
    // Đường dẫn xác nhận đặt lại mật khẩu
    String resetPath = "/api/auth/reset-password/confirm";
    String url = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    url += resetPath;
    return url + "?token=" + token;
}

}
