package com.example.shift.cosmocalendar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.shift.R;
import com.example.shift.cosmocalendar.model.Day;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayContent {
    private int contentColor;
    private String contentString;
    private Date contentDate;
    private CalendarSyncData syncData;

    public DayContent(){

    }

    public DayContent(int contentColor, String contentString, Date contentDate) {
        this.contentColor = contentColor;
        this.contentString = contentString;
        this.contentDate = contentDate;
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

    public String getContentString() {
        return contentString;
    }

    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    public Date getContentDate() {
        return contentDate;
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

    public void updateSelectedDaysPrefByColor(Context context, String key, String written, int color){
        List<DayContent> data = getSelectedDaysPref(context, key);
        List<DayContent> after = new ArrayList<>();
        for(DayContent d : data){
            if(d.getContentColor() == color)
                d.setContentString(written);
            after.add(d);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new GsonBuilder().create();
        String save = gson.toJson(after, new TypeToken<List<DayContent>>(){}.getType());
        editor.putString(key, save);
        editor.commit();
    }

    public void setColorStringPref(Context context, String key, Map<Integer, String> integerStringMap){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new GsonBuilder().create();
        String save = gson.toJson(integerStringMap, new TypeToken<Map<Integer, String>>(){}.getType());
        editor.putString(key, save);
        editor.commit();
    }

    public Map<Integer, String> getColorStringPref(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String saved = preferences.getString(key, null);
        Gson gson = new GsonBuilder().create();
        Map<Integer, String> integerStringMap = new HashMap<Integer, String>(){
            {
                put(context.getColor(R.color.color1),"");
                put(context.getColor(R.color.color2),"");
                put(context.getColor(R.color.color3),"");
            }
        };

        if(saved != null){
            integerStringMap = gson.fromJson(saved, new TypeToken<Map<Integer, String>>(){}.getType());
        }
        return integerStringMap;
    }

    public void setSelectedDaysPref(Context context, String key, List<Day> selectedDays, String written, int color){
        //기존 DayContent
        List<DayContent> data = getSelectedDaysPref(context, key);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //기존에 데이트가 있는 DayContent는 내용 변경, 나머지는 그대로 추가
        List<DayContent> _selectedDays = new ArrayList<>();
        if(!data.isEmpty()){
            Log.e("Shift","prev exist!");
            //이번에 선택된 녀석들은 전부 추가
            for(Day selectedDay : selectedDays){
                _selectedDays.add(new DayContent(color, written, selectedDay.getCalendar().getTime()));
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
                    _selectedDays.add(new DayContent(color, written, selectedDay.getCalendar().getTime()));
                }
            }
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new GsonBuilder().create();
        String save = gson.toJson(_selectedDays, new TypeToken<List<DayContent>>(){}.getType());
        editor.putString(key, save);
        editor.commit();
    }


    public void deleteSelectedDaysPref(Context context, String key, List<Day> selectedDays, String written, int color){
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
}
