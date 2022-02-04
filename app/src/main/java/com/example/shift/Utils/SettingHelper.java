package com.example.shift.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.example.shift.R;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.utils.CalendarSyncData;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingHelper {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private String key;
    private Gson gson;
    private Settings settings;

    public SettingHelper(Context context, String key){
        this.context = context;
        this.key = key;
        this.gson = new GsonBuilder().create();
        this.settings = new Settings();
        initSetting();
    }

    private void initSetting() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        String saved = preferences.getString(key, null);
        if(saved == null){
            //처음 초기화
            this.settings = new Settings();
        }
        else{
            Log.d("TEST__", "SettingHelper : initSetting : have saved data");
            Settings settings = gson.fromJson(saved, new TypeToken<Settings>(){}.getType());
            this.settings = settings;
            this.settings.printAll("TEST__");
        }

    }

    public boolean isImport() {
        return settings.isImport;
    }

    public boolean isExport() {
        return settings.isExport;
    }

    public List<ExportData> getExportDataList() {
        return settings.exportDataList;
    }

    public Pair<Integer, Integer> getExportIndices() {
        return settings.exportIndices;
    }

    public List<DayContent> getBeforeSyncDayContentList(){
        return settings.beforeSyncDayContentList;
    }

    public String getMyCalendarID(){
        return settings.myCalendarID;
    }

    public List<CalendarSyncData> getCalendarSyncDataList(){
        return settings.calendarSyncDataList;
    }

    public List<Pair<Integer, String>> getColorStringList(){
        return settings.colorStringList;
    }

    public Pair<Boolean,DayContent> haveSyncDayContent(DayContent dayContent){
        initSetting();
        boolean have = false;
        DayContent dayContent1 = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            if(settings.beforeSyncDayContentList == null){
                Log.e("TEST__","니가 왜 null인데 시발");
                return Pair.create(false, null);
            }
            for(DayContent d : settings.beforeSyncDayContentList){
                //Log.d("TEST___",d.getContentDate().toString() + " and " + dayContent.getContentDate().toString());
                String d_date = simpleDateFormat.format(d.getContentDate());
                String dayContent_date = simpleDateFormat.format(dayContent.getContentDate());
                if(d_date.equals(dayContent_date)){
                    have = true;
                    dayContent1 = d;
                }
            }
        }
        catch (Exception e){
            Log.d("TEST__", "ERROR while haveSyncDayContent \n" + e.toString());
            e.printStackTrace();
            return Pair.create(false, null);
        }

        return Pair.create(have, dayContent1);
    }




    public void setImport(boolean isImport) {
        initSetting();
        settings.isImport = isImport;
        saveSetting();
    }

    public void setExport(boolean export) {
        initSetting();
        settings.isExport = export;
        saveSetting();
    }
    public void setExportDataList(List<ExportData> exportDataList){
        initSetting();
        settings.exportDataList = exportDataList;
        saveSetting();
    }

    public void setExportIndices(int AccountPos, int DisplayPos){
        initSetting();
        settings.exportIndices = Pair.create(AccountPos, DisplayPos);
        saveSetting();
    }

    public void setBeforeSyncDayContentList(List<DayContent> dayContents){
        initSetting();
        settings.beforeSyncDayContentList = new ArrayList<>();
        settings.beforeSyncDayContentList.addAll(dayContents);
        saveSetting();
    }

    public void setColorStringList(List<Pair<Integer, String>> colorStringList){
        initSetting();
        settings.colorStringList = new ArrayList<>();
        settings.colorStringList.addAll(colorStringList);
        saveSetting();
    }

    public void setMyCalendarID(String id){
        initSetting();
        settings.myCalendarID = id;
        Log.d("TEST__","SettingHelper : id is set to " + settings.myCalendarID);
        saveSetting();
    }

    public void setCalendarSyncDataList(List<CalendarSyncData> calendarSyncDataList){
        initSetting();
        settings.calendarSyncDataList = new ArrayList<>();
        settings.calendarSyncDataList.addAll(calendarSyncDataList);
        Log.d("TEST__","SettingHelper : calendarSyncData is set to " + settings.calendarSyncDataList);
        saveSetting();
    }

    private void saveSetting(){
        String save = gson.toJson(this.settings, new TypeToken<Settings>(){}.getType());
        Log.d("TEST__","SettingHelper : saveSetting : " + save);
        editor.putString(key, save);
        editor.commit();
    }

     public class Settings{

        public Settings(){

        }

        public boolean isImport = false;
        public boolean isExport = false;
        public List<ExportData> exportDataList = new ArrayList<>();
        public Pair<Integer,Integer> exportIndices = Pair.create(0,-1);
        public List<DayContent> beforeSyncDayContentList = new ArrayList<>();
        public String myCalendarID = null;
        public List<CalendarSyncData> calendarSyncDataList = new ArrayList<>();
        public List<Pair<Integer, String>> colorStringList = Arrays.asList(
                Pair.create(context.getColor(R.color.googleCalendarColor4),"D"),
                Pair.create(context.getColor(R.color.googleCalendarColor6),"E"),
                Pair.create(context.getColor(R.color.googleCalendarColor7),"N"),
                Pair.create(context.getColor(R.color.googleCalendarColor11),"S"),
                Pair.create(context.getColor(R.color.googleCalendarColor9),"H"),
                Pair.create(context.getColor(R.color.googleCalendarColor2),"?")
                );

         public void printAll(String key){
             Log.d(key, " " + "\nisImoprt : " + isImport + "\nisExoprt : " + isExport
                     +"\nbeforeSyncDayContentList : null? " + (beforeSyncDayContentList == null)+ "\ncalendarID : " + myCalendarID);
             if(beforeSyncDayContentList != null){
                 Log.d(key,"\n beforeSyncDayContentList.size() is " + beforeSyncDayContentList.size());
             }
         }
    }
}
