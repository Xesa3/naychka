package com.example.healthapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;


public class PhotoFilter extends Fragment {
    private static final String ARG_PHOTO_PATH = "photo_path";
    private String photoPath;
    private ImageView imageView;


    public static PhotoFilter newInstance(String path) {
        PhotoFilter fragment = new PhotoFilter();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PhotoView photoView = view.findViewById(R.id.photoView);
        photoView.setImageBitmap(BitmapFactory.decodeFile(photoPath));
        Button btnBack = view.findViewById(R.id.btnBackToPatientDetails);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        if (getArguments() != null) {
            photoPath = getArguments().getString(ARG_PHOTO_PATH);
            photoView.setImageURI(android.net.Uri.fromFile(new File(photoPath)));
        }

        // Здесь позже можно добавлять кнопки для фильтров
    }

}