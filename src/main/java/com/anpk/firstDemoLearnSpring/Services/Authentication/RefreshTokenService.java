package com.anpk.firstDemoLearnSpring.Services.Authentication;

import com.anpk.firstDemoLearnSpring.domain.Entity.RefreshToken;
import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.RefreshTokenRepository;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    // Lấy thời gian sống từ application.properties (ví dụ: 7 ngày)
    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // 1. Tìm token trong Database
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // 2. Tạo mới Refresh Token cho User khi Login thành công
    @Transactional
    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Logic quan trọng: Xóa bỏ các token cũ của user này trước khi tạo cái mới
        // Việc này giúp tránh rác DB và đảm bảo mỗi user chỉ có 1 phiên đăng nhập active
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString()) // Tạo chuỗi ngẫu nhiên 36 ký tự
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // 3. Kiểm tra xem Token đã hết hạn chưa
    public RefreshToken verifyExpiration(RefreshToken token) {
        // Nếu thời gian hết hạn < thời gian hiện tại
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token); // Xóa luôn token hết hạn khỏi DB
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    // 4. Xóa token (Dùng khi Logout)
    @Transactional
    public void deleteByUserId(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        refreshTokenRepository.deleteByUser(user);
    }
}
