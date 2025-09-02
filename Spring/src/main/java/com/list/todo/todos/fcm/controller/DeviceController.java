package com.list.todo.todos.fcm.controller;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.global.exception.LoginException;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.todos.fcm.dto.DeviceDto;
import com.list.todo.todos.fcm.dto.FcmTokenRequest;
import com.list.todo.todos.fcm.entity.FcmTokenEntity;
import com.list.todo.todos.fcm.repository.FcmTokenRepository;
import com.list.todo.todos.todo.service.TodosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final TodosService todosService;
    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @PostMapping("/save-fcm-token")
    public ResponseEntity<?> saveFcmToken(@RequestBody FcmTokenRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        String loginId = userDetails.getUsername();
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

        int deviceCount = fcmTokenRepository.findAllByUser(user).size();
        if (deviceCount >= 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("기기는 최대 2개까지만 등록할 수 있습니다.");
        }

        fcmTokenRepository.findByToken(request.getToken())
                .orElseGet(() -> {
                    FcmTokenEntity tokenEntity = new FcmTokenEntity();
                    tokenEntity.setUser(user);
                    tokenEntity.setToken(request.getToken());
//                    tokenEntity.setDeviceInfo(request.getDeviceInfo()); // 만약 DTO에 deviceInfo 있다면
                    tokenEntity.setEnabled(true);
                    tokenEntity.setLastUpdated(LocalDateTime.now());
                    return fcmTokenRepository.save(tokenEntity);
                });

        return ResponseEntity.ok("FCM 토큰 저장 완료");
    }

    @GetMapping("")
    public List<DeviceDto> getMyDevices(@AuthenticationPrincipal UserEntity user) {
        return fcmTokenRepository.findAllByUser(user).stream()
                .map(DeviceDto::fromEntity)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteDevice(@PathVariable Long id, @AuthenticationPrincipal UserEntity user) {
        FcmTokenEntity token = fcmTokenRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        fcmTokenRepository.delete(token);
    }

    @PatchMapping("/{id}/toggle")
    public void toggleDevice(@PathVariable Long id, @AuthenticationPrincipal UserEntity user) {
        FcmTokenEntity token = fcmTokenRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        token.setEnabled(!token.isEnabled());
        fcmTokenRepository.save(token);
    }
}
