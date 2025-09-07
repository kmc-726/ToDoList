package com.list.todo.post.comment.repository;

import com.list.todo.post.board.entity.BoardEntity;
import com.list.todo.post.comment.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findByBoardIdAndDeletedFalseOrderByCreatedAtDesc(Long boardID, Pageable pageable);
    List<CommentEntity> findAllByDeletedTrueAndDeletedAtBefore(LocalDateTime cutoffDate);
}
