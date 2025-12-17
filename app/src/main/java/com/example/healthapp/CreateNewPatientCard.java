package com.example.healthapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import com.example.healthapp.model.Patient;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CreateNewPatientCard extends Fragment {

    private EditText nameEditText,secondnameEditText,surenameEditText,ageEditText;
    private Button saveButton;
    private Button backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_new_patient_card, container, false);

        nameEditText = view.findViewById(R.id.EnterName);
        secondnameEditText = view.findViewById(R.id.EnterSecondName);
        surenameEditText = view.findViewById(R.id.EnterSurname);
        ageEditText = view.findViewById(R.id.editTextDate);
        // Календарь
        ageEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select birthDate")
                    .setTheme(R.style.MyCalendarTheme)
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        .format(calendar.getTime());
                ageEditText.setText(date);
            });

            picker.show(getParentFragmentManager(), "DATE_PICKER");
        });


        saveButton = view.findViewById(R.id.btnSaveNewCardPatient);
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String secondname = secondnameEditText.getText().toString();
            String surename = surenameEditText.getText().toString();
            String age = ageEditText.getText().toString();

            String foolName = secondname + " " + name + " " + surename;

            String createdAt = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .format(new Date());

            Patient patinet = new Patient(foolName,age,createdAt);

            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            viewModel.addPatient(patinet);

            // Возврат к предыдущему фрагменту
            getParentFragmentManager().popBackStack();
        });


        backButton = view.findViewById(R.id.btnBackToPatientDetails);
        backButton.setOnClickListener(v->{
            getParentFragmentManager().popBackStack();
        });

        return view;
    }
}