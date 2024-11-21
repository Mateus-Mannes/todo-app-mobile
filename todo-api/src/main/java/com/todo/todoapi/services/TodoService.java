package com.todo.todoapi.services;

import com.todo.todoapi.domain.enums.StatusTodoEnum;
import com.todo.todoapi.domain.todos.Todo;
import com.todo.todoapi.infrastructure.todos.TodoRepository;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public void updateStatus(Integer todoId, Integer userId, StatusTodoEnum newStatus) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        if (!todo.getUserId().equals(userId)) {
            throw new RuntimeException("Operação não permitida");
        }

        todo.setStatus(newStatus);
        todoRepository.save(todo);
    }
}
