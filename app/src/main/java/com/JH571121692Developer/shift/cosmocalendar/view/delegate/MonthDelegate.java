package com.JH571121692Developer.shift.cosmocalendar.view.delegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.adapter.DaysAdapter;
import com.JH571121692Developer.shift.cosmocalendar.adapter.viewholder.MonthHolder;
import com.JH571121692Developer.shift.cosmocalendar.model.Month;
import com.JH571121692Developer.shift.cosmocalendar.settings.SettingsManager;


public class MonthDelegate {

    private SettingsManager appearanceModel;

    public MonthDelegate(SettingsManager appearanceModel) {
        this.appearanceModel = appearanceModel;
    }

    public MonthHolder onCreateMonthHolder(DaysAdapter adapter, ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.view_month, parent, false);
        final MonthHolder holder = new MonthHolder(view, appearanceModel);
        holder.setDayAdapter(adapter);
        return holder;
    }

    public void onBindMonthHolder(Month month, MonthHolder holder, int position) {
        holder.bind(month);
    }
}
