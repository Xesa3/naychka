package com.example.java_logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OperationAdapter extends RecyclerView.Adapter<OperationAdapter.OperationViewHolder> {

    private List<Operation> operationList; // Список операций

    // Конструктор адаптера
    public OperationAdapter(List<Operation> operationList) {
        this.operationList = operationList;
    }

    // Метод для создания нового ViewHolder
    @NonNull
    @Override
    public OperationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Инфлятируем layout для каждого элемента
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operation, parent, false);
        return new OperationViewHolder(view); // Возвращаем новый ViewHolder
    }

    // Метод для привязки данных к элементам UI
    @Override
    public void onBindViewHolder(@NonNull OperationViewHolder holder, int position) {
        Operation operation = operationList.get(position); // Получаем операцию из списка
        holder.typeTextView.setText(operation.getType());
        holder.priceTextView.setText(String.format("$%.2f", operation.getPrice()));
        holder.commentTextView.setText(operation.getComment());
    }

    // Метод для получения общего количества элементов
    @Override
    public int getItemCount() {
        return operationList.size(); // Возвращаем количество операций в списке
    }

    // Внутренний класс ViewHolder для одного элемента списка
    public static class OperationViewHolder extends RecyclerView.ViewHolder {
        TextView typeTextView;
        TextView priceTextView;
        TextView commentTextView;

        public OperationViewHolder(View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.textViewType);
            priceTextView = itemView.findViewById(R.id.textViewAmount);
            commentTextView = itemView.findViewById(R.id.textViewComment);
        }
    }

}
