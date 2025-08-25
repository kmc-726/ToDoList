package com.list.todo.auth.controller;

import com.list.todo.auth.dto.LoginRequestDto;
import com.list.todo.auth.dto.LoginResponseDto;
import com.list.todo.auth.dto.UserSignupRequestDto;
import com.list.todo.auth.jwt.JwtUtil;
import com.list.todo.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserSignupRequestDto dto){
        authService.signup(dto);
        return ResponseEntity.ok("회원가입성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto loginDto, HttpServletResponse response){
        LoginResponseDto loginResponseDto = authService.login(loginDto);

        Cookie accessTokenCookie = new Cookie("accessToken", loginResponseDto.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60);

        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String bearerToken){
        if (bearerToken == null || !bearerToken.startsWith("Bearer")) {
            return ResponseEntity.badRequest().body(null);
        }

        String refreshToken = bearerToken.substring(7);
        LoginResponseDto newTokens = authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(newTokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie deleteAccessToken = new Cookie("accessToken", null);
        deleteAccessToken.setHttpOnly(true);
        deleteAccessToken.setSecure(false);
        deleteAccessToken.setPath("/");
        deleteAccessToken.setMaxAge(0);

        Cookie deleteRefreshToken = new Cookie("refreshToken", null);
        deleteRefreshToken.setHttpOnly(true);
        deleteRefreshToken.setSecure(false);
        deleteRefreshToken.setPath("/");
        deleteRefreshToken.setMaxAge(0);

        response.addCookie(deleteAccessToken);
        response.addCookie(deleteRefreshToken);

        return ResponseEntity.ok("로그아웃 완료");
    }
}
