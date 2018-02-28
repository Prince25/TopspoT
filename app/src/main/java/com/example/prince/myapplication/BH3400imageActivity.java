package com.example.prince.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import at.lukle.clickableareasimage.ClickableArea;
import at.lukle.clickableareasimage.ClickableAreasImage;
import at.lukle.clickableareasimage.OnClickableAreaClickedListener;
import uk.co.senab.photoview.PhotoViewAttacher;


public class bh3400imageActivity extends AppCompatActivity implements OnClickableAreaClickedListener {


    final int BH3400TotalRows = 10, BH3400TotalCols = 20;
    Classroom BH3400 = new Classroom("BH 3400", BH3400TotalRows, BH3400TotalCols);
    seatClass BH3400SeatStatus[][] = new seatClass[BH3400TotalRows + 1][BH3400TotalCols + 1];

    int rowChosen = 0;
    int colChosen = 0;
    TextView seatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bh3400image);

        for (int i = 0; i <= BH3400TotalRows; i++)
            for (int j = 0; j <= BH3400TotalCols; j++)
            {
                BH3400SeatStatus[i][j] = new seatClass();
                BH3400SeatStatus[i][j].setRow(i);
                BH3400SeatStatus[i][j].setCol(j);
            }


        final EditText nameTextEdit = (EditText) findViewById(R.id.nameTextEdit);
        Button takeBtn = (Button) findViewById(R.id.takeBtn);
        Button emptyBtn = (Button) findViewById(R.id.emptyBtn);
        seatTextView = (TextView) findViewById(R.id.seatTextView);


        // Add image
        ImageView bh3400ImageView = (ImageView) findViewById(R.id.bh3400ImageView);
        bh3400ImageView.setImageResource(R.drawable.testclassroom);

        // Create your image
        ClickableAreasImage clickableAreasImage = new ClickableAreasImage(new PhotoViewAttacher(bh3400ImageView), this);

        // Initialize your clickable area list
        List<ClickableArea> clickableAreas = new ArrayList<>();


        // Define your clickable areas
        // parameter values (pixels): (x coordinate, y coordinate, width, height) and assign an object to it
        clickableAreas.add(new ClickableArea(8, 8, 116, 117, BH3400SeatStatus[1][1]));
        clickableAreas.add(new ClickableArea(200, 8, 116, 117, BH3400SeatStatus[1][2]));
        clickableAreas.add(new ClickableArea(400, 8, 116, 117, BH3400SeatStatus[1][3]));
        clickableAreas.add(new ClickableArea(600, 8, 116, 117, BH3400SeatStatus[1][4]));
        clickableAreas.add(new ClickableArea(800, 8, 116, 117, BH3400SeatStatus[1][5]));

        clickableAreas.add(new ClickableArea(5, 213, 116, 117, BH3400SeatStatus[2][1]));
        clickableAreas.add(new ClickableArea(196, 213, 116, 117, BH3400SeatStatus[2][2]));
        clickableAreas.add(new ClickableArea(398, 213, 116, 117, BH3400SeatStatus[2][3]));
        clickableAreas.add(new ClickableArea(600, 213, 116, 117, BH3400SeatStatus[2][4]));
        clickableAreas.add(new ClickableArea(800, 213, 116, 117, BH3400SeatStatus[2][5]));

        clickableAreas.add(new ClickableArea(3, 402, 116, 117, BH3400SeatStatus[3][1]));
        clickableAreas.add(new ClickableArea(195, 402, 116, 117, BH3400SeatStatus[3][2]));
        clickableAreas.add(new ClickableArea(398, 402, 116, 117, BH3400SeatStatus[3][3]));
        clickableAreas.add(new ClickableArea(599, 402, 116, 117, BH3400SeatStatus[3][4]));
        clickableAreas.add(new ClickableArea(797, 402, 116, 117, BH3400SeatStatus[3][5]));

        // ADD SHIT TON OF AREAS TO CLICK



        // Set your clickable areas to the image
        clickableAreasImage.setClickableAreas(clickableAreas);


        // Take button's action when clicked
        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameTextEdit.getText().toString().trim();
                //int row = Integer.parseInt(rowTextEdit.getText().toString());
                //int col = Integer.parseInt(colTextEdit.getText().toString());

                String result = "";

                if (rowChosen <= BH3400TotalRows && colChosen <= BH3400TotalCols)
                {
                    if (BH3400SeatStatus[rowChosen][colChosen].getSeatStatus() == false)
                    {
                        if (BH3400.fillSeat(name, rowChosen, colChosen))
                            result = "Seat Taken!";
                    }
                    else
                        result = "Seat is already occupied";
                }
                else
                    result = "Seat number is invalid";


                if (result != "")
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        });



        // Empty button's action when clicked
        emptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameTextEdit.getText().toString().trim();

                String result = "";
                if (Objects.equals(BH3400SeatStatus[rowChosen][colChosen].getSeatName(), name))
                    result = BH3400.emptySeat(rowChosen, colChosen);
                else
                    result = "You're not authorized!";


                if (result != "")
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

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
                    //Log.d("col: ",Integer.toString(currentCol));

                    if (item_snapshot.child("Status").getValue() != null)
                        BH3400SeatStatus[currentRow][currentCol].setSeatStatus(item_snapshot.child("Status").getValue(boolean.class));

                    if (item_snapshot.child("Name").getValue() != null)
                        BH3400SeatStatus[currentRow][currentCol].setSeatName(item_snapshot.child("Name").getValue().toString());

                    //Log.d("Name: ",item_snapshot.child("Name").getValue().toString());
                    //Log.d("Status: ",item_snapshot.child("Status").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    // Listen for touches on your images:
    @Override
    public void onClickableAreaTouched(Object item) {
        if (item instanceof seatClass) {

            rowChosen = ((seatClass) item).getRow();
            colChosen = ((seatClass) item).getCol();
            seatTextView.setText("Row: " + Integer.toString(rowChosen) + " Col: " + Integer.toString(colChosen));

            //String text = ((seatClass) item).getSeatStatus() + " " + ((seatClass) item).getSeatName() + ((seatClass) item).getRow() + " " + ((seatClass) item).getCol();
            //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            //Log.d("rowChosen: ",Integer.toString(rowChosen));
            //Log.d("colChosen: ",Integer.toString(colChosen));

        }
    }






}




