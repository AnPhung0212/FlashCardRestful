package com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository;

import com.anpk.firstDemoLearnSpring.domain.Entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {
    Optional<EmailVerificationToken> findByToken(String token);

    // Tìm các token đã hết hạn (expiredAt < thời điểm hiện tại)
    List<EmailVerificationToken> findByExpiredAtBefore(LocalDateTime expiredAt);

    // Xóa tất cả token liên quan đến một danh sách user (dùng để xóa token của user PENDING quá hạn)
    @Modifying
    void deleteAllByUserIn(List<User> users);

}
