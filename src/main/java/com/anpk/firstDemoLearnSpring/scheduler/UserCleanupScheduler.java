package com.anpk.firstDemoLearnSpring.scheduler;

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
     * Chạy mỗi 10 giây để test
     * Sau khi test xong, đổi thành: fixedRate = 3600000 (mỗi 1 giờ)
     */
    @Scheduled(fixedRate = 10000)
    public void cleanupExpiredPendingUsers() {
        try {
            log.info("=== BẮT ĐẦU TÁC VỤ TỰ ĐỘNG: Xóa user PENDING quá hạn ===");

            int deletedCount = userCleanupService.deleteExpiredPendingUsers();

            if (deletedCount > 0) {
                log.info("Đã xóa {} user PENDING quá 24 giờ", deletedCount);
            } else {
                log.info("Không có user PENDING nào quá 24 giờ để xóa");
            }

            log.info("=== KẾT THÚC TÁC VỤ TỰ ĐỘNG ===");
        } catch (Exception e) {
            log.error("LỖI khi chạy scheduled task cleanup user: {}", e.getMessage(), e);
        }
    }
}

