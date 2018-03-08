package com.example.prince.myapplication;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
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


    final int MATH5200TotalRows = 9, MATH5200TotalCols = 12;
    Classroom MATH5200 = new Classroom("Math 5200", MATH5200TotalRows, MATH5200TotalCols);
    seatClass MATH5200SeatStatus[][] = new seatClass[MATH5200TotalRows + 1][MATH5200TotalCols + 1];

    final int seatWidth = 75;
    final int seatHeight = 57;

    int rowChosen = 0;
    int colChosen = 0;
    TextView seatTextView;
    ImageView math5200ImageView;
    List<ClickableArea> clickableAreas = new ArrayList<>();     // Initialize clickable area list


    // Used to draw the bitmaps over the seats
    BitmapFactory.Options myOptions = new BitmapFactory.Options();
    Bitmap bitmap;
    Paint paint = new Paint();
    Bitmap workingBitmap;
    Bitmap mutableBitmap;
    Canvas canvas;

    // Used to update the bitmaps over the already taken seats
    int mInterval = 500;
    Handler mHandler;
    boolean seatsUpdated = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math5200image);


        // Used for drawing bitmaps
        mHandler = new Handler();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.math5200_seat_map,myOptions);
        workingBitmap = Bitmap.createBitmap(bitmap);
        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);



        final EditText nameTextEdit = findViewById(R.id.nameTextEdit);
        Button takeBtn = findViewById(R.id.takeBtn);
        Button emptyBtn = findViewById(R.id.emptyBtn);
        Button refreshBtn = findViewById(R.id.refreshBtn);
        seatTextView = findViewById(R.id.seatTextView);




        // Add image
        math5200ImageView = findViewById(R.id.math5200ImageView);
        math5200ImageView.setImageResource(R.drawable.math5200_seat_map);

        PhotoViewAttacher photo = new PhotoViewAttacher(math5200ImageView);
        photo.setScaleLevels(1.0f, 3.0f, 5.0f);


        // Create clickable image
        ClickableAreasImage clickableAreasImage = new ClickableAreasImage(photo, this);


        // Define your clickable areas
        // parameter values (pixels): (x coordinate, y coordinate, width, height) and assign an object to it
        addMATH5200Seats();


        // Start updating the seat status bitmaps
        startRepeatingTask();


        // Set clickable areas to the image
        clickableAreasImage.setClickableAreas(clickableAreas);





        // Refresh button's action when clicked
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seatsUpdated = false;
                mInterval = 100;
            }
        });



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

                seatsUpdated = false;
                mInterval = 100;

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

                seatsUpdated = false;
                mInterval = 100;

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

                seatsUpdated = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    // Draws bitmap over the current chosen seat by the user
    private void drawRectangle(int x, int y)
    {
        workingBitmap = Bitmap.createBitmap(bitmap);
        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        canvas = new Canvas(mutableBitmap);
        paint.setColor(Color.CYAN);
        paint.setAlpha(100);
        canvas.drawRoundRect(x + 2, y + 2, x + seatWidth - 2, y + seatHeight - 2,30f,15f, paint);

        math5200ImageView.setImageBitmap(mutableBitmap);
        seatsUpdated = false;
    }



    // Listen for touches on image
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


        // X values for the second row, B
        // Same for each row
        int rowX[] = new int[MATH5200TotalCols + 1];
        rowX[1] = 110;
        rowX[2] = 211;
        rowX[3] = 312;
        rowX[4] = 413;
        rowX[5] = 514;
        rowX[6] = 616;
        rowX[7] = 717;
        rowX[8] = 818;
        rowX[9] = 919;
        rowX[10] = 1020;
        rowX[11] = 1121;
        rowX[12] = 1222;



        // Y values for the Second row, B
        // Subtract by 103 for each higher row
        int difference = 103;
        int rowY[] = new int[MATH5200TotalCols + 1];
        rowY[1] = 912;
        rowY[2] = 908;
        rowY[3] = 903;
        rowY[4] = 899;
        rowY[5] = 897;
        rowY[6] = 896;
        rowY[7] = 894;
        rowY[8] = 894;
        rowY[9] = 895;
        rowY[10] = 897;
        rowY[11] = 901;
        rowY[12] = 903;





        // Row 1 / Row A
        for (int col = 2; col <= MATH5200TotalCols; col++)
        {
            MATH5200SeatStatus[1][col].setTopLeft(rowX[col], rowY[col]);
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[1][col].getX(), MATH5200SeatStatus[1][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[1][col]));
        }


        // Row 2 / Row B
        int diffFactor = difference;
        for (int col = 1; col <= 11; col++)
        {
            MATH5200SeatStatus[2][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[2][col].getX(), MATH5200SeatStatus[2][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[2][col]));
        }


        // Row 3 / Row C
        diffFactor += difference;
        for (int col = 1; col <= MATH5200TotalCols; col++ )
        {
            MATH5200SeatStatus[3][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[3][col].getX(), MATH5200SeatStatus[3][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[3][col]));
        }



        // Row 4 / Row D
        diffFactor += difference;
        for (int col = 1; col <= MATH5200TotalCols; col++ )
        {
            MATH5200SeatStatus[4][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[4][col].getX(), MATH5200SeatStatus[4][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[4][col]));
        }



        // Row 5 / Row E
        diffFactor += difference;
        for (int col = 1; col <= MATH5200TotalCols; col++ )
        {
            MATH5200SeatStatus[5][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[5][col].getX(), MATH5200SeatStatus[5][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[5][col]));
        }



        // Row 6 / Row F
        diffFactor += difference;
        for (int col = 1; col <= MATH5200TotalCols; col++ )
        {
            MATH5200SeatStatus[6][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[6][col].getX(), MATH5200SeatStatus[6][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[6][col]));
        }



        // Row 7 / Row G
        diffFactor += difference + 5;
        for (int col = 1; col <= MATH5200TotalCols; col++ )
        {
            MATH5200SeatStatus[7][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[7][col].getX(), MATH5200SeatStatus[7][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[7][col]));
        }



        // Row 8 / Row H
        diffFactor += difference;
        for (int col = 1; col <= 9; col++ )
        {
            MATH5200SeatStatus[8][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MATH5200SeatStatus[8][col].getX(), MATH5200SeatStatus[8][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[8][col]));
        }



        // Row 9 / Row I
        diffFactor += difference;
        for (int col = 1; col <= MATH5200TotalCols; col++ )
        {
            if (col != 8)
            {
                MATH5200SeatStatus[9][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
                clickableAreas.add(new ClickableArea(MATH5200SeatStatus[9][col].getX(), MATH5200SeatStatus[9][col].getY(), seatWidth, seatHeight, MATH5200SeatStatus[9][col]));
            }
        }

    }





    // Used to periodically draw bitmaps over the seat map
    private void startRepeatingTask()
    {
        statusChecker.run();
    }

    Runnable statusChecker = new Runnable() {
        @Override
        public void run() {
            try
            {
                //Log.d("Updated: ",Boolean.toString(seatsUpdated));
                if (!seatsUpdated)
                {
                    mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

                    canvas = new Canvas(mutableBitmap);
                    paint.setColor(Color.RED);
                    paint.setAlpha(100);

                    for (int i = 0; i <= MATH5200TotalRows; i++)
                        for (int j = 0; j <= MATH5200TotalCols; j++)
                        {
                            boolean status = MATH5200SeatStatus[i][j].getSeatStatus();
                            if (status)
                            {
                                int x = MATH5200SeatStatus[i][j].getX();
                                int y = MATH5200SeatStatus[i][j].getY();

                                canvas.drawRoundRect(x + 2, y + 2, x + seatWidth - 2, y + seatHeight - 2,30f,15f, paint);
                            }
                        }

                    math5200ImageView.setImageBitmap(mutableBitmap);
                    mInterval = 2500;
                    seatsUpdated = true;
                }
            }
            finally
            {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(statusChecker, mInterval);
            }
        }
    };



}