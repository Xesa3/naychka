package com.example.healthapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;

public class EditFromGallery extends Fragment {

    private static final int REQUEST_IMAGE_GALLERY = 1001;

    private PhotoView photoView;
    private ImageButton buttonGallery;
    private Button btnBack;
    private Spinner filterSpinner;
    private CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_from_gallery, container, false);

        photoView = view.findViewById(R.id.photoView);
        buttonGallery = view.findViewById(R.id.buttonGallery);
        filterSpinner = view.findViewById(R.id.filterSpinner);
        checkBox = view.findViewById(R.id.checkBox);
        btnBack = view.findViewById(R.id.btnBackToPatientDetails);

        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });


        // --------------------------
        // 1) Заполняем Spinner
        // --------------------------
        String[] filters = {"Без фильтра"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // --------------------------
        // 2) Открываем галерею
        // --------------------------
        buttonGallery.setOnClickListener(v -> openGallery());

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
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(), selectedImageUri);
                photoView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}