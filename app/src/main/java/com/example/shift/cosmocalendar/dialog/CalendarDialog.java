package com.example.shift.cosmocalendar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.shift.R;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.settings.appearance.AppearanceInterface;
import com.example.shift.cosmocalendar.settings.date.DateInterface;
import com.example.shift.cosmocalendar.settings.lists.CalendarListsInterface;
import com.example.shift.cosmocalendar.settings.lists.DisabledDaysCriteria;
import com.example.shift.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.example.shift.cosmocalendar.settings.lists.connected_days.ConnectedDaysManager;
import com.example.shift.cosmocalendar.settings.selection.SelectionInterface;
import com.example.shift.cosmocalendar.utils.CalendarSyncData;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.utils.SelectionType;
import com.example.shift.cosmocalendar.view.CalendarView;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarDialog extends Dialog implements View.OnClickListener,
        AppearanceInterface, DateInterface, CalendarListsInterface, SelectionInterface {

    //Views
    private FrameLayout flNavigationButtonsBar;
    private ImageView ivCancel;
    private ImageView ivDone;
    private EditText editText;
    private CalendarView calendarView;
    private ImageView colorIv1;
    private ImageView colorIv2;
    private ImageView colorIv3;
    private LinearLayout colorll1;
    private LinearLayout colorll2;
    private LinearLayout colorll3;
    private Map<Integer, String> integerStringMap;
    String colorkey = "";
    String key = "";

    private OnDaysSelectionListener onDaysSelectionListener;

    public CalendarDialog(@NonNull Context context) {
        super(context);
    }

    public CalendarDialog(@NonNull Context context, OnDaysSelectionListener onDaysSelectionListener) {
        super(context);
        this.onDaysSelectionListener = onDaysSelectionListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_calendar);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().gravity = Gravity.TOP;
        Log.e("Shift", "onCreate");
        colorkey = getContext().getString(R.string.colorkey);
        key = getContext().getString(R.string.key);
        initViews();
    }

    private void initViews() {
        flNavigationButtonsBar = (FrameLayout) findViewById(R.id.fl_navigation_buttons_bar);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        ivDone = (ImageView) findViewById(R.id.iv_done);
        editText = findViewById(R.id.edit_button);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        colorIv1 = findViewById(R.id.color1_ibtn);
        colorIv2 = findViewById(R.id.color2_ibtn);
        colorIv3 = findViewById(R.id.color3_ibtn);
        colorll1 = findViewById(R.id.color1_btn);
        colorll2 = findViewById(R.id.color2_btn);
        colorll3 = findViewById(R.id.color3_btn);
        integerStringMap = new DayContent().getColorStringPref(editText.getContext(), colorkey);
        editText.setText(integerStringMap.get(((ColorDrawable)colorll1.getBackground()).getColor()));

//        Drawable background = calendarView.getBackground();
        /*
        if (background instanceof ColorDrawable) {
            flNavigationButtonsBar.setBackgroundColor(((ColorDrawable) background).getColor());
        }
         */
        flNavigationButtonsBar.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.default_calendar_head_background_color));

        ivCancel.setOnClickListener(this);
        ivDone.setOnClickListener(this);
        colorll1.setOnClickListener(this);
        colorll2.setOnClickListener(this);
        colorll3.setOnClickListener(this);

    }

    public void setInvisibleIcon(){
        colorll1.setVisibility(View.INVISIBLE);
        colorll2.setVisibility(View.INVISIBLE);
        colorll3.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
    }

    public void setVisibleIcon(){
        colorll1.setVisibility(View.VISIBLE);
        colorll2.setVisibility(View.VISIBLE);
        colorll3.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
    }


    public void setOnDaysSelectionListener(OnDaysSelectionListener onDaysSelectionListener) {
        this.onDaysSelectionListener = onDaysSelectionListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_cancel) {
            cancel();
        } else if (id == R.id.iv_done) {
            doneClick();
        }
        else if(id == R.id.color1_btn){
            colorIv1.setVisibility(View.VISIBLE);
            colorIv2.setVisibility(View.INVISIBLE);
            colorIv3.setVisibility(View.INVISIBLE);
            editText.setText(integerStringMap.get(((ColorDrawable)colorll1.getBackground()).getColor()));
        }
        else if(id == R.id.color2_btn){
            colorIv1.setVisibility(View.INVISIBLE);
            colorIv2.setVisibility(View.VISIBLE);
            colorIv3.setVisibility(View.INVISIBLE);
            editText.setText(integerStringMap.get(((ColorDrawable)colorll2.getBackground()).getColor()));
        }
        else if(id == R.id.color3_btn){
            colorIv1.setVisibility(View.INVISIBLE);
            colorIv2.setVisibility(View.INVISIBLE);
            colorIv3.setVisibility(View.VISIBLE);
            editText.setText(integerStringMap.get(((ColorDrawable)colorll3.getBackground()).getColor()));
        }
    }

    private void doneClick() {
        List<Day> selectedDays = calendarView.getSelectedDays();
        String written = editText.getText().toString();
        int color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.color1_btn)).getBackground()).getColor();;
        if(colorIv1.getVisibility() == View.VISIBLE){
            color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.color1_btn)).getBackground()).getColor();
        }
        else if(colorIv2.getVisibility() == View.VISIBLE){
            color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.color2_btn)).getBackground()).getColor();
        }
        else if(colorIv3.getVisibility() == View.VISIBLE){
            color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.color3_btn)).getBackground()).getColor();
        }
        //이 구문을 onDaysSelected 앞에 둬야 색깔 정보가 업데이트 되고 이후 onDaysSelected가 실행된다.
        DayContent dayContent = new DayContent();
        Map<Integer, String> map = dayContent.getColorStringPref(this.getContext(), colorkey);
        map.put(color,editText.getText().toString());
        dayContent.setColorStringPref(this.getContext(),colorkey, map);
        dayContent.updateSelectedDaysPrefByColor(this.getContext(),key,editText.getText().toString(),color);

        if (onDaysSelectionListener != null) {
            onDaysSelectionListener.onDaysSelected(selectedDays, written, color);
        }

        dismiss();
    }


    @Override
    @SelectionType
    public int getSelectionType() {
        return calendarView.getSelectionType();
    }

    @Override
    public void setSelectionType(@SelectionType int selectionType) {
        Log.e("Shift","selection");
        calendarView.setSelectionType(selectionType);
    }

    @Override
    public int getCalendarBackgroundColor() {
        return calendarView.getCalendarBackgroundColor();
    }

    @Override
    public int getMonthTextColor() {
        return calendarView.getMonthTextColor();
    }

    @Override
    public int getOtherDayTextColor() {
        return calendarView.getOtherDayTextColor();
    }

    @Override
    public int getDayTextColor() {
        return calendarView.getDayTextColor();
    }

    @Override
    public int getWeekendDayTextColor() {
        return calendarView.getWeekendDayTextColor();
    }

    @Override
    public int getWeekDayTitleTextColor() {
        return calendarView.getWeekDayTitleTextColor();
    }

    @Override
    public int getSelectedDayTextColor() {
        return calendarView.getSelectedDayTextColor();
    }

    @Override
    public int getSelectedDayBackgroundColor() {
        return calendarView.getSelectedDayBackgroundColor();
    }

    @Override
    public int getSelectedDayBackgroundStartColor() {
        return calendarView.getSelectedDayBackgroundStartColor();
    }

    @Override
    public int getSelectedDayBackgroundEndColor() {
        return calendarView.getSelectedDayBackgroundEndColor();
    }

    @Override
    public int getCurrentDayTextColor() {
        return calendarView.getCurrentDayTextColor();
    }

    @Override
    public int getCurrentDayIconRes() {
        return calendarView.getCurrentDayIconRes();
    }

    @Override
    public int getCurrentDaySelectedIconRes() {
        return calendarView.getCurrentDaySelectedIconRes();
    }

    @Override
    public int getCalendarOrientation() {
        return calendarView.getCalendarOrientation();
    }

    @Override
    public int getConnectedDayIconRes() {
        return calendarView.getConnectedDayIconRes();
    }

    @Override
    public int getConnectedDaySelectedIconRes() {
        return calendarView.getConnectedDaySelectedIconRes();
    }

    @Override
    public int getConnectedDayIconPosition() {
        return calendarView.getConnectedDayIconPosition();
    }

    @Override
    public int getDisabledDayTextColor() {
        return calendarView.getDisabledDayTextColor();
    }

    @Override
    public int getSelectionBarMonthTextColor() {
        return calendarView.getSelectionBarMonthTextColor();
    }

    @Override
    public int getPreviousMonthIconRes() {
        return calendarView.getPreviousMonthIconRes();
    }

    @Override
    public int getNextMonthIconRes() {
        return calendarView.getNextMonthIconRes();
    }

    @Override
    public boolean isShowDaysOfWeek() {
        return calendarView.isShowDaysOfWeek();
    }

    @Override
    public boolean isShowDaysOfWeekTitle() {
        return calendarView.isShowDaysOfWeekTitle();
    }

    @Override
    public void setCalendarBackgroundColor(int calendarBackgroundColor) {
        calendarView.setCalendarBackgroundColor(calendarBackgroundColor);
    }

    @Override
    public void setMonthTextColor(int monthTextColor) {
        calendarView.setMonthTextColor(monthTextColor);
    }

    @Override
    public void setOtherDayTextColor(int otherDayTextColor) {
        calendarView.setOtherDayTextColor(otherDayTextColor);
    }

    @Override
    public void setDayTextColor(int dayTextColor) {
        calendarView.setDayTextColor(dayTextColor);
    }

    @Override
    public void setWeekendDayTextColor(int weekendDayTextColor) {
        calendarView.setWeekendDayTextColor(weekendDayTextColor);
    }

    @Override
    public void setWeekDayTitleTextColor(int weekDayTitleTextColor) {
        calendarView.setWeekDayTitleTextColor(weekDayTitleTextColor);
    }

    @Override
    public void setSelectedDayTextColor(int selectedDayTextColor) {
        calendarView.setSelectedDayTextColor(selectedDayTextColor);
    }

    @Override
    public void setSelectedDayBackgroundColor(int selectedDayBackgroundColor) {
        calendarView.setSelectedDayBackgroundColor(selectedDayBackgroundColor);
    }

    @Override
    public void setSelectedDayBackgroundStartColor(int selectedDayBackgroundStartColor) {
        calendarView.setSelectedDayBackgroundStartColor(selectedDayBackgroundStartColor);
    }

    @Override
    public void setSelectedDayBackgroundEndColor(int selectedDayBackgroundEndColor) {
        calendarView.setSelectedDayBackgroundEndColor(selectedDayBackgroundEndColor);
    }

    @Override
    public void setCurrentDayTextColor(int currentDayTextColor) {
        calendarView.setCurrentDayTextColor(currentDayTextColor);
    }

    @Override
    public void setCurrentDayIconRes(int currentDayIconRes) {
        calendarView.setCurrentDayIconRes(currentDayIconRes);
    }

    @Override
    public void setCurrentDaySelectedIconRes(int currentDaySelectedIconRes) {
        calendarView.setCurrentDaySelectedIconRes(currentDaySelectedIconRes);
    }

    @Override
    public void setCalendarOrientation(int calendarOrientation) {
        calendarView.setCalendarOrientation(calendarOrientation);
    }

    @Override
    public void setConnectedDayIconRes(int connectedDayIconRes) {
        calendarView.setConnectedDayIconRes(connectedDayIconRes);
    }

    @Override
    public void setConnectedDaySelectedIconRes(int connectedDaySelectedIconRes) {
        calendarView.setConnectedDaySelectedIconRes(connectedDaySelectedIconRes);
    }

    @Override
    public void setConnectedDayIconPosition(int connectedDayIconPosition) {
        calendarView.setConnectedDayIconPosition(connectedDayIconPosition);
    }

    @Override
    public void setDisabledDayTextColor(int disabledDayTextColor) {
        calendarView.setDisabledDayTextColor(disabledDayTextColor);
    }

    @Override
    public void setSelectionBarMonthTextColor(int selectionBarMonthTextColor) {
        calendarView.setSelectionBarMonthTextColor(selectionBarMonthTextColor);
    }

    @Override
    public void setPreviousMonthIconRes(int previousMonthIconRes) {
        calendarView.setPreviousMonthIconRes(previousMonthIconRes);
    }

    @Override
    public void setNextMonthIconRes(int nextMonthIconRes) {
        calendarView.setNextMonthIconRes(nextMonthIconRes);
    }

    @Override
    public void setShowDaysOfWeek(boolean showDaysOfWeek) {
        calendarView.setShowDaysOfWeek(showDaysOfWeek);
    }

    @Override
    public void setShowDaysOfWeekTitle(boolean showDaysOfWeekTitle) {
        calendarView.setShowDaysOfWeekTitle(showDaysOfWeekTitle);
    }

    @Override
    public Set<Long> getDisabledDays() {
        return calendarView.getDisabledDays();
    }

    @Override
    public List<DayContent> getDayContents() {
        return calendarView.getDayContents();
    }

    @Override
    public List<CalendarSyncData> getDaySyncData() {
        return calendarView.getDaySyncData();
    }

    @Override
    public ConnectedDaysManager getConnectedDaysManager() {
        return calendarView.getConnectedDaysManager();
    }

    @Override
    public Set<Long> getWeekendDays() {
        return calendarView.getWeekendDays();
    }

    @Override
    public DisabledDaysCriteria getDisabledDaysCriteria() {
        return calendarView.getDisabledDaysCriteria();
    }

    @Override
    public void setDisabledDays(Set<Long> disabledDays) {
        calendarView.setDisabledDays(disabledDays);
    }

    @Override
    public void setDayContents(List<DayContent> dayContents) {
        calendarView.setDayContents(dayContents);
    }

    @Override
    public void setDaySyncData(List<CalendarSyncData> syncDataList) {
        calendarView.setDaySyncData(syncDataList);
    }

    @Override
    public void setWeekendDays(Set<Long> weekendDays) {
        calendarView.setWeekendDays(weekendDays);
    }

    @Override
    public void setDisabledDaysCriteria(DisabledDaysCriteria criteria) {
        calendarView.setDisabledDaysCriteria(criteria);
    }

    @Override
    public void addConnectedDays(ConnectedDays connectedDays) {
        calendarView.addConnectedDays(connectedDays);
    }

    @Override
    public int getFirstDayOfWeek() {
        return calendarView.getFirstDayOfWeek();
    }

    @Override
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        calendarView.setFirstDayOfWeek(firstDayOfWeek);
    }
}
