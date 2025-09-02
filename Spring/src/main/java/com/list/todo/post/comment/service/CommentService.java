package com.list.todo.post.comment.service;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.post.board.entity.BoardEntity;
import com.list.todo.post.board.repository.BoardRepository;
import com.list.todo.post.comment.dto.CommentDto;
import com.list.todo.post.comment.dto.CommentResponse;
import com.list.todo.post.comment.entity.CommentEntity;
import com.list.todo.post.comment.repository.CommentRepository;
import com.list.todo.post.shared.entity.LikeEntity;
import com.list.todo.post.shared.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final LikeService likeService;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public CommentResponse createComment(CommentDto dto, String loginId) {

        BoardEntity board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 댓글 생성 로직
        CommentEntity comment = new CommentEntity();
        comment.setContent(dto.getContent());
        comment.setBoard(board);
        comment.setUser(user);

        // 댓글 저장
        commentRepository.save(comment);
        return toResponse(comment);
    }

    public CommentResponse updateComment(Long commentId, CommentDto dto, String loginId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(dto.getContent());
        commentRepository.save(comment);
        return toResponse(comment);
    }

    public Page<CommentResponse> getCommentList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CommentEntity> boards = commentRepository.findAll(pageable);

        return boards.map(this::toResponse);
    }

    public void deleteComment(Long commentId, String loginId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }

    private CommentResponse toResponse(CommentEntity comment) {
        CommentResponse res = new CommentResponse();
        res.setId(comment.getId());
        res.setContent(comment.getContent());
        res.setUserName(comment.getUser().getUserName());
        res.setCreatedAt(comment.getCreatedAt());
        res.setUpdatedAt(comment.getUpdatedAt());

//        Map<String, Long> likeCount = likeService.getLikeCount
        Map<String, Integer> likeCounts = likeService.getLikeCount(comment.getId(), "COMMENT");
        res.setLikes(likeCounts.getOrDefault("like", 0));
        res.setDisLikes(likeCounts.getOrDefault("dislike", 0));
        // 추가적인 속성
        return res;
    }

    // 좋아요/싫어요 처리
    public String handleLike(Long commentId, String loginId, LikeEntity.LikeType type) {
        return likeService.handleLike(commentId, loginId, type, "COMMENT");
    }

}
