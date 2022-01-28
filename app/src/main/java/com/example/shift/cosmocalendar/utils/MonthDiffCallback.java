package com.example.shift.cosmocalendar.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.model.Month;

import java.util.List;

public class MonthDiffCallback extends DiffUtil.Callback {

    private final List<Month> mOldMonthList;
    private final List<Month> mNewMonthList;

    public MonthDiffCallback(List<Month> mOldMonthList, List<Month> mNewMonthList) {
        this.mOldMonthList = mOldMonthList;
        this.mNewMonthList = mNewMonthList;
    }

    @Override
    public int getOldListSize() {
        return mOldMonthList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewMonthList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        final Month oldMonth = mOldMonthList.get(oldItemPosition);
        final Month newMonth = mNewMonthList.get(newItemPosition);
        return oldMonth.getMonthName().equals(newMonth.getMonthName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Month oldMonth = mOldMonthList.get(oldItemPosition);
        final Month newMonth = mNewMonthList.get(newItemPosition);
        boolean isSame = false;
        for(Day oldDay : oldMonth.getDays()){
            for(Day newDay : newMonth.getDays()){
                if(newDay.equals(oldDay)){
                    isSame = true;
                }
                else{
                    isSame = false;
                    break;
                }
            }
        }
        return isSame;
    }


}
