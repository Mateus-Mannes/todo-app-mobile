package com.todo.todoapi.controller.todo;

import com.todo.todoapi.controller.ApiResponse;
import com.todo.todoapi.domain.todos.Todo;
import com.todo.todoapi.domain.todos.TodoLists;
import com.todo.todoapi.domain.todos.TodoTodoList;
import com.todo.todoapi.infrastructure.todos.TodoListRepository;
import com.todo.todoapi.infrastructure.todos.TodoRepository;
import com.todo.todoapi.infrastructure.todos.TodoTodoListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class TodoController {

    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;
    private final TodoTodoListRepository todoTodoListRepository;

    public TodoController (
        TodoRepository todoRepository,
        TodoListRepository todoListRepository,
        TodoTodoListRepository todoTodoListRepository) {
        this.todoRepository = todoRepository;
        this.todoListRepository = todoListRepository;
        this.todoTodoListRepository = todoTodoListRepository;
    }

    @GetMapping("/todos")
    public Iterable<Todo> getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();
        return this.todoRepository.getAllByUserId(Integer.parseInt(userId));
    }

    @PostMapping("/todos/{todoId}/add-to-list/{listId}")
    public ResponseEntity<ApiResponse> addTodoToList(@PathVariable Integer todoId, @PathVariable Integer listId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();

        TodoLists todoList = todoListRepository.findById(listId).orElse(null);
        Todo todo = todoRepository.findById(todoId).orElse(null);

        if (todoList == null || todo == null || !todoList.getUserId().equals(Integer.parseInt(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("Operação não permitida", false));
        }

        // Criar a associação
        TodoTodoList todoTodoList = new TodoTodoList();
        todoTodoList.setTodo(todo);
        todoTodoList.setTodoList(todoList);
        todoTodoListRepository.save(todoTodoList);

        return ResponseEntity.ok(new ApiResponse("Tarefa adicionada à lista com sucesso", true));
    }

    @PostMapping("/todos")
    public Todo add(@RequestBody Todo todo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();
        todo.setUserId(Integer.parseInt(userId));

        todo.getTodoList().setUserId(Integer.parseInt(userId));

        TodoTodoList todoTodoList = new TodoTodoList();
        todoTodoList.setTodo(todo);
        todoTodoList.setTodoList(todo.getTodoList());

        todo.getTodoList().getTodoTodoLists().add(todoTodoList);

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

    // Endpoint para obter todas as listas de tarefas do usuário
    @GetMapping("/todolists")
    public Iterable<TodoLists> getAllLists() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();
        return this.todoListRepository.getAllByUserId(Integer.parseInt(userId));
    }

    @PostMapping("/todolists")
    public TodoLists addList(@RequestBody TodoLists todoList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();
        todoList.setUserId(Integer.parseInt(userId));
        return this.todoListRepository.save(todoList);
    }

    @DeleteMapping("/todolists/{id}")
    public ResponseEntity<?> deleteList(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();

        TodoLists todoList = todoListRepository.findById(id).orElse(null);
        if (todoList == null || !todoList.getUserId().equals(Integer.parseInt(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Operação não permitida");
        }

        todoListRepository.delete(todoList);
        return ResponseEntity.ok(new ApiResponse("Lista de tarefas deletada com sucesso", true));
    }

    @PostMapping("/todolists/{listId}/todos")
    public Todo addTodoToList(@PathVariable Integer listId, @RequestBody Todo todo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getDetails();

        TodoLists todoList = todoListRepository.findById(listId).orElse(null);
        if (todoList == null || !todoList.getUserId().equals(Integer.parseInt(userId))) {
            return null;
        }

        todo.setTodoList(todoList);
        return todoRepository.save(todo);
    }
}
