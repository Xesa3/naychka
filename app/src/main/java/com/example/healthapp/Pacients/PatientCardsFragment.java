package com.example.healthapp.Pacients;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.SearchView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapp.CreateNewPatientCard;
import com.example.healthapp.R;
import com.example.healthapp.SharedViewModel;
import com.example.healthapp.model.Patient;


public class PatientCardsFragment extends Fragment {
    private SharedViewModel viewModel;
    private RecyclerView recyclerView;
    private PatientAdapter adapter;
    //private PatientFileAdapter adapterFile;
    //private File patientsRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_patient_cards, container, false);
        Button button = view.findViewById(R.id.btnNewPatientCard);


        recyclerView = view.findViewById(R.id.PatientListCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
/*
        patientsRoot = new File(requireContext().getExternalFilesDir(null), "Patients");
        if (!patientsRoot.exists()) patientsRoot.mkdirs();
*/
        adapter = new PatientAdapter(new ArrayList<>(), patient -> {

            Bundle bundle = new Bundle();
            bundle.putSerializable("patient", patient);

            PatientDetailsFragment fragment = new PatientDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        /*
        adapterFile = new PatientFileAdapter(loadPatients(), file -> {

            Bundle bundle = new Bundle();
            bundle.putString("patientPath", file.getAbsolutePath());

            PatientDetailsFragment fragment = new PatientDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapterFile);

        button.setOnClickListener(v -> createNewPatient());
*/
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.initTestDataPatient();


        viewModel.getPatientList().observe(getViewLifecycleOwner(), patients -> {
            adapter.updateList(patients);
        });


        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                0
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {

                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();;

                adapter.moveItem(fromPos, toPos);
                return true;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                // сохраняем новый порядок только после отпуска
                viewModel.setPatientList(new ArrayList<>(adapter.patients));
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // свайпы не нужны
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

        // Adapter сообщает ViewModel о перемещении
        adapter.setOnPatientMoveListener((from, to) -> viewModel.movePatient(from, to));


        button.setOnClickListener(v -> {
            // Переход на второй фрагмент
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new CreateNewPatientCard())
                    .addToBackStack(null) // чтобы можно было вернуться назад
                    .commit();
        });


        return view;
    }
    /*
    private List<File> loadPatients() {
        List<File> list = new ArrayList<>();
        File[] files = patientsRoot.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) list.add(f);
            }
        }
        return list;
    }

    // Создание папки нового пациента
    private void createNewPatient() {
        String name = "Пациент_" + System.currentTimeMillis();
        File newPatient = new File(patientsRoot, name);
        newPatient.mkdirs();

        adapterFile.updateList(loadPatients());
    }
*/
}