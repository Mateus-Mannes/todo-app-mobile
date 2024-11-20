package com.todo.todoapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.todo.todoapp.models.TodoLists;
import com.todo.todoapp.models.enums.StatusTodoEnum;
import com.todo.todoapp.services.ApiService;
import com.todo.todoapp.R;
import com.todo.todoapp.services.RetrofitClient;
import com.todo.todoapp.models.Todo;
import com.todo.todoapp.services.TodoAdapter;
import com.todo.todoapp.services.TokenManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        fabAdd.setOnClickListener(v -> showAddDialog());

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

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma opção");

        String[] options = {"Adicionar uma atividade", "Adicionar uma lista de atividades"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showAddTodoDialog();
                    break;
                case 1:
                    showAddListDialog();
                    break;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAddListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar Lista");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_list, null); // Layout do diálogo
        builder.setView(view);

        EditText editTextListName = view.findViewById(R.id.editTextListName);  // Campo para o nome da lista
        Button buttonSave = view.findViewById(R.id.buttonSave);  // Botão de salvar
        Button buttonCancel = view.findViewById(R.id.buttonCancel);  // Botão de cancelar

        AlertDialog dialog = builder.create();

        buttonSave.setOnClickListener(v -> {
            String listName = editTextListName.getText().toString().trim();

            if (listName.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, digite um nome para a lista", Toast.LENGTH_SHORT).show();
            } else {
                createTodoList(listName);  // Chama o método para criar a lista de tarefas
                dialog.dismiss();  // Fecha o diálogo
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());  // Fecha o diálogo se o usuário clicar em "Cancelar"

        dialog.show();  // Exibe o diálogo
    }

    private void createTodoList(String listName) {
        String token = tokenManager.getToken();
        TodoLists newList = new TodoLists();
        newList.setListName(listName);

        Call<TodoLists> call = RetrofitClient.getRetrofitInstance(this).create(ApiService.class).createTodoList(token, newList);
        call.enqueue(new Callback<TodoLists>()  {
            @Override
            public void onResponse(Call<TodoLists> call, Response<TodoLists> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MainActivity.this, "Lista criada com sucesso", Toast.LENGTH_SHORT).show();
                    fetchTodos();
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao criar a lista", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TodoLists> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Falha de conexão ao criar a lista", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTaskLists(Spinner spinnerTodoLists) {
        Call<List<TodoLists>> call = RetrofitClient.getRetrofitInstance(this)
                .create(ApiService.class)
                .getTodoLists(tokenManager.getToken());

        call.enqueue(new Callback<List<TodoLists>>() {
            @Override
            public void onResponse(Call<List<TodoLists>> call, Response<List<TodoLists>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TodoLists> taskLists = response.body();
                    List<String> listNames = new ArrayList<>();

                    for (TodoLists list : taskLists) {
                        listNames.add(list.getListName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, listNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTodoLists.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao carregar as listas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TodoLists>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Falha ao carregar as listas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddTodoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar Todo");

        // Configurar o layout do diálogo
        View view = getLayoutInflater().inflate(R.layout.dialog_add_todo, null);
        builder.setView(view);

        Spinner spinnerTodoLists = view.findViewById(R.id.spinnerTodoLists);
        EditText editTextTodo = view.findViewById(R.id.editTextTodo);
        EditText editTextDate = view.findViewById(R.id.editTextDate);
        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);
        Button buttonSave = view.findViewById(R.id.buttonSave);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        fetchTaskLists(spinnerTodoLists);

        // Configurar o Spinner de Status
        ArrayAdapter<StatusTodoEnum> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, StatusTodoEnum.values());
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Configurar DatePicker para o campo de data
        editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                editTextDate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        AlertDialog dialog = builder.create();

        // Salvar
        buttonSave.setOnClickListener(v -> {
            String todoText = editTextTodo.getText().toString();
            String dateText = editTextDate.getText().toString();
            StatusTodoEnum selectedStatus = (StatusTodoEnum) spinnerStatus.getSelectedItem();
            String selectedListName = (String) spinnerTodoLists.getSelectedItem();

            LocalDate targetDate = null;
            // Validação dos campos
            if (todoText.isEmpty()) {
                Toast.makeText(this, "Por favor, digite um texto para o todo", Toast.LENGTH_SHORT).show();
            } else if (selectedListName == null || selectedListName.isEmpty()) {
                Toast.makeText(this, "Selecione uma lista para adicionar o todo", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    if (!dateText.isEmpty()) {
                        targetDate = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    }

                    createTodo(todoText, targetDate, selectedStatus, selectedListName);
                    dialog.hide();
                } catch (DateTimeParseException e) {
                    editTextDate.setError("Formato de data inválido!");
                }
            }
        });

        // Cancelar
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }



    private void createTodo(String todoText, LocalDate targetDate, StatusTodoEnum status, String listName) {
        Todo newTodo = new Todo();
        newTodo.setText(todoText);
        newTodo.setTargetDate(targetDate);
        newTodo.setStatus(status);
        newTodo.setListName(listName);

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
