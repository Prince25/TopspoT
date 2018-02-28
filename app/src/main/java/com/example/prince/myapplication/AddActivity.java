package com.example.prince.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Button addBtn = findViewById(R.id.addBtn);
        final EditText firstNumEditText = findViewById(R.id.firstNumEditText);
        final EditText secondNumEditText = findViewById(R.id.secondNumEditText);
        final TextView resultTextView = findViewById(R.id.resultTextView);

        // Gets information from another activity
        if (getIntent().hasExtra("com.example.prince.SWITCHSCREEN")){
            String text = getIntent().getExtras().getString("com.example.prince.SWITCHSCREEN");
            resultTextView.setText(text);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int num1 = Integer.parseInt(firstNumEditText.getText().toString());
                int num2 = Integer.parseInt(secondNumEditText.getText().toString());
                int result = num1 + num2;

                resultTextView.setText(result + "");
            }
        });
    }
}
