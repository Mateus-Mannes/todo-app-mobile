package com.todo.todoapi.infrastructure.todos;

import com.todo.todoapi.domain.todos.Todo;
import com.todo.todoapi.infrastructure.CrudRepository;

public interface TodoRepository extends CrudRepository<Todo, Integer> { }