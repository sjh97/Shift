package com.example.shift.cosmocalendar.dialog;


import com.example.shift.cosmocalendar.model.Day;

import java.util.List;

public interface OnDaysSelectionListener {
    void onDaysSelected(List<Day> selectedDays, String written, int color);
}
