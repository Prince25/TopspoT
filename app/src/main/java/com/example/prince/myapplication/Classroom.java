package com.example.prince.myapplication;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Prince on 2/24/2018.
 */

public class Classroom{

    private String m_classroomName;

    private int m_rows;
    private int m_cols;
    private int m_totalSeats;
    private double m_seatNumber;

    private DatabaseReference m_ClassroomDatabase = FirebaseDatabase.getInstance().getReference("Classroom");
    private DatabaseReference m_CurrentClassroom;
    private DatabaseReference m_Seat;

    // Constructor
    public Classroom(){}

    public Classroom(String classroomName, int rows, int cols)
    {
        m_classroomName = classroomName;
        m_rows = rows;
        m_cols = cols;
        m_totalSeats = m_rows * m_cols;

        m_CurrentClassroom = m_ClassroomDatabase.child(m_classroomName);
    }


    // Main Functions
    public boolean fillSeat(String name, int row, int col)
    {
        m_seatNumber = row * col;
        if (m_seatNumber > 0 && m_seatNumber < m_totalSeats)
        {
            m_Seat = m_CurrentClassroom.child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
            m_Seat.child("Name").setValue(name);
            m_Seat.child("Status").setValue(true);
            return true;
        }
        else return false;
    }

    public boolean emptySeat(int row, int col)
    {
        m_seatNumber = row * col;
        if (m_seatNumber > 0 && m_seatNumber < m_totalSeats)
        {
            m_Seat = m_CurrentClassroom.child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
            m_Seat.child("Name").setValue("");
            m_Seat.child("Status").setValue(false);
            return true;
        }
        else return false;
    }

/*
    private boolean seatAvailable(int row, int col)
    {

        m_CurrentClassroom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot seats: dataSnapshot.getChildren())
                {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        })
        return false;
    }
*/


    // Getters and Setters
    public String getClassroom() { return m_classroomName; }
    public int getRows() { return m_rows; }
    public int getCols() { return m_cols; }

}
