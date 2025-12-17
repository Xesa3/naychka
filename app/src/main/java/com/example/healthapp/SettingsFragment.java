package com.example.healthapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;


public class SettingsFragment extends Fragment {

    private Spinner langSpinner;
    private Button applyButton;

    private final String[] languages = {"Русский", "English"};
    private final String[] langCodes = {"ru", "en"}; // одинаковые индексы!


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        langSpinner = view.findViewById(R.id.langSpinner);
        applyButton = view.findViewById(R.id.applyButton);

        // адаптер для spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                languages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(adapter);

        // восстановление выбранного языка
        SharedPreferences prefs =
                requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        String savedLang = prefs.getString("lang", "ru"); // по умолчанию ru

        int index = savedLang.equals("ru") ? 0 : 1;
        langSpinner.setSelection(index);

        // кнопка применить
        applyButton.setOnClickListener(v -> applyLanguage());

        return view;
    }

    private void applyLanguage() {
        int selectedIndex = langSpinner.getSelectedItemPosition();

        String lang = langCodes[selectedIndex];

        // сохраняем в SharedPreferences
        requireContext()
                .getSharedPreferences("settings", Context.MODE_PRIVATE)
                .edit()
                .putString("lang", lang)
                .apply();

        // меняем язык
        LocaleHelper.applyLanguage(requireContext(), lang);

        // пересоздать активность для обновления текста
        requireActivity().recreate();
    }

}