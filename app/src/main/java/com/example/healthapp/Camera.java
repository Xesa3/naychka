package com.example.healthapp;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.Manifest;


public class Camera extends Fragment {

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Button btnBack;
    private androidx.camera.core.Camera camera;
    private final int REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        previewView = view.findViewById(R.id.previewView);
        ImageButton btnTakePhoto = view.findViewById(R.id.btnTakePhoto);

        btnBack = view.findViewById(R.id.btnBackToPatientDetails);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        previewView.setOnTouchListener(new View.OnTouchListener() {
            private float lastZoom = 1f;

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                float currentZoom = camera.getCameraInfo().getZoomState().getValue().getZoomRatio();

                if (event.getPointerCount() == 2) {
                    float distance = getFingerSpacing(event);

                    if (lastZoom == 1f) {
                        lastZoom = distance;
                    } else {
                        float scale = distance / lastZoom;
                        float newZoom = currentZoom * scale;

                        camera.getCameraControl().setZoomRatio(newZoom);
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    lastZoom = 1f;
                }

                return true;
            }
        });


        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE
            );
        }

        btnTakePhoto.setOnClickListener(v -> takePhoto());
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Camera start error", e);
            }

        }, ContextCompat.getMainExecutor(getContext()));
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        File photoDir = new File(getActivity().getExternalFilesDir(null), "patient_photos");
        if (!photoDir.exists()) photoDir.mkdirs();

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg";

        File photoFile = new File(photoDir, fileName);

        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                options,
                ContextCompat.getMainExecutor(getContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Log.d("CameraX", "Saved: " + photoFile.getAbsolutePath());
                        Toast.makeText(requireContext(),
                                "Фото сохранено: " + photoFile.getAbsolutePath(),
                                Toast.LENGTH_SHORT).show();

                        // 1. Создаем фрагмент с фото
                        PhotoFilter photoFragment = PhotoFilter.newInstance(photoFile.getAbsolutePath());

                        // 2. Меняем фрагмент
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main, photoFragment) // container — id фреймлайаута в activity
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e("CameraX", "Error: " + exc.getMessage());
                    }
                }
        );
    }
}