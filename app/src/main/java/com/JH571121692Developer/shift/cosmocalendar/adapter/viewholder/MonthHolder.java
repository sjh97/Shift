package com.JH571121692Developer.shift.cosmocalendar.adapter.viewholder;


import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.adapter.DaysAdapter;
import com.JH571121692Developer.shift.cosmocalendar.model.Month;
import com.JH571121692Developer.shift.cosmocalendar.settings.SettingsManager;
import com.JH571121692Developer.shift.cosmocalendar.view.MonthView;

public class MonthHolder extends RecyclerView.ViewHolder {

    private LinearLayout llMonthHeader;
    private TextView tvMonthName;
    private View viewLeftLine;
    private View viewRightLine;
    private MonthView monthView;
    private SettingsManager appearanceModel;

    public MonthHolder(View itemView, SettingsManager appearanceModel) {
        super(itemView);
        llMonthHeader = (LinearLayout) itemView.findViewById(R.id.ll_month_header);
        monthView = (MonthView) itemView.findViewById(R.id.month_view);
        tvMonthName = (TextView) itemView.findViewById(R.id.tv_month_name);
        viewLeftLine = itemView.findViewById(R.id.view_left_line);
        viewRightLine = itemView.findViewById(R.id.view_right_line);
        this.appearanceModel = appearanceModel;
    }

    public void setDayAdapter(DaysAdapter adapter) {
        getMonthView().setAdapter(adapter);
    }

    public void bind(Month month) {
//        tvMonthName.setText(month.getMonthName());
//        tvMonthName.setTextColor(appearanceModel.getMonthTextColor());

        viewLeftLine.setVisibility(appearanceModel.getCalendarOrientation() == OrientationHelper.HORIZONTAL ? View.INVISIBLE : View.VISIBLE);
        viewRightLine.setVisibility(appearanceModel.getCalendarOrientation() == OrientationHelper.HORIZONTAL ? View.INVISIBLE : View.VISIBLE);
//        llMonthHeader.setBackgroundResource(appearanceModel.getCalendarOrientation() == OrientationHelper.HORIZONTAL ? R.drawable.border_top_bottom : 0);
        llMonthHeader.setBackgroundColor(ContextCompat.getColor(llMonthHeader.getContext(), R.color.default_calendar_head_background_color));
        monthView.setBackgroundResource(R.drawable.top_round_border);
        monthView.initAdapter(month);
    }

    public MonthView getMonthView() {
        return monthView;
    }
}
