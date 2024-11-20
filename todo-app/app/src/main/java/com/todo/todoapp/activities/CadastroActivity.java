package com.todo.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.todo.todoapp.services.ApiService;
import com.todo.todoapp.R;
import com.todo.todoapp.services.RetrofitClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {

    EditText name, email, password;
    Button registrarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.nome);

        registrarButton = findViewById(R.id.loginButton);

        registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                String userName = name.getText().toString();

                // Validação básica
                if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(CadastroActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    var apiService = RetrofitClient.getRetrofitInstance(CadastroActivity.this).create(ApiService.class);
                    // Criar o Map para enviar as credenciais
                    Map<String, String> cadastro = new HashMap<>();
                    cadastro.put("name", userName);
                    cadastro.put("email", userEmail);
                    cadastro.put("password", userPassword);

                    // Fazer a chamada à API com o Map
                    Call<Map<String, Object>> call = apiService.registrarUser(cadastro);
                    call.enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(CadastroActivity.this, "Cadastro realizado! Realize o login.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                var responseBody = response.errorBody();
                                if(responseBody != null) {
                                    try {
                                        Toast.makeText(CadastroActivity.this, responseBody.string(), Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                else Toast.makeText(CadastroActivity.this, "Erro no cadastro", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            Toast.makeText(CadastroActivity.this, "Falha de conexão", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}