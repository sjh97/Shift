package com.example.shift.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.example.shift.cosmocalendar.utils.DayContent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
            settings.isExport = true;
            settings.isExport = true;
        }
        else{
            Settings settings = gson.fromJson(saved, new TypeToken<Settings>(){}.getType());
            this.settings = settings;
        }

    }

    public boolean isImport() {
        return settings.isImport;
    }

    public void setImport(boolean isImport) {
        settings.isImport = isImport;
        saveSetting();
    }

    public boolean isExport() {
        return settings.isExport;
    }

    public void setExport(boolean export) {
        settings.isExport = export;
        saveSetting();
    }

    private void saveSetting(){
        String save = gson.toJson(this.settings, new TypeToken<Settings>(){}.getType());
        editor.putString(key, save);
        editor.commit();
    }

     public class Settings{

        public Settings(){

        }

        public boolean isImport;
        public boolean isExport;
    }

}
