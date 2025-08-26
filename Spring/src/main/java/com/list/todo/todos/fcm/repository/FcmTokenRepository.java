package com.list.todo.todos.fcm.repository;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.todos.fcm.entity.FcmTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmTokenEntity, Long> {
    Optional<FcmTokenEntity> findByToken(String token);
    List<FcmTokenEntity> findAllByUser(UserEntity user);
    List<FcmTokenEntity> findAllByUserAndIsEnabledTrue(UserEntity user);
    Optional<FcmTokenEntity> findByIdAndUser(Long id, UserEntity user);
    int countByUser(UserEntity user);
}
