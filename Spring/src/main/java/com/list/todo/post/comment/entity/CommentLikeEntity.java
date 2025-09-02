package com.list.todo.post.comment.entity;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.post.shared.entity.LikeEntity;
import com.list.todo.post.comment.entity.CommentEntity;
import jakarta.persistence.*;

@Entity
public class CommentLikeEntity extends LikeEntity<CommentEntity> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity comment;

    @Override
    public void setEntity(CommentEntity comment) {
        this.comment = comment;
        this.setEntityType("COMMENT");  // entityType을 설정
    }

    @Override
    public CommentEntity getEntity() {
        return this.comment;
    }
}
