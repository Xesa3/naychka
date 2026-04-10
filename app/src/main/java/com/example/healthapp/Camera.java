package com.example.healthapp;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.Manifest;


public class Camera extends Fragment {

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Button btnBack;
    private androidx.camera.core.Camera camera;
    private final int REQUEST_CODE = 101;

    //
    private ImageView overlayView;
    private ExecutorService analysisExecutor;
    private boolean filterEnabled = false;


    private Spinner filtersSpiner;

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
        analysisExecutor = Executors.newSingleThreadExecutor();
        overlayView = view.findViewById(R.id.overlayView);

        //
        //filtersSpiner = view.findViewById(R.id.filterSpinner);
        // ArrayAdapter<String> adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, filters);
        // Определяем разметку для использования при выборе элемента
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        //filtersSpiner.setAdapter(adapter);

       /* AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

            boolean isFirst = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isFirst) {
                    isFirst = false;
                    return;
                }

                String item = (String)parent.getItemAtPosition(position);
                if(item.equals("UV")){
                    UVlampFragment uv = new UVlampFragment();
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView, uv)
                            .addToBackStack(null)
                            .commit();

                    Toast toast = Toast.makeText( getContext(),"Чтобы выйти нажмите на экран!", Toast.LENGTH_LONG);
                    toast.show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        filtersSpiner.setOnItemSelectedListener(itemSelectedListener);
        */

        RecyclerView filtersList = view.findViewById(R.id.filtersList);

        filtersList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        List<String> filters = Arrays.asList("UV", "Relief", "Anomaly", "Anomaly2", "Anomaly3", "Anomaly4");

        FilterAdapter adapter = new FilterAdapter(filters, filter -> {

            if (filter.equals("UV")) {
                UVlampFragment uv = new UVlampFragment();

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, uv)
                        .addToBackStack(null)
                        .commit();
            }

            // пока остальные просто лог
            if (filter.equals("Relief")) {
                filterEnabled = !filterEnabled; // 🔥 переключатель

                if (filterEnabled) {
                    overlayView.setVisibility(View.VISIBLE);
                    previewView.setAlpha(0f);
                } else {
                    overlayView.setVisibility(View.GONE);
                    previewView.setAlpha(1f);
                }
            }

            if (filter.equals("Anomaly")) {
                Log.d("FILTER", "Anomaly clicked");
            }

        });

        filtersList.setAdapter(adapter);


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

                // За филльтр отвечает
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        //.setTargetResolution(new Size(1280, 720)) // 🔥 ВСТАВИТЬ СЮДА
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(analysisExecutor, image -> {
                    if(!filterEnabled){
                        image.close();
                        return;
                    }
                    try {
                        Bitmap bmp = normalize(yuvToRgb(image));              // конвертация
                        Bitmap filtered = applyCppFilter(bmp);           // фильтр
                        requireActivity().runOnUiThread(() -> overlayView.setImageBitmap(filtered));
                    } catch (Exception e) {
                        // лог при желании
                    } finally {
                        image.close(); // КРИТИЧНО! иначе камера зависнет
                    }
                });
                //

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalysis
                );

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Camera start error", e);
            }

        }, ContextCompat.getMainExecutor(getContext()));
    }

    // Тоже фильтр
    private Bitmap yuvToRgb(ImageProxy image) {

        int width = image.getWidth();
        int height = image.getHeight();

        ImageProxy.PlaneProxy[] planes = image.getPlanes();

        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int yRowStride = planes[0].getRowStride();
        int uvRowStride = planes[1].getRowStride();
        int uvPixelStride = planes[1].getPixelStride();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] pixels = new int[width * height];

        byte[] yBytes = new byte[yBuffer.remaining()];
        byte[] uBytes = new byte[uBuffer.remaining()];
        byte[] vBytes = new byte[vBuffer.remaining()];

        yBuffer.get(yBytes);
        uBuffer.get(uBytes);
        vBuffer.get(vBytes);

        for (int y = 0; y < height; y++) {
            int yRow = yRowStride * y;
            int uvRow = uvRowStride * (y / 2);

            for (int x = 0; x < width; x++) {

                int yIndex = yRow + x;
                int uvIndex = uvRow + (x / 2) * uvPixelStride;

                int Y = yBytes[yIndex] & 0xFF;
                int U = uBytes[uvIndex] & 0xFF;
                int V = vBytes[uvIndex] & 0xFF;

                int R = (int)(Y + 1.370705f * (V - 128));
                int G = (int)(Y - 0.337633f * (U - 128) - 0.698001f * (V - 128));
                int B = (int)(Y + 1.732446f * (U - 128));

                R = Math.max(0, Math.min(255, R));
                G = Math.max(0, Math.min(255, G));
                B = Math.max(0, Math.min(255, B));

                pixels[y * width + x] = 0xFF000000 | (R << 16) | (G << 8) | B;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        // 🔥 ВОТ ЭТО ДОБАВЬ
        int rotation = image.getImageInfo().getRotationDegrees();

        if (rotation != 0) {
            Matrix m = new Matrix();
            m.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), m, true);
        }

        return bitmap;
    }


    private Bitmap yuvToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        // NV21 = Y + V + U
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, out);
        byte[] jpegBytes = out.toByteArray();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length, opts);

        // Поворот под ориентацию
        int rotation = image.getImageInfo().getRotationDegrees();
        if (rotation != 0) {
            Matrix m = new Matrix();
            m.postRotate(rotation);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
        }

        return bmp;
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
                                .replace(R.id.fragmentContainerView, photoFragment) // container — id фреймлайаута в activity
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
    // Тоже фильтр
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (analysisExecutor != null) analysisExecutor.shutdown();
    }

    private Bitmap applyCppFilter(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        byte[] input = new byte[width * height * 4];
        byte[] output = new byte[width * height * 4];

        ByteBuffer buffer = ByteBuffer.wrap(input);
        bitmap.copyPixelsToBuffer(buffer);

        // 👉 ВЫЗОВ C++
        NativeLib.reliefFilter(input, output, width, height);

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(ByteBuffer.wrap(output));

        return result;
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