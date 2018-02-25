package com.example.prince.myapplication;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Prince on 2/24/2018.
 */

public class Classroom{

    private String m_classroomName;

    private int m_rows;
    private int m_cols;
    private int m_totalSeats;
    private double m_seatNumber;
    private boolean m_seatTaken;

    private DatabaseReference m_ClassroomDatabaseRef = FirebaseDatabase.getInstance().getReference("Classroom");
    private DatabaseReference m_CurrentClassroomRef;
    private DatabaseReference m_SeatRef;
    private DatabaseReference m_SeatStatusRef;


    // Constructors
    public Classroom(){}

    public Classroom(String classroomName, int rows, int cols)
    {
        m_classroomName = classroomName;
        m_rows = rows;
        m_cols = cols;
        m_totalSeats = m_rows * m_cols;

        m_CurrentClassroomRef = m_ClassroomDatabaseRef.child(m_classroomName);
    }


    // Main Functions
    public String fillSeat(String name, int row, int col)
    {
        m_seatNumber = row * col;

        seatTaken(row, col);

        if (m_seatTaken == false)
        {
            if (m_seatNumber > 0 && m_seatNumber < m_totalSeats) {
                m_SeatRef = m_CurrentClassroomRef.child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
                m_SeatRef.child("Name").setValue(name);
                m_SeatRef.child("Status").setValue(true);
                return "";
            }
            else return "Seat number is invalid";
        }
        else return "Seat already taken!";
    }


    private void seatTaken(int row, int col)
    {
        m_SeatRef = m_CurrentClassroomRef.child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
        m_SeatStatusRef = m_SeatRef.child("Status");

        readData( m_SeatStatusRef, new OnGetDataListener()
        {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue() != null) {
                    m_seatTaken = dataSnapshot.getValue(boolean.class);
                    Log.i("seatStatus", "dataSnapshot: " + dataSnapshot.getValue(boolean.class));
                }
                else
                    m_seatTaken = false;
            }

            @Override
            public void onStart() {
                //when starting
                Log.d("ONSTART", "Started");
            }

            @Override
            public void onFailure() {
                Log.d("onFailure", "Failed");
            }
        });
        Log.i("seatStatus", "m_seatTaken:  " + m_seatTaken);
    }



    public boolean emptySeat(int row, int col)
    {
        m_seatNumber = row * col;
        if (m_seatNumber > 0 && m_seatNumber < m_totalSeats)
        {
            m_SeatRef = m_CurrentClassroomRef.child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
            m_SeatRef.child("Name").setValue("");
            m_SeatRef.child("Status").setValue(false);
            return true;
        }
        else return false;
    }





    private interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }


    private void readData(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });

    }




    // Getters and Setters
    public String getClassroom() { return m_classroomName; }
    public int getRows() { return m_rows; }
    public int getCols() { return m_cols; }

}
