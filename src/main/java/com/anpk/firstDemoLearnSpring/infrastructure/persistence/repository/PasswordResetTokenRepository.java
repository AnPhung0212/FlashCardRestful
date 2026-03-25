package com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.anpk.firstDemoLearnSpring.domain.Entity.PasswordResetToken;
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    // Tìm token theo chuỗi token
    Optional<PasswordResetToken> findByToken(String token);
    // Xóa token sau khi sử dụng hoặc hết hạn
    void deleteByToken(String token);
    // Xóa tất cả token của một User (nếu cần)
    void deleteByUserId(UUID userId);

}
