package com.todo.todoapp;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.code() == 401 && !chain.request().url().encodedPath().equals("/identity/check")) {
            // Se a resposta for 401, redireciona para a tela de login
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpa a pilha de atividades
            context.startActivity(intent);
        }

        return response;
    }
}
