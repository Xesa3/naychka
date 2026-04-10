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
import java.util.Locale;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    public interface OnPatientClickListener {
        void onPatientClick(Patient patient);
    }

    private OnPatientClickListener clicklistener;

    // отображаемый список (после фильтра)
    private final List<Patient> patients = new ArrayList<>();
    // полный список (источник для фильтра)
    private final List<Patient> originalList = new ArrayList<>();

    // текущий поисковый запрос (нужен, чтобы понимать включён фильтр или нет)
    private String currentQuery = "";

    // Конструктор принимает начальныйсписок пациентов
    public PatientAdapter(List<Patient> initial, OnPatientClickListener listener) {
        this.clicklistener = listener;
        updateList(initial);
    }

    // Обновляем список пациентов и уведомляем RecyclerView, что данные изменились
    // Полный обновляемый список (пришёл из ViewModel)
    public void updateList(List<Patient> newList) {
        originalList.clear();
        if(newList!= null) originalList.addAll(newList);
        // если фильтр не активен — просто показываем всё
        // если фильтр активен — пересчитываем отображаемый список по currentQuery
        applyFilterInternal(currentQuery);

        notifyDataSetChanged();
    }

    // Фильтруем по ФИО
    public void filter(String query) {
        currentQuery = (query == null) ? "" : query.trim().toLowerCase(Locale.ROOT);
        applyFilterInternal(currentQuery);
        notifyDataSetChanged();
    }

    // true, если сейчас показан фильтрованный список
    public boolean isFilterActive() {
        return currentQuery != null && !currentQuery.isEmpty();
    }

    // Текущий список в правильном порядке (копия) — чтобы фрагмент мог сохранить порядок
    public List<Patient> getCurrentPatients() {
        return new ArrayList<>(patients);
    }

    // Перемещение (разрешаем только если фильтр НЕ активен)
    public void moveItem(int fromPosition, int toPosition) {
        if (isFilterActive()) return;  // запрещаем drag при фильтре
        if (fromPosition == toPosition) return;
        if (fromPosition < 0 || toPosition < 0) return;
        if (fromPosition >= patients.size() || toPosition >= patients.size()) return;

        Patient item = patients.remove(fromPosition);
        patients.add(toPosition, item);

        // синхронизируем originalList, чтобы порядок не "откатился"
        originalList.clear();
        originalList.addAll(patients);

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

        String fullName = safe(patient.getFoolName());
        holder.fullNameTextView.setText(fullName);

        String created = holder.itemView.getContext()
                .getString(R.string.field_create_date, safe(patient.getCreatedAt()));

        holder.birthDateTextView.setText(created);

        holder.itemView.setOnClickListener(v -> {
            if (clicklistener != null)
                clicklistener.onPatientClick(patient);
        });
    }

    // ---------------- Helpers ----------------

    // Возвращает количество элементов в списке
    @Override
    public int getItemCount() {
        return patients.size();
    }

    private void applyFilterInternal(String q) {
        patients.clear();

        if (q == null || q.isEmpty()) {
            patients.addAll(originalList);
            return;
        }

        for (Patient p : originalList) {
            String name = p.getFoolName();
            if (name != null && name.toLowerCase(Locale.ROOT).contains(q)) {
                patients.add(p);
            }
        }
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
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

    public void removeItem(int position) {
        if (position < 0 || position >= patients.size()) return;

        patients.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Patient patient, int position) {
        patients.add(position, patient);
        notifyItemInserted(position);
    }

    public Patient getPatientAt(int position) {
        return patients.get(position);
    }

}

