package com.todo.todoapi.domain.todos;

import jakarta.persistence.*;
import com.todo.todoapi.domain.enums.*;
import java.time.LocalDate;

@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    private String text;

    private LocalDate targetDate;

    private StatusTodoEnum status;

    @ManyToOne
    @JoinColumn(name = "list_id")
    private TodoLists todoList;

    public Todo() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public StatusTodoEnum getStatus() {
        return status;
    }

    public void setStatus(StatusTodoEnum status) {
        this.status = status;
    }

    public TodoLists getTodoList() {
        return todoList;
    }

    public void setTodoList(TodoLists todoList) {
        this.todoList = todoList;
    }
}
