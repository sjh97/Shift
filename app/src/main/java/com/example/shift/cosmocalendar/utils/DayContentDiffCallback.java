package com.example.shift.cosmocalendar.utils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DayContentDiffCallback extends DiffUtil.Callback {

    private final List<DayContent> mOldDayContentList;
    private final List<DayContent> mNewDayContentList;

    public DayContentDiffCallback(List<DayContent> mOldDayContentList, List<DayContent> mNewDayContentList) {
        this.mOldDayContentList = mOldDayContentList;
        this.mNewDayContentList = mNewDayContentList;
    }

    @Override
    public int getOldListSize() {
        return mOldDayContentList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewDayContentList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        final DayContent oldDayContent = mOldDayContentList.get(oldItemPosition);
        final DayContent newDayContent = mNewDayContentList.get(newItemPosition);
        return oldDayContent.getContentDate().equals(newDayContent.getContentDate());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final DayContent oldDayContent = mOldDayContentList.get(oldItemPosition);
        final DayContent newDayContent = mNewDayContentList.get(newItemPosition);
        boolean isSame = false;
        if(oldDayContent.getContentColor() == newDayContent.getContentColor()){
            if(oldDayContent.getContentString().equals(newDayContent.getContentString())){
                isSame = true;
            }
        }
        return isSame;
    }


}
