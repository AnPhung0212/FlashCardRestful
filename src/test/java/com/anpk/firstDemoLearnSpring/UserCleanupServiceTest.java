package com.anpk.firstDemoLearnSpring;

import com.anpk.firstDemoLearnSpring.Services.scheduler.UserCleanupService;
import com.anpk.firstDemoLearnSpring.domain.Entity.EmailVerificationToken;
import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserStatus;
import com.anpk.firstDemoLearnSpring.repository.EmailVerificationTokenRepository;
import com.anpk.firstDemoLearnSpring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cho scheduled task cleanup user PENDING và verification token
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class UserCleanupServiceTest {

    @Autowired
    private UserCleanupService cleanupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        // Xóa dữ liệu test cũ
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testDeleteExpiredPendingUsers_ShouldDeleteUsersCreatedMoreThan24HoursAgo() {
        System.out.println("\n=== TEST: Xóa user PENDING quá 24 giờ ===");

        // Arrange: Tạo user PENDING đã tạo 25 giờ trước (quá hạn)
        User expiredUser = new User();
        expiredUser.setUsername("expired_user");
        expiredUser.setEmail("expired@test.com");
        expiredUser.setPasswordHash("hash123");
        expiredUser.setStatus(UserStatus.PENDING);
        // Override createdAt bằng cách set trực tiếp (trong BaseEntity)
        userRepository.save(expiredUser);
        // Manually set createdAt để test
        expiredUser.setCreatedAt(LocalDateTime.now().minusHours(25));
        userRepository.save(expiredUser);

        System.out.println("Đã tạo user PENDING quá 24h: " + expiredUser.getUsername());

        // Act: Chạy cleanup
        int deletedCount = cleanupService.deleteExpiredPendingUsers();

        // Assert
        assertEquals(1, deletedCount, "Phải xóa đúng 1 user");
        assertFalse(userRepository.existsByUsername("expired_user"), "User phải bị xóa khỏi DB");

        System.out.println("✅ Test PASSED: Đã xóa " + deletedCount + " user PENDING quá hạn");
    }

    @Test
    void testDeleteExpiredPendingUsers_ShouldNotDeleteRecentPendingUsers() {
        System.out.println("\n=== TEST: KHÔNG xóa user PENDING mới tạo (<24h) ===");

        // Arrange: Tạo user PENDING mới (chưa quá 24h)
        User recentUser = new User();
        recentUser.setUsername("recent_user");
        recentUser.setEmail("recent@test.com");
        recentUser.setPasswordHash("hash456");
        recentUser.setStatus(UserStatus.PENDING);
        userRepository.save(recentUser);

        System.out.println("Đã tạo user PENDING mới: " + recentUser.getUsername());

        // Act: Chạy cleanup
        int deletedCount = cleanupService.deleteExpiredPendingUsers();

        // Assert
        assertEquals(0, deletedCount, "Không được xóa user mới");
        assertTrue(userRepository.existsByUsername("recent_user"), "User mới phải còn trong DB");

        System.out.println("✅ Test PASSED: Không xóa user PENDING mới (<24h)");
    }

    @Test
    void testDeleteExpiredPendingUsers_ShouldNotDeleteActiveUsers() {
        System.out.println("\n=== TEST: KHÔNG xóa user ACTIVE (dù đã quá 24h) ===");

        // Arrange: Tạo user ACTIVE đã tạo 25 giờ trước
        User activeUser = new User();
        activeUser.setUsername("active_user");
        activeUser.setEmail("active@test.com");
        activeUser.setPasswordHash("hash789");
        activeUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(activeUser);
        activeUser.setCreatedAt(LocalDateTime.now().minusHours(25));
        userRepository.save(activeUser);

        System.out.println("Đã tạo user ACTIVE quá 24h: " + activeUser.getUsername());

        // Act: Chạy cleanup
        int deletedCount = cleanupService.deleteExpiredPendingUsers();

        // Assert
        assertEquals(0, deletedCount, "Không được xóa user ACTIVE");
        assertTrue(userRepository.existsByUsername("active_user"), "User ACTIVE phải còn trong DB");

        System.out.println("✅ Test PASSED: Không xóa user ACTIVE");
    }

    @Test
    void testDeleteExpiredVerificationTokens_ShouldDeleteExpiredTokens() {
        System.out.println("\n=== TEST: Xóa verification token đã hết hạn ===");

        // Arrange: Tạo user và token hết hạn
        User user = new User();
        user.setUsername("test_user");
        user.setEmail("test@test.com");
        user.setPasswordHash("hash");
        user.setStatus(UserStatus.PENDING);
        userRepository.save(user);

        EmailVerificationToken expiredToken = new EmailVerificationToken();
        expiredToken.setToken("expired-token-123");
        expiredToken.setExpiredAt(LocalDateTime.now().minusHours(1)); // Hết hạn 1 giờ trước
        expiredToken.setUser(user);
        tokenRepository.save(expiredToken);

        System.out.println("Đã tạo token hết hạn: " + expiredToken.getToken());

        // Act: Chạy cleanup
        int deletedCount = cleanupService.deleteExpiredVerificationTokens();

        // Assert
        assertEquals(1, deletedCount, "Phải xóa đúng 1 token");
        assertFalse(tokenRepository.findByToken("expired-token-123").isPresent(), "Token phải bị xóa");

        System.out.println("✅ Test PASSED: Đã xóa " + deletedCount + " token hết hạn");
    }

    @Test
    void testDeleteExpiredVerificationTokens_ShouldNotDeleteValidTokens() {
        System.out.println("\n=== TEST: KHÔNG xóa token còn hạn ===");

        // Arrange: Tạo user và token còn hạn
        User user = new User();
        user.setUsername("test_user2");
        user.setEmail("test2@test.com");
        user.setPasswordHash("hash");
        user.setStatus(UserStatus.PENDING);
        userRepository.save(user);

        EmailVerificationToken validToken = new EmailVerificationToken();
        validToken.setToken("valid-token-456");
        validToken.setExpiredAt(LocalDateTime.now().plusHours(1)); // Còn hạn 1 giờ nữa
        validToken.setUser(user);
        tokenRepository.save(validToken);

        System.out.println("Đã tạo token còn hạn: " + validToken.getToken());

        // Act: Chạy cleanup
        int deletedCount = cleanupService.deleteExpiredVerificationTokens();

        // Assert
        assertEquals(0, deletedCount, "Không được xóa token còn hạn");
        assertTrue(tokenRepository.findByToken("valid-token-456").isPresent(), "Token còn hạn phải còn trong DB");

        System.out.println("✅ Test PASSED: Không xóa token còn hạn");
    }
}

