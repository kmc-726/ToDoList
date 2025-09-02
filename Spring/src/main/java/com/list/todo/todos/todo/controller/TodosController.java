package com.list.todo.todos.todo.controller;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.global.exception.LoginException;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.todos.fcm.dto.DeviceDto;
import com.list.todo.todos.fcm.dto.FcmTokenRequest;
import com.list.todo.todos.todo.dto.TodosDto;
import com.list.todo.todos.fcm.entity.FcmTokenEntity;
import com.list.todo.todos.fcm.repository.FcmTokenRepository;
import com.list.todo.todos.todo.service.TodosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodosController {

    private final TodosService todosService;
    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @GetMapping
    public ResponseEntity<List<TodosDto>> getTodos(@AuthenticationPrincipal UserDetails principal) {
        log.info("Current user: {}, authorities: {}", principal.getUsername(), principal.getAuthorities());
        String loginId = principal.getUsername();
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

        List<TodosDto> todos = todosService.getTodosByUser(user);

        return ResponseEntity.ok(todos);
    }

//    @PreAuthorize("permitAll()")
    @PostMapping("/psing")
    public ResponseEntity<?> createTodo(@RequestBody @Valid TodosDto dto,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal UserDetails principal
    ) {
        log.info("=== createTodo 진입 ===");
        log.info("📌 principal: {}", principal);

        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        String loginId = principal.getUsername();
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

        TodosDto posting = todosService.createTodo(dto, user);

        return ResponseEntity.ok(posting);
    }

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

    @PutMapping("/updateTodo/{listId}")
    public ResponseEntity<TodosDto> updatedTodo(@PathVariable Long listId,
                                               @RequestBody TodosDto dto,
                                               @AuthenticationPrincipal UserDetails principal){
        String loginId = principal.getUsername();
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

        TodosDto updated = todosService.updateTodo(listId, dto, user);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long listId,
                           @AuthenticationPrincipal UserDetails principal){
        String loginId = principal.getUsername();
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

        todosService.deleteTodo(listId, user);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/devices")
    public List<DeviceDto> getMyDevices(@AuthenticationPrincipal UserEntity user) {
        return fcmTokenRepository.findAllByUser(user).stream()
                .map(DeviceDto::fromEntity)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/devices/{id}")
    public void deleteDevice(@PathVariable Long id, @AuthenticationPrincipal UserEntity user) {
        FcmTokenEntity token = fcmTokenRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        fcmTokenRepository.delete(token);
    }

    @PatchMapping("/devices/{id}/toggle")
    public void toggleDevice(@PathVariable Long id, @AuthenticationPrincipal UserEntity user) {
        FcmTokenEntity token = fcmTokenRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        token.setEnabled(!token.isEnabled());
        fcmTokenRepository.save(token);
    }
}
