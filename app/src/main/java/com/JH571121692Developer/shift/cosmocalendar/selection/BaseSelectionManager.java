package com.JH571121692Developer.shift.cosmocalendar.selection;


import androidx.annotation.NonNull;

import com.JH571121692Developer.shift.cosmocalendar.model.Day;

public abstract class BaseSelectionManager {

    protected OnDaySelectedListener onDaySelectedListener;

    public abstract void toggleDay(@NonNull Day day);

    public abstract boolean isDaySelected(@NonNull Day day);

    public abstract void clearSelections();

    public BaseSelectionManager() {
    }
}
