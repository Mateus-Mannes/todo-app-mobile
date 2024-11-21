package com.todo.todoapp.services;

import com.todo.todoapp.models.Todo;
import com.todo.todoapp.models.TodoLists;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/identity/login")
    Call<Map<String, Object>> loginUser(@Body Map<String, String> credentials);

    @POST("/identity/register")
    Call<Map<String, Object>> registrarUser(@Body Map<String, String> cadastro);

    @GET("/identity/check")
    Call<Map<String, Object>> check(@Header("Authorization") String token);

    @GET("/todos")
    Call<List<Todo>> getTodos(@Header("Authorization") String token);

    @POST("/todos")
    Call<Todo> createTodo(@Header("Authorization") String token, @Body Todo todo);

    @DELETE("/todos/{id}")
    Call<Void> deleteTodo(@Header("Authorization") String token, @Path("id") Integer id);

    @GET("/todolists")
    Call<List<TodoLists>> getTodoLists(@Header("Authorization") String token);

    @POST("/todolists")
    Call<TodoLists> createTodoList(@Header("Authorization") String token, @Body TodoLists todoList);

    @GET("/todolists/{listName}")
    Call<TodoLists> getList(@Header("Authorization") String token, @Path("listName") String listName);
}

