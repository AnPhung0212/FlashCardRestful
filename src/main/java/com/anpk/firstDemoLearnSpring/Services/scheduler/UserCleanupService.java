package com.anpk.firstDemoLearnSpring.Services.scheduler;

import com.anpk.firstDemoLearnSpring.domain.Entity.EmailVerificationToken;
import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserStatus;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.EmailVerificationTokenRepository;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service để xử lý việc xóa các user và token đã hết hạn
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserCleanupService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;

    /**
     * Xóa tất cả user có status PENDING và đã được tạo quá 24 giờ
     *
     * @return Số lượng user đã bị xóa
     */
    @Transactional
    public int deleteExpiredPendingUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);

        // Tìm tất cả user PENDING đã tạo quá 24 giờ
        List<User> expiredUsers = userRepository.findByStatusAndCreatedAtBefore(
                UserStatus.PENDING,
                threshold
        );

        if (expiredUsers.isEmpty()) {
            log.info("Không có user PENDING nào quá hạn");
            return 0;
        }
        tokenRepository.deleteAllByUserIn(expiredUsers);

        // Log thông tin các user sẽ bị xóa
        expiredUsers.forEach(user ->
            log.debug("Xóa user PENDING quá hạn: {} (email: {}, created: {})",
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
            )
        );

        // Xóa user (cascade sẽ tự động xóa verification token liên quan)
        userRepository.deleteAll(expiredUsers);

        int deletedCount = expiredUsers.size();
        log.info("Đã xóa {} user PENDING quá 24 giờ", deletedCount);

        return deletedCount;
    }

    /**
     * Xóa tất cả verification token đã hết hạn (expiredAt < now)
     *
     * @return Số lượng token đã bị xóa
     */
    @Transactional
    public int deleteExpiredVerificationTokens() {
        LocalDateTime now = LocalDateTime.now();

        // Tìm tất cả token đã hết hạn
        List<EmailVerificationToken> expiredTokens = tokenRepository.findByExpiredAtBefore(now);

        if (expiredTokens.isEmpty()) {
            log.info("Không có verification token nào đã hết hạn");
            return 0;
        }

        // Log thông tin
        log.debug("Tìm thấy {} verification token đã hết hạn", expiredTokens.size());

        // Xóa token
        tokenRepository.deleteAll(expiredTokens);

        int deletedCount = expiredTokens.size();
        log.info("Đã xóa {} verification token đã hết hạn", deletedCount);

        return deletedCount;
    }
}

