package com.JH571121692Developer.shift.cosmocalendar.view.delegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.adapter.viewholder.DayOfWeekHolder;
import com.JH571121692Developer.shift.cosmocalendar.model.Day;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;


public class DayOfWeekDelegate extends BaseDelegate {

    public DayOfWeekDelegate(CalendarView calendarView) {
        this.calendarView = calendarView;
    }

    public DayOfWeekHolder onCreateDayHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_day_of_week, parent, false);
        return new DayOfWeekHolder(view, calendarView);
    }

    public void onBindDayHolder(Day day, DayOfWeekHolder holder, int position) {
        holder.bind(day);
    }
}
