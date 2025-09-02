package com.list.todo.post.board.entity;

import com.list.todo.post.shared.entity.LikeEntity;
import com.list.todo.post.board.entity.BoardEntity;
import jakarta.persistence.*;

@Entity
public class BoardLikeEntity extends LikeEntity<BoardEntity> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @Override
    public void setEntity(BoardEntity board) {
        this.board = board;
        this.setEntityType("BOARD");  // entityType을 설정
    }

    @Override
    public BoardEntity getEntity() {
        return this.board;
    }
}