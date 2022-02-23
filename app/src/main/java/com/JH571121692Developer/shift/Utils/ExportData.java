package com.JH571121692Developer.shift.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ExportData {
    private String accountName = null;
    private List<String> displayNames = new ArrayList<>();

    public ExportData(String accountName, List<String> displayNames) {
        this.accountName = accountName;
        this.displayNames = displayNames;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void add(String displayName){
        this.displayNames.add(displayName);
    }

    public List<String> getDisplayNames() {
        return displayNames;
    }

    public void printAll(String logKey){
        Log.d(logKey,"accountName : " + this.accountName);
        for(String displayName : this.displayNames)
            Log.d(logKey, "\t displayName : " + displayName);
    }

    public void setDisplayNames(List<String> displayNames) {
        this.displayNames = displayNames;
    }
}
