package com.todo.todoapi.domain.todos;

import jakarta.persistence.*;
import com.todo.todoapi.domain.enums.*;
import com.todo.todoapi.domain.todos.Todo;

@Entity
@Table(name = "TodoTodoList")
public class TodoTodoList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "list_id", nullable = false)
    private TodoLists todoList;
    
    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    public TodoTodoList() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TodoLists getTodoList() {
        return todoList;
    }

    public void setTodoList(TodoLists todoList) {
        this.todoList = todoList;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }
}
