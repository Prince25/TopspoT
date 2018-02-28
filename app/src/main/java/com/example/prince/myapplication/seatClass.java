package com.example.prince.myapplication;

/**
 * Created by Prince on 2/27/2018.
 */

public class seatClass {

    private boolean m_seatStatus;
    private String m_seatName;
    private int m_row;
    private int m_col;


    public seatClass() { }

    public boolean getSeatStatus() {
        return m_seatStatus;
    }
    public void setSeatStatus(boolean m_seatStatus) {
        this.m_seatStatus = m_seatStatus;
    }

    public String getSeatName() {
        return m_seatName;
    }
    public void setSeatName(String m_seatName) {
        this.m_seatName = m_seatName;
    }

    public int getRow() { return m_row; }
    public void setRow(int row) { this.m_row = row; }

    public int getCol() { return m_col; }
    public void setCol(int col) { this.m_col = col; }
}
