package com.example.shift.Dialog;


import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.example.shift.ColorPicker.CustomFlag;
import com.example.shift.ColorPicker.MyColorPickerDialog;
import com.example.shift.MainActivity;
import com.example.shift.R;
import com.example.shift.Sync.GooglePlayService;
import com.example.shift.Utils.ExportData;
import com.example.shift.Utils.SettingHelper;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.view.CalendarView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingDialog extends Dialog implements View.OnClickListener{

    //Views
    private ImageButton ivDone;
    private EditText editText;
    private LinearLayout changeColorButton;
    private LinearLayout setting_colorBunch;
    private Switch importSwitch;
    private Switch exportSwitch;
    private CalendarView calendarView;
    private Activity activity;

    private List<Pair<Integer, String>> integerStringList = new ArrayList<>();
    private List<Pair<Integer, String>> beforeintegerStringList = new ArrayList<>();
    SettingHelper settingHelper;
    String colorkey = "";
    String key = "";
    String setting_key = "";
    private boolean restartCode = false;
    private GooglePlayService googlePlayService;

    private OnSettingListener onSettingListener;

    public SettingDialog(@NonNull Context context) {
        super(context);
    }

    public SettingDialog(@NonNull Context context, Activity activity, CalendarView calendarView,
                         GooglePlayService googlePlayService,SettingHelper settingHelper,OnSettingListener onSettingListener) {
        super(context, R.style.full_screen_dialog);
        this.onSettingListener = onSettingListener;
        this.calendarView = calendarView;
        this.activity = activity;
        this.googlePlayService = googlePlayService;
        this.settingHelper = settingHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getAttributes().gravity = Gravity.TOP;
        colorkey = getContext().getString(R.string.colorkey);
        key = getContext().getString(R.string.key);
        setting_key = activity.getString(R.string.settingkey);
        initViews();
    }

    private void initViews() {
        SettingHelper settingHelper = new SettingHelper(getContext(), setting_key);
//        beforeintegerStringList = new DayContent().getColorStringPref(getContext(), colorkey);
        beforeintegerStringList = settingHelper.getColorStringList();
        Log.d("TEST__","settingDialog : iniViews : beforeintergerStringList is null ? " + (beforeintegerStringList == null));
        integerStringList.addAll(beforeintegerStringList);

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

                    PermissionListener permissionListener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            settingHelper.setImport(true);
                            restartCode = true;
                        }
                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {
                            settingHelper.setImport(false);
                            restartCode = false;
                            importSwitch.setChecked(false);
                        }
                    };
                    TedPermission.with(getContext())
                            .setPermissionListener(permissionListener)
                            .setDeniedMessage("가져오기 기능을 사용하실 수 없어요...ㅠㅜ\n\n" +
                                    "[설정]>[권한]에서 권한을 허용할 수 있어요.")
                            .setPermissions(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                            .check();
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
                    Log.d("TEST__", "exportSwitch : to be true");
                    settingHelper.setExport(true);
                }
                else{
                    Log.d("TEST__", "exportSwitch : to be false");
                    settingHelper.setExport(false);
                    //DeleteAll
                    googlePlayService.getResultsFromApi(3);
                    settingHelper.setBeforeSyncDayContentList(new ArrayList<>());
                }
            }
        });


    }


    public void setOnSettingListener(OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
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
            //이전에 체크되어있던 정보들을 저장한다.
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
            //현재 체크 될 녀석으로 정보를 업데이트하고 기존의 체크된 놈을 체크 해제한다.
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
        //마지막에 색과 내용이 바뀐뒤에 다른 색깔로 체크가 안되면 그 정보는 저장이 되지 않으므로 done하고 나서 정보를 다시 저장해 주어야 한다.
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

//        new DayContent().setColorStringPref(getContext(), colorkey, integerStringList);
        settingHelper.setColorStringList(integerStringList);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doneClick();
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
}
