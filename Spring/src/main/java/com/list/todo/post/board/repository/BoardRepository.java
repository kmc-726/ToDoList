package com.list.todo.post.board.repository;

import com.list.todo.post.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    List<BoardEntity> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
}