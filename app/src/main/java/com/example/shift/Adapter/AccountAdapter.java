package com.example.shift.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.shift.R;
import com.example.shift.Utils.ExportData;
import com.example.shift.Utils.SettingHelper;

import java.util.Set;

public class AccountAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ExportData exportData = null;
    int exportDataListIndex = 0;
    SettingHelper settingHelper;

    public AccountAdapter(Context mContext, ExportData exportData, int position, SettingHelper settingHelper) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.exportData = exportData;
        this.exportDataListIndex = position;
        this.settingHelper = settingHelper;
        Log.d("CheckFlow", " AccountAdapter : exportData.size() : "
                + exportData.getDisplayNames().size());
    }

    @Override
    public int getCount() {
        return exportData.getDisplayNames().size();
    }

    @Override
    public String getItem(int position) {
        return exportData.getDisplayNames().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_item_view_export, parent, false);
        Log.d("CheckFlow","AccountAdapter : " + exportData.getDisplayNames().get(position));
        CheckBox checkBox = view.findViewById(R.id.item_item_view_export_checkbox);
        TextView textView = view.findViewById(R.id.item_item_view_export_title);
        textView.setText(exportData.getDisplayNames().get(position));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    settingHelper.setExportIndices(exportDataListIndex, position);
                }
            }
        });
        if(position == settingHelper.getExportIndices().second && exportData.getAccountName().equals(
                settingHelper.getExportDataList().get(settingHelper.getExportIndices().first).getAccountName()))
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                    settingHelper.setExportIndices(exportDataListIndex, position);
                }
                else{
                    checkBox.setChecked(true);
                }

            }
        });
        return view;
    }
}