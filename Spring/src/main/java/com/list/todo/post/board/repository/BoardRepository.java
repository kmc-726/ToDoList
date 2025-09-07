package com.list.todo.post.board.repository;

import com.list.todo.post.board.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    Page<BoardEntity> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    List<BoardEntity> findAllByDeletedTrueAndDeletedAtBefore(LocalDateTime cutoffDate);
}