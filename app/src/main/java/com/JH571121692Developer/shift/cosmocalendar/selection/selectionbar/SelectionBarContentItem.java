package com.JH571121692Developer.shift.cosmocalendar.selection.selectionbar;


import com.JH571121692Developer.shift.cosmocalendar.model.Day;

public class SelectionBarContentItem implements SelectionBarItem {

    private Day day;

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public SelectionBarContentItem(Day day) {
        this.day = day;
    }
}

