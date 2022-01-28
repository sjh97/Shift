package com.example.shift.cosmocalendar.settings.selection;


import com.example.shift.cosmocalendar.utils.SelectionType;

public interface SelectionInterface {

    @SelectionType
    int getSelectionType();

    void setSelectionType(@SelectionType int selectionType);
}
