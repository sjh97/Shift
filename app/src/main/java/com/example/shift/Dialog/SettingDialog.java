package com.example.shift.Dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.shift.R;
import com.example.shift.cosmocalendar.dialog.OnDaysSelectionListener;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.view.CalendarView;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingDialog extends Dialog implements View.OnClickListener, View.OnLongClickListener{

    //Views
    private TextView ivDone;
    private EditText editText;
    private ImageView colorIv1;
    private ImageView colorIv2;
    private ImageView colorIv3;
    private ImageView colorIv4;
    private ImageView colorIv5;
    private LinearLayout colorll1;
    private LinearLayout colorll2;
    private LinearLayout colorll3;
    private LinearLayout colorll4;
    private LinearLayout colorll5;
    private Map<Integer, String> integerStringMap;
    String colorkey = "";
    String key = "";

    private OnSettingListener onSettingListener;

    public SettingDialog(@NonNull Context context) {
        super(context);
    }

    public SettingDialog(@NonNull Context context, OnSettingListener onSettingListener) {
        super(context);
        this.onSettingListener = onSettingListener;
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
        initViews();
    }

    private void initViews() {
        ivDone = findViewById(R.id.setting_done_button);
        editText = findViewById(R.id.setting_edit_button);
        colorIv1 = findViewById(R.id.setting_color_ib_1);
        colorIv2 = findViewById(R.id.setting_color_ib_2);
        colorIv3 = findViewById(R.id.setting_color_ib_3);
        colorIv4 = findViewById(R.id.setting_color_ib_4);
        colorIv5 = findViewById(R.id.setting_color_ib_5);

        colorll1 = findViewById(R.id.setting_color_ll_1);
        colorll2 = findViewById(R.id.setting_color_ll_2);
        colorll3 = findViewById(R.id.setting_color_ll_3);
        colorll4 = findViewById(R.id.setting_color_ll_4);
        colorll5 = findViewById(R.id.setting_color_ll_5);

        integerStringMap = new DayContent().getColorStringPref(editText.getContext(), colorkey);
        editText.setText(integerStringMap.get(((ColorDrawable)colorll1.getBackground()).getColor()));

        ivDone.setOnClickListener(this);
        colorll1.setOnClickListener(this);
        colorll2.setOnClickListener(this);
        colorll3.setOnClickListener(this);
        colorll4.setOnClickListener(this);
        colorll5.setOnClickListener(this);

        colorll1.setOnLongClickListener(this);

    }


    public void setOnSettingListener(OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if(id == R.id.setting_color_ll_1){
            Toast.makeText(view.getContext(), "1 is clicked",Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.setting_color_ll_2){
        }
        else if(id == R.id.setting_color_ll_3){
        }

        else if(id == R.id.setting_color_ll_4){
        }

        else if(id == R.id.setting_color_ll_5){
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.setting_done_button) {
            doneClick();
        }
        else if(id == R.id.setting_color_ll_1){
            colorIv1.setVisibility(View.VISIBLE);
            colorIv2.setVisibility(View.INVISIBLE);
            colorIv3.setVisibility(View.INVISIBLE);
            colorIv4.setVisibility(View.INVISIBLE);
            colorIv5.setVisibility(View.INVISIBLE);
            editText.setText(integerStringMap.get(((ColorDrawable)colorll1.getBackground()).getColor()));
        }
        else if(id == R.id.setting_color_ll_2){
            colorIv1.setVisibility(View.INVISIBLE);
            colorIv2.setVisibility(View.VISIBLE);
            colorIv3.setVisibility(View.INVISIBLE);
            colorIv4.setVisibility(View.INVISIBLE);
            colorIv5.setVisibility(View.INVISIBLE);
            editText.setText(integerStringMap.get(((ColorDrawable)colorll2.getBackground()).getColor()));
        }
        else if(id == R.id.setting_color_ll_3){
            colorIv1.setVisibility(View.INVISIBLE);
            colorIv2.setVisibility(View.INVISIBLE);
            colorIv3.setVisibility(View.VISIBLE);
            colorIv4.setVisibility(View.INVISIBLE);
            colorIv5.setVisibility(View.INVISIBLE);
            editText.setText(integerStringMap.get(((ColorDrawable)colorll3.getBackground()).getColor()));
        }

        else if(id == R.id.setting_color_ll_4){
            colorIv1.setVisibility(View.INVISIBLE);
            colorIv2.setVisibility(View.INVISIBLE);
            colorIv3.setVisibility(View.INVISIBLE);
            colorIv4.setVisibility(View.VISIBLE);
            colorIv5.setVisibility(View.INVISIBLE);
            editText.setText(integerStringMap.get(((ColorDrawable)colorll4.getBackground()).getColor()));
        }

        else if(id == R.id.setting_color_ll_5){
            colorIv1.setVisibility(View.INVISIBLE);
            colorIv2.setVisibility(View.INVISIBLE);
            colorIv3.setVisibility(View.INVISIBLE);
            colorIv4.setVisibility(View.INVISIBLE);
            colorIv5.setVisibility(View.VISIBLE);
            editText.setText(integerStringMap.get(((ColorDrawable)colorll5.getBackground()).getColor()));
        }
    }

    private void doneClick() {
        String written = editText.getText().toString();
        int color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.setting_color_ll_1)).getBackground()).getColor();;
        if(colorIv1.getVisibility() == View.VISIBLE){
            color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.setting_color_ll_1)).getBackground()).getColor();
        }
        else if(colorIv2.getVisibility() == View.VISIBLE){
            color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.setting_color_ll_2)).getBackground()).getColor();
        }
        else if(colorIv3.getVisibility() == View.VISIBLE){
            color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.setting_color_ll_3)).getBackground()).getColor();
        }
        else if(colorIv4.getVisibility() == View.VISIBLE){
            color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.setting_color_ll_4)).getBackground()).getColor();
        }
        else if(colorIv5.getVisibility() == View.VISIBLE){
            color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.setting_color_ll_5)).getBackground()).getColor();
        }

        DayContent dayContent = new DayContent();
        Map<Integer, String> map = dayContent.getColorStringPref(this.getContext(), colorkey);
        map.put(color,editText.getText().toString());
        dayContent.setColorStringPref(this.getContext(),colorkey, map);
        dayContent.updateSelectedDaysPrefByColor(this.getContext(),key,editText.getText().toString(),color);

        if (onSettingListener != null) {
            onSettingListener.OnSettingListener(dayContent);
        }

        dismiss();
    }
}
