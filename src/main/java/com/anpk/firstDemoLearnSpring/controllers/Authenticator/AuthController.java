package com.anpk.firstDemoLearnSpring.controllers.Authenticator;

import com.anpk.firstDemoLearnSpring.Services.Authentication.RegisterService;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Authenticator.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API quản lý xác thực và đăng ký tài khoản")
public class AuthController {
    private final RegisterService registerService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới", description = "Tạo tài khoản và gửi email xác thực")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        try {
            registerService.register(request);
            return ResponseEntity.ok("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    @Operation(summary = "Xác thực email", description = "Kích hoạt tài khoản thông qua token trong email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            registerService.verifyEmailToken(token);
            return ResponseEntity.ok("Xác thực tài khoản thành công! Bạn có thể đăng nhập.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}