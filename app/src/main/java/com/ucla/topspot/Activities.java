package com.ucla.topspot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Activities extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        // Launches another activity
        Button addActivityBtn = findViewById(R.id.addActivityBtn);
        addActivityBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), AddActivity.class);

                startIntent.putExtra("com.example.prince.SWITCHSCREEN", "Your screen switched!");
                startActivity(startIntent);
            }
        });


        // Launches List activity
        Button listActivityBtn = findViewById(R.id.listActivityBtn);
        listActivityBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(startIntent);
            }
        });



        // Launch Navigation Activity (Main)
        Button navigationActivityBtn = findViewById(R.id.navigationActivityBtn);
        navigationActivityBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });




        // Launch Activity (go to website) outside of app
        Button googleBtn = findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String google = "http://www.google.com";
                Uri webAddress = Uri.parse(google);

                Intent goToGoogle = new Intent(Intent.ACTION_VIEW, webAddress);
                if (goToGoogle.resolveActivity(getPackageManager()) != null){
                    startActivity(goToGoogle);
                }
            }
        });

    }
}
