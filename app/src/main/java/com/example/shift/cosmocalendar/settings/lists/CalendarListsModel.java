package com.example.shift.cosmocalendar.settings.lists;


import com.example.shift.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.example.shift.cosmocalendar.settings.lists.connected_days.ConnectedDaysManager;
import com.example.shift.cosmocalendar.utils.CalendarSyncData;
import com.example.shift.cosmocalendar.utils.DayContent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CalendarListsModel implements CalendarListsInterface {

    //Disabled days cannot be selected
    private Set<Long> disabledDays = new TreeSet<>();

    private DisabledDaysCriteria disabledDaysCriteria;

    private List<DayContent> dayContents = new ArrayList<>();
    private List<CalendarSyncData> syncDataList = new ArrayList<>();

    //Custom connected days for displaying in calendar
    private ConnectedDaysManager connectedDaysManager = ConnectedDaysManager.getInstance();

    private Set<Long> weekendDays = new HashSet() {{
        add(Calendar.SUNDAY);
    }};

    @Override
    public Set<Long> getDisabledDays() {
        return disabledDays;
    }

    @Override
    public ConnectedDaysManager getConnectedDaysManager() {
        return connectedDaysManager;
    }

    @Override
    public Set<Long> getWeekendDays() {
        return weekendDays;
    }

    @Override
    public DisabledDaysCriteria getDisabledDaysCriteria() {
        return disabledDaysCriteria;
    }

    @Override
    public List<DayContent> getDayContents() {
        return dayContents;
    }

    @Override
    public List<CalendarSyncData> getDaySyncData() {
        return syncDataList;
    }

    @Override
    public void setDisabledDays(Set<Long> disabledDays) {
        this.disabledDays = disabledDays;
    }

    @Override
    public void setWeekendDays(Set<Long> weekendDays) {
        this.weekendDays = weekendDays;
    }

    @Override
    public void setDisabledDaysCriteria(DisabledDaysCriteria criteria) {
        this.disabledDaysCriteria = criteria;
    }

    @Override
    public void addConnectedDays(ConnectedDays connectedDays) {
        connectedDaysManager.addConnectedDays(connectedDays);
    }

    @Override
    public void setDayContents(List<DayContent> dayContents) {
        this.dayContents =  dayContents;
    }

    @Override
    public void setDaySyncData(List<CalendarSyncData> syncDataList) {
        this.syncDataList = syncDataList;
    }
}
