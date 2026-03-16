package com.anpk.firstDemoLearnSpring.helpers.common;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateVerificationToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
