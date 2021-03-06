package com.JH571121692Developer.shift.cosmocalendar.view.delegate;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.adapter.MonthAdapter;
import com.JH571121692Developer.shift.cosmocalendar.adapter.viewholder.DayHolder;
import com.JH571121692Developer.shift.cosmocalendar.model.Day;
import com.JH571121692Developer.shift.cosmocalendar.selection.BaseSelectionManager;
import com.JH571121692Developer.shift.cosmocalendar.selection.MultipleSelectionManager;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;

public class DayDelegate extends BaseDelegate {

    private MonthAdapter monthAdapter;

    public DayDelegate(CalendarView calendarView, MonthAdapter monthAdapter) {
        this.calendarView = calendarView;
        this.monthAdapter = monthAdapter;
    }

    public DayHolder onCreateDayHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_day, parent, false);
        return new DayHolder(view, calendarView);
    }



    public void onBindDayHolder(final RecyclerView.Adapter daysAdapter, final Day day,
                                final DayHolder holder, final int position, int height) {
        final BaseSelectionManager selectionManager = monthAdapter.getSelectionManager();
        holder.itemView.getLayoutParams().height = height;
        holder.itemView.requestLayout();
        holder.bind(day, selectionManager);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!day.isDisabled()) {
                    selectionManager.toggleDay(day);
                    if (selectionManager instanceof MultipleSelectionManager) {
                        daysAdapter.notifyItemChanged(position);
                    }
                    else {
                        monthAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
