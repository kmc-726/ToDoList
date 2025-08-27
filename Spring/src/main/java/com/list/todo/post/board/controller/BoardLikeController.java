package com.list.todo.post.board.controller;

import com.list.todo.post.board.service.BoardLikeService;
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
@RequestMapping("/boards/{boardId}")
public class BoardLikeController {

    private final BoardLikeService boardLikeService;

    @PostMapping("/like")
    public ResponseEntity<String> like(@PathVariable Long boardId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        String result = boardLikeService.likeBoard(boardId, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/dislike")
    public ResponseEntity<String> dislike(@PathVariable Long boardId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        String result = boardLikeService.dislikeBoard(boardId, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }
}