package com.list.todo.post.board.dto;

import lombok.Data;

@Data
public class BoardRequest {
    private String title;
    private String description;
    private Boolean isPublic;
}