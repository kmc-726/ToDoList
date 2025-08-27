package com.list.todo.post.comment.service;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.post.board.entity.BoardEntity;
import com.list.todo.post.board.repository.BoardRepository;
import com.list.todo.post.comment.dto.CommentDto;
import com.list.todo.post.comment.dto.CommentResponse;
import com.list.todo.post.comment.entity.CommentEntity;
import com.list.todo.post.comment.entity.CommentLikeEntity;
import com.list.todo.post.comment.repository.CommentLikeRepository;
import com.list.todo.post.comment.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public CommentResponse createComment(CommentDto dto, String loginId) {
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        BoardEntity board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));


        CommentEntity comment = new CommentEntity();
        comment.setContent(dto.getContent());
        comment.setUser(user);
        comment.setBoard(board);

        CommentEntity saved = commentRepository.save(comment);

        return toResponse(saved);
    }

    @Transactional
    public List<CommentResponse> getComments(Long boardId, Integer page, Integer size, String loginId) {
        Pageable pageable = PageRequest.of(page, size);
        List<CommentEntity> comments = commentRepository.findByBoardIdAndDeletedFalseOrderByCreatedAtDesc(boardId, pageable);

        return comments.stream()
                .map(comment -> loginToResponse(comment, loginId))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentDto dto, String loginId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getLoginId().equals(loginId)) {
            throw new RuntimeException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(dto.getContent());
        return toResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, String loginId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getLoginId().equals(loginId)) {
            throw new RuntimeException("본인의 댓글만 삭제할 수 있습니다.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    private CommentResponse toResponse(CommentEntity entity) {
        return loginToResponse(entity, null);
    }

    private CommentResponse loginToResponse(CommentEntity entity, String loginId) {
        CommentResponse response = new CommentResponse();
        response.setId(entity.getId());
        response.setContent(entity.getContent());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setUserName(entity.getUser().getUserName());

        int likes = commentLikeRepository.countByCommentAndType(entity, CommentLikeEntity.LikeType.LIKE).intValue();
        int dislikes = commentLikeRepository.countByCommentAndType(entity, CommentLikeEntity.LikeType.DISLIKE).intValue();
        response.setLikes(likes);
        response.setDisLikes(dislikes);

        // 로그인한 유저의 반응 조회
        userRepository.findByLoginId(loginId)
                .flatMap(user -> commentLikeRepository.findByUserAndComment(user, entity))
                .ifPresent(likeEntity -> response.setMyReaction(likeEntity.getType().name()));

        return response;
    }
}
