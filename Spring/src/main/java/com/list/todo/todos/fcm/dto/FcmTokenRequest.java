package com.list.todo.todos.fcm.dto;

import lombok.Data;

@Data
public class FcmTokenRequest {
    private String token;
    private String deviceInfo;
}
