package com.todo.todoapp.services;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.todoapp.R;
import com.todo.todoapp.models.Todo;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private ArrayList<Todo> todoList;
    private OnDeleteClickListener onDeleteClickListener;

    public TodoAdapter(ArrayList<Todo> todoList, OnDeleteClickListener onDeleteClickListener) {
        this.todoList = todoList;
        this.onDeleteClickListener = onDeleteClickListener;
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

        //Lista
        holder.listNameTextView.setText("Lista: " + todo.getListName());

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
}