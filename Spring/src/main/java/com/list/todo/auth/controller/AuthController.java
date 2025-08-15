package com.list.todo.auth.controller;

import com.list.todo.auth.dto.UserSignupRequestDto;
import com.list.todo.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<?> signup(@RequestBody @Valid UserSignupRequestDto dto){
        authService.signup(dto);
        return ResponseEntity.ok("회원가입성공");
    }
}
