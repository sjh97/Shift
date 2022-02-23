package com.JH571121692Developer.shift.cosmocalendar.settings.selection;


import com.JH571121692Developer.shift.cosmocalendar.utils.SelectionType;

public interface SelectionInterface {

    @SelectionType
    int getSelectionType();

    void setSelectionType(@SelectionType int selectionType);
}
