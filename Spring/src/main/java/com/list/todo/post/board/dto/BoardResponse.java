package com.list.todo.post.board.dto;

import com.list.todo.auth.dto.UserDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponse {
    private Long id;
    private String title;
    private String description;
//    private String userName;
    private UserDto user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likes;
    private Integer disLikes;
    private Boolean isPublic;
    private String loginId;
    private Boolean likedByMe;
    private Boolean dislikedByMe;
}