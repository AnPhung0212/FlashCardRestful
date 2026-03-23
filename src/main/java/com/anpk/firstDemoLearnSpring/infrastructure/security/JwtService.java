package com.anpk.firstDemoLearnSpring.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // Đây là chữ ký bí mật để ký JWT.
    // Mặc định nó sẽ lấy giá trị từ application.properties (biến jwt.secret).
    // Nếu không có, nó dùng chuỗi mặc định dài ngoằng ở dưới để test.
    @Value("${jwt.secret}")
    private String secretKey;

    // Thời gian sống của Access Token (Ví dụ: 3600000 = 1 giờ)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // 1. Hàm rút trích Username (trong trường hợp của ta là Email) từ Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 2. Hàm tạo Token dành cho lúc Login thành công
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Set hạn sử dụng
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Ký bằng thuật toán HS256
                .compact();
    }

    // 3. Hàm kiểm tra xem Token có hợp lệ với User đang request không
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Hàm core để giải mã Token bằng Secret Key
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Hàm lấy Secret Key để mã hóa/giải mã
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
