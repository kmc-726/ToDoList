package com.list.todo.auth.security.jwt;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.auth.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    private static final long accessTokenValidTime = 1000 * 60 * 60;
    private static final long refreshTokenValidTime = 1000 * 60 * 60 * 24 * 7;
    private final UserRepository userRepository;
    private final SecretKey key;

    public JwtUtil(UserRepository userRepository, @Value("${jwt.secret}") String base64SecretKey){
        this.userRepository = userRepository;
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Authentication authentication){
        return createToken(authentication, accessTokenValidTime);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenValidTime);
    }

    private String createToken(Authentication authentication, long validTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validTime);

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String loginId = userDetails.getLoginId();

        log.info("유저테일즈 정보 {}", userDetails);

        return Jwts.builder()
                .subject(loginId)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        String loginId = claims.getSubject();
        log.info("로그인 아이디{}", claims.getSubject());

        Object rolesObject = claims.get("roles");
        List<String> roles;

        if (rolesObject instanceof List<?>) {
            roles = ((List<?>) rolesObject).stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        } else if (rolesObject instanceof String) {
            roles = List.of(((String) rolesObject).split(","));
        } else {
            roles = new ArrayList<>();
        }

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.info("rolesObject: {}", rolesObject);
        log.info("authorities: {}", authorities);
        log.info("JWT subject: {}", claims.getSubject());
        log.info("JWT roles: {}", claims.get("roles"));

        UserEntity userEntity = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        UserDetails userDetails = new CustomUserDetails(userEntity, authorities);

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public Boolean validateToken(String token){
        try {
            Claims claims = getClaims(token);
            log.info("토큰 만료일: {}", claims.getExpiration());
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.info("토큰 검증실패:{}", e.getMessage());
            log.warn("토큰 검증실패:{}", e.getMessage());
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            log.info("resolveToken - Authorization 헤더에서 토큰 추출됨");
            return bearerToken.substring(7);
        }

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    log.info("resolveToken - 쿠키에서 토큰 추출됨");
                    return cookie.getValue();
                }
            }
        }
        log.info("resolveToken - 토큰 없음");
        return null;
    }

}
