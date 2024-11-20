package com.todo.todoapi.infrastructure.todos;

import com.todo.todoapi.domain.todos.TodoTodoList;
import org.springframework.data.repository.CrudRepository;

public interface TodoTodoListRepository extends CrudRepository<TodoTodoList, Integer> {
    Iterable<TodoTodoList> getAllByTodoListId(Integer listId);
}