package com.ucla.topspot;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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


    // DatabaseReference seatRef = BH3400.getCurrentClassroomRef().child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
    // DatabaseReference seatStatusRef = seatRef.child("Status");
    // DatabaseReference nameStatusRef = seatRef.child("Name");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Display Icon on the Status Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" ");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setIcon(R.drawable.ic_launcher_status);


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


        // The UCLA Logo
        // Goes to Activities class when clicked
        ImageView uclaImageView = findViewById(R.id.uclaImageView);
        uclaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), Activities.class);
                startActivity(startIntent);
            }
        });
        Animation fadeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_longer);
        uclaImageView.startAnimation(fadeAnimation);




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

                //m_DrawerLayout.closeDrawers();
                int itemId = menuItem.getItemId();

                switch (itemId) {
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
                        finishAndRemoveTask();
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