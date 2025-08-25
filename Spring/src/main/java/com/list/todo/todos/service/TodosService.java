package com.list.todo.todos.service;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.todos.dto.TodosDto;
import com.list.todo.todos.entity.TodosEntity;
import com.list.todo.todos.repository.TodosRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodosService {

    private final TodosRepository todosRepository;

    public List<TodosDto> getTodosByUser(UserEntity user){
        List<TodosEntity> todos = todosRepository.findByUser(user);
        return todos.stream().map(this::toDto).collect(Collectors.toList());
    }

    public TodosDto createTodo(TodosDto dto, UserEntity user) {
        if (dto.getDueDate() != null && dto.getDueDate().isBefore(LocalDate.now())){
            throw new RuntimeException("마감일은 오늘 이후여야 합니다.");
        }

        TodosEntity todosEntity = new TodosEntity();
        todosEntity.setUser(user);
        todosEntity.setCreatedAt(dto.getCreatedAt());
        todosEntity.setTitle(dto.getTitle());
        todosEntity.setDescription(dto.getDescription());
        todosEntity.setCompleted(dto.getCompleted() != null ? dto.getCompleted() : false);
        todosEntity.setPriority(dto.getPriority());
        todosEntity.setDueDate(dto.getDueDate());

        TodosEntity saved = todosRepository.save(todosEntity);

        return toDto(saved);
    }

    public TodosDto updateTodo(Long listId, TodosDto dto, UserEntity user){
        TodosEntity todo = todosRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("해당 투두를 찾을 수 없습니다."));

        if (!todo.getUser().getId().equals(user.getId()) && !user.getRole().equals("admin")){
            throw new RuntimeException("권한이 없습니다");
        }

        if (dto.getDueDate() != null && dto.getDueDate().isBefore(LocalDate.now())){
            throw new RuntimeException("마감일은 오늘 이후여야 합니다.");
        }

        todo.setTitle(dto.getTitle());
        todo.setCompleted(dto.getCompleted());
        todo.setDescription(dto.getDescription());
        todo.setDueDate(dto.getDueDate());
        todo.setPriority(dto.getPriority());

        TodosEntity updated = todosRepository.save(todo);

        return toDto(updated);
    }

    public void deleteTodo(Long listId, UserEntity user){
        TodosEntity todo = todosRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("해당 투두를 찾을 수 없습니다."));

        if (!todo.getUser().getId().equals(user.getId()) && !user.getRole().equals("admin")){
            throw new RuntimeException("권한이 없습니다");
        }

        todosRepository.delete(todo);

    }

    private TodosDto toDto(TodosEntity todosEntity){
        TodosDto todosDto = new TodosDto();
        todosDto.setCompleted(todosEntity.getCompleted());
        todosDto.setDescription(todosEntity.getDescription());
        todosDto.setPriority(todosEntity.getPriority());
        todosDto.setTitle(todosEntity.getTitle());
        todosDto.setDueDate(todosEntity.getDueDate());
        todosDto.setCreatedAt(todosEntity.getCreatedAt());
        todosDto.setListId(todosEntity.getListId());
        todosDto.setUpdatedAt(todosEntity.getUpdatedAt());
        todosDto.setUserId(todosEntity.getUser().getId());

        return todosDto;
    }
}
