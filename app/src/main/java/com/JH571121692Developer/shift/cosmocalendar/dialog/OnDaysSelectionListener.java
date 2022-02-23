package com.JH571121692Developer.shift.cosmocalendar.dialog;


import com.JH571121692Developer.shift.cosmocalendar.model.Day;

import java.util.List;

public interface OnDaysSelectionListener {
    void onDaysSelected(List<Day> selectedDays, String written, int color, int id);
}
