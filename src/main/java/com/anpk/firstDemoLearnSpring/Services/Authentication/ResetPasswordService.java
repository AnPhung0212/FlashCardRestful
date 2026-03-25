package com.anpk.firstDemoLearnSpring.Services.Authentication;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.anpk.firstDemoLearnSpring.Services.MailService.VerificationMailService;
import com.anpk.firstDemoLearnSpring.domain.Entity.PasswordResetToken;
import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Authentication.ResetPasswordRequest;
import com.anpk.firstDemoLearnSpring.helpers.common.PasswordHasher;
import com.anpk.firstDemoLearnSpring.helpers.common.TokenGenerator;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.PasswordResetTokenRepository;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final VerificationMailService mailService;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator; // giống RegisterService


    // 1. Gửi email reset password
    @Transactional
    public void requestResetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Tạo token mới
        String token = tokenGenerator.generateVerificationToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(15));
        tokenRepository.saveAndFlush(resetToken);

        // Gửi email
               mailService.sendResetPasswordEmail(
                user.getEmail(),
                user.getUsername(),
                token
        );
    }

    // 2. Đổi mật khẩu bằng token
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String newPassword = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        String token = request.getToken();

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }
        User user = resetToken.getUser();

         user.setPasswordHash(passwordHasher.hash(newPassword)); // Đúng với entity User của bạn
         userRepository.save(user);

        // Xóa token sau khi dùng
        tokenRepository.deleteByToken(token);
    }

}
