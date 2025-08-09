package com.list.todo.todos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemindersDto {
    private Long reminderId;
    private Long todosId;
    private String method;
    private Boolean isSent;
    private LocalDateTime remindAt;
    private LocalDateTime createdAt;
}
