package com.example.shift.cosmocalendar.view.delegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shift.R;
import com.example.shift.cosmocalendar.adapter.viewholder.OtherDayHolder;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.view.CalendarView;

public class OtherDayDelegate {

    private CalendarView calendarView;

    public OtherDayDelegate(CalendarView calendarView) {
        this.calendarView = calendarView;
    }

    public OtherDayHolder onCreateDayHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_other_day, parent, false);
        return new OtherDayHolder(view, calendarView);
    }

    public void onBindDayHolder(Day day, OtherDayHolder holder, int position, int height) {
        holder.itemView.getLayoutParams().height = height;
        holder.itemView.requestLayout();
        holder.bind(day);
    }
}
