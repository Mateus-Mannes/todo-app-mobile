package com.todo.todoapi.infrastructure.todos;

import com.todo.todoapi.domain.todos.TodoLists;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TodoListRepository extends CrudRepository<TodoLists, Integer> {
    Iterable<TodoLists> getAllByUserId(Integer userId);

    Optional<TodoLists> findByListNameAndUserId(String name, Integer userId);
}