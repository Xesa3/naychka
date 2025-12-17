package com.example.healthapp.Pacients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapp.R;
import com.example.healthapp.model.Patient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    public interface OnPatientClickListener {
        void onPatientClick(Patient patient);
    }
    public interface OnPatientMoveListener {
        void onPatientMove(int fromPosition, int toPosition);
    }

    private OnPatientMoveListener moveListener;


    private OnPatientClickListener listener;

    public List<Patient> patients;       // текущий отображаемый список
    private List<Patient> originalList;  // полный список для фильтрации

    // Конструктор принимает начальный список пациентов
    public PatientAdapter(List<Patient> patients, OnPatientClickListener listener) {
        this.patients = new ArrayList<>(patients);
        this.originalList = new ArrayList<>(patients);
        this.listener = listener;
    }



    // Обновляем список пациентов и уведомляем RecyclerView, что данные изменились
    public void updateList(List<Patient> newList) {
        this.patients = new ArrayList<>(newList);
        this.originalList = new ArrayList<>(newList);
        notifyItemRangeChanged(0, patients.size()); // заставляет RecyclerView перерисовать все элементы
    }

    // Фильтруем по имени
    public void filter(String query) {
        patients.clear();
        if (query.isEmpty()) {
            patients.addAll(originalList);
        } else {
            query = query.toLowerCase();
            for (Patient patient : originalList) {
                if (patient.getFoolName().toLowerCase().contains(query)) {
                    patients.add(patient);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setOnPatientMoveListener(OnPatientMoveListener listener) {
        this.moveListener = listener;
    }

    // Перемещаем элемент и обновляем оригинальный список
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) return;

        // забираем элемент
        Patient item = patients.remove(fromPosition);

        // вставляем его на новое место
        patients.add(toPosition, item);

        notifyItemMoved(fromPosition, toPosition);


    }



    // Создаёт новый ViewHolder (мини-карточку) при необходимости
    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    // Заполняет ViewHolder данными пациента
    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patients.get(position);
        holder.fullNameTextView.setText(patient.getFoolName());

        String created = holder.itemView.getContext()
                .getString(R.string.field_create_date, patient.getCreatedAt());

        holder.birthDateTextView.setText(created);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onPatientClick(patient);
        });
    }

    // Возвращает количество элементов в списке
    @Override
    public int getItemCount() {
        return patients.size();
    }

    // Класс ViewHolder хранит ссылки на TextView мини-карточки
    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView;
        TextView birthDateTextView;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            birthDateTextView = itemView.findViewById(R.id.birthDateTextView);
        }
    }
}

