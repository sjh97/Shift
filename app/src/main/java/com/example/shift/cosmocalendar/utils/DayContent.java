package com.example.shift.cosmocalendar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.example.shift.R;
import com.example.shift.cosmocalendar.model.Day;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayContent implements Comparable<DayContent> {
    public static final int START = 1;
    public static final int KEEP = 2;
    public static final int END = 3;
    public static final int ALONE = 4;
    private int contentColor;
    private String contentString;
    private Date contentDate;
    private CalendarSyncData syncData;
    private int contentColorId = 0;
    private int contentState = START;

    public DayContent(){

    }

    public DayContent(int contentColor, String contentString, Date contentDate, int contentColorId) {
        this.contentColor = contentColor;
        this.contentString = contentString;
        this.contentDate = contentDate;
        this.contentColorId = contentColorId;
    }

    public DayContent(int contentColor, String contentString, Date contentDate, int contentColorId, int contentState) {
        this.contentColor = contentColor;
        this.contentString = contentString;
        this.contentDate = contentDate;
        this.contentColorId = contentColorId;
        this.contentState = contentState;
    }

    public int getContentState() {
        return contentState;
    }

    public void setContentState(int contentState) {
        this.contentState = contentState;
    }

    public CalendarSyncData getSyncData() {
        return syncData;
    }

    public void setSyncData(CalendarSyncData syncData) {
        this.syncData = syncData;
    }

    public int getContentColor() {
        return contentColor;
    }

    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
    }

    public void setContentColorId(int contentColorId){
        this.contentColorId = contentColorId;
    }

    public String getContentString() {
        return contentString;
    }

    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    public Date getContentDate() {
        return contentDate;
    }

    public int getContentColorId(){
        return this.contentColorId;
    }

    public void setContentDate(Date contentDate) {
        this.contentDate = contentDate;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof DayContent){
            if(this.contentDate.equals(((DayContent) obj).contentDate) && this.contentString.equals(((DayContent) obj).contentString)
            && this.contentColor == ((DayContent) obj).contentColor){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    public List<DayContent> setState(List<DayContent> selectedDays){
        for(int i=0;i<selectedDays.size();i++){
            int currentID = selectedDays.get(i).contentColorId;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
            SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM");
            int currentDay = Integer.parseInt(simpleDateFormat.format(selectedDays.get(i).contentDate));
            int curentMonth = Integer.parseInt(monthDateFormat.format(selectedDays.get(i).contentDate));
            if(i==0){
                if(selectedDays.size()==1){
                    selectedDays.get(i).contentState = ALONE;
                }
                else if(currentID - selectedDays.get(i+1).contentColorId == 0){
                    int nextDay = Integer.parseInt(simpleDateFormat.format(selectedDays.get(i+1).contentDate));
                    int nextMonth = Integer.parseInt(monthDateFormat.format(selectedDays.get(i+1).contentDate));
                    if((nextDay - currentDay == 1) && (nextMonth - curentMonth == 0)){
                        selectedDays.get(i).contentState = START;
                    }
                    else{
                        selectedDays.get(i).contentState = ALONE;
                    }
                }
                else{
                    selectedDays.get(i).contentState = ALONE;
                }
            }
            else if(i==selectedDays.size()-1){
                if(selectedDays.get(i-1).contentColorId - currentID == 0){
                    int beforeDay = Integer.parseInt(simpleDateFormat.format(selectedDays.get(i-1).contentDate));
                    int beforeMonth = Integer.parseInt(monthDateFormat.format(selectedDays.get(i-1).contentDate));
                    if((currentDay - beforeDay == 1) && (curentMonth - beforeMonth == 0)){
                        selectedDays.get(i).contentState = END;
                    }
                    else{
                        selectedDays.get(i).contentState = ALONE;
                    }
                }
                else{
                    selectedDays.get(i).contentState = ALONE;
                }
            }
            //여기에 온다는 건 자신의 앞 뒤에 item이 있다는 것을 의미
            else{
                int prevID = selectedDays.get(i-1).contentColorId;
                int postID = selectedDays.get(i+1).contentColorId;
                int beforeDay = Integer.parseInt(simpleDateFormat.format(selectedDays.get(i-1).contentDate));
                int nextDay = Integer.parseInt(simpleDateFormat.format(selectedDays.get(i+1).contentDate));
                int beforeMonth = Integer.parseInt(monthDateFormat.format(selectedDays.get(i-1).contentDate));
                int nextMonth = Integer.parseInt(monthDateFormat.format(selectedDays.get(i+1).contentDate));
                Log.d("KSK","beforeMonth : " + beforeMonth + " currentMonth : " + curentMonth);
                int state;
                if((currentID - prevID == 0 && currentDay - beforeDay == 1 && curentMonth - beforeMonth == 0) && (currentID - postID != 0 || nextDay - currentDay != 1 || nextMonth - curentMonth != 0))
                    state = END;
                else if((currentID - prevID == 0 && currentDay - beforeDay == 1 && curentMonth - beforeMonth == 0) && (currentID - postID == 0  && nextDay - currentDay == 1 && nextMonth - curentMonth == 0))
                    state = KEEP;
                else if((currentID - prevID != 0 || currentDay - beforeDay != 1 || curentMonth - beforeMonth != 0) && (currentID - postID == 0 && nextDay - currentDay == 1 && nextMonth - curentMonth == 0))
                    state = START;
                else
                    state = ALONE;
                selectedDays.get(i).contentState = state;
            }
        }
        return selectedDays;
    }

    public void updateSelectedDaysPrefByColor(Context context, String key, List<Pair<Integer, String>> integerStringList){
        List<DayContent> data = getSelectedDaysPref(context, key);
        List<DayContent> after = new ArrayList<>();
        for(int i=0;i<integerStringList.size();i++){
            int afterColor = integerStringList.get(i).first;
            String written = integerStringList.get(i).second;
            for(DayContent d : data){
                if(d.getContentColorId() == i){
                    d.setContentColor(afterColor);
                    d.setContentString(written);
                    after.add(d);
                }
                //여기에 after.add(d)가 있으면 5배씩(beforeintegerStringList.size()) daycontent가 증가하게 된다...
                //after.add(d);
            }
        }
        Collections.sort(after);
        after = setState(after);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new GsonBuilder().create();
        String save = gson.toJson(after, new TypeToken<List<DayContent>>(){}.getType());
        editor.putString(key, save);
        editor.commit();
    }

    public void updateSelectedDaysPrefByColor(Context context, String key, String written, int color, int id){
        Log.d("KSK","update1");
        List<DayContent> data = getSelectedDaysPref(context, key);
        List<DayContent> after = new ArrayList<>();
        for(DayContent d : data){
            if(d.getContentColorId() == id)
                d.setContentString(written);
            after.add(d);
        }
        Collections.sort(after);
        after = setState(after);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new GsonBuilder().create();
        String save = gson.toJson(after, new TypeToken<List<DayContent>>(){}.getType());
        editor.putString(key, save);
        editor.commit();
    }


    public void setSelectedDaysPref(Context context, String key, List<Day> selectedDays, String written, int color, int id){
        Log.d("KSK","set");
        //기존 DayContent
        List<DayContent> data = getSelectedDaysPref(context, key);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //기존에 데이트가 있는 DayContent는 내용 변경, 나머지는 그대로 추가
        List<DayContent> _selectedDays = new ArrayList<>();
        if(!data.isEmpty()){
            Log.e("Shift","prev exist!");
            //이번에 선택된 녀석들은 전부 추가
            for(Day selectedDay : selectedDays){
                _selectedDays.add(new DayContent(color, written, selectedDay.getCalendar().getTime(), id));
            }
            //기존의 데이터와 겹치지 않는 녀석만 추가
            for(DayContent d : data){
                String prev_date = simpleDateFormat.format(d.getContentDate());
                boolean same = false;
                for(Day selectedDay : selectedDays){
                    String selected = simpleDateFormat.format(selectedDay.getCalendar().getTime());
                    if(selected.equals(prev_date)){
                        same = true;
                    }
                }
                if(!same){
                    _selectedDays.add(d);
                }

            }
            Log.e("Shift",Integer.toString(_selectedDays.size()));
        }
        else{
            Log.e("Shift","prev not exist!");
            if(!selectedDays.isEmpty()){
                for(Day selectedDay : selectedDays){
                    _selectedDays.add(new DayContent(color, written, selectedDay.getCalendar().getTime(),id));
                }
            }

        }
        Collections.sort(_selectedDays);
        _selectedDays = setState(_selectedDays);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new GsonBuilder().create();
        String save = gson.toJson(_selectedDays, new TypeToken<List<DayContent>>(){}.getType());
        editor.putString(key, save);
        editor.commit();
    }

    public void deleteSelectedDaysPref(Context context, String key, List<Day> selectedDays, String written, int color){
        Log.d("KSK","delete");
        //기존 DayContent
        List<DayContent> data = getSelectedDaysPref(context, key);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //기존에 데이트가 있는 DayContent는 내용 변경, 나머지는 그대로 추가
        List<DayContent> _selectedDays = new ArrayList<>();
        if(!data.isEmpty()){
            Log.e("Shift","prev exist!");
            //기존의 데이터와 겹치지 않는 녀석만 추가
            for(DayContent d : data){
                String prev_date = simpleDateFormat.format(d.getContentDate());
                boolean same = false;
                for(Day selectedDay : selectedDays){
                    String selected = simpleDateFormat.format(selectedDay.getCalendar().getTime());
                    if(selected.equals(prev_date)){
                        same = true;
                    }
                }
                if(!same){
                    _selectedDays.add(d);
                }

            }
            Log.e("Shift",Integer.toString(_selectedDays.size()));
        }
        _selectedDays = setState(_selectedDays);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new GsonBuilder().create();
        String save = gson.toJson(_selectedDays, new TypeToken<List<Day>>(){}.getType());
        editor.putString(key, save);
        editor.commit();
    }

    public List<DayContent> getSelectedDaysPref(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String saved = preferences.getString(key, null);
        Gson gson = new GsonBuilder().create();
        List<DayContent> days = new ArrayList<>();

        if(saved != null){
            days = gson.fromJson(saved, new TypeToken<List<DayContent>>(){}.getType());
        }
        return days;
    }

    @Override
    public int compareTo(DayContent dayContent) {
        if(dayContent.contentDate.getTime() < contentDate.getTime()){
            return 1;
        }
        else if(dayContent.contentDate.getTime() > contentDate.getTime()){
            return -1;
        }
        return 0;
    }
}
