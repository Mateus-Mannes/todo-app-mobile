package com.todo.todoapi.controller.todo;

import com.todo.todoapi.controller.ApiResponse;
import com.todo.todoapi.domain.todos.Todo;
import com.todo.todoapi.infrastructure.todos.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();
        return this.todoRepository.getAllByUserId(Integer.parseInt(userId));
    }

    @PostMapping("/todos")
    public Todo add(@RequestBody Todo todo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();
        todo.setUserId(Integer.parseInt(userId));
        return this.todoRepository.save(todo);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();

        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo == null || !todo.getUserId().equals(Integer.parseInt(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Operação não permitida");
        }

        todoRepository.delete(todo);
        return ResponseEntity.ok(new ApiResponse("Todo deletado com sucesso", true));
    }
}
