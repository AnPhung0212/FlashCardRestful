package com.anpk.firstDemoLearnSpring.domain.Entity;

import com.anpk.firstDemoLearnSpring.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_verification_tokens")
/// Dùng để lưu token giúp xác thực email của user sau khi đăng ký
public class EmailVerificationToken extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
