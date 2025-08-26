package com.list.todo.auth.service;

import com.list.todo.auth.dto.LoginRequestDto;
import com.list.todo.auth.dto.LoginResponseDto;
import com.list.todo.auth.dto.UserSignupRequestDto;
import com.list.todo.auth.entity.RefreshTokenEntity;
import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.exception.LoginException;
import com.list.todo.auth.exception.SignupException;
import com.list.todo.auth.exception.TokenException;
import com.list.todo.auth.security.jwt.JwtUtil;
import com.list.todo.auth.repository.RefreshTokenRepository;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.todos.fcm.entity.FcmTokenEntity;
import com.list.todo.todos.fcm.repository.FcmTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FcmTokenRepository fcmTokenRepository;

    public void signup(UserSignupRequestDto dto){
        if (userRepository.existsByLoginId(dto.getLoginId())){
            throw new SignupException("이미 사용중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())){
            throw new SignupException("이미 사용중인 이메일입니다.");
        }
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())){
            throw new SignupException("이미 사용중인 번호입니다.");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())){
            throw new SignupException("비밀번호가 일치하지 않습니다.");
        }

        log.info("Received FCM Token: {}", dto.getFcmToken());

        UserEntity user = new UserEntity();
        user.setLoginId(dto.getLoginId());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserName(dto.getUserName());
        user.setNickName(dto.getNickName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole("USER");
//        user.setFcmToken(dto.getFcmToken());

        userRepository.save(user);

        if (dto.getFcmToken() != null && !dto.getFcmToken().isBlank()) {
            fcmTokenRepository.findByToken(dto.getFcmToken())
                    .or(() -> {
                        FcmTokenEntity tokenEntity = new FcmTokenEntity();
                        tokenEntity.setUser(user);
                        tokenEntity.setToken(dto.getFcmToken());
                        tokenEntity.setLastUpdated(LocalDateTime.now());
                        return Optional.of(fcmTokenRepository.save(tokenEntity));
                    });
        }
    }

    // signupException 의미 혼동 있을 수 있으니 추후 새로 만들어서 사용
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        try {
            UserEntity user = userRepository.findByLoginId(loginRequestDto.getLoginId())
                    .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getLoginId(), loginRequestDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            String accessToken = jwtUtil.createAccessToken(authentication);
            String refreshToken = jwtUtil.createRefreshToken(authentication);

            refreshTokenRepository.findByUser(user)
                    .ifPresentOrElse(
                            existing -> {
                                existing.setToken(refreshToken);
                                existing.setExpiredAt(LocalDateTime.now().plusDays(7));
                                refreshTokenRepository.save(existing);
                            },
                            () -> {
                                RefreshTokenEntity newToken = new RefreshTokenEntity();
                                newToken.setUser(user);
                                newToken.setToken(refreshToken);
                                newToken.setExpiredAt(LocalDateTime.now().plusDays(7));
                                refreshTokenRepository.save(newToken);
                            }
                    );

            log.info("Generated AccessToken: {}", accessToken);
            return new LoginResponseDto(accessToken, refreshToken);

        } catch (AuthenticationException e) {
            throw new LoginException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    public LoginResponseDto reissueAccessToken(String refreshToken){
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new TokenException("Refresh Token 이 유효하지 않습니다.");
        }

        Claims claims = jwtUtil.getClaims(refreshToken);
//        Long userId = Long.parseLong(claims.getSubject());

        String loginId = claims.getSubject();

//        UserEntity userEntity = userRepository.findById(userId)
//                .orElseThrow(() -> new TokenException("사용자를 찾을 수 없습니다."));
        UserEntity userEntity = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new TokenException("사용자를 찾을 수 없습니다."));

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByUser(userEntity)
                .orElseThrow(() -> new TokenException("저장된 Refresh Token 이 없습니다."));

        if (!refreshTokenEntity.getToken().equals(refreshToken)) {
            throw new TokenException("Refresh Token 정보가 일치하지 않습니다.");
        }

        if (refreshTokenEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenException("Refresh Token 이 만료되었습니다.");
        }

        Authentication authentication = jwtUtil.getAuthentication(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(authentication);
        String newRefreshToken = jwtUtil.createRefreshToken(authentication);

        refreshTokenEntity.setToken(newRefreshToken);
        refreshTokenEntity.setExpiredAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

}
