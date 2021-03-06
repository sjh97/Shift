package com.JH571121692Developer.shift.cosmocalendar.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;


public abstract class BaseDayHolder extends RecyclerView.ViewHolder {

    protected TextView tvDay;
    protected CalendarView calendarView;

    public BaseDayHolder(View itemView, CalendarView calendarView) {
        super(itemView);
        this.calendarView = calendarView;
    }
}
