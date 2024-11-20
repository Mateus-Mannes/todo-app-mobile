package com.todo.todoapp.models;

import com.google.gson.annotations.SerializedName;

public class TodoLists {
    private Integer id;

    private Integer userId;

    @SerializedName("listName")
    private String listName;

    public TodoLists() { }

    public TodoLists(String listName) {
        this.setListName(listName);
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
}
