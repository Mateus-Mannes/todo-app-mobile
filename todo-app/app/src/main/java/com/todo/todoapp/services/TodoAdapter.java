package com.todo.todoapp.services;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.todoapp.R;
import com.todo.todoapp.activities.MainActivity;
import com.todo.todoapp.models.Todo;
import com.todo.todoapp.models.enums.StatusTodoEnum;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private ArrayList<Todo> todoList;
    private OnDeleteClickListener onDeleteClickListener;
    private Context context;

    public TodoAdapter(ArrayList<Todo> todoList, OnDeleteClickListener onDeleteClickListener, Context context) {
        this.todoList = todoList;
        this.onDeleteClickListener = onDeleteClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);

        // Lista
        holder.listNameTextView.setText("Lista: " + todo.getTodoList().getListName());

        // Título
        holder.titleTextView.setText(todo.getText());

        // Data limite formatada
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = todo.getTargetDate() != null ? todo.getTargetDate().format(formatter) : "Sem data";
        holder.targetDateTextView.setText("Data Limite: " + formattedDate);

        // Status
        String status = todo.getStatus() != null ? todo.getStatus().name() : "Desconhecido";
        holder.statusTextView.setText("Status: " + status);

        // Botão de deletar
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(position));

        // Botão de editar status
        holder.itemView.setOnClickListener(v -> {
            showStatusDialog(todo.getId(), todo.getStatus());
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView listNameTextView;
        TextView titleTextView;
        TextView targetDateTextView;
        TextView statusTextView;
        ImageButton deleteButton;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            listNameTextView = itemView.findViewById(R.id.listName);
            titleTextView = itemView.findViewById(R.id.title);
            targetDateTextView = itemView.findViewById(R.id.targetDate);
            statusTextView = itemView.findViewById(R.id.status);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    private void showStatusDialog(int todoId, StatusTodoEnum currentStatus) {
        StatusTodoEnum[] statuses = StatusTodoEnum.values();
        int currentIndex = -1;

        // Encontrar o índice do status atual no enum
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i] == currentStatus) {
                currentIndex = i;
                break;
            }
        }

        // Exibir o diálogo para selecionar o novo status
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alterar Status")
                .setSingleChoiceItems(
                        Arrays.stream(statuses)
                                .map(StatusTodoEnum::getDescricao) // Obter descrição do status
                                .toArray(CharSequence[]::new),   // Converter para array de CharSequence
                        currentIndex, (dialog, which) -> {
                            StatusTodoEnum newStatus = statuses[which];
                            // Aqui chamamos o método que deve ser definido na Activity
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).updateTodoStatus(todoId, newStatus);
                            }
                            dialog.dismiss();
                        })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}