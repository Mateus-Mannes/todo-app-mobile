package com.todo.todoapi.infrastructure.todos;

import com.todo.todoapi.domain.todos.TodoLists;
import org.springframework.data.repository.CrudRepository;

public interface TodoListRepository extends CrudRepository<TodoLists, Integer> {
    Iterable<TodoLists> getAllByUserId(Integer userId);
}