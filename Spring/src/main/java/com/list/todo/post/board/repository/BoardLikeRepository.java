package com.list.todo.post.board.repository;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.post.board.entity.BoardEntity;
import com.list.todo.post.board.entity.BoardLikeEntity;
import com.list.todo.post.shared.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLikeEntity, Long> {
    Optional<BoardLikeEntity> findByUserAndBoard(UserEntity user, BoardEntity board);
    void deleteByUserAndBoard(UserEntity user, BoardEntity board);
    int countByBoardAndType(BoardEntity board, LikeEntity.LikeType type);
}