package com.JH571121692Developer.shift.Dialog;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.JH571121692Developer.shift.ColorPicker.CustomFlag;
import com.JH571121692Developer.shift.ColorPicker.MyColorPickerDialog;
import com.JH571121692Developer.shift.MainActivity;
import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.Sync.GooglePlayService;
import com.JH571121692Developer.shift.Utils.SettingHelper;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;
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
    private Switch connectSwitch;
    private CalendarView calendarView;
    private Activity activity;
    private Spinner spinner;
    private LinearLayout colorCodePreview;
    private ImageView infoButton;

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
        beforeintegerStringList = settingHelper.getColorStringList();
//        Log.d("TEST__","settingDialog : iniViews : beforeintergerStringList is null ? " + (beforeintegerStringList == null));
        integerStringList.addAll(beforeintegerStringList);

        ivDone = findViewById(R.id.setting_done_button);
        editText = findViewById(R.id.setting_edit_button);
        infoButton = findViewById(R.id.setting_info);
        changeColorButton = findViewById(R.id.setting_color_button);
        changeColorButton.setBackgroundColor(integerStringList.get(0).first);
        changeColorButton.setOnClickListener(this);
        setting_colorBunch = findViewById(R.id.setting_colorBunch);
        spinner = findViewById(R.id.setting_colorCode_spinner);
        colorCodePreview = findViewById(R.id.setting_colorCode_preview);
        for(int i=0;i<setting_colorBunch.getChildCount();i++){
            setting_colorBunch.getChildAt(i).setOnClickListener(this);
            //setting_colorBunch.getChildAt(i).setBackgroundColor(integerStringList.get(i).first);
            ((TextView) ((FrameLayout) setting_colorBunch.getChildAt(i)).getChildAt(1)).setText(integerStringList.get(i).second);
        }
        editText.setText(integerStringList.get(0).second);

        String[] colorCodeSet = getContext().getResources().getStringArray(R.array.colorCodeSet);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                colorCodeSet);
        int spinnerPosition = settingHelper.getColorCodeSpinnerPosition();
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerPosition);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String[] colorcode;
                switch (colorCodeSet[position]){
                    case "내맘대로" :
                        ArrayList<String> col = new ArrayList<>();
                        for(Pair<Integer, String> pair : beforeintegerStringList)
                            col.add("#" + String.format("%06X", (0xFFFFFF & pair.first)));
                        colorcode = col.toArray(new String[col.size()]);
                        break;
                    case "색상코드1" :
                        colorcode = getContext().getResources().getStringArray(R.array.colorCode1);
                        break;
                    case "색상코드2" :
                        colorcode = getContext().getResources().getStringArray(R.array.colorCode2);
                        break;

                    case "색상코드3" :
                        colorcode = getContext().getResources().getStringArray(R.array.colorCode3);
                        break;

                    case "색상코드4" :
                        colorcode = getContext().getResources().getStringArray(R.array.colorCode4);
                        break;

                    default :
                        colorcode = getContext().getResources().getStringArray(R.array.colorCode1);
                        break;
                }
                settingHelper.setColorCodeSpinnerPosition(position);
                for(int j=0;j<colorCodePreview.getChildCount();j++){
                    colorCodePreview.getChildAt(j).setBackgroundColor(Color.parseColor(colorcode[j]));
                    String written = integerStringList.get(j).second;
                    integerStringList.set(j,Pair.create(Color.parseColor(colorcode[j]),written));
                }
                //이거를 안 하면 changeColorButton의 정보가 업데이트 되지 않는다.
                for(int k=0;k<setting_colorBunch.getChildCount();k++){
                    FrameLayout workView = (FrameLayout) setting_colorBunch.getChildAt(k);
                    if(workView.getChildAt(0).getVisibility() == View.VISIBLE){
                        changeColorButton.setBackgroundColor(Color.parseColor(colorcode[k]));
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ivDone.setOnClickListener(this);

        importSwitch = findViewById(R.id.setting_import_sw);
        exportSwitch = findViewById(R.id.setting_export_sw);
        connectSwitch = findViewById(R.id.setting_connect_sw);
        importSwitch.setChecked(settingHelper.isImport());
        exportSwitch.setChecked(settingHelper.isExport());
        connectSwitch.setChecked(settingHelper.isConnected());
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
//                    Log.d("TEST__", "exportSwitch : to be true");
                    settingHelper.setExport(true);
                }
                else{
//                    Log.d("TEST__", "exportSwitch : to be false");
                    settingHelper.setExport(false);
                    //DeleteAll
                    googlePlayService.getResultsFromApi(3);
                    settingHelper.setBeforeSyncDayContentList(new ArrayList<>());
                }
            }
        });

        connectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    settingHelper.setConnect(true);
                }
                else{
                    settingHelper.setConnect(false);
                }
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InfoDialog(view.getContext()).show();
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
//            Log.d("Shift___","setting_done_button");
            doneClick();
        }
        else if(id == R.id.setting_color_button){
//            Log.d("Shift___","setting_change_color_button");
            for(int i=0;i<setting_colorBunch.getChildCount();i++){
                //colorview is colorll (R.id.color1_btn)
                FrameLayout workView = (FrameLayout) setting_colorBunch.getChildAt(i);
                if(workView.getChildAt(0).getVisibility() == View.VISIBLE){
                    MyColorPickerDialog.Builder builder = makeColorPickerBuilder(new ArrayList<>(Arrays.asList(v)));
                    ColorPickerView colorPickerView = builder.getColorPickerView();
                    colorPickerView.setFlagView(new CustomFlag(getContext(), R.layout.custom_flag));
                    builder.show();
                    break;
                }
            }
            //색깔을 다시 선택했으므로 "내맘대로"로 돌아가야 함.
            settingHelper.setColorCodeSpinnerPosition(0);
        }
        //여기는 color 선택들만 나머지는 위에 else if에 다세용
        else{
            //이전에 체크되어있던 정보들을 저장한다.
            for(int i=0;i<setting_colorBunch.getChildCount();i++){
                //colorview is colorll (R.id.color1_btn)
                FrameLayout workView = (FrameLayout) setting_colorBunch.getChildAt(i);
                if(workView.getChildAt(0).getVisibility() == View.VISIBLE){
                    int color = ((ColorDrawable) changeColorButton.getBackground()).getColor();
                    String written = editText.getText().toString();
                    integerStringList.set(i,Pair.create(color,written));
                    ((TextView) workView.getChildAt(1)).setText(written);
                    break;
                }
            }
            //현재 체크 될 녀석으로 정보를 업데이트하고 기존의 체크된 놈을 체크 해제한다.
            for(int i=0;i<setting_colorBunch.getChildCount();i++){
                //colorview is colorll (R.id.color1_btn)
                FrameLayout workView = (FrameLayout) setting_colorBunch.getChildAt(i);
                if(id == workView.getId()){
                    int color = integerStringList.get(i).first;
                    changeColorButton.setBackgroundColor(color);
                    workView.getChildAt(0).setVisibility(View.VISIBLE);
                    editText.setText(integerStringList.get(i).second);
                }
                else{
                    workView.getChildAt(0).setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void doneClick() {
        //마지막에 색과 내용이 바뀐뒤에 다른 색깔로 체크가 안되면 그 정보는 저장이 되지 않으므로 done하고 나서 정보를 다시 저장해 주어야 한다.
        for(int i=0;i<setting_colorBunch.getChildCount();i++){
            //colorview is colorll (R.id.color1_btn)
            FrameLayout workView = (FrameLayout) setting_colorBunch.getChildAt(i);
            if(workView.getChildAt(0).getVisibility() == View.VISIBLE){
                int color = ((ColorDrawable) changeColorButton.getBackground()).getColor();
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
