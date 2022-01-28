package com.example.shift.cosmocalendar.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shift.cosmocalendar.adapter.viewholder.DayHolder;
import com.example.shift.cosmocalendar.adapter.viewholder.DayOfWeekHolder;
import com.example.shift.cosmocalendar.adapter.viewholder.OtherDayHolder;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.model.Month;
import com.example.shift.cosmocalendar.utils.Constants;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.utils.MonthDiffCallback;
import com.example.shift.cosmocalendar.view.CalendarView;
import com.example.shift.cosmocalendar.view.ItemViewType;
import com.example.shift.cosmocalendar.view.delegate.DayDelegate;
import com.example.shift.cosmocalendar.view.delegate.DayOfWeekDelegate;
import com.example.shift.cosmocalendar.view.delegate.OtherDayDelegate;

import java.util.List;


public class DaysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Month month;
    private DayOfWeekDelegate dayOfWeekDelegate;
    private DayDelegate dayDelegate;
    private OtherDayDelegate otherDayDelegate;
    private CalendarView calendarView;
    private List<DayContent> dayContents;

    private DaysAdapter(Month month,
                        DayOfWeekDelegate dayOfWeekDelegate,
                        DayDelegate dayDelegate,
                        OtherDayDelegate otherDayDelegate,
                        CalendarView calendarView) {
        setHasStableIds(false);
        this.month = month;
        this.dayOfWeekDelegate = dayOfWeekDelegate;
        this.dayDelegate = dayDelegate;
        this.otherDayDelegate = otherDayDelegate;
        this.calendarView = calendarView;
    }

    /*
    public void updateMonthListItems(List<Month> months){
        final MonthDiffCallback diffCallback = new MonthDiffCallback(this.months, months);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.months.clear();
        this.months.addAll(months);
        diffResult.dispatchUpdatesTo(this);
    }
     */



    @Override
    public int getItemViewType(int position) {
        if (position < Constants.DAYS_IN_WEEK && calendarView.isShowDaysOfWeek()) {
            return ItemViewType.DAY_OF_WEEK;
        }
        if (month.getDays().get(position).isBelongToMonth()) {
            return ItemViewType.MONTH_DAY;
        } else {
            return ItemViewType.OTHER_MONTH_DAY;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.DAY_OF_WEEK:
                return dayOfWeekDelegate.onCreateDayHolder(parent, viewType);
            case ItemViewType.MONTH_DAY:
                return dayDelegate.onCreateDayHolder(parent, viewType);
            case ItemViewType.OTHER_MONTH_DAY:
                return otherDayDelegate.onCreateDayHolder(parent, viewType);
            default:
                throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Day day = month.getDays().get(position);
        final int numWeek = (int) Math.ceil(getItemCount() / 7);
        final double margin = 0.9;
        final int height = (int) (this.calendarView.getrvMonthsHeight() / numWeek * margin);
        switch (holder.getItemViewType()) {
            case ItemViewType.DAY_OF_WEEK:
                dayOfWeekDelegate.onBindDayHolder(day, (DayOfWeekHolder) holder, position);
                break;
            case ItemViewType.OTHER_MONTH_DAY:
                otherDayDelegate.onBindDayHolder(day, (OtherDayHolder) holder, position, height);
                break;
            case ItemViewType.MONTH_DAY:
                dayDelegate.onBindDayHolder(this, day, (DayHolder) holder, position, height);
                break;
        }
    }

    //그 달의 날 개수 리턴
    @Override
    public int getItemCount() {
        return month == null ? 0 : month.getDays().size();
    }

    public void setMonth(Month month) {
        this.month = month;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return month.getDays().get(position).getCalendar().getTimeInMillis();
    }

    public static class DaysAdapterBuilder {

        private Month month;
        private DayOfWeekDelegate dayOfWeekDelegate;
        private DayDelegate dayDelegate;
        private OtherDayDelegate anotherDayDelegate;
        private CalendarView calendarView;
        private List<DayContent> dayContents;

        public DaysAdapterBuilder setMonth(Month month) {
            this.month = month;
            return this;
        }

        public DaysAdapterBuilder setDayOfWeekDelegate(DayOfWeekDelegate dayOfWeekDelegate) {
            this.dayOfWeekDelegate = dayOfWeekDelegate;
            return this;
        }

        public DaysAdapterBuilder setDayDelegate(DayDelegate dayDelegate) {
            this.dayDelegate = dayDelegate;
            return this;
        }

        public DaysAdapterBuilder setOtherDayDelegate(OtherDayDelegate anotherDayDelegate) {
            this.anotherDayDelegate = anotherDayDelegate;
            return this;
        }

        public DaysAdapterBuilder setDayContents(List<DayContent> dayContents){
            this.dayContents = dayContents;
            return this;
        }

        public DaysAdapterBuilder setCalendarView(CalendarView calendarView) {
            this.calendarView = calendarView;
            return this;
        }

        public DaysAdapter createDaysAdapter() {
            return new DaysAdapter(month, dayOfWeekDelegate, dayDelegate, anotherDayDelegate, calendarView);
        }
    }
}
