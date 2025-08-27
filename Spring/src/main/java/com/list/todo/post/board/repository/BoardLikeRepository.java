package com.list.todo.post.board.repository;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.post.board.entity.BoardEntity;
import com.list.todo.post.board.entity.BoardLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLikeEntity, Long> {
    Optional<BoardLikeEntity> findByUserAndBoard(UserEntity user, BoardEntity board);
    Long countByBoardAndType(BoardEntity board, BoardLikeEntity.LikeType type);
}