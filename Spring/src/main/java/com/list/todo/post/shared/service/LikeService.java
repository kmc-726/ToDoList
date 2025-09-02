package com.list.todo.post.shared.service;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.repository.UserRepository;
import com.list.todo.global.exception.*;
import com.list.todo.post.board.entity.BoardEntity;
import com.list.todo.post.board.entity.BoardLikeEntity;
import com.list.todo.post.board.repository.BoardLikeRepository;
import com.list.todo.post.board.repository.BoardRepository;
import com.list.todo.post.comment.entity.CommentEntity;
import com.list.todo.post.comment.entity.CommentLikeEntity;
import com.list.todo.post.comment.repository.CommentLikeRepository;
import com.list.todo.post.comment.repository.CommentRepository;
import com.list.todo.post.shared.entity.LikeEntity;
import com.list.todo.post.shared.entity.LikeEntity.LikeType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BoardLikeRepository boardLikeRepository;  // 구체적인 BoardLikeRepository
    private final CommentLikeRepository commentLikeRepository; // 구체적인 CommentLikeRepository

    @Transactional
    public String handleLike(Long entityId, String username, LikeType type, String entityType) {
        UserEntity user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

        LikeEntity<?> likeEntity = findLikeEntity(entityId, user, entityType);

        if (likeEntity != null) {
            if (likeEntity.getType() == type) {
                // 이미 눌렀다면 취소
                removeLike(likeEntity);
                return "Like/Dislike removed!";
            } else {
                // 좋아요를 눌렀다면 싫어요로 변경
                likeEntity.setType(type);
                saveLike(likeEntity);
                return "Like/Dislike updated!";
            }
        } else {
            // 새로운 좋아요/싫어요 추가
            LikeEntity<?> newLike = createLikeEntity(entityId, user, type, entityType);
            saveLike(newLike);
            return "Like/Dislike added!";
        }
    }

    private LikeEntity<?> findLikeEntity(Long entityId, UserEntity user, String entityType) {
        if ("BOARD".equalsIgnoreCase(entityType)) {
            BoardEntity board = new BoardEntity();
            board.setId(entityId);  // id만 설정
            return boardLikeRepository.findByUserAndBoard(user, board).orElse(null);  // BoardLikeEntity 반환
        } else if ("COMMENT".equalsIgnoreCase(entityType)) {
            CommentEntity comment = new CommentEntity();
            comment.setId(entityId);  // id만 설정
            return commentLikeRepository.findByUserAndComment(user, comment).orElse(null);  // CommentLikeEntity 반환
        }
        return null;
    }

    private LikeEntity<?> createLikeEntity(Long entityId, UserEntity user, LikeType type, String entityType) {
        if ("BOARD".equalsIgnoreCase(entityType)) {
            BoardEntity board = boardRepository.findById(entityId)
                    .orElseThrow(() -> new BoardException("게시글을 찾을 수 없습니다."));
            BoardLikeEntity likeEntity = new BoardLikeEntity();
            likeEntity.setEntity(board);  // BoardEntity 설정
            likeEntity.setUser(user);
            likeEntity.setType(type);
            return likeEntity;
        } else if ("COMMENT".equalsIgnoreCase(entityType)) {
            CommentEntity comment = commentRepository.findById(entityId)
                    .orElseThrow(() -> new CommentException("댓글을 찾을 수 없습니다."));
            CommentLikeEntity likeEntity = new CommentLikeEntity();
            likeEntity.setEntity(comment);  // CommentEntity 설정
            likeEntity.setUser(user);
            likeEntity.setType(type);
            return likeEntity;
        }
        throw new UnsupportedEntityTypeException("처리할 수 없는 엔티티타입: " + entityType);
    }

    private void saveLike(LikeEntity<?> likeEntity) {
        if (likeEntity instanceof BoardLikeEntity) {
            boardLikeRepository.save((BoardLikeEntity) likeEntity);
        } else if (likeEntity instanceof CommentLikeEntity) {
            commentLikeRepository.save((CommentLikeEntity) likeEntity);
        } else {
            throw new UnsupportedLikeEntityTypeException("처리할 수 없는 엔티티타입: " + likeEntity.getClass());
        }
    }

    private void removeLike(LikeEntity<?> likeEntity) {
        if (likeEntity instanceof BoardLikeEntity boardLikeEntity) {
            boardLikeRepository.deleteByUserAndBoard(boardLikeEntity.getUser(), boardLikeEntity.getEntity());
        } else if (likeEntity instanceof CommentLikeEntity commentLikeEntity) {
            commentLikeRepository.deleteByUserAndComment(commentLikeEntity.getUser(), commentLikeEntity.getEntity());
        } else {
            throw new UnsupportedLikeEntityTypeException("처리할 수 없는 엔티티타입: " + likeEntity.getClass());

        }
    }

    public Map<String, Integer> getLikeCount(Long entityId, String entityType) {
        int likeCount = 0;
        int dislikeCount = 0;

        if ("BOARD".equalsIgnoreCase(entityType)) {
            BoardEntity board = boardRepository.findById(entityId)
                    .orElseThrow(() -> new BoardException("게시글을 찾을 수 없습니다."));

            likeCount = boardLikeRepository.countByBoardAndType(board, LikeType.LIKE);
            dislikeCount = boardLikeRepository.countByBoardAndType(board, LikeType.DISLIKE);
        } else if ("COMMENT".equalsIgnoreCase(entityType)) {
            CommentEntity comment = commentRepository.findById(entityId)
                    .orElseThrow(() -> new CommentException("댓글을 찾을 수 없습니다."));

            likeCount = commentLikeRepository.countByCommentAndType(comment, LikeType.LIKE);
            dislikeCount = commentLikeRepository.countByCommentAndType(comment, LikeType.DISLIKE);

        } else {
            throw new UnsupportedEntityTypeException("처리할 수 없는 엔티티타입: " + entityType);

        }

        return Map.of("like", likeCount, "dislike", dislikeCount);
    }
}
