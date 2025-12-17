package com.example.healthapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthapp.Pacients.PatientCardsFragment;

public class MainActivity extends AppCompatActivity {

    private Button buttonCamera, buttonCards, buttonGalerry, buttonSettings;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs =
                newBase.getSharedPreferences("settings", Context.MODE_PRIVATE);

        String lang = prefs.getString("lang", "ru"); // если ничего нет — русский
        LocaleHelper.applyLanguage(newBase, lang);

        super.attachBaseContext(newBase);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        buttonCamera = findViewById(R.id.btcCamera);
        buttonCards = findViewById(R.id.btnCrds);
        buttonGalerry = findViewById(R.id.btnEditGallery);
        buttonSettings = findViewById(R.id.btnSettings);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraFragment();
            }
        });

        buttonCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCardFragment();
            }
        });

        buttonGalerry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenEditGalleryFragment();
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {OpenSettingsFragment();}
        });



    }

    private void openCameraFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main, new Camera())
                .addToBackStack(null)
                .commit();
    }

    private void openCardFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView,new PatientCardsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void OpenEditGalleryFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main, new EditFromGallery())
                .addToBackStack(null)
                .commit();

    }

    private void OpenSettingsFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new SettingsFragment())
                .addToBackStack(null)
                .commit();

    }


}