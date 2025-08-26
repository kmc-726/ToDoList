package com.list.todo.todos.repository;

import com.list.todo.todos.entity.RemindersEntity;
import com.list.todo.todos.entity.TodosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<RemindersEntity, Long> {
    @Query("SELECT r FROM RemindersEntity r " +
            "JOIN FETCH r.todos t " +
            "JOIN FETCH t.user u " +
            "WHERE r.sent = false AND r.remindAt <= :now")
    List<RemindersEntity> findBySentFalseAndRemindAtBefore(LocalDateTime now);
    Optional<RemindersEntity> findByTodos(TodosEntity todos);

}
