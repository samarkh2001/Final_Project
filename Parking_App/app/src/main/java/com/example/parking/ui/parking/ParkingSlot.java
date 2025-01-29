package com.example.parking.ui.parking;

import android.widget.ImageView;

public class ParkingSlot {
    private ImageView img;
    private int row;
    private int col;

    private long timeOfEntry;
    private int entryHour, entryMin;
    private String plateId;

    public ParkingSlot(ImageView img, int row, int col){
        this.img = img;
        this.row = row;
        this.col = col;
        timeOfEntry = -1;
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public long getTimeOfEntry() {
        return timeOfEntry;
    }

    public void setTimeOfEntry(long timeOfEntry) {
        this.timeOfEntry = timeOfEntry;
    }

    public int getEntryHour() {
        return entryHour;
    }

    public void setEntryHour(int entryHour) {
        this.entryHour = entryHour;
    }

    public int getEntryMin() {
        return entryMin;
    }

    public void setEntryMin(int entryMin) {
        this.entryMin = entryMin;
    }

    public double getCost(double cost){
        return ((double) (System.currentTimeMillis() - timeOfEntry) /1000) * cost;
    }

    public String getPlateId() {
        return plateId;
    }

    public void setPlateId(String plateId) {
        this.plateId = plateId;
    }
}
