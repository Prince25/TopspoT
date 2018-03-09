package com.ucla.topspot;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ClassroomList extends AppCompatActivity {

    String[] classrooms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("  Classrooms");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setIcon(R.drawable.classroom_status);



        final ListView classroomListView = findViewById(R.id.classroomListView);

        classrooms = getResources().getStringArray(R.array.classrooms);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, classrooms){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                /// Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                TextView tv = view.findViewById(android.R.id.text1);

                // Set the text size 25 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                // Return the view
                return view;
            }
        };

        // DataBind ListView with items from ArrayAdapter
        classroomListView.setAdapter(arrayAdapter);



        classroomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                int itemPosition = position;
                String choosenClassroom = (String) classroomListView.getItemAtPosition(itemPosition);

                switch (choosenClassroom)
                {
                    case "Boelter Hall 3400":
                        Intent BH3400 = new Intent(getApplicationContext(), bh3400imageActivity.class);
                        startActivity(BH3400);
                        break;

                    case "Math 5200":
                        Intent MATH5200 = new Intent(getApplicationContext(), math5200imageActivity.class);
                        startActivity(MATH5200);
                        break;

                    case "Moore 100":
                        Intent MOORE100 = new Intent(getApplicationContext(), moore100imageActivity.class);
                        startActivity(MOORE100);
                        break;
                }





            }
        });

    }
}
