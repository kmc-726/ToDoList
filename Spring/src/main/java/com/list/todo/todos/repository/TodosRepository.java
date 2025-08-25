package com.list.todo.todos.repository;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.todos.entity.TodosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodosRepository extends JpaRepository<TodosEntity, Long> {
    List<TodosEntity> findByUser(UserEntity user);
}
