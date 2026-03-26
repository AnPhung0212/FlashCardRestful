package com.anpk.firstDemoLearnSpring.helpers.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// Helper class để lấy thông tin người dùng hiện tại từ SecurityContext
public class SecurityHelper {
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}

