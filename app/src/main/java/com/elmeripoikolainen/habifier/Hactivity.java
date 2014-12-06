package com.elmeripoikolainen.habifier;

import java.util.Date;

/**
 * Created by elmeripoikolainen on 28/10/14.
 */
public class Hactivity {
    private long id;
    private String activity;
    private long time;
    private Date date;
    private boolean selected;

    public boolean isSelected() { return selected; }

    public void setSelected(boolean selected) { this.selected = selected; }

    public void setSelected(int selected) {
        if (selected == 1){
            this.setSelected(true);
        } else {
            this.setSelected(false);
        }
    }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public long getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHactivity() {
        return activity;
    }

    public void setHactivity(String comment) {
        this.activity = comment;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return activity;
    }
}