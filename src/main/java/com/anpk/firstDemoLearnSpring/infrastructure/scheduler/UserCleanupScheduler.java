package com.anpk.firstDemoLearnSpring.infrastructure.scheduler;

import com.anpk.firstDemoLearnSpring.Services.scheduler.UserCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler để tự động xóa các user có trạng thái PENDING quá 24 giờ
 * Chạy tự động mỗi 10 giây (để test)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserCleanupScheduler {

    private final UserCleanupService userCleanupService;

    /**
     * Chay moi 10 giay de test
     * Sau khi test xong, doi thanh: fixedRate = 3600000 (moi 1 gio)
     */
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredPendingUsers() {
        try {
            log.info("=== BAT DAU TAC VU TU DONG: Xoa user PENDING qua han ===");

            int deletedCount = userCleanupService.deleteExpiredPendingUsers();

            if (deletedCount > 0) {
                log.info("Da xoa {} user PENDING qua 24 gio", deletedCount);
            } else {
                log.info("Khong co user PENDING nao qua 24 gio de xoa");
            }

            log.info("=== KET THUC TAC VU TU DONG ===");
        } catch (Exception e) {
            log.error("LOI khi chay scheduled task cleanup user: {}", e.getMessage(), e);
        }
    }
}

