package com.example.shift.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.shift.Adapter.AccountAdapter;
import com.example.shift.R;
import com.example.shift.Utils.ExportData;
import com.example.shift.Utils.SettingHelper;

import java.util.ArrayList;
import java.util.List;

public class ExportDialog extends Dialog {
    private ListView listView;
    private Spinner spinner;
    private ImageView doneView;
    private ImageView cancelView;
    private SettingHelper settingHelper;

    public ExportDialog(@NonNull Context context, SettingHelper settingHelper) {
        super(context);
        this.settingHelper = settingHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.view_export);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getAttributes().gravity = Gravity.TOP;
        initViews();
    }

    private void initViews(){
        listView = findViewById(R.id.view_export_listView);
        spinner = findViewById(R.id.view_export_spinner);
        List<String> accountList = new ArrayList<>();
        List<ExportData> exportDataList = settingHelper.getExportDataList();
        for(int i=0; i<exportDataList.size();i++){
            accountList.add(exportDataList.get(i).getAccountName());
        }
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item,accountList);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                AccountAdapter exportAdapter = new AccountAdapter(getContext(),
                        exportDataList.get(position), position, settingHelper);
                listView.setAdapter(exportAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setSelection(settingHelper.getExportIndices().first);

        doneView = findViewById(R.id.view_export_iv_done);
        cancelView = findViewById(R.id.view_export_iv_cancel);

        doneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
