package com.list.todo.post.comment.dto;

import com.list.todo.post.comment.entity.CommentEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String content  ;
    private String userName;
    private LocalDateTime createdAt;
    private Integer likes;
    private Integer disLikes;
    private String myReaction;
    private LocalDateTime updatedAt;

}
