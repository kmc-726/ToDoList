package com.list.todo.post.comment.repository;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.post.board.entity.BoardLikeEntity;
import com.list.todo.post.comment.entity.CommentEntity;
import com.list.todo.post.comment.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
    Optional<CommentLikeEntity> findByUserAndComment(UserEntity user, CommentEntity comment);
    Long countByCommentAndType(CommentEntity comment, CommentLikeEntity.LikeType type);

}
