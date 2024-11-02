package com.todo.todoapi.controller.todo;

import com.todo.todoapi.domain.todos.Todo;
import com.todo.todoapi.infrastructure.todos.TodoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class TodoController {

    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping("/todos")
    public Iterable<Todo> getAll() {
        return this.todoRepository.findAll();
    }


    @PostMapping("/todos")
    public Todo add(@RequestBody Todo todo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();
        todo.setUserId(Integer.parseInt(userId));
        return this.todoRepository.save(todo);
    }
}
