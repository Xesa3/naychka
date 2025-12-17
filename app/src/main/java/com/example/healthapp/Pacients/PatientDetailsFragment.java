package com.example.healthapp.Pacients;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.healthapp.R;
import com.example.healthapp.SharedViewModel;
import com.example.healthapp.model.Patient;
import com.example.healthapp.study.Study;
import com.example.healthapp.study.StudyAdapter;
import com.example.healthapp.study.studyDetails;
import com.example.healthapp.study.study_card_create;

import java.util.ArrayList;
import java.util.List;


public class PatientDetailsFragment extends Fragment {

    private String name, age;
    private Patient patient; // объект пациента
    private Button backButton, addstudypat;
    private SharedViewModel viewModel;
    private StudyAdapter studyAdapter;
    private RecyclerView recyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_details, container, false);

        if (getArguments() != null) {
            patient = (Patient) getArguments().getSerializable("patient");
        }
        List<Study> studies = patient.getStudy();

        TextView tvName = view.findViewById(R.id.tvNameStudy);
        TextView tvAge = view.findViewById(R.id.tvAge);
        addstudypat = view.findViewById(R.id.btnAddStudy);
        recyclerView = view.findViewById(R.id.rvStudies);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.setStudyList(patient.getStudy());


        tvName.setText(patient.getFoolName());
        tvAge.setText(getContext().getString(R.string.field_birthdate,patient.getAge()));

        studyAdapter = new StudyAdapter(patient.getStudy(), study -> {

            Bundle bundle = new Bundle();
            bundle.putSerializable("study", study);

            studyDetails fragment = new studyDetails();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView,fragment)
                    .addToBackStack(null)
                    .commit();

        });
        recyclerView.setAdapter(studyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addstudypat.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putSerializable("patient", patient);

            study_card_create fragment = new study_card_create();
            fragment.setArguments(bundle);

            // Переход на второй фрагмент
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null) // чтобы можно было вернуться назад
                    .commit();
        });;
        viewModel.getStudyList().observe(getViewLifecycleOwner(), study->{
            studyAdapter.updateList(study);
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                0
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();

                studyAdapter.moveItem(fromPos, toPos);

                return true;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                // 1. Обновляем LiveData
                List<Study> newOrder = new ArrayList<>(studyAdapter.studies);
                viewModel.setStudyList(newOrder);

                // 2. ОБНОВЛЯЕМ ПАЦИЕНТА (важно!)
                patient.setStudy(newOrder);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean canDropOver(@NonNull RecyclerView recyclerView,
                                       @NonNull RecyclerView.ViewHolder current,
                                       @NonNull RecyclerView.ViewHolder target) {
                return true;
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
        studyAdapter.setOnStudyMoveListener(((from, to) -> viewModel.moveStudy(from,to)));


        backButton = view.findViewById(R.id.btnBackToPatientDetails);
        backButton.setOnClickListener(v->{
            getParentFragmentManager().popBackStack();
        });


        return view;
    }
}