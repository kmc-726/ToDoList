package com.list.todo.auth.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);
        log.info("JwtAuthenticationFilter - 토큰 추출됨? {}", token != null ? "예" : "아니오");
        log.info("JwtAuthenticationFilter - 토큰 내용: {}", token);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    Authentication authentication = jwtUtil.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("JwtAuthenticationFilter - 인증 성공: {}", authentication.getName());
                } else {
                    log.info("JwtAuthenticationFilter - 토큰 유효하지 않음");
                }
            } catch (Exception e) {
                log.info("JwtAuthenticationFilter - 토큰 검증 중 예외 발생: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            log.info("JwtAuthenticationFilter - 토큰 없거나 이미 인증 처리됨");
        }

        filterChain.doFilter(request, response);
    }
}
