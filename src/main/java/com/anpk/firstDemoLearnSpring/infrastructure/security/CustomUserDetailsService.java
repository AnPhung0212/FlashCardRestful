package com.anpk.firstDemoLearnSpring.infrastructure.security;

import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Đánh dấu đây là một Bean Service để Spring tự động Inject vào SecurityConfig
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    /** Mỗi lần login spring se sẽ gọi hàm này để lấy thông tin user từ database,
     sau đó so sánh password và các thông tin khác để xác thực. **/
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
// Gọi xuống DB tìm user bằng email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        // Nếu tìm thấy, bọc nó vào CustomUserDetails và trả về cho Spring Security xử lý tiếp
        return new CustomUserDetails(user);    }
}
