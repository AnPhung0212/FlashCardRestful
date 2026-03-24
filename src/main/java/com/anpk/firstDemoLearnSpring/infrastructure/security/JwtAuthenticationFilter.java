package com.anpk.firstDemoLearnSpring.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// JwtAuthenticationFilter sẽ được Spring Security gọi cho mỗi request để kiểm tra xem có JWT hợp lệ trong header không rồi mới cho phép đi tiếp vào Controller
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Lấy Header Authorization từ Request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Kiểm tra xem có Token theo chuẩn Bearer không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Cắt chuỗi lấy Token (bỏ chữ "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // 4. Giải mã lấy Email từ Token
            userEmail = jwtService.extractUsername(jwt);

            // 5. Nếu có email và request này chưa được xác thực (chưa có trong Context)
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load User từ DB lên (thông qua CustomUserDetailsService bạn đã viết)
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 6. Nếu Token còn hạn và khớp với User
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Tạo "vé thông hành" cho Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 7. Lưu vào Context để các Filter phía sau biết ông này đã Login
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log lỗi nếu token giả hoặc hết hạn (không nên throw exception ở đây để filter đi tiếp)
            logger.error("JWT Authentication failed: " + e.getMessage());
        }

        // Cho phép request đi tiếp đến Controller
        filterChain.doFilter(request, response);
    }
}
