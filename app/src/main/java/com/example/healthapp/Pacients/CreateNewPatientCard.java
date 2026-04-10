package com.example.healthapp.Pacients;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import com.example.healthapp.R;
import com.example.healthapp.SharedViewModel;
import com.example.healthapp.model.Patient;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CreateNewPatientCard extends Fragment {

    private EditText nameEt,secondnameEt,surnameEt,ageEt;
    private Button saveButton;
    private Button backButton;
    private SharedViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_new_patient_card, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        bindViews(view); // Инициализация кнопок и полей
        setupBirthDatePicker(); // Календарь
        setupClicks(); // Обработчик кнопок


        return view;
    }

    private void bindViews(View view){
        nameEt = view.findViewById(R.id.EnterName);
        secondnameEt = view.findViewById(R.id.EnterSecondName);
        surnameEt = view.findViewById(R.id.EnterSurname);
        ageEt = view.findViewById(R.id.editTextDate);
        saveButton = view.findViewById(R.id.btnSaveNewCardPatient);
        backButton = view.findViewById(R.id.btnBackToPatientDetails);
    }

    // Календарь
    private void setupBirthDatePicker(){
        ageEt.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select birthDate")
                    .setTheme(R.style.MyCalendarTheme)
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        .format(calendar.getTime());
                ageEt.setText(date);
            });

            picker.show(getParentFragmentManager(), "DATE_PICKER");
        });
    }


    private void setupClicks() {
        saveButton.setOnClickListener(v -> onSaveClicked());
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void onSaveClicked(){
        saveButton.setOnClickListener(v -> {
            String name = safeText(nameEt);
            String secondname = secondnameEt.getText().toString();
            String surname = surnameEt.getText().toString();
            String age = ageEt.getText().toString();
            Log.d("DEBUG", "Age: " + age);

            // простая валидация
            if (surname.isEmpty()) { surnameEt.setError("Введите фамилию"); return; }
            if (name.isEmpty()) { nameEt.setError("Введите имя"); return; }
            if (age.isEmpty()) { ageEt.setError("Выберите дату рождения"); return; }

            String foolName = (secondname + " " + name + " " + surname).trim().replaceAll("\\s+", " ");;

            String createdAt = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .format(new Date());

            Patient patinet = new Patient(foolName,age,createdAt);

            viewModel.addPatient(patinet);

            // Возврат к предыдущему фрагменту
            getParentFragmentManager().popBackStack();
        });
    }

    private String safeText(EditText et){
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}