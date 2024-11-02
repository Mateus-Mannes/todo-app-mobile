package com.todo.todoapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private List<String> todoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar lista de dados
        todoList = new ArrayList<>();
        todoList.add("Tarefa 1");
        todoList.add("Tarefa 2");
        todoList.add("Tarefa 3");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar o adapter
        adapter = new TodoAdapter(todoList, position -> {
            // Ação para o botão de deletar
            todoList.remove(position);
            adapter.notifyItemRemoved(position);
        });

        recyclerView.setAdapter(adapter);
    }
}
