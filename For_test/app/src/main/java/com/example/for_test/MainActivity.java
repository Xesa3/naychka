package com.example.for_test;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.LinearLayout;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layoutContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutContainer = findViewById(R.id.linearLayout1);
        Button bthAdd = findViewById(R.id.buttonAdd);

        bthAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewButton();
            }
        });

    }


    private void addNewButton(){
        Button newButton = new Button(this);
        Button original = findViewById(R.id.buttonAdd);

        newButton.setText("New");
        newButton.setWidth(original.getWidth());
        newButton.setHeight(original.getHeight());

        newButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        layoutContainer.addView(newButton);

    }



}