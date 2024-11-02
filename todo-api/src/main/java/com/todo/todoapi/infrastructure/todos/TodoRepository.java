package com.todo.todoapi.infrastructure.todos;

import com.todo.todoapi.domain.identity.User;
import com.todo.todoapi.domain.todos.Todo;
import com.todo.todoapi.infrastructure.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends CrudRepository<Todo, Integer> {
    List<Todo> getAllByUserId(Integer userId);
}