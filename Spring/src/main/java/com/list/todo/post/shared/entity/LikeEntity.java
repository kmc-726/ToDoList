package com.list.todo.post.shared.entity;

import com.list.todo.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "entity_type")
@Getter
@Setter
public abstract class LikeEntity<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private LikeType type;

    @Column(name = "entity_type", insertable = false, updatable = false)
    private String entityType;

    public enum LikeType {
        LIKE, DISLIKE
    }

    public abstract void setEntity(T entity);

    public abstract T getEntity();
}
