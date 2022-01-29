package com.example.shift.Dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.example.shift.ColorPicker.CustomFlag;
import com.example.shift.ColorPicker.MyColorPickerDialog;
import com.example.shift.MainActivity;
import com.example.shift.R;
import com.example.shift.Utils.ExportData;
import com.example.shift.Utils.SettingHelper;
import com.example.shift.cosmocalendar.utils.CalendarSyncData;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.view.CalendarView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingDialog extends Dialog implements View.OnClickListener{

    //Views
    private TextView ivDone;
    private EditText editText;
    private LinearLayout changeColorButton;
    private LinearLayout setting_colorBunch;
    private Switch importSwitch;
    private Switch exportSwitch;
    private CalendarView calendarView;
    private Activity activity;

    private List<Pair<Integer, String>> integerStringList;
    private List<Pair<Integer, String>> beforeintegerStringList;
    String colorkey = "";
    String key = "";
    String settingkey = "";
    private boolean restartCode = false;
    private SettingHelper settingHelper;

    private OnSettingListener onSettingListener;

    public SettingDialog(@NonNull Context context) {
        super(context);
    }

    public SettingDialog(@NonNull Context context, Activity activity, CalendarView calendarView, OnSettingListener onSettingListener) {
        super(context);
        this.onSettingListener = onSettingListener;
        this.calendarView = calendarView;
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_setting);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getAttributes().gravity = Gravity.TOP;
        colorkey = getContext().getString(R.string.colorkey);
        key = getContext().getString(R.string.key);
        settingkey = getContext().getString(R.string.settingkey);
        settingHelper = new SettingHelper(getContext(), settingkey);
        initViews();
    }

    private void initViews() {
        beforeintegerStringList = new DayContent().getColorStringPref(getContext(), colorkey);
        integerStringList = new DayContent().getColorStringPref(getContext(), colorkey);

        ivDone = findViewById(R.id.setting_done_button);
        editText = findViewById(R.id.setting_edit_button);
        changeColorButton = findViewById(R.id.setting_color_button);
        changeColorButton.setBackgroundColor(integerStringList.get(0).first);
        changeColorButton.setOnClickListener(this);
        setting_colorBunch = findViewById(R.id.setting_colorBunch);
        for(int i=0;i<setting_colorBunch.getChildCount();i++){
            setting_colorBunch.getChildAt(i).setOnClickListener(this);
            setting_colorBunch.getChildAt(i).setBackgroundColor(integerStringList.get(i).first);
        }
        editText.setText(integerStringList.get(0).second);

        ivDone.setOnClickListener(this);

        importSwitch = findViewById(R.id.setting_import_sw);
        exportSwitch = findViewById(R.id.setting_export_sw);
        importSwitch.setChecked(settingHelper.isImport());
        exportSwitch.setChecked(settingHelper.isExport());
        importSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    settingHelper.setImport(true);
                    restartCode = true;
                }
                else{
                    settingHelper.setImport(false);
                    restartCode = true;
                }
            }
        });
        exportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    settingHelper.setExport(true);
                    getCalendarAccount();
                    new ExportDialog(getContext(), settingHelper).show();
//                    calendarView.sendDataToCalendar();
                }
                else{
                    settingHelper.setExport(false);
                }
            }
        });


    }

    public void getCalendarAccount(){
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        Cursor cur = null;
        ContentResolver cr = this.getContext().getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        cur = cr.query(uri, EVENT_PROJECTION, null, null, null);
        List<ExportData> exportDataList = new ArrayList<>();
        // Use the cursor to step through the returned records
        while(cur.moveToNext()){

            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
            boolean isSame = false;
            for(int i = 0; i < exportDataList.size(); i++){
                if(exportDataList.get(i).getAccountName().equals(accountName)){
                    exportDataList.get(i).add(displayName);
                    isSame = true;
                    break;
                }
            }
            if(!isSame){
                exportDataList.add(new ExportData(accountName, new ArrayList<>()));
                exportDataList.get(exportDataList.size()-1).add(displayName);
            }

        }
        for(ExportData exportData : exportDataList)
            exportData.printAll("Shift___");

        settingHelper.setExportDataList(exportDataList);
        cur.close();
    }


    public void setOnSettingListener(OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }

    public MyColorPickerDialog.Builder makeColorPickerBuilder(List<View> willbeChangedViews){
        MyColorPickerDialog.Builder builder;
        builder = new MyColorPickerDialog.Builder(getContext());
        builder.setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("OK", new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                        int getGridColor = builder.getColorGridColor();
                        int color;
                        SharedPreferences preferences = getContext().getSharedPreferences(getContext().getApplicationContext().getString(R.string.changeColorKey)
                                , getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        if(getGridColor == Color.parseColor("#000000")){
                            editor.putInt(getContext().getApplicationContext().getString(R.string.backgroundColorKey), envelope.getColor());
                            editor.commit();
                            color = envelope.getColor();
                        }
                        else{
                            editor.putInt(getContext().getApplicationContext().getString(R.string.backgroundColorKey), getGridColor);
                            editor.commit();
                            color = getGridColor;
                        }
                        for(View view : willbeChangedViews)
                            view.setBackgroundColor(color);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(true);

        return builder;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.setting_done_button) {
            Log.d("Shift___","setting_done_button");
            doneClick();
        }
        else if(id == R.id.setting_color_button){
            Log.d("Shift___","setting_change_color_button");
            for(int i=0;i<setting_colorBunch.getChildCount();i++){
                //colorview is colorll (R.id.color1_btn)
                LinearLayout colorView = (LinearLayout) setting_colorBunch.getChildAt(i);
                if(colorView.getChildAt(0).getVisibility() == View.VISIBLE){
                    MyColorPickerDialog.Builder builder = makeColorPickerBuilder(new ArrayList<>(Arrays.asList(colorView, v)));
                    ColorPickerView colorPickerView = builder.getColorPickerView();
                    colorPickerView.setFlagView(new CustomFlag(getContext(), R.layout.custom_flag));
                    builder.show();
                    break;
                }
            }
        }
        //여기는 color 선택들만 나머지는 위에 else if에 다세용
        else{
            for(int i=0;i<setting_colorBunch.getChildCount();i++){
                //colorview is colorll (R.id.color1_btn)
                LinearLayout colorView = (LinearLayout) setting_colorBunch.getChildAt(i);
                if(colorView.getChildAt(0).getVisibility() == View.VISIBLE){
                    int color = ((ColorDrawable) colorView.getBackground()).getColor();
                    String written = editText.getText().toString();
                    integerStringList.set(i,Pair.create(color,written));
                    break;
                }
            }
            for(int i=0;i<setting_colorBunch.getChildCount();i++){
                //colorview is colorll (R.id.color1_btn)
                LinearLayout colorView = (LinearLayout) setting_colorBunch.getChildAt(i);
                if(id == colorView.getId()){
                    changeColorButton.setBackgroundColor(((ColorDrawable)colorView.getBackground()).getColor());
                    colorView.getChildAt(0).setVisibility(View.VISIBLE);
                    editText.setText(integerStringList.get(i).second);
                }
                else{
                    colorView.getChildAt(0).setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void doneClick() {

        for(int i=0;i<setting_colorBunch.getChildCount();i++){
            //colorview is colorll (R.id.color1_btn)
            LinearLayout colorView = (LinearLayout) setting_colorBunch.getChildAt(i);
            if(colorView.getChildAt(0).getVisibility() == View.VISIBLE){
                int color = ((ColorDrawable) colorView.getBackground()).getColor();
                String written = editText.getText().toString();
                integerStringList.set(i,Pair.create(color,written));
                break;
            }
        }

        new DayContent().setColorStringPref(getContext(), colorkey, integerStringList);

        if (onSettingListener != null) {
            onSettingListener.OnSettingListener(beforeintegerStringList,integerStringList);
        }
        if(restartCode){
            dismiss();
            activity.finish();
            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(intent);
        }
        else
            dismiss();

    }
}
