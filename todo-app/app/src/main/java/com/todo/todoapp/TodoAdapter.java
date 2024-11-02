package com.todo.todoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<String> todoList;
    private OnDeleteClickListener onDeleteClickListener;

    public TodoAdapter(List<String> todoList, OnDeleteClickListener onDeleteClickListener) {
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
        String title = todoList.get(position);
        holder.titleTextView.setText(title);

        // Botão de deletar
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageButton deleteButton;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}
