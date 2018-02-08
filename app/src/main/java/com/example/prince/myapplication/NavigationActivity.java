package com.example.prince.myapplication;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class NavigationActivity extends AppCompatActivity {

    private DrawerLayout m_DrawerLayout;
    private ActionBarDrawerToggle m_Toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        m_DrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        m_Toggle = new ActionBarDrawerToggle(this, m_DrawerLayout, R.string.open, R.string.close);
        m_DrawerLayout.addDrawerListener(m_Toggle);
        m_Toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (m_Toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
