package com.example.shift.cosmocalendar.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.example.shift.R;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.view.CalendarView;


public class OtherDayHolder extends BaseDayHolder {

    public OtherDayHolder(View itemView, CalendarView calendarView) {
        super(itemView, calendarView);
        tvDay = (TextView) itemView.findViewById(R.id.tv_day_number);
    }

    public void bind(Day day) {
        tvDay.setText(String.valueOf(day.getDayNumber()));
        tvDay.setTextColor(calendarView.getOtherDayTextColor());
    }
}
