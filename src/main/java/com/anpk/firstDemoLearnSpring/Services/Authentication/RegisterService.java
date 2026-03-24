package com.anpk.firstDemoLearnSpring.Services.Authentication;

import com.anpk.firstDemoLearnSpring.Services.MailService.VerificationMailService;
import com.anpk.firstDemoLearnSpring.domain.Entity.EmailVerificationToken;
import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserRole;
import com.anpk.firstDemoLearnSpring.domain.Enum.UserStatus;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Authentication.RegisterRequest;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Register.RegisterUserResponse;
import com.anpk.firstDemoLearnSpring.helpers.common.PasswordHasher;
import com.anpk.firstDemoLearnSpring.helpers.common.TokenGenerator;
import com.anpk.firstDemoLearnSpring.helpers.validate.PasswordValidator;
import com.anpk.firstDemoLearnSpring.infrastructure.custom.BadRequestException;
import com.anpk.firstDemoLearnSpring.infrastructure.custom.ConflictException;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.EmailVerificationTokenRepository;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationMailService verificationMailService;
    private final TokenGenerator tokenGenerator;
    private final PasswordHasher passwordHasher;


    @Transactional
    /// TODO: Implement register account .
    public RegisterUserResponse register(RegisterRequest request) {

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new BadRequestException("Mật khẩu xác nhận không khớp");        }

        PasswordValidator.validate(request.getPassword());

        if(userRepository.existsByEmail(request.getEmail())){
            throw new ConflictException("Email already exists");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new ConflictException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        user.setStatus(UserStatus.PENDING);
        user.setRole(UserRole.USER);

        userRepository.save(user);

        String token = tokenGenerator.generateVerificationToken();
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
        return  RegisterUserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus().name()).build();
    }
    ///  Dùng để xác thực email khi người dùng click vào link trong email.
    public void verifyEmailToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token xác thực không hợp lệ"));
        // nếu token đã hết hạn, xóa token khỏi database và trả về lỗi.
        if (verificationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken);
            throw new ConflictException("Token xác thực đã hết hạn");
        }
        // Nếu token hợp lệ, kích hoạt tài khoản người dùng và xóa token khỏi database.
        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        //tokenRepository.delete(verificationToken);
    }
}
