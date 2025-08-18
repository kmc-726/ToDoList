package com.list.todo.auth.advice;

import com.list.todo.auth.exception.LoginException;
import com.list.todo.auth.exception.SignupException;
import com.list.todo.auth.exception.TokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SignupException.class)
    public ResponseEntity<String> handleSignupException(SignupException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> handleLoginException(LoginException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<String> handleTokenException(TokenException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
