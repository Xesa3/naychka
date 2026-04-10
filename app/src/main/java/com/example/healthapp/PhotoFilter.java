package com.example.healthapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;


public class PhotoFilter extends Fragment {
    private static final String ARG_PHOTO_PATH = "photo_path";
    private String photoPath;
    private ImageView imageView;

    private Bitmap originalBitmap;
    private Bitmap currentBitmap;
    private PhotoView photoView;


    public static PhotoFilter newInstance(String path) {
        PhotoFilter fragment = new PhotoFilter();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }
    private Bitmap applyCppFilter(Bitmap bitmap, String type) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        byte[] input = new byte[width * height * 4];
        byte[] output = new byte[width * height * 4];

        ByteBuffer buffer = ByteBuffer.wrap(input);
        bitmap.copyPixelsToBuffer(buffer);

        if (type.equals("relief")) {
            NativeLib.reliefFilter(input, output, width, height);
        }

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(ByteBuffer.wrap(output));

        return result;
    }
    private void applyFilter(String type) {

        new Thread(() -> {

            Bitmap result = applyCppFilter(originalBitmap, type);

            requireActivity().runOnUiThread(() -> {
                currentBitmap = result;
                photoView.setImageBitmap(result);
            });

        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        photoView = view.findViewById(R.id.photoView);

        if (getArguments() != null) {
            photoPath = getArguments().getString(ARG_PHOTO_PATH);

            //Добавил
            originalBitmap = normalize(BitmapFactory.decodeFile(photoPath));
            currentBitmap = originalBitmap;

            photoView.setImageBitmap(originalBitmap);
        }

        RecyclerView filtersList = view.findViewById(R.id.filtersList);

        filtersList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        List<String> filters = Arrays.asList("Original", "Relief", "Anomaly");

        FilterAdapter adapter = new FilterAdapter(filters, filter -> {

            if (filter.equals("Original")) {
                currentBitmap = originalBitmap;
                photoView.setImageBitmap(currentBitmap);
                return;
            }

            if (filter.equals("Relief")) {
                applyFilter("relief");
            }

            if (filter.equals("Anomaly")) {
                applyFilter("anomaly");
            }

        });

        filtersList.setAdapter(adapter);





        Button btnBack = view.findViewById(R.id.btnBackToPatientDetails);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        Button btnSave = view.findViewById(R.id.btnSaveCard);
        btnSave.setOnClickListener(v->{
            fragment_attach_photo f = fragment_attach_photo.newInstance(photoPath);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, f) // заменишь на свой контейнер
                    .addToBackStack(null)
                    .commit();
        });


        // Здесь позже можно добавлять кнопки для фильтров

    }

    private Bitmap normalize(Bitmap bmp) {

        int targetSize = 1024;

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        float scale = (float) targetSize / Math.max(w, h);

        int newW = Math.round(w * scale);
        int newH = Math.round(h * scale);

        return Bitmap.createScaledBitmap(bmp, newW, newH, false);
    }

}