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

    private DatabaseReference m_ClassroomDatabaseRef = FirebaseDatabase.getInstance().getReference("Classroom");
    private DatabaseReference m_CurrentClassroomRef;
    private DatabaseReference m_SeatRef;


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
    public boolean fillSeat(String name, int row, int col)
    {
        m_seatNumber = row * col;

        if (m_seatNumber > 0 && m_seatNumber <= m_totalSeats) {
            m_SeatRef = m_CurrentClassroomRef.child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
            m_SeatRef.child("Name").setValue(name);
            m_SeatRef.child("Status").setValue(true);
            return true;
        }
        return false;
    }




    public String emptySeat(int row, int col)
    {
        m_seatNumber = row * col;
        if (m_seatNumber > 0 && m_seatNumber <= m_totalSeats)
        {
            m_SeatRef = m_CurrentClassroomRef.child("Row: " + Integer.toString(row) + " Col: " + Integer.toString(col));
            m_SeatRef.child("Name").setValue("");
            m_SeatRef.child("Status").setValue(false);
            return "Seat Emptied!";
        }
        else return "Seat number is invalid";
    }




    // Getters and Setters
    public String getClassroom() { return m_classroomName; }
    public int getRows() { return m_rows; }
    public int getCols() { return m_cols; }
    public DatabaseReference getCurrentClassroomRef() { return m_CurrentClassroomRef; }



}
