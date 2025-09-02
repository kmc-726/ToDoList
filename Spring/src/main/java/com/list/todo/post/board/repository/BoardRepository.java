package com.list.todo.post.board.repository;

import com.list.todo.post.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    List<BoardEntity> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
}