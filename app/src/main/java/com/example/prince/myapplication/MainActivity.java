package com.example.prince.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout m_DrawerLayout;
    private ActionBarDrawerToggle m_Toggle;
    Classroom BH3400 = new Classroom("BH 3400", 10, 20);
    Classroom Moore100 = new Classroom("Moore 100", 40, 15);
    Classroom Math5400 = new Classroom("Math 5400", 15, 10);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_DrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        m_Toggle = new ActionBarDrawerToggle(this, m_DrawerLayout, R.string.open, R.string.close);
        m_DrawerLayout.addDrawerListener(m_Toggle);
        m_Toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Launches Activities activity
        Button allActivitiesBtn = (Button) findViewById(R.id.allActivitiesBtn);
        allActivitiesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), Activities.class);
                startActivity(startIntent);
            }
        });



        Button takeBtn = (Button) findViewById(R.id.takeBtn);
        Button emptyBtn = (Button) findViewById(R.id.emptyBtn);
        final EditText nameTextEdit = (EditText) findViewById(R.id.nameTextEdit);
        final EditText rowTextEdit = (EditText) findViewById(R.id.rowTextEdit);
        final EditText colTextEdit = (EditText) findViewById(R.id.colTextEdit);

        final Spinner dropdown = findViewById(R.id.classroomSpinner);
        final String selectedItem = dropdown.getSelectedItem().toString();


        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameTextEdit.getText().toString().trim();
                int row = Integer.parseInt(rowTextEdit.getText().toString());
                int col = Integer.parseInt(colTextEdit.getText().toString());
                boolean result = true;

                switch (selectedItem)
                {
                    case "BH 3400":
                        if (!BH3400.fillSeat(name, row, col))
                            result = false;
                        break;

                    case "Moore 100":
                        if (!Moore100.fillSeat(name, row, col))
                            result = false;
                        break;

                    case "Math 5400":
                        if (!Math5400.fillSeat(name, row, col))
                            result = false;
                        break;
                }

                if (!result)
                    Toast.makeText(getApplicationContext(), "Seat number is invalid", Toast.LENGTH_SHORT).show();
            }
        });


        emptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameTextEdit.getText().toString().trim();
                int row = Integer.parseInt(rowTextEdit.getText().toString());
                int col = Integer.parseInt(colTextEdit.getText().toString());
                boolean result = true;

                switch (selectedItem)
                {
                    case "BH 3400":
                        if (!BH3400.emptySeat(row, col))
                            result = false;
                        break;

                    case "Moore 100":
                        if (!Moore100.emptySeat(row, col))
                            result = false;
                        break;

                    case "Math 5400":
                        if (!Math5400.emptySeat(row, col))
                            result = false;
                        break;
                }

                if (!result)
                    Toast.makeText(getApplicationContext(), "Seat number is invalid", Toast.LENGTH_SHORT).show();


            }
        });


    }



    // For Navigation Menu Items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (m_Toggle.onOptionsItemSelected(item)){
            switch (item.getItemId()) {
                case R.id.classrooms:
                    Intent i = new Intent("com.test.demo.ABOUT");
                    startActivity(i);
                    break;
                case R.id.lots:
                    Intent p = new Intent("com.test.demo.PREFS");
                    startActivity(p);
                    break;
                case R.id.settings:
                    Intent g = new Intent("com.test.demo.PREFS");
                    startActivity(g);
                    break;
                case R.id.quit:
                    finish();
                    break;
            }
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
