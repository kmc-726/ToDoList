package com.list.todo.post.comment.dto;

import lombok.Data;

@Data
public class CommentDto {
    private String content;
    private Long boardId;
}
