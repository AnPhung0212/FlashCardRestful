package com.anpk.firstDemoLearnSpring.Services.Authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.domain.Enum.AuthProvider;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserRole;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserStatus;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Authentication.TokenResponse;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.UserRepository;
import com.anpk.firstDemoLearnSpring.infrastructure.security.CustomUserDetails;
import com.anpk.firstDemoLearnSpring.infrastructure.security.JwtService;

import lombok.RequiredArgsConstructor;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    // Hàm này sẽ được gọi khi OAuth2 Login thành công, nó sẽ xử lý logic tạo user (nếu chưa có) và trả về TokenResponse
    public TokenResponse handleOAuth2Login(Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");
        // Kiểm tra nếu user đã tồn tại trong DB chưa, nếu chưa thì tạo mới
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name != null ? name : email);
            newUser.setAuthProvider(AuthProvider.GOOGLE);
            newUser.setProviderId(providerId);
            newUser.setRole(UserRole.USER);
            newUser.setStatus(UserStatus.ACTIVE);
            return userRepository.save(newUser);
        });

        // Tạo CustomUserDetails từ User để dùng cho generateToken

        var userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .roles(Collections.singletonList(user.getRole().name()))
                .build();
    }
}
