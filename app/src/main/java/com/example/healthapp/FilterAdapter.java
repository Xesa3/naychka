package com.example.healthapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<String> filters;
    private OnFilterClickListener listener;

    public interface OnFilterClickListener {
        void onClick(String filter);
    }

    public FilterAdapter(List<String> filters, OnFilterClickListener listener) {
        this.filters = filters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String filter = filters.get(position);
        holder.text.setText(filter);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(filter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.filterText);
        }
    }



}
