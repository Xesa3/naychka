package com.example.java_logic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.List;
import com.example.java_logic.Operation;
import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OperationAdapter adapter;
    public static List<Operation> operationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if(intent!= null && intent.hasExtra("operation_type")){
            String type = intent.getStringExtra("operation_type");
            double price = intent.getDoubleExtra("operation_amount", 0);
            String comment = intent.getStringExtra("operation_comment");

            Operation operation = new Operation(type, price, comment, System.currentTimeMillis());
            operationList.add(operation);

        }

        recyclerView = findViewById(R.id.RecyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OperationAdapter(operationList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Если список обновился — уведомляем адаптер
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void clickAdd(View view) { //Нажатие на кнопку логин и переход к главному меню
        Intent intent = new Intent(this, addOperation.class);
        startActivity(intent);
        finish();
    }

}
