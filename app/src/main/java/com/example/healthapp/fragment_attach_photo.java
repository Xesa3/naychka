package com.example.healthapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthapp.R;
import com.example.healthapp.SharedViewModel;
import com.example.healthapp.model.Patient;
import com.example.healthapp.study.Study;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class fragment_attach_photo extends Fragment {

    private static final String ARG_PHOTO_PATH = "photo_path";

    private String photoPath;
    private SharedViewModel vm;

    public static fragment_attach_photo newInstance(String photoPath) {
        fragment_attach_photo f = new fragment_attach_photo();
        Bundle b = new Bundle();
        b.putString(ARG_PHOTO_PATH, photoPath);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attach_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 1) Получаем путь к фото
        if (getArguments() != null) {
            photoPath = getArguments().getString(ARG_PHOTO_PATH);
        }

        // 2) Находим элементы UI
        ImageView imagePreview = view.findViewById(R.id.imagePreview);
        AutoCompleteTextView actvPatients = view.findViewById(R.id.actvPatients);
        Spinner spStudies = view.findViewById(R.id.spStudies);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnAttach = view.findViewById(R.id.btnAttach);

        // 3) Показываем фото (из файла)
        if (photoPath != null && !photoPath.isEmpty()) {
            imagePreview.setImageURI(Uri.fromFile(new File(photoPath)));
        }

        // 4) Кнопка "Отмена"
        btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // 5) Подписка на список пациентов
        vm.getPatientList().observe(getViewLifecycleOwner(), patientList -> {
            final List<Patient> patients =
                    (patientList != null) ? patientList : new ArrayList<>();

            // --- Заполняем спиннер пациентов ---
            List<String> patientNames = new ArrayList<>();
            for (Patient p : patients) {
                patientNames.add(p.getFoolName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    patientNames
            );

            actvPatients.setAdapter(adapter);

            // --- обработка выбора пациента ---
            actvPatients.setOnItemClickListener((parent, view1, position, id) -> {
                // Находим индекс пациента в оригинальном списке по имени
                String selectedName = (String) parent.getItemAtPosition(position);
                int patientIndex = -1;
                for (int i = 0; i < patients.size(); i++) {
                    if (patients.get(i).getFoolName().equals(selectedName)) {
                        patientIndex = i;
                        break;
                    }
                }

                if (patientIndex >= 0) {
                    vm.setSelectedPatientIndex(patientIndex);

                    Patient selectedPatient = patients.get(patientIndex);
                    List<Study> studies = selectedPatient.getStudies();
                    if (studies == null) studies = new ArrayList<>();
                    if (studies.isEmpty()) {
                        Toast.makeText(requireContext(), "У пациента нет исследований", Toast.LENGTH_SHORT).show();
                        spStudies.setAdapter(null);
                        return;
                    }

                    List<String> studyLabels = new ArrayList<>();
                    for (Study s : studies) {
                        studyLabels.add(s.getTitle() + " • " + s.getDate());
                    }

                    ArrayAdapter<String> sa = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            studyLabels
                    );
                    sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spStudies.setAdapter(sa);

                    // автоматически выбрать первое исследование
                    if (!studyLabels.isEmpty()) spStudies.setSelection(0);
                }
            });

        });

        // 6) Кнопка "Прикрепить"
        btnAttach.setOnClickListener(v -> {
            List<Patient> patients = vm.getPatientList().getValue();
            if (patients == null || patients.isEmpty()) {
                Toast.makeText(requireContext(), "Нет пациентов", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer patientIndex = vm.getSelectedPatientIndex();
            if (patientIndex == null || patientIndex < 0 || patientIndex >= patients.size()) {
                Toast.makeText(requireContext(), "Выберите пациента", Toast.LENGTH_SHORT).show();
                return;
            }

            Patient p = patients.get(patientIndex);
            List<Study> studies = p.getStudies();
            if (studies == null || studies.isEmpty()) {
                Toast.makeText(requireContext(), "У пациента нет исследований", Toast.LENGTH_SHORT).show();
                return;
            }

            int studyPos = spStudies.getSelectedItemPosition();
            if (studyPos < 0 || studyPos >= studies.size()) {
                Toast.makeText(requireContext(), "Выберите исследование", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoPath == null || photoPath.isEmpty()) {
                Toast.makeText(requireContext(), "Фото не найдено", Toast.LENGTH_SHORT).show();
                return;
            }

            int studyId = studies.get(studyPos).getId();
            vm.attachPhotoToStudy(patientIndex, studyId, photoPath);

            Toast.makeText(requireContext(), "Фото прикреплено", Toast.LENGTH_SHORT).show();

            // Вернуться назад (например в PhotoFilter или в карточку пациента)
            getParentFragmentManager().popBackStack();
        });
    }

}