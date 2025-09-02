package com.list.todo.post.shared.controller;

import com.list.todo.post.shared.service.LikeService;
import com.list.todo.post.shared.entity.LikeEntity.LikeType;
import com.list.todo.post.shared.entity.LikeEntity.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    // 댓글 좋아요/싫어요 처리
    @PostMapping("/comment/{commentId}/{type}")
    public ResponseEntity<String> handleCommentLike(@PathVariable Long commentId,
                                                    @PathVariable LikeType type,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        String result = likeService.handleLike(commentId, userDetails.getUsername(), type, "COMMENT");
        return ResponseEntity.ok(result);
    }

    // 게시글 좋아요/싫어요 처리
    @PostMapping("/board/{boardId}/{type}")
    public ResponseEntity<String> handleBoardLike(@PathVariable Long boardId,
                                                  @PathVariable LikeType type,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        String result = likeService.handleLike(boardId, userDetails.getUsername(), type, "BOARD");
        return ResponseEntity.ok(result);
    }
}
