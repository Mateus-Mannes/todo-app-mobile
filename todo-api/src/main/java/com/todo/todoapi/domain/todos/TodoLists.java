package com.todo.todoapi.domain.todos;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "TodoLists")
public class TodoLists {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String listName;

    @OneToMany(mappedBy = "todoList")
    private List<TodoTodoList> todoTodoLists;

    public TodoLists() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<TodoTodoList> getTodoTodoLists() {
        return todoTodoLists;
    }

    public void setTodoTodoLists(List<TodoTodoList> todoTodoLists) {
        this.todoTodoLists = todoTodoLists;
    }
}
