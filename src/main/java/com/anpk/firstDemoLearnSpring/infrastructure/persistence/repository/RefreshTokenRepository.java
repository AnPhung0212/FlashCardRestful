package com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository;

import com.anpk.firstDemoLearnSpring.domain.Entity.RefreshToken;
import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    // 1. Tìm kiếm token chuỗi UUID dưới DB
    Optional<RefreshToken> findByToken(String token);

    // 2. Xóa toàn bộ token của một User cụ thể
    // @Modifying là bắt buộc khi bạn dùng lệnh xóa/cập nhật tùy chỉnh
    @Modifying
    void deleteByUser(User user);
}
