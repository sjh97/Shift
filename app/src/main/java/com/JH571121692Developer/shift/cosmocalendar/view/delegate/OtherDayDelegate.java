package com.JH571121692Developer.shift.cosmocalendar.view.delegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.adapter.viewholder.OtherDayHolder;
import com.JH571121692Developer.shift.cosmocalendar.model.Day;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;

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
