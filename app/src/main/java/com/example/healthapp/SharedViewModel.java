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


    public void initTestDataPatient() {
        if (patientList.getValue() == null || patientList.getValue().isEmpty()) {
            List<Patient> testPatients = new ArrayList<>();
            Patient p1 = new Patient("Георгий Иванов Ивнович", "02.05.1999",  "01.01.2025");

            p1.addStudy(new Study(1, "Псориаз", "(Комментарии врача)", "16.07.2025", null));

            testPatients.add(p1);
            testPatients.add(new Patient("Иван Менделеев Ивнович", "12.08.2004",  "01.01.2025"));
            testPatients.add(new Patient("Мария Петрова Алексеевна", "13.08.2000", "02.02.2025"));
            testPatients.add(new Patient("Сергей Сидоров Анатольевич", "17.07.1980", "03.03.2025"));
            testPatients.add(new Patient("Анна Смирнова Кирилловна", "24.02.1999", "04.04.2025"));

            patientList.setValue(testPatients);
        }
    }
    private final MutableLiveData<Integer> selectedPatientIndex = new MutableLiveData<>(-1);
    public void setSelectedPatientIndex(int index) { selectedPatientIndex.setValue(index); }
    public Integer getSelectedPatientIndex() { return selectedPatientIndex.getValue(); }

    public void attachPhotoToStudy(int patientIndex, int studyId, String photoPath) {
        List<Patient> list = patientList.getValue();
        if (list == null) return;
        if (patientIndex < 0 || patientIndex >= list.size()) return;

        Patient p = list.get(patientIndex);
        List<Study> studies = p.getStudies(); // у тебя именно getStudy()
        if (studies == null) return;

        for (Study s : studies) {
            if (s.getId() == studyId) {
                s.addPhoto(photoPath);
                patientList.setValue(new ArrayList<>(list)); // “пнуть” обновление UI
                return;
            }
        }
    }

    public void deletePatient(Patient patient){
        List<Patient> list = patientList.getValue();
        if(list == null) return;

        list.remove(patient);

        // важно: новый список, чтобы LiveData обновился
        patientList.setValue(new ArrayList<>(list));

    }

}
