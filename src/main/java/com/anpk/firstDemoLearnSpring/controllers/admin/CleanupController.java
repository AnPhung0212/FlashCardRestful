package com.anpk.firstDemoLearnSpring.controllers.admin;

import com.anpk.firstDemoLearnSpring.Services.scheduler.UserCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * API endpoint để trigger cleanup task thủ công (dùng cho testing/admin)
 * CHỈ DÙNG CHO TESTING - Nên bảo vệ bằng authentication trong production
 */
@RestController
@RequestMapping("/api/admin/cleanup")
@RequiredArgsConstructor
public class CleanupController {

    private final UserCleanupService cleanupService;

    /**
     * Trigger xóa user PENDING quá hạn thủ công
     *
     * @return Số lượng user đã xóa
     */
    @PostMapping("/pending-users")
    public ResponseEntity<Map<String, Object>> cleanupPendingUsers() {
        int deletedCount = cleanupService.deleteExpiredPendingUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deletedCount", deletedCount);
        response.put("message", "Đã xóa " + deletedCount + " user PENDING quá 24 giờ");

        return ResponseEntity.ok(response);
    }

    /**
     * Trigger xóa verification token hết hạn thủ công
     *
     * @return Số lượng token đã xóa
     */
    @PostMapping("/expired-tokens")
    public ResponseEntity<Map<String, Object>> cleanupExpiredTokens() {
        int deletedCount = cleanupService.deleteExpiredVerificationTokens();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deletedCount", deletedCount);
        response.put("message", "Đã xóa " + deletedCount + " verification token đã hết hạn");

        return ResponseEntity.ok(response);
    }

    /**
     * Trigger cả 2 cleanup task cùng lúc
     *
     * @return Thống kê kết quả
     */
    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> cleanupAll() {
        int deletedUsers = cleanupService.deleteExpiredPendingUsers();
        int deletedTokens = cleanupService.deleteExpiredVerificationTokens();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deletedUsers", deletedUsers);
        response.put("deletedTokens", deletedTokens);
        response.put("message", String.format(
            "Đã xóa %d user PENDING và %d token hết hạn",
            deletedUsers,
            deletedTokens
        ));

        return ResponseEntity.ok(response);
    }
}

