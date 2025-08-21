package com.list.todo.auth.repository;

import com.list.todo.auth.entity.RefreshTokenEntity;
import com.list.todo.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByUser(UserEntity user);
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUser(UserEntity user);
}