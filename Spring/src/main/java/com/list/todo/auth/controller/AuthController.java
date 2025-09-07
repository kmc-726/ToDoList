package com.list.todo.auth.controller;

import com.list.todo.auth.dto.LoginRequestDto;
import com.list.todo.auth.dto.LoginResponseDto;
import com.list.todo.auth.dto.UserDto;
import com.list.todo.auth.dto.UserSignupRequestDto;
import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.security.CustomUserDetails;
import com.list.todo.auth.security.jwt.JwtUtil;
import com.list.todo.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.web.server.Cookie.SameSite;

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
//
//        Cookie accessTokenCookie = new Cookie("accessToken", loginResponseDto.getAccessToken());
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setSecure(true); // ✅ HTTPS 환경일 경우 true
//        accessTokenCookie.setPath("/");
//        accessTokenCookie.setMaxAge(60 * 60);
//
//// ✅ SameSite 설정 (Spring Cookie에는 직접 안 붙음, 헤더 직접 세팅 필요할 수도)
//        response.setHeader("Set-Cookie", "accessToken=" + loginResponseDto.getAccessToken() +
//                "; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=3600");
//
//        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);
//
//        response.addCookie(refreshTokenCookie);
//        response.addCookie(accessTokenCookie);
        boolean isSecure = true; // 배포 환경에서만 true로

        String accessTokenCookieValue = "accessToken=" + loginResponseDto.getAccessToken()
                + "; HttpOnly; Path=/; Max-Age=3600; SameSite=None" + (isSecure ? "; Secure" : "");
        String refreshTokenCookieValue = "refreshToken=" + loginResponseDto.getRefreshToken()
                + "; HttpOnly; Path=/; Max-Age=" + (60 * 60 * 24 * 7) + "; SameSite=None" + (isSecure ? "; Secure" : "");

        response.addHeader("Set-Cookie", accessTokenCookieValue);
        response.addHeader("Set-Cookie", refreshTokenCookieValue);

        return ResponseEntity.ok(loginResponseDto);
    }
//
//    @PostMapping
//    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String bearerToken){
//        if (bearerToken == null || !bearerToken.startsWith("Bearer")) {
//            return ResponseEntity.badRequest().body(null);
//        }
//
//        String refreshToken = bearerToken.substring(7);
//        LoginResponseDto newTokens = authService.reissueAccessToken(refreshToken);
//        return ResponseEntity.ok(newTokens);
//    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }

        LoginResponseDto newTokens = authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(newTokens);
    }


    //    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletResponse response) {
//        Cookie deleteAccessToken = new Cookie("accessToken", null);
//        deleteAccessToken.setHttpOnly(true);
//        deleteAccessToken.setSecure(false);
//        deleteAccessToken.setPath("/");
//        deleteAccessToken.setMaxAge(0);
//
//        Cookie deleteRefreshToken = new Cookie("refreshToken", null);
//        deleteRefreshToken.setHttpOnly(true);
//        deleteRefreshToken.setSecure(false);
//        deleteRefreshToken.setPath("/");
//        deleteRefreshToken.setMaxAge(0);
//
//        response.addCookie(deleteAccessToken);
//        response.addCookie(deleteRefreshToken);
//
//        return ResponseEntity.ok("로그아웃 완료");
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        boolean isSecure = true; // 로그인 시와 동일하게 맞춰주세요

        String deleteAccessToken = "accessToken=; HttpOnly; Path=/; Max-Age=0; SameSite=None" + (isSecure ? "; Secure" : "");
        String deleteRefreshToken = "refreshToken=; HttpOnly; Path=/; Max-Age=0; SameSite=None" + (isSecure ? "; Secure" : "");

        response.addHeader("Set-Cookie", deleteAccessToken);
        response.addHeader("Set-Cookie", deleteRefreshToken);

        return ResponseEntity.ok("로그아웃 완료");
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal CustomUserDetails user){
        if (user == null){
            throw new UnsupportedOperationException("로그인이 필요합니다.");
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUserName(user.getUsername());
        userDto.setLoginId(user.getLoginId());
//        userDto.setId(user.getId());

        return userDto;
    }
}
