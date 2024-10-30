package com.todo.todoapp;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/identity/login")
    Call<Map<String, Object>> loginUser(@Body Map<String, String> credentials);
}

