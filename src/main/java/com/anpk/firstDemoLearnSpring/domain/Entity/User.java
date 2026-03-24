package com.anpk.firstDemoLearnSpring.domain.Entity;

import com.anpk.firstDemoLearnSpring.domain.Enum.AuthProvider;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserRole;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserStatus;
import com.anpk.firstDemoLearnSpring.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    private String passwordHash;
    
    // Vai trò của user: USER, STAFF, ADMIN
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    // để lưu tk khi dky bằng Google/Facebook nên passwordHash có thể null
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    // Lưu ID định danh mà Google/Facebook trả về để dễ dàng truy xuất sau này
    @Column(length = 100)
    private String providerId;

    // Quan hệ 1-n với Deck: Một User có nhiều Decks
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deck> decks = new ArrayList<>();

    // trạng thái account: đã xác thực, chưa xác thực, bị ban
    @Column(nullable = false)
    private UserStatus status;

}

