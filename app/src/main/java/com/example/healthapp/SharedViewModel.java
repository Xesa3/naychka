package com.example.healthapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthapp.model.Patient;
import com.example.healthapp.study.Study;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

    // Список пациентов, который будет наблюдать наш фрагмент списка
    private final MutableLiveData<List<Patient>> patientList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Study>> studyList = new MutableLiveData<>(new ArrayList<>());
    // Метод возвращает LiveData для наблюдения. Фрагменты используют этот метод, чтобы автоматически получать обновления
    public LiveData<List<Patient>> getPatientList() {
        return patientList;
    }

    public LiveData<List<Study>> getStudyList() {
        return studyList;
    }

    // Метод добавляет нового пациента в список и уведомляет всех наблюдателей
    public void addPatient(Patient patient) {
        List<Patient> current = patientList.getValue(); // получаем текущий список
        current.add(patient);                           // добавляем нового пациента
        patientList.setValue(current);                  // обновляем LiveData, это вызывает уведомление всех наблюдателей (фрагментов)
    }

    public void addStudyToPatient(Patient patient, Study study) {
        // добавляем исследование
        patient.addStudy(study);

        // обновляем LiveData, чтобы фрагменты видели изменения
        List<Patient> current = new ArrayList<>(patientList.getValue());
        patientList.setValue(current);
    }

    // Обновление полного списка (например после загрузки)
    public void setPatientList(List<Patient> list) {
        patientList.setValue(new ArrayList<>(list));
    }

    public void setStudyList(List<Study> list){
        studyList.setValue(new ArrayList<>(list));
    }

    // Перемещение пациента (drag & drop)
    public void movePatient(int fromPosition, int toPosition) {
        List<Patient> current = new ArrayList<>(patientList.getValue());
        if (fromPosition < 0 || toPosition < 0 || fromPosition >= current.size() || toPosition >= current.size()) return;

        Patient moved = current.remove(fromPosition);
        current.add(toPosition, moved);

        patientList.setValue(current);
    }

    public void moveStudy(int fromPosition, int toPosition) {
        List<Study> current = new ArrayList<>(studyList.getValue());
        if (fromPosition < 0 || toPosition < 0 || fromPosition >= current.size() || toPosition >= current.size()) return;

        Study moved = current.remove(fromPosition);
        current.add(toPosition,moved);

        studyList.setValue(current);
    }
    public void initTestDataPatient() {
        if (patientList.getValue() == null || patientList.getValue().isEmpty()) {
            List<Patient> testPatients = new ArrayList<>();
            Patient p1 = new Patient("Иван Иванов Ивнович", "25",  "01.01.2025");

            p1.addStudy(new Study(1, "Дерматит", "Покраснения и жжение", "27.12.2025", null));

            testPatients.add(p1);
            testPatients.add(new Patient("Иван Менделеев Ивнович", "21",  "01.01.2025"));
            testPatients.add(new Patient("Мария Петрова Алексеевна", "30", "02.02.2025"));
            testPatients.add(new Patient("Сергей Сидоров Анатольевич", "40", "03.03.2025"));
            testPatients.add(new Patient("Анна Смирнова Кирилловна", "28", "04.04.2025"));

            patientList.setValue(testPatients);
        }
    }




}
