package com.list.todo.todos.entity;

import com.list.todo.auth.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "fcm_token")
@Entity
@Getter
@Setter
public class FcmTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column
    private String deviceInfo; // ex. "Chrome on Windows", optional

    @Column(nullable = false)
    private boolean isEnabled = true;

    @Column
    private LocalDateTime lastUpdated;
}
