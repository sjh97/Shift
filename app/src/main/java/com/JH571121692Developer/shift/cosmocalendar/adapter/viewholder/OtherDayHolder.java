package com.JH571121692Developer.shift.cosmocalendar.adapter.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.model.Day;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;


public class OtherDayHolder extends BaseDayHolder {

    View thisView;

    public OtherDayHolder(View itemView, CalendarView calendarView) {
        super(itemView, calendarView);
        tvDay = (TextView) itemView.findViewById(R.id.tv_day_number);
        thisView = itemView;
    }

    public void bind(Day day) {
        tvDay.setText(String.valueOf(day.getDayNumber()));
        tvDay.setTextColor(calendarView.getOtherDayTextColor());
        thisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(day.getDayNumber() <= 14)
                    calendarView.goToNextMonth();
                else
                    calendarView.goToPreviousMonth();
            }
        });
    }
}
