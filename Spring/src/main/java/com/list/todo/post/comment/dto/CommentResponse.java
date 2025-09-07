package com.list.todo.post.comment.dto;

import com.list.todo.auth.dto.UserDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String content  ;
//    private String userName;
    private UserDto user;
    private LocalDateTime createdAt;
    private Integer likes;
    private Integer disLikes;
    private String myReaction;
    private LocalDateTime updatedAt;
    private Boolean likedByMe;
    private Boolean dislikedByMe;
}
