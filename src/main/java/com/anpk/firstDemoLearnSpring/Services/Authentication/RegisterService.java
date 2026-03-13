package com.anpk.firstDemoLearnSpring.Services.Authentication;

import com.anpk.firstDemoLearnSpring.Services.MailService.VerificationMailService;
import com.anpk.firstDemoLearnSpring.domain.Entity.EmailVerificationToken;
import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserStatus;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Authenticator.RegisterRequest;
import com.anpk.firstDemoLearnSpring.helpers.validate.PasswordValidator;
import com.anpk.firstDemoLearnSpring.repository.EmailVerificationTokenRepository;
import com.anpk.firstDemoLearnSpring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationMailService verificationMailService;

    @Transactional
    /// TODO: Implement register account .
    public void register(RegisterRequest request) {

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("Password not match");
        }

        PasswordValidator.validate(request.getPassword());

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING);

        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiredAt(LocalDateTime.now().plusHours(24));
        tokenRepository.saveAndFlush(verificationToken);

        verificationMailService.sendVerificationEmail(
                user.getEmail(),
                user.getUsername(),
                token
        );
    }
    ///  Dùng để xác thực email khi người dùng click vào link trong email.
    public void verifyEmailToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token xác thực không hợp lệ"));
        // nếu token đã hết hạn, xóa token khỏi database và trả về lỗi.
        if (verificationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken);
            throw new RuntimeException("Token xác thực đã hết hạn");
        }
        // Nếu token hợp lệ, kích hoạt tài khoản người dùng và xóa token khỏi database.
        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }
}
