package com.list.todo.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final long accessTokenValidTime = 1000 * 60 * 60;
    private static final long refreshTokenValidTime = 1000 * 60 * 60 * 7;
    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secretKey){
        this.key= Keys.hmacShaKeyFor(secretKey.getBytes());
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

        // 클레임에서 권한 리스트 꺼내기 (예: "roles" 클레임에 권한 배열이 있다고 가정)
        Object rolesObject = claims.get("roles");
        List<String> roles;

        if (rolesObject instanceof List<?>) {
            roles = ((List<?>) rolesObject).stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        } else {
            roles = new ArrayList<>();
        }

        // 권한 문자열 리스트를 GrantedAuthority 리스트로 변환
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails userDetails = new User(loginId, "", authorities);

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public Boolean validateToken(String token){
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
