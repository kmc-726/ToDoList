package com.list.todo.auth.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String base64SecretKey){
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

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        String loginId = claims.getSubject();

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

        UserDetails userDetails = new User(loginId, "", authorities);

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
            return bearerToken.substring(7);
        }

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
