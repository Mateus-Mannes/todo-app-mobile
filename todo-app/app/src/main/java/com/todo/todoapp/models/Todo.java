package com.todo.todoapp.models;

import com.google.gson.annotations.SerializedName;
import com.todo.todoapp.models.enums.StatusTodoEnum;

import java.time.LocalDate;

public class Todo {
    private Integer id;

    private Integer userId;

    private String text;

    @SerializedName("targetDate")
    private LocalDate targetDate;

    private StatusTodoEnum status;

    @SerializedName("listName")
    private String listName;

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

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
