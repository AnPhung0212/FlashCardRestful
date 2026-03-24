package com.anpk.firstDemoLearnSpring.domain.Entity;

import com.anpk.firstDemoLearnSpring.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String token;

    // Instant là chuẩn của Java 8+ để lưu timestamp (tương đương UTC trong DB)
    @Column(nullable = false)
    private Instant expiryDate;

    // Quan hệ N-1: Một User có thể có nhiều Refresh Token (đăng nhập nhiều thiết bị)
    // FetchType.LAZY giúp tăng hiệu năng, chỉ query bảng User khi thực sự cần gọi refreshToken.getUser()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
