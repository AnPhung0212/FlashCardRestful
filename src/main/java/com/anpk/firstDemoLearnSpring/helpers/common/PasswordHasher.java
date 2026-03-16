package com.anpk.firstDemoLearnSpring.helpers.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordHasher {
    private final PasswordEncoder passwordEncoder;
   // Hàm này dùng để mã hoá (hash) password trước khi lưu vào database.
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    //Hàm này dùng để so sánh password user nhập khi login với password đã hash trong DB.
    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
