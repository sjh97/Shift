package com.JH571121692Developer.shift.cosmocalendar.model;


import com.JH571121692Developer.shift.cosmocalendar.selection.SelectionState;
import com.JH571121692Developer.shift.cosmocalendar.utils.CalendarSyncData;
import com.JH571121692Developer.shift.cosmocalendar.utils.DateUtils;
import com.JH571121692Developer.shift.cosmocalendar.utils.DayContent;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Day {

    private Calendar calendar;
    private boolean belongToMonth;
    private boolean current;
    private boolean selected;
    private boolean disabled;
    private boolean weekend;

    //Connected days
    private boolean fromConnectedCalendar;
    private int connectedDaysTextColor;
    private int connectedDaysSelectedTextColor;
    private int connectedDaysDisabledTextColor;

    //For animation states
    private SelectionState selectionState;
    private boolean isSelectionCircleDrawed;

    //For content
    private DayContent dayContent;
    private List<CalendarSyncData> syncDataList;

    public Day(Date date) {
        this.calendar = DateUtils.getCalendar(date);
        this.current = DateUtils.isCurrentDate(date);
        this.selected = false;
        this.dayContent = null;
        this.syncDataList = null;
    }

    public Day(Calendar calendar) {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(calendar.getTime());
        this.calendar = tempCalendar;
        this.current = DateUtils.isCurrentDate(calendar.getTime());
        this.selected = false;
        this.dayContent = null;
        this.syncDataList = null;
    }

    public DayContent getDayContent() {
        return dayContent;
    }

    public List<CalendarSyncData> getSyncDataList() {return syncDataList;};

    public void setDayContent(DayContent dayContent) {
        this.dayContent = dayContent;
    }

    public void setSyncData(List<CalendarSyncData> syncDataList){this.syncDataList = syncDataList;}

    public boolean isBelongToMonth() {
        return belongToMonth;
    }

    public void setBelongToMonth(boolean belongToMonth) {
        this.belongToMonth = belongToMonth;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    public boolean isFromConnectedCalendar() {
        return fromConnectedCalendar;
    }

    public void setFromConnectedCalendar(boolean fromConnectedCalendar) {
        this.fromConnectedCalendar = fromConnectedCalendar;
    }

    public boolean isSelectionCircleDrawed() {
        return isSelectionCircleDrawed;
    }

    public void setSelectionCircleDrawed(boolean selectionCircleDrawed) {
        isSelectionCircleDrawed = selectionCircleDrawed;
    }

    //custom
    public int getDayName(){
        return getCalendar().get(Calendar.DAY_OF_WEEK);
    }

    public SelectionState getSelectionState() {
        return selectionState;
    }

    public void setSelectionState(SelectionState selectionState) {
        this.selectionState = selectionState;
    }

    public int getConnectedDaysTextColor() {
        return connectedDaysTextColor;
    }

    public void setConnectedDaysTextColor(int connectedDaysTextColor) {
        this.connectedDaysTextColor = connectedDaysTextColor;
    }

    public int getConnectedDaysSelectedTextColor() {
        return connectedDaysSelectedTextColor;
    }

    public void setConnectedDaysSelectedTextColor(int connectedDaysSelectedTextColor) {
        this.connectedDaysSelectedTextColor = connectedDaysSelectedTextColor;
    }

    public int getConnectedDaysDisabledTextColor() {
        return connectedDaysDisabledTextColor;
    }

    public void setConnectedDaysDisabledTextColor(int connectedDaysDisabledTextColor) {
        this.connectedDaysDisabledTextColor = connectedDaysDisabledTextColor;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public int getDayNumber() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public String toString() {
        return "Day{day=" + calendar.getTime() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Day day = (Day) o;
        Calendar anotherCalendar = day.getCalendar();
        return anotherCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                anotherCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public int hashCode() {
        return calendar != null ? calendar.hashCode() : 0;
    }
}
