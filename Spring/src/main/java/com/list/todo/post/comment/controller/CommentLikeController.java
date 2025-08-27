package com.list.todo.post.comment.controller;

import com.list.todo.post.comment.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/{commentId}")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/like")
    public ResponseEntity<String> like(@PathVariable Long commentId,
                                       @AuthenticationPrincipal UserDetails userDetails){
        String result = commentLikeService.likeComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/dislike")
    public ResponseEntity<String> dislike(@PathVariable Long commentId,
                                          @AuthenticationPrincipal UserDetails userDetails){
        String result = commentLikeService.dislikeComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }
}
