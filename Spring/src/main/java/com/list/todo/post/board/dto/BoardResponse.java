package com.list.todo.post.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponse {
    private Long id;
    private String title;
    private String description;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likes;
    private Integer disLikes;
    private Boolean isPublic;
}