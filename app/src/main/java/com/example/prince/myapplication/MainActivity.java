package com.example.prince.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout m_DrawerLayout;
    private ActionBarDrawerToggle m_Toggle;
    ImageView topspotImageView1;
    ImageView topspotImageView2;


    final int Moore100TotalRows = 40, Moore100TotalCols = 15;
    Classroom Moore100 = new Classroom("Moore 100", Moore100TotalRows, Moore100TotalCols);

    final int Math5200TotalRows = 15, Math5200TotalCols = 10;
    Classroom Math5200 = new Classroom("Math 5200", Math5200TotalRows, Math5200TotalCols);


   // DatabaseReference seatRef = BH3400.getCurrentClassroomRef().child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
    //DatabaseReference seatStatusRef = seatRef.child("Status");
   // DatabaseReference nameStatusRef = seatRef.child("Name");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topspotImageView1 = findViewById(R.id.topspotImageView1);
        topspotImageView2 = findViewById(R.id.topspotImageView2);

        topspotImageView1.setImageResource(R.drawable.topspot);
        fade(getCurrentFocus());

        findViewById(R.id.switcher).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ViewSwitcher switcher = (ViewSwitcher) v;

                if (switcher.getDisplayedChild() == 0) {

                    switcher.showNext();
                } else {
                    switcher.showPrevious();
                }
            }
        });




        // Launches "All Activities" activity
        Button allActivitiesBtn = findViewById(R.id.allActivitiesBtn);
        allActivitiesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), Activities.class);
                startActivity(startIntent);
            }
        });




        // For drawing navigation pane
        m_DrawerLayout = findViewById(R.id.drawer);
        m_Toggle = new ActionBarDrawerToggle(this, m_DrawerLayout, R.string.open, R.string.close);
        m_DrawerLayout.addDrawerListener(m_Toggle);
        m_Toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().show();

        // Navigation Pane Items
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                m_DrawerLayout.closeDrawers();
                int itemId = menuItem.getItemId();

                switch (menuItem.getItemId()) {
                    case R.id.classrooms:
                        Intent classrooms = new Intent(getApplicationContext(), ClassroomList.class);
                        startActivity(classrooms);
                        break;
                    case R.id.lots:
                        //Intent parkingLots = new Intent("com.test.demo.PREFS");
                       // startActivity(parkingLots);
                        break;
                    case R.id.settings:
                        //Intent settings = new Intent("com.test.demo.PREFS");
                        //startActivity(settings);
                        break;
                    case R.id.quit:
                        finish();
                        break;
                }

                return true;
            }
        });


    }


    public void fade(View view)
    {
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        topspotImageView1.startAnimation(animation1);
    }



    // For Navigation Menu Items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (m_Toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}