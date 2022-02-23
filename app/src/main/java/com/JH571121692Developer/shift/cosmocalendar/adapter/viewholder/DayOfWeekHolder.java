package com.JH571121692Developer.shift.cosmocalendar.adapter.viewholder;

import android.view.View;
import android.widget.TextView;


import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.model.Day;
import com.JH571121692Developer.shift.cosmocalendar.utils.Constants;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DayOfWeekHolder extends BaseDayHolder {

    private SimpleDateFormat mDayOfWeekFormatter;

    public DayOfWeekHolder(View itemView, CalendarView calendarView) {
        super(itemView, calendarView);
        tvDay = (TextView) itemView.findViewById(R.id.tv_day_name);
        mDayOfWeekFormatter = new SimpleDateFormat(Constants.DAY_NAME_FORMAT, Locale.getDefault());
    }

    public void bind(Day day) {
        tvDay.setText(mDayOfWeekFormatter.format(day.getCalendar().getTime()));
        tvDay.setTextColor(calendarView.getWeekDayTitleTextColor());
    }
}