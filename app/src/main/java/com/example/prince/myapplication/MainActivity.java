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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout m_DrawerLayout;
    private ActionBarDrawerToggle m_Toggle;


    final int BH3400TotalRows = 10, BH3400TotalCols = 20;
    final int Moore100TotalRows = 40, Moore100TotalCols = 15;
    final int Math5200TotalRows = 15, Math5200TotalCols = 10;

    Classroom BH3400 = new Classroom("BH 3400", BH3400TotalRows, BH3400TotalCols);
    Classroom Moore100 = new Classroom("Moore 100", Moore100TotalRows, Moore100TotalCols);
    Classroom Math5200 = new Classroom("Math 5200", Math5200TotalRows, Math5200TotalCols);

    boolean BH3400SeatStatus[][] = new boolean[BH3400TotalRows + 1][BH3400TotalCols + 1];
    boolean Moore100SeatStatus[][] = new boolean[Moore100TotalRows + 1][Moore100TotalCols + 1];
    boolean Math5200SeatStatus[][] = new boolean[Math5200TotalRows + 1][Math5200TotalCols + 1];

    String BH3400Name[][] = new String[BH3400TotalRows + 1][BH3400TotalCols + 1];
    String Moore100Name[][] = new String[Moore100TotalRows + 1][Moore100TotalCols + 1];
    String Math5200Name[][] = new String[Math5200TotalRows + 1][Math5200TotalCols + 1];


   // DatabaseReference seatRef = BH3400.getCurrentClassroomRef().child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
    //DatabaseReference seatStatusRef = seatRef.child("Status");
   // DatabaseReference nameStatusRef = seatRef.child("Name");





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_DrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        m_Toggle = new ActionBarDrawerToggle(this, m_DrawerLayout, R.string.open, R.string.close);
        m_DrawerLayout.addDrawerListener(m_Toggle);
        m_Toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        // Launches "All Activities" activity
        Button allActivitiesBtn = (Button) findViewById(R.id.allActivitiesBtn);
        allActivitiesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), Activities.class);
                startActivity(startIntent);
            }
        });


        // Initialize Activity Objects
        final Spinner dropdown = findViewById(R.id.classroomSpinner);
        final EditText nameTextEdit = (EditText) findViewById(R.id.nameTextEdit);
        final EditText rowTextEdit = (EditText) findViewById(R.id.rowTextEdit);
        final EditText colTextEdit = (EditText) findViewById(R.id.colTextEdit);
        final Button takeBtn = (Button) findViewById(R.id.takeBtn);
        final Button emptyBtn = (Button) findViewById(R.id.emptyBtn);
        final TextView statusTextView = (TextView) findViewById(R.id.statusTextView);


        // Take button's action when clicked
        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedItem = dropdown.getSelectedItem().toString();
                String name = nameTextEdit.getText().toString().trim();
                int row = Integer.parseInt(rowTextEdit.getText().toString());
                int col = Integer.parseInt(colTextEdit.getText().toString());

                String result = "";

                switch (selectedItem)
                {
                    case "BH 3400":
                        if (row <= BH3400TotalRows && col <= BH3400TotalCols)
                        {
                            if (BH3400SeatStatus[row][col] == false)
                            {
                                if (BH3400.fillSeat(name, row, col))
                                    result = "Seat Taken!";
                            }
                            else
                                result = "Seat is already occupied";
                        }
                        else
                            result = "Seat number is invalid";

                        break;


                    case "Moore 100":
                        break;


                    case "Math 5200":
                        break;
                }

                statusTextView.setText(result);

                //if (result != "")
                    //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        });



        // Empty button's action when clicked
        emptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedItem = dropdown.getSelectedItem().toString();
                String name = nameTextEdit.getText().toString().trim();
                int row = Integer.parseInt(rowTextEdit.getText().toString());
                int col = Integer.parseInt(colTextEdit.getText().toString());

                String result = "";

                switch (selectedItem)
                {
                    case "BH 3400":
                        if (Objects.equals(BH3400Name[row][col], name))
                            result = BH3400.emptySeat(row, col);
                        else
                            result = "You're not authorized!";
                        break;

                    case "Moore 100":
                        if (Objects.equals(Moore100Name[row][col], name))
                            result = BH3400.emptySeat(row, col);
                        else
                            result = "You're not authorized!";
                        break;

                    case "Math 5200":
                        if (Objects.equals(Math5200Name[row][col], name))
                            result = BH3400.emptySeat(row, col);
                        else
                            result = "You're not authorized!";
                        break;
                }

                statusTextView.setText(result);

                //if (!result)
                    //Toast.makeText(getApplicationContext(), "Seat number is invalid", Toast.LENGTH_SHORT).show();
            }
        });





        // Continuously reads data from database and records it to the array
        // ONLY FROM BH3400
        DatabaseReference classroomRef = BH3400.getCurrentClassroomRef();
        classroomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot item_snapshot : dataSnapshot.getChildren())
                {
                    DatabaseReference currentSeatStatus = item_snapshot.child("Status").getRef();
                    String currentSeat = currentSeatStatus.getParent().getKey();


                    String row = "";
                    int i = 5;
                    do {
                        row = row + currentSeat.charAt(i);
                        i++;
                    } while (Character.getNumericValue(currentSeat.charAt(i)) != -1);

                    String col = "";
                    i = 12 + (i - 6);
                    do {
                        col = col + currentSeat.charAt(i);
                        i++;
                    } while (i < currentSeat.length() && Character.getNumericValue(currentSeat.charAt(i)) != -1);


                    int currentRow = Integer.parseInt(row);
                    int currentCol = Integer.parseInt(col);

                    //Log.d("row: ",Integer.toString(currentRow));
                   // Log.d("col: ",Integer.toString(currentCol));

                    if (item_snapshot.child("Status").getValue() != null)
                        BH3400SeatStatus[currentRow][currentCol] = item_snapshot.child("Status").getValue(boolean.class);

                    if (item_snapshot.child("Name").getValue() != null)
                        BH3400Name[currentRow][currentCol] = item_snapshot.child("Name").getValue().toString();

                    //Log.d("Name: ",item_snapshot.child("Name").getValue().toString());
                    //Log.d("Status: ",item_snapshot.child("Status").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



}





    // For Navigation Menu Items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (m_Toggle.onOptionsItemSelected(item)){
            switch (item.getItemId()) {
                case R.id.classrooms:
                    Intent classrooms = new Intent(getApplicationContext(), ClassroomList.class);
                    startActivity(classrooms);
                    break;
                case R.id.lots:
                    Intent parkingLots = new Intent("com.test.demo.PREFS");
                    startActivity(parkingLots);
                    break;
                case R.id.settings:
                    Intent settings = new Intent("com.test.demo.PREFS");
                    startActivity(settings);
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