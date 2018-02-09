package com.example.prince.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout m_DrawerLayout;
    private ActionBarDrawerToggle m_Toggle;


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
