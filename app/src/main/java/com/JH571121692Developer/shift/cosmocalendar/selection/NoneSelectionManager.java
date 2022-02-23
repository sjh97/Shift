package com.JH571121692Developer.shift.cosmocalendar.selection;


import androidx.annotation.NonNull;

import com.JH571121692Developer.shift.cosmocalendar.model.Day;

/**
 * Created by leonardo2204 on 06/10/17.
 */

public class NoneSelectionManager extends BaseSelectionManager {

    @Override
    public void toggleDay(@NonNull Day day) {

    }

    @Override
    public boolean isDaySelected(@NonNull Day day) {
        return false;
    }

    @Override
    public void clearSelections() {

    }
}
