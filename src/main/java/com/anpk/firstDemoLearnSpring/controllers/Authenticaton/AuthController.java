package com.anpk.firstDemoLearnSpring.controllers.Authenticaton;
import org.springframework.security.core.Authentication;
import com.anpk.firstDemoLearnSpring.Services.Authentication.OAuth2Service;
import com.anpk.firstDemoLearnSpring.Services.Authentication.RegisterService;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Authentication.RegisterRequest;
import com.anpk.firstDemoLearnSpring.helpers.common.ApiResponse;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Authentication.TokenResponse;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Register.RegisterUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API quản lý xác thực và đăng ký tài khoản")
public class AuthController {
    private final RegisterService registerService;
    private final OAuth2Service oauth2Service;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới", description = "Tạo tài khoản và gửi email xác thực")
    public ResponseEntity<ApiResponse<RegisterUserResponse>> register(@Valid @RequestBody RegisterRequest request) {

        RegisterUserResponse data = registerService.register(request);
        ApiResponse<RegisterUserResponse> response =
                new ApiResponse<>(HttpStatus.OK.value(), "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    @Operation(summary = "Xác thực email", description = "Kích hoạt tài khoản thông qua token trong email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {

        registerService.verifyEmailToken(token);

        ApiResponse<String> response =
                new ApiResponse<>(HttpStatus.OK.value(), "Xác thực tài khoản thành công! Bạn có thể đăng nhập.", null);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/oauth2/login-success")
    @Operation(summary = "Đăng nhập OAuth2 thành công", description = "Xử lý đăng nhập OAuth2 thành công và trả về token")
    public ResponseEntity<TokenResponse> loginSuccess(Authentication authentication) {
    return ResponseEntity.ok(oauth2Service.handleOAuth2Login(authentication));
}
}