package com.list.todo.auth.controller;

import com.list.todo.auth.dto.LoginRequestDto;
import com.list.todo.auth.dto.LoginResponseDto;
import com.list.todo.auth.dto.UserSignupRequestDto;
import com.list.todo.auth.jwt.JwtUtil;
import com.list.todo.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
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
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto loginDto){
        LoginResponseDto loginResponseDto = authService.login(loginDto);
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
}
