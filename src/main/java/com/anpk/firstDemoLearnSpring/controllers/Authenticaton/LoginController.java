package com.anpk.firstDemoLearnSpring.controllers.Authenticaton;

import com.anpk.firstDemoLearnSpring.Services.Authentication.OAuth2Service;
import com.anpk.firstDemoLearnSpring.Services.Authentication.RefreshTokenService;
import com.anpk.firstDemoLearnSpring.domain.Entity.RefreshToken;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Authentication.LoginRequest;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Authentication.TokenResponse;
import com.anpk.firstDemoLearnSpring.infrastructure.security.CustomUserDetails;
import com.anpk.firstDemoLearnSpring.infrastructure.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Login", description = "API quản lý người dùng đăng nhập và làm mới token")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OAuth2Service oauth2Service;


    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Đăng nhập vào hệ thống bằng email và password")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {

        // 1. Xác thực Email và Password (Spring sẽ tự gọi CustomUserDetailsService để check)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Nếu không có ngoại lệ nghĩa là login thành công, lấy thông tin user ra
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 3. Tạo Access Token (JWT)
        String accessToken = jwtService.generateToken(userDetails);

        // 4. Tạo Refresh Token và lưu vào DB
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        // 5. Trả về cho Frontend
        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(userDetails.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority().replace("ROLE_", ""))
                        .collect(Collectors.toList()))
                .build());
    }
    
    // api xử lý đăng nhập OAuth2 thành công (nếu có)
    @GetMapping("/oauth2/login-success")
    @Operation(summary = "Đăng nhập OAuth2 thành công", description = "Xử lý đăng nhập OAuth2 thành công và trả về token")
    public ResponseEntity<TokenResponse> loginSuccess(Authentication authentication) {
    return ResponseEntity.ok(oauth2Service.handleOAuth2Login(authentication));
    }
}
