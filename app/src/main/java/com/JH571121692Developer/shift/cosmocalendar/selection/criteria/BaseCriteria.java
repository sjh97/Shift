package com.JH571121692Developer.shift.cosmocalendar.selection.criteria;


import com.JH571121692Developer.shift.cosmocalendar.model.Day;

import java.util.Set;

public abstract class BaseCriteria {

    protected Set<Integer> weekendDays;
    protected Set<Integer> disabledDays;

    public abstract boolean isCriteriaPassed(Day day);

    public void setWeekendDays(Set<Integer> weekendDays) {
        this.weekendDays = weekendDays;
    }

    public void setDisabledDays(Set<Integer> disabledDays) {
        this.disabledDays = disabledDays;
    }
}
