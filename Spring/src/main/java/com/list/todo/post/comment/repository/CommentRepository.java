package com.list.todo.post.comment.repository;

import com.list.todo.post.comment.entity.CommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByBoardIdAndDeletedFalseOrderByCreatedAtDesc(Long boardID, Pageable pageable);
}
