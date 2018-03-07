package com.example.prince.myapplication;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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



public class math5200imageActivity extends AppCompatActivity implements OnClickableAreaClickedListener {


    final int MATH5200TotalRows = 7, MATH5200TotalCols = 26;
    Classroom MATH5200 = new Classroom("Math 5200", MATH5200TotalRows, MATH5200TotalCols);
    seatClass MATH5200SeatStatus[][] = new seatClass[MATH5200TotalRows + 1][MATH5200TotalCols + 1];

    final int seatWidth = 35;
    final int seatHeight = 30;

    int rowChosen = 0;
    int colChosen = 0;
    TextView seatTextView;
    ImageView math5200ImageView;
    List<ClickableArea> clickableAreas = new ArrayList<>();     // Initialize clickable area list




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math5200image);




        final EditText nameTextEdit = findViewById(R.id.nameTextEdit);
        Button takeBtn = findViewById(R.id.takeBtn);
        Button emptyBtn = findViewById(R.id.emptyBtn);
        seatTextView = findViewById(R.id.seatTextView);




        // Add image
        math5200ImageView = findViewById(R.id.math5200ImageView);
        //math5200ImageView.setImageBitmap((decodeImage(R.drawable.math5200_seat_map)));

        math5200ImageView.setImageResource(R.drawable.math5200_seat_map);
        PhotoViewAttacher photo = new PhotoViewAttacher(math5200ImageView);
        photo.setScaleLevels(1.0f, 3.0f, 5.0f);



        // Create your image
        ClickableAreasImage clickableAreasImage = new ClickableAreasImage(photo, this);


        // Define your clickable areas
        // parameter values (pixels): (x coordinate, y coordinate, width, height) and assign an object to it
        addMATH5200Seats();


        // Set your clickable areas to the image
        clickableAreasImage.setClickableAreas(clickableAreas);





        // Take button's action when clicked
        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameTextEdit.getText().toString().trim();

                String result = "";

                if (rowChosen <= MATH5200TotalRows && colChosen <= MATH5200TotalCols)
                {
                    if (MATH5200SeatStatus[rowChosen][colChosen].getSeatStatus() == false)
                    {
                        if (MATH5200.fillSeat(name, rowChosen, colChosen))
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
                if (Objects.equals(MATH5200SeatStatus[rowChosen][colChosen].getSeatName(), name))
                    result = MATH5200.emptySeat(rowChosen, colChosen);
                else
                    result = "You're not authorized!";


                if (result != "")
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            }
        });




        // Continuously reads data from database and records it to the array
        // ONLY FROM MATH5200
        DatabaseReference classroomRef = MATH5200.getCurrentClassroomRef();
        classroomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot item_snapshot : dataSnapshot.getChildren())
                {
                    DatabaseReference currentSeatStatus = item_snapshot.child("Status").getRef();
                    String currentSeat = currentSeatStatus.getParent().getKey();


                    char currentRowChar = currentSeat.charAt(5);
                    int currentRowInt = currentRowChar - 64;


                    String col = "";
                    int i = 12;
                    do {
                        col = col + currentSeat.charAt(i);
                        i++;
                    } while (i < currentSeat.length() && Character.getNumericValue(currentSeat.charAt(i)) != -1);
                    int currentCol = Integer.parseInt(col);


                    //Log.d("row: ",Integer.toString(currentRow));
                    //Log.d("col: ",Integer.toString(currentCol));


                    if (item_snapshot.child("Status").getValue() != null)
                        MATH5200SeatStatus[currentRowInt][currentCol].setSeatStatus(item_snapshot.child("Status").getValue(boolean.class));

                    if (item_snapshot.child("Name").getValue() != null)
                        MATH5200SeatStatus[currentRowInt][currentCol].setSeatName(item_snapshot.child("Name").getValue().toString());

                    //Log.d("Name: ",item_snapshot.child("Name").getValue().toString());
                    //Log.d("Status: ",item_snapshot.child("Status").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    private void drawRectangle(int x, int y)
    {
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.math5200_seat_map,myOptions);
        Paint paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setAlpha(100);

        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawRoundRect(x + 2, y + 2, x + seatWidth - 2, y + seatHeight - 2,30f,15f, paint);


        math5200ImageView.setImageBitmap(mutableBitmap);
    }




    // Listen for touches on image:
    @Override
    public void onClickableAreaTouched(Object item) {
        if (item instanceof seatClass) {

            rowChosen = ((seatClass) item).getRow();
            colChosen = ((seatClass) item).getCol();
            seatTextView.setText("Row: " + (char) (64 + rowChosen) + " Col: " + Integer.toString(colChosen));

            drawRectangle(((seatClass) item).getX(), ((seatClass) item).getY());

            //String text = ((seatClass) item).getSeatStatus() + " " + ((seatClass) item).getSeatName() + ((seatClass) item).getRow() + " " + ((seatClass) item).getCol();
            //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            //Log.d("rowChosen: ",Integer.toString(rowChosen));
            //Log.d("colChosen: ",Integer.toString(colChosen));

        }
    }




    // Define clickable areas
    // parameter values (pixels): (x coordinate, y coordinate, width, height) and assign an object to it
    // ADD SHIT TON OF AREAS TO CLICK
    private void addMATH5200Seats()
    {
        for (int i = 0; i <= MATH5200TotalRows; i++)
            for (int j = 0; j <= MATH5200TotalCols; j++)
            {
                MATH5200SeatStatus[i][j] = new seatClass(i, j);
                MATH5200SeatStatus[i][j].setSize(seatWidth, seatHeight);
            }


        // X values for the first row, A
        // Same for each row
        int row1X = 51;
        int row2X = 102;
        int row3X = 151;
        int row4X = 200;
        int row5X = 250;
        int row6X = 300;
        int row7X = 348;
        int row8X = 398;
        int row9X = 446;
        int row10X = 495;
        int row11X = 545;
        int row12X = 594;
        int row13X = 645;
        int row14X = 740;
        int row15X = 790;
        int row16X = 838;
        int row17X = 887;
        int row18X = 937;
        int row19X = 987;
        int row20X = 1035;
        int row21X = 1085;
        int row22X = 1135;
        int row23X = 1185;
        int row24X = 1232;
        int row25X = 1282;
        int row26X = 1332;


        // Y values for the first row, A
        // Subtract by 53 for each higher row
        int row1Y = 405;
        int row2Y = 400;
        int row3Y = 395;
        int row4Y = 391;
        int row5Y = 387;
        int row6Y = 383;
        int row7Y = 380;
        int row8Y = 378;
        int row9Y = 375;
        int row10Y = 373;
        int row11Y = 372;
        int row12Y = 371;
        int row13Y = 371;
        int row14Y = 370;
        int row15Y = 370;
        int row16Y = 371;
        int row17Y = 372;
        int row18Y = 373;
        int row19Y = 375;
        int row20Y = 378;
        int row21Y = 380;
        int row22Y = 384;
        int row23Y = 388;
        int row24Y = 395;
        int row25Y = 400;
        int row26Y = 405;






/*
        // Row 1 / Row A
        MATH5200SeatStatus[1][4].setTopLeft(row4X, row4Y);
        MATH5200SeatStatus[1][5].setTopLeft(row5X, row5Y);
        MATH5200SeatStatus[1][6].setTopLeft(row6X ,row6Y);
        MATH5200SeatStatus[1][7].setTopLeft(row7X ,row7Y);
        MATH5200SeatStatus[1][8].setTopLeft(row8X, row8Y);
        MATH5200SeatStatus[1][9].setTopLeft(row9X, row9Y);
        MATH5200SeatStatus[1][10].setTopLeft(row10X, row10Y);
        MATH5200SeatStatus[1][11].setTopLeft(row11X, row11Y);
        MATH5200SeatStatus[1][12].setTopLeft(row12X, row12Y);
        MATH5200SeatStatus[1][13].setTopLeft(row13X, row13Y);
        MATH5200SeatStatus[1][14].setTopLeft(row14X, row14Y);
        MATH5200SeatStatus[1][15].setTopLeft(row15X, row15Y);
        MATH5200SeatStatus[1][16].setTopLeft(row16X, row16Y);
        MATH5200SeatStatus[1][17].setTopLeft(row17X, row17Y);
        MATH5200SeatStatus[1][18].setTopLeft(row18X, row18Y);
        MATH5200SeatStatus[1][19].setTopLeft(row19X, row19Y);
        MATH5200SeatStatus[1][20].setTopLeft(row20X, row20Y);
        MATH5200SeatStatus[1][21].setTopLeft(row21X, row21Y);
        MATH5200SeatStatus[1][22].setTopLeft(row22X, row22Y);
        MATH5200SeatStatus[1][23].setTopLeft(row23X, row23Y);


        // Row 2 / Row B
        int diffFactor = 1;
        MATH5200SeatStatus[2][3].setTopLeft(row3X, row3Y - (53 * diffFactor));
        MATH5200SeatStatus[2][4].setTopLeft(row4X, row4Y - (53 * diffFactor));
        MATH5200SeatStatus[2][5].setTopLeft(row5X, row5Y - (53 * diffFactor));
        MATH5200SeatStatus[2][6].setTopLeft(row6X ,row6Y - (53 * diffFactor));
        MATH5200SeatStatus[2][7].setTopLeft(row7X ,row7Y - (53 * diffFactor));
        MATH5200SeatStatus[2][8].setTopLeft(row8X, row8Y - (53 * diffFactor));
        MATH5200SeatStatus[2][9].setTopLeft(row9X, row9Y - (53 * diffFactor));
        MATH5200SeatStatus[2][10].setTopLeft(row10X, row10Y - (53 * diffFactor));
        MATH5200SeatStatus[2][11].setTopLeft(row11X, row11Y - (53 * diffFactor));
        MATH5200SeatStatus[2][12].setTopLeft(row12X, row12Y - (53 * diffFactor));
        MATH5200SeatStatus[2][13].setTopLeft(row13X, row13Y - (53 * diffFactor));
        MATH5200SeatStatus[2][14].setTopLeft(row14X, row14Y - (53 * diffFactor));
        MATH5200SeatStatus[2][15].setTopLeft(row15X, row15Y - (53 * diffFactor));
        MATH5200SeatStatus[2][16].setTopLeft(row16X, row16Y - (53 * diffFactor));
        MATH5200SeatStatus[2][17].setTopLeft(row17X, row17Y - (53 * diffFactor));
        MATH5200SeatStatus[2][18].setTopLeft(row18X, row18Y - (53 * diffFactor));
        MATH5200SeatStatus[2][19].setTopLeft(row19X, row19Y - (53 * diffFactor));
        MATH5200SeatStatus[2][20].setTopLeft(row20X, row20Y - (53 * diffFactor));
        MATH5200SeatStatus[2][21].setTopLeft(row21X, row21Y - (53 * diffFactor));
        MATH5200SeatStatus[2][22].setTopLeft(row22X, row22Y - (53 * diffFactor));
        MATH5200SeatStatus[2][23].setTopLeft(row23X, row23Y - (53 * diffFactor));
        MATH5200SeatStatus[2][24].setTopLeft(row24X, row24Y - (53 * diffFactor));


        // Row 3 / Row C
        diffFactor = 2;
        MATH5200SeatStatus[3][2].setTopLeft(row2X, row2Y - (53 * diffFactor));
        MATH5200SeatStatus[3][3].setTopLeft(row3X, row3Y - (53 * diffFactor));
        MATH5200SeatStatus[3][4].setTopLeft(row4X, row4Y - (53 * diffFactor));
        MATH5200SeatStatus[3][5].setTopLeft(row5X, row5Y - (53 * diffFactor));
        MATH5200SeatStatus[3][6].setTopLeft(row6X ,row6Y - (53 * diffFactor));
        MATH5200SeatStatus[3][7].setTopLeft(row7X ,row7Y - (53 * diffFactor));
        MATH5200SeatStatus[3][8].setTopLeft(row8X, row8Y - (53 * diffFactor));
        MATH5200SeatStatus[3][9].setTopLeft(row9X, row9Y - (53 * diffFactor));
        MATH5200SeatStatus[3][10].setTopLeft(row10X, row10Y - (53 * diffFactor));
        MATH5200SeatStatus[3][11].setTopLeft(row11X, row11Y - (53 * diffFactor));
        MATH5200SeatStatus[3][12].setTopLeft(row12X, row12Y - (53 * diffFactor));
        MATH5200SeatStatus[3][13].setTopLeft(row13X, row13Y - (53 * diffFactor));
        MATH5200SeatStatus[3][14].setTopLeft(row14X, row14Y - (53 * diffFactor));
        MATH5200SeatStatus[3][15].setTopLeft(row15X, row15Y - (53 * diffFactor));
        MATH5200SeatStatus[3][16].setTopLeft(row16X, row16Y - (53 * diffFactor));
        MATH5200SeatStatus[3][17].setTopLeft(row17X, row17Y - (53 * diffFactor));
        MATH5200SeatStatus[3][18].setTopLeft(row18X, row18Y - (53 * diffFactor));
        MATH5200SeatStatus[3][19].setTopLeft(row19X, row19Y - (53 * diffFactor));
        MATH5200SeatStatus[3][20].setTopLeft(row20X, row20Y - (53 * diffFactor));
        MATH5200SeatStatus[3][21].setTopLeft(row21X, row21Y - (53 * diffFactor));
        MATH5200SeatStatus[3][22].setTopLeft(row22X, row22Y - (53 * diffFactor));
        MATH5200SeatStatus[3][23].setTopLeft(row23X, row23Y - (53 * diffFactor));
        MATH5200SeatStatus[3][24].setTopLeft(row24X, row24Y - (53 * diffFactor));
        MATH5200SeatStatus[3][25].setTopLeft(row25X, row25Y - (53 * diffFactor));


        // Row 4 / Row D
        diffFactor = 3;
        MATH5200SeatStatus[4][1].setTopLeft(row1X, row1Y - (53 * diffFactor));
        MATH5200SeatStatus[4][2].setTopLeft(row2X, row2Y - (53 * diffFactor));
        MATH5200SeatStatus[4][3].setTopLeft(row3X, row3Y - (53 * diffFactor));
        MATH5200SeatStatus[4][4].setTopLeft(row4X, row4Y - (53 * diffFactor));
        MATH5200SeatStatus[4][5].setTopLeft(row5X, row5Y - (53 * diffFactor));
        MATH5200SeatStatus[4][6].setTopLeft(row6X ,row6Y - (53 * diffFactor));
        MATH5200SeatStatus[4][7].setTopLeft(row7X ,row7Y - (53 * diffFactor));
        MATH5200SeatStatus[4][8].setTopLeft(row8X, row8Y - (53 * diffFactor));
        MATH5200SeatStatus[4][9].setTopLeft(row9X, row9Y - (53 * diffFactor));
        MATH5200SeatStatus[4][10].setTopLeft(row10X, row10Y - (53 * diffFactor));
        MATH5200SeatStatus[4][11].setTopLeft(row11X, row11Y - (53 * diffFactor));
        MATH5200SeatStatus[4][12].setTopLeft(row12X, row12Y - (53 * diffFactor));
        MATH5200SeatStatus[4][13].setTopLeft(row13X, row13Y - (53 * diffFactor));
        MATH5200SeatStatus[4][14].setTopLeft(row14X, row14Y - (53 * diffFactor));
        MATH5200SeatStatus[4][15].setTopLeft(row15X, row15Y - (53 * diffFactor));
        MATH5200SeatStatus[4][16].setTopLeft(row16X, row16Y - (53 * diffFactor));
        MATH5200SeatStatus[4][17].setTopLeft(row17X, row17Y - (53 * diffFactor));
        MATH5200SeatStatus[4][18].setTopLeft(row18X, row18Y - (53 * diffFactor));
        MATH5200SeatStatus[4][19].setTopLeft(row19X, row19Y - (53 * diffFactor));
        MATH5200SeatStatus[4][20].setTopLeft(row20X, row20Y - (53 * diffFactor));
        MATH5200SeatStatus[4][21].setTopLeft(row21X, row21Y - (53 * diffFactor));
        MATH5200SeatStatus[4][22].setTopLeft(row22X, row22Y - (53 * diffFactor));
        MATH5200SeatStatus[4][23].setTopLeft(row23X, row23Y - (53 * diffFactor));
        MATH5200SeatStatus[4][24].setTopLeft(row24X, row24Y - (53 * diffFactor));
        MATH5200SeatStatus[4][25].setTopLeft(row25X, row25Y - (53 * diffFactor));
        MATH5200SeatStatus[4][26].setTopLeft(row26X, row26Y - (53 * diffFactor));


        // Row 5 / Row E
        diffFactor = 4;
        MATH5200SeatStatus[5][1].setTopLeft(row1X, row1Y - (53 * diffFactor));
        MATH5200SeatStatus[5][2].setTopLeft(row2X, row2Y - (53 * diffFactor));
        MATH5200SeatStatus[5][3].setTopLeft(row3X, row3Y - (53 * diffFactor));
        MATH5200SeatStatus[5][4].setTopLeft(row4X, row4Y - (53 * diffFactor));
        MATH5200SeatStatus[5][5].setTopLeft(row5X, row5Y - (53 * diffFactor));
        MATH5200SeatStatus[5][6].setTopLeft(row6X ,row6Y - (53 * diffFactor));
        MATH5200SeatStatus[5][7].setTopLeft(row7X ,row7Y - (53 * diffFactor));
        MATH5200SeatStatus[5][8].setTopLeft(row8X, row8Y - (53 * diffFactor));
        MATH5200SeatStatus[5][9].setTopLeft(row9X, row9Y - (53 * diffFactor));
        MATH5200SeatStatus[5][10].setTopLeft(row10X, row10Y - (53 * diffFactor));
        MATH5200SeatStatus[5][11].setTopLeft(row11X, row11Y - (53 * diffFactor));
        MATH5200SeatStatus[5][12].setTopLeft(row12X, row12Y - (53 * diffFactor));
        MATH5200SeatStatus[5][13].setTopLeft(row13X, row13Y - (53 * diffFactor));
        MATH5200SeatStatus[5][14].setTopLeft(row14X, row14Y - (53 * diffFactor));
        MATH5200SeatStatus[5][15].setTopLeft(row15X, row15Y - (53 * diffFactor));
        MATH5200SeatStatus[5][16].setTopLeft(row16X, row16Y - (53 * diffFactor));
        MATH5200SeatStatus[5][17].setTopLeft(row17X, row17Y - (53 * diffFactor));
        MATH5200SeatStatus[5][18].setTopLeft(row18X, row18Y - (53 * diffFactor));
        MATH5200SeatStatus[5][19].setTopLeft(row19X, row19Y - (53 * diffFactor));
        MATH5200SeatStatus[5][20].setTopLeft(row20X, row20Y - (53 * diffFactor));
        MATH5200SeatStatus[5][21].setTopLeft(row21X, row21Y - (53 * diffFactor));
        MATH5200SeatStatus[5][22].setTopLeft(row22X, row22Y - (53 * diffFactor));
        MATH5200SeatStatus[5][23].setTopLeft(row23X, row23Y - (53 * diffFactor));
        MATH5200SeatStatus[5][24].setTopLeft(row24X, row24Y - (53 * diffFactor));
        MATH5200SeatStatus[5][25].setTopLeft(row25X, row25Y - (53 * diffFactor));
        MATH5200SeatStatus[5][26].setTopLeft(row26X, row26Y - (53 * diffFactor));


        // Row 6 / Row F
        diffFactor = 5;
        MATH5200SeatStatus[6][1].setTopLeft(row1X, row1Y - (53 * diffFactor));
        MATH5200SeatStatus[6][2].setTopLeft(row2X, row2Y - (53 * diffFactor));
        MATH5200SeatStatus[6][3].setTopLeft(row3X, row3Y - (53 * diffFactor));
        MATH5200SeatStatus[6][4].setTopLeft(row4X, row4Y - (53 * diffFactor));
        MATH5200SeatStatus[6][5].setTopLeft(row5X, row5Y - (53 * diffFactor));
        MATH5200SeatStatus[6][6].setTopLeft(row6X ,row6Y - (53 * diffFactor));
        MATH5200SeatStatus[6][7].setTopLeft(row7X ,row7Y - (53 * diffFactor));
        MATH5200SeatStatus[6][8].setTopLeft(row8X, row8Y - (53 * diffFactor));
        MATH5200SeatStatus[6][9].setTopLeft(row9X, row9Y - (53 * diffFactor));
        MATH5200SeatStatus[6][10].setTopLeft(row10X, row10Y - (53 * diffFactor));
        MATH5200SeatStatus[6][11].setTopLeft(row11X, row11Y - (53 * diffFactor));
        MATH5200SeatStatus[6][12].setTopLeft(row12X, row12Y - (53 * diffFactor));
        MATH5200SeatStatus[6][13].setTopLeft(row13X, row13Y - (53 * diffFactor));
        MATH5200SeatStatus[6][14].setTopLeft(row14X, row14Y - (53 * diffFactor));
        MATH5200SeatStatus[6][15].setTopLeft(row15X, row15Y - (53 * diffFactor));
        MATH5200SeatStatus[6][16].setTopLeft(row16X, row16Y - (53 * diffFactor));
        MATH5200SeatStatus[6][17].setTopLeft(row17X, row17Y - (53 * diffFactor));
        MATH5200SeatStatus[6][18].setTopLeft(row18X, row18Y - (53 * diffFactor));
        MATH5200SeatStatus[6][19].setTopLeft(row19X, row19Y - (53 * diffFactor));
        MATH5200SeatStatus[6][20].setTopLeft(row20X, row20Y - (53 * diffFactor));
        MATH5200SeatStatus[6][21].setTopLeft(row21X, row21Y - (53 * diffFactor));
        MATH5200SeatStatus[6][22].setTopLeft(row22X, row22Y - (53 * diffFactor));
        MATH5200SeatStatus[6][23].setTopLeft(row23X, row23Y - (53 * diffFactor));
        MATH5200SeatStatus[6][24].setTopLeft(row24X, row24Y - (53 * diffFactor));
        MATH5200SeatStatus[6][25].setTopLeft(row25X, row25Y - (53 * diffFactor));
        MATH5200SeatStatus[6][26].setTopLeft(row26X, row26Y - (53 * diffFactor));


        // Row 7 / Row G
        diffFactor = 6;
        MATH5200SeatStatus[7][1].setTopLeft(row1X, row1Y - (53 * diffFactor));
        MATH5200SeatStatus[7][2].setTopLeft(row2X, row2Y - (53 * diffFactor));
        MATH5200SeatStatus[7][3].setTopLeft(row3X, row3Y - (53 * diffFactor));
        MATH5200SeatStatus[7][4].setTopLeft(row4X, row4Y - (53 * diffFactor));
        MATH5200SeatStatus[7][5].setTopLeft(row5X, row5Y - (53 * diffFactor));
        MATH5200SeatStatus[7][6].setTopLeft(row6X ,row6Y - (53 * diffFactor));
        MATH5200SeatStatus[7][7].setTopLeft(row7X ,row7Y - (53 * diffFactor));
        MATH5200SeatStatus[7][8].setTopLeft(row8X, row8Y - (53 * diffFactor));
        MATH5200SeatStatus[7][11].setTopLeft(row11X, row11Y - (53 * diffFactor));
        MATH5200SeatStatus[7][12].setTopLeft(row12X, row12Y - (53 * diffFactor));
        MATH5200SeatStatus[7][13].setTopLeft(row13X, row13Y - (53 * diffFactor));
        MATH5200SeatStatus[7][19].setTopLeft(row19X, row19Y - (53 * diffFactor));
        MATH5200SeatStatus[7][20].setTopLeft(row20X, row20Y - (53 * diffFactor));
        MATH5200SeatStatus[7][21].setTopLeft(row21X, row21Y - (53 * diffFactor));
        MATH5200SeatStatus[7][22].setTopLeft(row22X, row22Y - (53 * diffFactor));
        MATH5200SeatStatus[7][23].setTopLeft(row23X, row23Y - (53 * diffFactor));
        MATH5200SeatStatus[7][24].setTopLeft(row24X, row24Y - (53 * diffFactor));
        MATH5200SeatStatus[7][25].setTopLeft(row25X, row25Y - (53 * diffFactor));
        MATH5200SeatStatus[7][26].setTopLeft(row26X, row26Y - (53 * diffFactor));





        // Add Row 1 (A) to Clickable Areas
        for (int col = 4; col <= 23; col++)
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[1][col].getX(), MATH5200SeatStatus[1][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[1][col]));


        // Add Row 2 (B) to Clickable Areas
        for (int col = 3; col <= 24; col++)
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[2][col].getX(), MATH5200SeatStatus[2][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[2][col]));


        // Add Row 3 (C) to Clickable Areas
        for (int col = 2; col <= 25; col++)
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[3][col].getX(), MATH5200SeatStatus[3][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[3][col]));


        // Add Row 4 (D), Row 5 (E), and Row 6 (F) to Clickable Areas
        for (int col = 1; col <= 26; col++)
        {
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[4][col].getX(), MATH5200SeatStatus[4][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[4][col]));
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[5][col].getX(), MATH5200SeatStatus[5][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[5][col]));
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[6][col].getX(), MATH5200SeatStatus[6][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[6][col]));
        }

        // Add Row 7 (G) to Clickable Areas
        for (int col = 1; col <= 8; col++)
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[7][col].getX(), MATH5200SeatStatus[7][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[7][col]));

        for (int col = 11; col <= 13; col++)
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[7][col].getX(), MATH5200SeatStatus[7][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[7][col]));

        for (int col = 19; col <= 26; col++)
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[7][col].getX(), MATH5200SeatStatus[7][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[7][col]));
*/



    }




}