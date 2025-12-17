package com.example.healthapp.Pacients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapp.R;

import java.io.File;
import java.util.List;

public class PatientFileAdapter extends RecyclerView.Adapter<PatientFileAdapter.PatientViewHolder> {

    private List<File> patients;
    private final OnPatientClickListener listener;

    public interface OnPatientClickListener {
        void onPatientClick(File patientFolder);
    }

    public PatientFileAdapter(List<File> patients, OnPatientClickListener listener) {
        this.patients = patients;
        this.listener = listener;
    }

    public void updateList(List<File> newList) {
        this.patients = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_patient_folder, parent, false);
        return new PatientViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        File patientFolder = patients.get(position);

        // имя папки = имя пациента
        holder.name.setText(patientFolder.getName());

        holder.itemView.setOnClickListener(v ->
                listener.onPatientClick(patientFolder)
        );
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.patientFolderName);
        }
    }
}
