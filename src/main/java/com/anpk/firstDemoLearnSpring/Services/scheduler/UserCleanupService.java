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

        // Tim tat ca user PENDING da tao qua 24 gio
        List<User> expiredUsers = userRepository.findByStatusAndCreatedAtBefore(
                UserStatus.PENDING,
                threshold
        );

        if (expiredUsers.isEmpty()) {
            log.info("Khong co user PENDING nao qua han");
            return 0;
        }
        tokenRepository.deleteAllByUserIn(expiredUsers);

        // Log thong tin cac user se bi xoa
        expiredUsers.forEach(user ->
            log.debug("Xoa user PENDING qua han: {} (email: {}, created: {})",
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
            )
        );

        // Xoa user (cascade se tu dong xoa verification token lien quan)
        userRepository.deleteAll(expiredUsers);

        int deletedCount = expiredUsers.size();
        log.info("Da xoa {} user PENDING qua 24 gio", deletedCount);

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

        // Tim tat ca token da het han
        List<EmailVerificationToken> expiredTokens = tokenRepository.findByExpiredAtBefore(now);

        if (expiredTokens.isEmpty()) {
            log.info("Khong co verification token nao da het han");
            return 0;
        }

        // Log thong tin
        log.debug("Tim thay {} verification token da het han", expiredTokens.size());

        // Xoa token
        tokenRepository.deleteAll(expiredTokens);

        int deletedCount = expiredTokens.size();
        log.info("Da xoa {} verification token da het han", deletedCount);

        return deletedCount;
    }
}

