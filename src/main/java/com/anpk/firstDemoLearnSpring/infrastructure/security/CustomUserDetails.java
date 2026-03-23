package com.anpk.firstDemoLearnSpring.infrastructure.security;

import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

// Class này dùng để framwork có thể hiểu được các thông tin của User trong hệ thống của mình. Ví dụ như username, password, role,...
@Getter
@AllArgsConstructor
public class CustomUserDetails  implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    // Dùng Email làm tài khoản định danh chính trong hệ thống
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Trả về false nếu user bị khóa (BANNED). Spring sẽ tự văng lỗi không cho đăng nhập.
    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != UserStatus.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // dùng để kiểm tra xem tài khoản đã được xác thực email chưa. Nếu chưa xác thực thì trả về false, Spring sẽ tự văng lỗi không cho đăng nhập.
    @Override
    public boolean isEnabled() {
        // Hiện tại cứ cho phép true để dễ test.
        return true;
    }
}
