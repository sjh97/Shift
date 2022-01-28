package com.example.shift.cosmocalendar.adapter.viewholder;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.shift.R;
import com.example.shift.cosmocalendar.adapter.SyncDataAdapter;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.selection.BaseSelectionManager;
import com.example.shift.cosmocalendar.settings.appearance.ConnectedDayIconPosition;
import com.example.shift.cosmocalendar.utils.CalendarSyncData;
import com.example.shift.cosmocalendar.utils.CalendarUtils;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.view.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class DayHolder extends BaseDayHolder {
    //private CircleAnimationTextView ctvDay;
    public static final int IMPOSSIBLE_COLOR = -100;
    private TextView ctvDay;
    private TextView ctvText;
    private LinearLayout llDay;
    private ListView listView;
    private BaseSelectionManager selectionManager;
    private int backColor;
    private DayContent dayContent_saving;
    SimpleDateFormat simpleDateFormat;

    public DayHolder(View itemView, CalendarView calendarView) {
        super(itemView, calendarView);
        ctvDay = itemView.findViewById(R.id.tv_day_number);
        ctvText = itemView.findViewById(R.id.tv_day_text);
        llDay = itemView.findViewById(R.id.ll_day);
        listView = itemView.findViewById(R.id.listview_day_list);
        backColor = ((ColorDrawable) llDay.getBackground()).getColor();
        String key = "selectedDays";
        dayContent_saving = new DayContent();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void bind(Day day, BaseSelectionManager selectionManager) {
        this.selectionManager = selectionManager;
        ctvDay.setText(String.valueOf(day.getDayNumber()));
        ctvDay.setBackground(null);
        List<CalendarSyncData> syncDataList = day.getSyncDataList();
        if(syncDataList != null){
            final SyncDataAdapter syncDataAdapter = new SyncDataAdapter(listView.getContext(), syncDataList);
            listView.setAdapter(syncDataAdapter);
        }
        DayContent dayContent = day.getDayContent();
        if(dayContent != null){
            ctvText.setBackgroundColor(dayContent.getContentColor());
            ctvText.setText(dayContent.getContentString());
        }

        boolean isSelected = selectionManager.isDaySelected(day);
        if (isSelected && !day.isDisabled()) {
            select(day);
        } else {
            unselect(day);
        }

        if (day.isCurrent()) {
            ctvDay.setBackground(ContextCompat.getDrawable(ctvDay.getContext(), R.drawable.round_button));
            ctvDay.setTextColor(ContextCompat.getColor(ctvDay.getContext(), R.color.default_border_color));
            //addCurrentDayIcon(isSelected);
        }

        if(day.isDisabled()){
            ctvDay.setTextColor(calendarView.getDisabledDayTextColor());
        }
    }

    private void addCurrentDayIcon(boolean isSelected){
        ctvDay.setCompoundDrawablePadding(getPadding(getCurrentDayIconHeight(isSelected)) * -1);
        ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, isSelected
                ? calendarView.getCurrentDaySelectedIconRes()
                : calendarView.getCurrentDayIconRes(), 0, 0);
    }

    private int getCurrentDayIconHeight(boolean isSelected){
        if (isSelected) {
            return CalendarUtils.getIconHeight(calendarView.getContext().getResources(), calendarView.getCurrentDaySelectedIconRes());
        } else {
            return CalendarUtils.getIconHeight(calendarView.getContext().getResources(), calendarView.getCurrentDayIconRes());
        }
    }

    private int getConnectedDayIconHeight(boolean isSelected){
        if (isSelected) {
            return CalendarUtils.getIconHeight(calendarView.getContext().getResources(), calendarView.getConnectedDaySelectedIconRes());
        } else {
            return CalendarUtils.getIconHeight(calendarView.getContext().getResources(), calendarView.getConnectedDayIconRes());
        }
    }

    private void select(Day day) {
        if (day.isFromConnectedCalendar()) {
            if(day.isDisabled()){
                ctvDay.setTextColor(day.getConnectedDaysDisabledTextColor());
            } else {
                ctvDay.setTextColor(day.getConnectedDaysSelectedTextColor());
            }
            addConnectedDayIcon(true);
        }
        else {
            llDay.setBackgroundColor(calendarView.getSelectedDayBackgroundColor());
            ctvText.setBackgroundColor(calendarView.getSelectedDayBackgroundColor());
            ctvText.setText("");
            ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        if(day.isCurrent()){
            ctvDay.setBackground(null);
            ctvDay.setTextColor(ContextCompat.getColor(ctvDay.getContext(), R.color.default_day_text_color));
        }
        ctvDay.setTextColor(ContextCompat.getColor(ctvDay.getContext(), R.color.default_day_text_color));
    }

    private void unselect(Day day) {
        int textColor;
        if (day.isFromConnectedCalendar()) {
            if(day.isDisabled()){
                textColor = day.getConnectedDaysDisabledTextColor();
            } else {
                textColor = day.getConnectedDaysTextColor();
            }
            addConnectedDayIcon(false);
        }
        else if (day.isWeekend()) {
            if(day.getDayName() == Calendar.SATURDAY)
                textColor = ContextCompat.getColor(calendarView.getContext(), R.color.default_saturday_text_color);
            else
                textColor = calendarView.getWeekendDayTextColor();
            ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        else if (day.isCurrent()){
            ctvDay.setBackground(ContextCompat.getDrawable(ctvDay.getContext(), R.drawable.round_button));
            textColor = ContextCompat.getColor(ctvDay.getContext(), R.color.default_border_color);
        }
        else {
            textColor = calendarView.getDayTextColor();
            ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        day.setSelectionCircleDrawed(false);
        ctvDay.setTextColor(textColor);

        DayContent dayContent = day.getDayContent();
        int color = (dayContent != null) ? dayContent.getContentColor() : backColor;
        String content_string = (dayContent != null) ? dayContent.getContentString() : "";
        llDay.setBackgroundColor(backColor);
        ctvText.setBackgroundColor(color);
        ctvText.setText(content_string);
    }

    private void addConnectedDayIcon(boolean isSelected){
        ctvDay.setCompoundDrawablePadding(getPadding(getConnectedDayIconHeight(isSelected)) * -1);

        switch (calendarView.getConnectedDayIconPosition()){
            case ConnectedDayIconPosition.TOP:
                ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, isSelected
                        ? calendarView.getConnectedDaySelectedIconRes()
                        : calendarView.getConnectedDayIconRes(), 0, 0);
                break;

            case ConnectedDayIconPosition.BOTTOM:
                ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, isSelected
                        ? calendarView.getConnectedDaySelectedIconRes()
                        : calendarView.getConnectedDayIconRes());
                break;
        }
    }


    private int getPadding(int iconHeight){
        return (int) (iconHeight * Resources.getSystem().getDisplayMetrics().density);
    }
}
