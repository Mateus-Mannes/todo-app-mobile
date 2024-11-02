package com.todo.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.todo.todoapp.services.ApiService;
import com.todo.todoapp.R;
import com.todo.todoapp.services.RetrofitClient;
import com.todo.todoapp.models.Todo;
import com.todo.todoapp.services.TodoAdapter;
import com.todo.todoapp.services.TokenManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private ArrayList<Todo> todoList;
    private DrawerLayout drawerLayout;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tokenManager = new TokenManager(this);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurar DrawerLayout e NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar o botão de abrir/fechar o drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Inicializar lista de tarefas
        todoList = new ArrayList<Todo>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar o adapter
        adapter = new TodoAdapter(todoList, position -> {
            deleteTodo(todoList.get(position).getId(), position); // Deletar ao clicar
        });
        recyclerView.setAdapter(adapter);

        // Configurar o botão de adicionar
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_todo);
        fabAdd.setOnClickListener(v -> showAddTodoDialog());

        fetchTodos();
    }

    private void fetchTodos() {
        String token = tokenManager.getToken();
        var apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<List<Todo>> call = apiService.getTodos(token);
        call.enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(Call<List<Todo>> call, Response<List<Todo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Todo> todos = response.body();
                    todoList.clear();
                    for (Todo todo : todos) {
                        todoList.add(todo);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao carregar todos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Todo>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Falha de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddTodoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar Todo");

        // Configurar o layout do diálogo
        View view = getLayoutInflater().inflate(R.layout.dialog_add_todo, null);
        builder.setView(view);

        EditText editTextTodo = view.findViewById(R.id.editTextTodo);
        Button buttonSave = view.findViewById(R.id.buttonSave);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        AlertDialog dialog = builder.create();

        // Salvar
        buttonSave.setOnClickListener(v -> {
            String todoText = editTextTodo.getText().toString();
            if (!todoText.isEmpty()) {
                createTodo(todoText);
                dialog.hide();
            } else {
                Toast.makeText(this, "Por favor, digite um texto para o todo", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancelar
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void createTodo(String todoText) {
        Todo newTodo = new Todo();
        newTodo.setText(todoText);

        Call<Todo> call = RetrofitClient.getRetrofitInstance(this).create(ApiService.class).createTodo(tokenManager.getToken(), newTodo);
        call.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todoList.add(response.body());
                    adapter.notifyItemInserted(todoList.size() - 1);
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao criar o todo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTodo(Integer id, int position) {
        Call<Void> call = RetrofitClient.getRetrofitInstance(this).create(ApiService.class).deleteTodo(tokenManager.getToken(), id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    todoList.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao deletar o todo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logoff) {
            // Ação de logoff, redireciona para a tela de login
            this.tokenManager.clearToken();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
