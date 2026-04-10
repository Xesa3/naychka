package com.example.healthapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


public class UVlampFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uvlamp, container, false);

        view.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // яркость
        WindowManager.LayoutParams params = requireActivity().getWindow().getAttributes();
        params.screenBrightness = 1.0f;
        requireActivity().getWindow().setAttributes(params);

        // fullscreen
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );


    }
    @Override
    public void onPause() {
        super.onPause();
        // 🔻 вернуть системную яркость
        WindowManager.LayoutParams params = requireActivity().getWindow().getAttributes();
        params.screenBrightness = -1f; // авто
        requireActivity().getWindow().setAttributes(params);

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
        );
    }

}