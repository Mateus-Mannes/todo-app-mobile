package com.todo.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginButton;

    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.tokenManager = new TokenManager(this);
        var tokenAtual = tokenManager.getToken();
        if(tokenAtual != null) {
            validateToken(tokenAtual);
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                // Validação básica
                if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    var apiService = RetrofitClient.getRetrofitInstance(LoginActivity.this).create(ApiService.class);
                    // Criar o Map para enviar as credenciais
                    Map<String, String> credentials = new HashMap<>();
                    credentials.put("email", userEmail);
                    credentials.put("password", userPassword);

                    // Fazer a chamada à API com o Map
                    Call<Map<String, Object>> call = apiService.loginUser(credentials);
                    call.enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Map<String, Object> responseBody = response.body();
                                String token = (String) responseBody.get("token");
                                tokenManager.saveToken(token);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Falha de conexão", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        TextView cadastroLink = findViewById(R.id.cadastroLink);
        cadastroLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validateToken(String token) {
        var apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);

        Call<Map<String, Object>> call = apiService.check(token);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if(response.code() == 401) {
                    Toast.makeText(LoginActivity.this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
                    tokenManager.clearToken();
                } else {
                    Toast.makeText(LoginActivity.this, "Erro de conexão, teste novamente.", Toast.LENGTH_SHORT).show();
                    tokenManager.clearToken();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erro de conexão, teste novamente.", Toast.LENGTH_SHORT).show();
                tokenManager.clearToken();
            }
        });
    }

}