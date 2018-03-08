package com.ucla.topspot;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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



public class moore100imageActivity extends AppCompatActivity implements OnClickableAreaClickedListener {


    final int MOORE100TotalRows = 20, MOORE100TotalCols = 23;
    Classroom MOORE100 = new Classroom("Moore 100", MOORE100TotalRows, MOORE100TotalCols);
    seatClass MOORE100SeatStatus[][] = new seatClass[MOORE100TotalRows + 1][MOORE100TotalCols + 1];

    final int seatWidth = 35;
    final int seatHeight = 30;

    int rowChosen = 0;
    int colChosen = 0;
    TextView seatTextView;
    ImageView moore100ImageView;
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
        setContentView(R.layout.activity_moore100image);


        // Used for drawing bitmaps
        mHandler = new Handler();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moore100_seat_map,myOptions);
        workingBitmap = Bitmap.createBitmap(bitmap);
        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);



        final EditText nameTextEdit = findViewById(R.id.nameTextEdit);
        Button takeBtn = findViewById(R.id.takeBtn);
        Button emptyBtn = findViewById(R.id.emptyBtn);
        Button refreshBtn = findViewById(R.id.refreshBtn);
        seatTextView = findViewById(R.id.seatTextView);




        // Add image
        moore100ImageView = findViewById(R.id.moore100ImageView);
        moore100ImageView.setImageResource(R.drawable.moore100_seat_map);

        PhotoViewAttacher photo = new PhotoViewAttacher(moore100ImageView);
        photo.setScaleLevels(1.0f, 3.0f, 5.0f);


        // Create clickable image
        ClickableAreasImage clickableAreasImage = new ClickableAreasImage(photo, this);


        // Define your clickable areas
        // parameter values (pixels): (x coordinate, y coordinate, width, height) and assign an object to it
        addMOORE100Seats();


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

                if (rowChosen <= MOORE100TotalRows && colChosen <= MOORE100TotalCols)
                {
                    if (MOORE100SeatStatus[rowChosen][colChosen].getSeatStatus() == false)
                    {
                        if (MOORE100.fillSeat(name, rowChosen, colChosen))
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
                if (Objects.equals(MOORE100SeatStatus[rowChosen][colChosen].getSeatName(), name))
                    result = MOORE100.emptySeat(rowChosen, colChosen);
                else
                    result = "You're not authorized!";

                seatsUpdated = false;
                mInterval = 100;

                if (result != "")
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            }
        });




        // Continuously reads data from database and records it to the array
        // ONLY FROM MOORE100
        DatabaseReference classroomRef = MOORE100.getCurrentClassroomRef();
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
                        MOORE100SeatStatus[currentRowInt][currentCol].setSeatStatus(item_snapshot.child("Status").getValue(boolean.class));

                    if (item_snapshot.child("Name").getValue() != null)
                        MOORE100SeatStatus[currentRowInt][currentCol].setSeatName(item_snapshot.child("Name").getValue().toString());

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

        moore100ImageView.setImageBitmap(mutableBitmap);
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
    private void addMOORE100Seats()
    {
        for (int i = 0; i <= MOORE100TotalRows; i++)
            for (int j = 0; j <= MOORE100TotalCols; j++)
            {
                MOORE100SeatStatus[i][j] = new seatClass(i, j);
                MOORE100SeatStatus[i][j].setSize(seatWidth, seatHeight);
            }


        // X values for the second row, B
        // Same for each row
        int rowX[] = new int[MOORE100TotalCols + 1];
        rowX[1] = 40;
        rowX[2] = 90;
        rowX[3] = 136;
        rowX[4] = 182;
        rowX[5] = 230;
        rowX[6] = 276;
        rowX[7] = 390;
        rowX[8] = 437;
        rowX[9] = 485;
        rowX[10] = 532;
        rowX[11] = 578;
        rowX[12] = 624;
        rowX[13] = 670;
        rowX[14] = 718;
        rowX[15] = 764;
        rowX[16] = 812;
        rowX[17] = 858;
        rowX[18] = 968;
        rowX[19] = 1014;
        rowX[20] = 1061;
        rowX[21] = 1108;
        rowX[22] = 1155;
        rowX[23] = 1202;


        // Y values for the Second row, B
        // Subtract by 48 for each higher row
        int difference = 50;
        int rowY[] = new int[MOORE100TotalCols + 1];
        rowY[1] = 966;
        rowY[2] = 962;
        rowY[3] = 958;
        rowY[4] = 954;
        rowY[5] = 951;
        rowY[6] = 948;
        rowY[7] = 945;
        rowY[8] = 944;
        rowY[9] = 942;
        rowY[10] = 940;
        rowY[11] = 940;
        rowY[12] = 938;
        rowY[13] = 938;
        rowY[14] = 938;
        rowY[15] = 939;
        rowY[16] = 941;
        rowY[17] = 942;
        rowY[18] = 944;
        rowY[19] = 946;
        rowY[20] = 949;
        rowY[21] = 952;
        rowY[22] = 956;
        rowY[23] = 963;





        // Row 1 / Row A
        int diffFactor = difference;
        MOORE100SeatStatus[1][1].setTopLeft(rowX[1], rowY[1] + diffFactor);
        MOORE100SeatStatus[1][6].setTopLeft(rowX[6], rowY[6] + diffFactor);
        MOORE100SeatStatus[1][7].setTopLeft(rowX[7] ,rowY[7] + diffFactor);
        MOORE100SeatStatus[1][11].setTopLeft(rowX[11], rowY[11] + diffFactor);
        MOORE100SeatStatus[1][15].setTopLeft(rowX[15], rowY[15] + diffFactor);
        MOORE100SeatStatus[1][16].setTopLeft(rowX[16], rowY[16] + diffFactor);
        MOORE100SeatStatus[1][17].setTopLeft(rowX[17], rowY[17] + diffFactor);
        MOORE100SeatStatus[1][20].setTopLeft(rowX[20], rowY[20] + diffFactor);
        MOORE100SeatStatus[1][21].setTopLeft(rowX[21], rowY[21] + diffFactor);
        MOORE100SeatStatus[1][22].setTopLeft(rowX[22], rowY[22] + diffFactor);
        MOORE100SeatStatus[1][23].setTopLeft(rowX[23], rowY[23] + diffFactor);

        // Add Row 1 (A) to Clickable Areas
        clickableAreas.add(new ClickableArea(MOORE100SeatStatus[1][1].getX(), MOORE100SeatStatus[1][1].getY(), seatWidth, seatHeight, MOORE100SeatStatus[1][1]));
        clickableAreas.add(new ClickableArea(MOORE100SeatStatus[1][6].getX(), MOORE100SeatStatus[1][6].getY(), seatWidth, seatHeight, MOORE100SeatStatus[1][6]));
        clickableAreas.add(new ClickableArea(MOORE100SeatStatus[1][7].getX(), MOORE100SeatStatus[1][7].getY(), seatWidth, seatHeight, MOORE100SeatStatus[1][7]));
        clickableAreas.add(new ClickableArea(MOORE100SeatStatus[1][11].getX(), MOORE100SeatStatus[1][11].getY(), seatWidth, seatHeight, MOORE100SeatStatus[1][11]));

        for (int col = 15; col <= 17; col++)
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[1][col].getX(), MOORE100SeatStatus[1][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[1][col]));

        for (int col = 20; col <= 23; col++)
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[1][col].getX(), MOORE100SeatStatus[1][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[1][col]));



        // Row 2 / Row B
        for (int col = 1; col <= MOORE100TotalCols; col++)
        {
            MOORE100SeatStatus[2][col].setTopLeft(rowX[col], rowY[col]);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[2][col].getX(), MOORE100SeatStatus[2][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[2][col]));
        }


        // Row 3 / Row C
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[3][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[3][col].getX(), MOORE100SeatStatus[3][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[3][col]));
        }



        // Row 4 / Row D
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[4][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[4][col].getX(), MOORE100SeatStatus[4][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[4][col]));
        }



        // Row 5 / Row E
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[5][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[5][col].getX(), MOORE100SeatStatus[5][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[5][col]));
        }



        // Row 6 / Row F
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[6][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[6][col].getX(), MOORE100SeatStatus[6][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[6][col]));
        }



        // Row 7 / Row G
        diffFactor += difference + 5;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[7][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[7][col].getX(), MOORE100SeatStatus[7][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[7][col]));
        }



        // Row 8 / Row H
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[8][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[8][col].getX(), MOORE100SeatStatus[8][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[8][col]));
        }



        // Row 9 / Row I
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[9][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[9][col].getX(), MOORE100SeatStatus[9][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[9][col]));
        }



        // Row 10 / Row J
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[10][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[10][col].getX(), MOORE100SeatStatus[10][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[10][col]));
        }



        // Row 11 / Row K
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[11][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[11][col].getX(), MOORE100SeatStatus[11][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[11][col]));
        }



        // Row 12 / Row L
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[12][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[12][col].getX(), MOORE100SeatStatus[12][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[12][col]));
        }



        // Row 13 / Row M
        diffFactor += difference + 5;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[13][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[13][col].getX(), MOORE100SeatStatus[13][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[13][col]));
        }



        // Row 14 / Row N
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[14][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[14][col].getX(), MOORE100SeatStatus[14][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[14][col]));
        }



        // Row 15 / Row O
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[15][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[15][col].getX(), MOORE100SeatStatus[15][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[15][col]));
        }



        // Row 16 / Row P
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[16][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[16][col].getX(), MOORE100SeatStatus[16][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[16][col]));
        }



        // Row 17 / Row Q
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[17][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[17][col].getX(), MOORE100SeatStatus[17][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[17][col]));
        }



        // Row 18 / Row R
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[18][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[18][col].getX(), MOORE100SeatStatus[18][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[18][col]));
        }



        // Row 19 / Row S
        diffFactor += difference;
        for (int col = 1; col <= MOORE100TotalCols; col++ )
        {
            MOORE100SeatStatus[19][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[19][col].getX(), MOORE100SeatStatus[19][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[19][col]));
        }



        // Row 20 / Row T
        diffFactor += difference + 1;
        for (int col = 1; col <= 3; col++ )
        {
            MOORE100SeatStatus[20][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[20][col].getX(), MOORE100SeatStatus[20][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[20][col]));
        }

        for (int col = 7; col <= 17; col++ )
        {
            MOORE100SeatStatus[20][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[20][col].getX(), MOORE100SeatStatus[20][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[20][col]));
        }

        for (int col = 21; col <= 23; col++ )
        {
            MOORE100SeatStatus[20][col].setTopLeft(rowX[col], rowY[col] - diffFactor);
            clickableAreas.add(new ClickableArea(MOORE100SeatStatus[20][col].getX(), MOORE100SeatStatus[20][col].getY(), seatWidth, seatHeight, MOORE100SeatStatus[20][col]));
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

                    for (int i = 0; i <= MOORE100TotalRows; i++)
                        for (int j = 0; j <= MOORE100TotalCols; j++)
                        {
                            boolean status = MOORE100SeatStatus[i][j].getSeatStatus();
                            if (status)
                            {
                                int x = MOORE100SeatStatus[i][j].getX();
                                int y = MOORE100SeatStatus[i][j].getY();

                                canvas.drawRoundRect(x + 2, y + 2, x + seatWidth - 2, y + seatHeight - 2,30f,15f, paint);
                            }
                        }

                    moore100ImageView.setImageBitmap(mutableBitmap);
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