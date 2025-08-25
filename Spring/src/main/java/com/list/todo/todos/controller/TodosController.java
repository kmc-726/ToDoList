package com.list.todo.todos.controller;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.todos.dto.TodosDto;
import com.list.todo.todos.entity.TodosEntity;
import com.list.todo.todos.service.TodosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodosController {

    private final TodosService todosService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<TodosDto>> getTodos(@AuthenticationPrincipal UserDetails principal) {
        log.info("Current user: {}, authorities: {}", principal.getUsername(), principal.getAuthorities());
        String loginId = principal.getUsername();
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        TodosDto posting = todosService.createTodo(dto, user);

        return ResponseEntity.ok(posting);
    }

    @PutMapping("/updateTodo/{listId}")
    public ResponseEntity<TodosDto> updatedTodo(@PathVariable Long listId,
                                               @RequestBody TodosDto dto,
                                               @AuthenticationPrincipal UserDetails principal){
        String loginId = principal.getUsername();
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        TodosDto updated = todosService.updateTodo(listId, dto, user);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long listId,
                           @AuthenticationPrincipal UserDetails principal){
        String loginId = principal.getUsername();
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        todosService.deleteTodo(listId, user);

        return ResponseEntity.noContent().build();
    }
}
