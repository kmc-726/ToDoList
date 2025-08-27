package com.list.todo.post.board.entity;

import com.list.todo.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
@Entity
@Table(name = "board_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "board_id"})
})
@Getter
@Setter
public class BoardLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum LikeType {
        LIKE, DISLIKE
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @CreatedDate
    private LocalDateTime createdAt;
}