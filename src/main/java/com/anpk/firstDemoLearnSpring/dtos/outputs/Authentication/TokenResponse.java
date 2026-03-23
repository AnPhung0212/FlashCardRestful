package com.anpk.firstDemoLearnSpring.dtos.outputs.Authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;

    private String refreshToken;

    // Mặc định luôn là Bearer
    @Builder.Default
    private String tokenType = "Bearer";

    // Trả thêm email và role để frontend tiện hiển thị UI
    private String email;

    private List<String> roles;
}
