package com.example.healthapp.study;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.healthapp.R;
import com.example.healthapp.SharedViewModel;
import com.example.healthapp.model.Patient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class study_card_create extends Fragment {

    private static final int REQUEST_IMAGE_GALLERY = 1001;
    private EditText etPatientData, title;
    private ImageView imagePatient;
    private Button btnSave, btnBack, btnAddPhoto;
    private SharedViewModel sharedViewModel;
    private Patient patient;
    private String selectedPhotoUri = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study_card_create, container, false);

        etPatientData = view.findViewById(R.id.etPatientData);
        imagePatient = view.findViewById(R.id.imagePatient);
        btnSave = view.findViewById(R.id.btnSave);
        title = view.findViewById(R.id.etTitle);
        btnAddPhoto = view.findViewById(R.id.btnAddPhoto);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        if (getArguments() != null) {
            patient = (Patient) getArguments().getSerializable("patient");
        }

        btnSave.setOnClickListener(v -> {
            Log.e("DEBUG", "Patient = " + patient);
            String text = etPatientData.getText().toString();
            String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
            String lable = title.getText().toString();

            // Генерация id нового исследования
            int newId = patient.getStudy().size() + 1;

            Study newStudy = new Study(
                    newId,
                    lable,
                    text,
                    date,
                    selectedPhotoUri
            );

            sharedViewModel.addStudyToPatient(patient, newStudy);

            // Возврат к карточке пациента
            getParentFragmentManager().popBackStack();
        });

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v->{
            getParentFragmentManager().popBackStack();
        });

        btnAddPhoto.setOnClickListener(v-> openGallery());

        return view;
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            selectedPhotoUri = selectedImageUri.toString();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(), selectedImageUri);
                imagePatient.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}