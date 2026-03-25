package com.anpk.firstDemoLearnSpring.domain.Entity;


import java.time.LocalDateTime;
import java.util.UUID;

import com.anpk.firstDemoLearnSpring.domain.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "password_reset_tokens")
public class PasswordResetToken extends BaseEntity {
     private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    
    private LocalDateTime expiryTime;
}
