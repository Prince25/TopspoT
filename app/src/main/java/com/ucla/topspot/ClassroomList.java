package com.ucla.topspot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ClassroomList extends AppCompatActivity {

    String[] classrooms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_list);


        final ListView classroomListView = findViewById(R.id.classroomListView);

        classrooms = getResources().getStringArray(R.array.classrooms);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_selectable_list_item, classrooms);

        classroomListView.setAdapter(adapter);


        classroomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                int itemPosition = position;
                String choosenClassroom = (String) classroomListView.getItemAtPosition(itemPosition);

                switch (choosenClassroom)
                {
                    case "BH 3400":
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
