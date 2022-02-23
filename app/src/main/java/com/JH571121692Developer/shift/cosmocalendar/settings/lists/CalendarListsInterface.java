package com.JH571121692Developer.shift.cosmocalendar.settings.lists;


import com.JH571121692Developer.shift.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.JH571121692Developer.shift.cosmocalendar.settings.lists.connected_days.ConnectedDaysManager;
import com.JH571121692Developer.shift.cosmocalendar.utils.CalendarSyncData;
import com.JH571121692Developer.shift.cosmocalendar.utils.DayContent;

import java.util.List;
import java.util.Set;

public interface CalendarListsInterface {

    Set<Long> getDisabledDays();

    ConnectedDaysManager getConnectedDaysManager();

    Set<Long> getWeekendDays();

    DisabledDaysCriteria getDisabledDaysCriteria();

    List<DayContent> getDayContents();

    List<CalendarSyncData> getDaySyncData();

    void setDisabledDays(Set<Long> disabledDays);

    void setWeekendDays(Set<Long> weekendDays);

    void setDisabledDaysCriteria(DisabledDaysCriteria criteria);

    void addConnectedDays(ConnectedDays connectedDays);

    void setDayContents(List<DayContent> dayContents);

    void setDaySyncData(List<CalendarSyncData> syncDataList);
}
