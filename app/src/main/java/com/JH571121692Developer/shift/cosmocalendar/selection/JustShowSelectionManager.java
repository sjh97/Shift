package com.JH571121692Developer.shift.cosmocalendar.selection;

import androidx.annotation.NonNull;

import com.JH571121692Developer.shift.cosmocalendar.model.Day;

public class JustShowSelectionManager extends BaseSelectionManager{

    private Day day;

    public JustShowSelectionManager(OnDaySelectedListener onDaySelectedListener) {
        this.onDaySelectedListener = onDaySelectedListener;
    }

    @Override
    public void toggleDay(@NonNull Day day) {
        this.day = day;
        onDaySelectedListener.onDaySelected();
    }

    @Override
    public boolean isDaySelected(@NonNull Day day) {
        return day.equals(this.day);
    }

    @Override
    public void clearSelections() {
        day = null;
    }
}