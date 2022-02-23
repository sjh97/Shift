package com.JH571121692Developer.shift.cosmocalendar.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Month {

    private List<Day> days;
    private Day firstDay;
    private boolean isSynced;

    public Month(Day firstDay, List<Day> days) {
        this.days = days;
        this.firstDay = firstDay;
        this.isSynced = false;
    }

    public Day getFirstDay() {
        return firstDay;
    }

    public void setFirstDay(Day firstDay) {
        this.firstDay = firstDay;
    }

    public void setSynced(boolean synced){
        isSynced = synced;
    }

    public boolean isSynced(){
        return this.isSynced;
    }

    public List<Day> getDays() {
        return days;
    }

    /**
     * Returns selected days that belong only to current month
     *
     * @return
     */
    public List<Day> getDaysWithoutTitlesAndOnlyCurrent() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDay.getCalendar().getTime());
        int currentMonth = calendar.get(Calendar.MONTH);

        List<Day> result = new ArrayList<>();
        for (Day day : days) {
            calendar.setTime(day.getCalendar().getTime());
            if (!(day instanceof DayOfWeek) && calendar.get(Calendar.MONTH) == currentMonth) {
                result.add(day);
            }
        }
        return result;
    }

    public String getMonthName() {
        return new SimpleDateFormat("yyyy MMMM", Locale.getDefault()).format(firstDay.getCalendar().getTime());
    }


}
