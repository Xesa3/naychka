package com.example.java_logic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.lang.Double;

public class addOperation extends AppCompatActivity {

    private EditText textType, textPrice, textComment;
    private Button addBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_operation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addBtn = findViewById(R.id.buttonAddOperation);

        addBtn.setOnClickListener(v -> {
            textType = findViewById(R.id.textTypeOp);
            textPrice = findViewById(R.id.textPriceOp);
            textComment = findViewById(R.id.textCommOp);

            String priceStr = textPrice.getText().toString();
            String comment = textComment.getText().toString();
            String type = textType.getText().toString();

            double price = 0;
            if (!priceStr.isEmpty()) {
                price = Double.parseDouble(priceStr);
            }

            Operation operation = new Operation(type, price, comment, System.currentTimeMillis());
            HomePage.operationList.add(operation);

            Intent intent = new Intent(addOperation.this, HomePage.class);
            // Запускаем HomePage
            startActivity(intent);
            finish();

        });


    }
}