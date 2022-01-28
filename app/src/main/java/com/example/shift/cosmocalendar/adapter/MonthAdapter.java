package com.example.shift.cosmocalendar.adapter;

import android.util.Log;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shift.cosmocalendar.adapter.viewholder.MonthHolder;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.model.Month;
import com.example.shift.cosmocalendar.selection.BaseSelectionManager;
import com.example.shift.cosmocalendar.settings.lists.DisabledDaysCriteria;
import com.example.shift.cosmocalendar.utils.CalendarSyncData;
import com.example.shift.cosmocalendar.utils.CalendarUtils;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.utils.DayFlag;
import com.example.shift.cosmocalendar.view.CalendarView;
import com.example.shift.cosmocalendar.view.ItemViewType;
import com.example.shift.cosmocalendar.view.delegate.DayDelegate;
import com.example.shift.cosmocalendar.view.delegate.DayOfWeekDelegate;
import com.example.shift.cosmocalendar.view.delegate.MonthDelegate;
import com.example.shift.cosmocalendar.view.delegate.OtherDayDelegate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class MonthAdapter extends RecyclerView.Adapter<MonthHolder> {

    private final List<Month> months;

    private MonthDelegate monthDelegate;

    private CalendarView calendarView;
    private BaseSelectionManager selectionManager;
    private DaysAdapter daysAdapter;
    private List<DayContent> dayContents;
    private List<CalendarSyncData> syncDataList;

    private MonthAdapter(List<Month> months,
                         MonthDelegate monthDelegate,
                         CalendarView calendarView,
                         BaseSelectionManager selectionManager,
                         List<DayContent> dayContents,
                         List<CalendarSyncData> syncDataList) {
        setHasStableIds(true);
        this.months = months;
        this.monthDelegate = monthDelegate;
        this.calendarView = calendarView;
        this.selectionManager = selectionManager;
        this.dayContents = dayContents;
        this.syncDataList = syncDataList;
    }

    public void setSelectionManager(BaseSelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }

    public BaseSelectionManager getSelectionManager() {
        return selectionManager;
    }

    @Override
    public MonthHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        daysAdapter = new DaysAdapter.DaysAdapterBuilder()
                .setDayOfWeekDelegate(new DayOfWeekDelegate(calendarView))
                .setOtherDayDelegate(new OtherDayDelegate(calendarView))
                .setDayDelegate(new DayDelegate(calendarView, this))
                .setCalendarView(calendarView)
                .createDaysAdapter();
        return monthDelegate.onCreateMonthHolder(daysAdapter, parent, viewType);
    }

    @Override
    public void onBindViewHolder(MonthHolder holder, int position) {
        final Month month = months.get(position);
        //Sync calendar data imported from default calendar app.
        if(!month.isSynced() && syncDataList != null){
            attachSyncData(month);
        }

        monthDelegate.onBindMonthHolder(month, holder, position);
    }

    @Override
    public int getItemCount() {
        return months.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ItemViewType.MONTH;
    }

    @Override
    public long getItemId(int position) {
        return months.get(position).getFirstDay().getCalendar().getTimeInMillis();
    }

    public List<Month> getData() {
        return months;
    }

    public static class MonthAdapterBuilder {

        private List<Month> months;
        private MonthDelegate monthDelegate;
        private CalendarView calendarView;
        private BaseSelectionManager selectionManager;
        private List<DayContent> dayContents;
        private List<CalendarSyncData> syncDataList = new ArrayList<>();

        public MonthAdapterBuilder setMonths(List<Month> months) {
            this.months = months;
            return this;
        }

        public MonthAdapterBuilder setMonthDelegate(MonthDelegate monthHolderDelegate) {
            this.monthDelegate = monthHolderDelegate;
            return this;
        }

        public MonthAdapterBuilder setCalendarView(CalendarView calendarView) {
            this.calendarView = calendarView;
            return this;
        }

        public MonthAdapterBuilder setSelectionManager(BaseSelectionManager selectionManager) {
            this.selectionManager = selectionManager;
            return this;
        }

        public MonthAdapterBuilder setDayContent(List<DayContent> dayContents) {
            this.dayContents = dayContents;
            return this;
        }

        public MonthAdapterBuilder setDaySynData(List<CalendarSyncData> syncDataList){
            this.syncDataList = syncDataList;
            return this;
        }


        public MonthAdapter createMonthAdapter() {
            return new MonthAdapter(months,
                    monthDelegate,
                    calendarView,
                    selectionManager,
                    dayContents,
                    syncDataList);
        }
    }

    public void setWeekendDays(Set<Long> weekendDays) {
        setDaysAccordingToSet(weekendDays, DayFlag.WEEKEND);
    }

    public void setDisabledDays(Set<Long> disabledDays) {
        setDaysAccordingToSet(disabledDays, DayFlag.DISABLED);
    }

    public void setConnectedCalendarDays(Set<Long> connectedCalendarDays) {
        setDaysAccordingToSet(connectedCalendarDays, DayFlag.FROM_CONNECTED_CALENDAR);
    }

    public void setDisabledDaysCriteria(DisabledDaysCriteria criteria){
        for (Month month : months) {
            for (Day day : month.getDays()) {
                if(!day.isDisabled()){
                    day.setDisabled(CalendarUtils.isDayDisabledByCriteria(day, criteria));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setDayContents(List<DayContent> dayContents){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<DayContent> deleted = new ArrayList<>();
        if(this.dayContents.size() > dayContents.size()){
            deleted.addAll(this.dayContents);
            for(DayContent dayContent : dayContents){
                deleted.remove(deleted.indexOf(dayContent));
            }
        }
        for (Month month : months){
            //추가되거나 변경될 때
            for(Day day : month.getDays()){
                String dayDate = simpleDateFormat.format(day.getCalendar().getTime());
                for(DayContent selected : dayContents){
                    String selectedDate = simpleDateFormat.format(selected.getContentDate());
                    if(dayDate.equals(selectedDate)){
                        day.setDayContent(selected);
                    }
                }
                //삭제할 때
                if(deleted.size()!=0){
                    for(DayContent del : deleted){
                        String delDate = simpleDateFormat.format(del.getContentDate());
                        if(dayDate.equals(delDate)){
                            DayContent current = day.getDayContent();
                            current.setContentString(null);
                            current.setContentColor(calendarView.getCalendarBackgroundColor());
                            day.setDayContent(current);
                        }
                    }
                }
            }
        }
        this.dayContents = dayContents;
    }

    public void setDaySyncData(List<CalendarSyncData> syncDataList){
        this.syncDataList = syncDataList;
    }

    private void attachSyncData(Month month){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for(Day day : month.getDays()){
            List<CalendarSyncData> eachDaySyncDataList = new ArrayList<>();
            for(CalendarSyncData syncData : this.syncDataList){
                String dayDate = simpleDateFormat.format(day.getCalendar().getTime());
                String selectedDate = simpleDateFormat.format(syncData.getStartDate());
                if(dayDate.equals(selectedDate)){
                    eachDaySyncDataList.add(syncData);
                }
            }
            day.setSyncData(eachDaySyncDataList);
        }
        month.setSynced(true);
    }



    private void setDaysAccordingToSet(Set<Long> days, DayFlag dayFlag) {
        if (days != null && !days.isEmpty()) {
            for (Month month : months) {
                for (Day day : month.getDays()) {
                    switch (dayFlag) {
                        case WEEKEND:
                            day.setWeekend(days.contains(day.getCalendar().get(Calendar.DAY_OF_WEEK)));
                            break;

                        case DISABLED:
                            day.setDisabled(CalendarUtils.isDayInSet(day, days));
                            break;

                        case FROM_CONNECTED_CALENDAR:
                            day.setFromConnectedCalendar(CalendarUtils.isDayInSet(day, days));
                            break;
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
}
