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

    private String buildVerificationUrl(String token) {
        String baseUrl = mailProperties.getVerificationBaseUrl();
        Assert.hasText(baseUrl, "Missing verification URL (app.mail.verification-base-url)");
        return baseUrl.contains("?") ? baseUrl + "&token=" + token : baseUrl + "?token=" + token;
    }
}
