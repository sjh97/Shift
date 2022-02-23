package com.JH571121692Developer.shift.cosmocalendar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.Utils.SettingHelper;
import com.JH571121692Developer.shift.cosmocalendar.model.Day;
import com.JH571121692Developer.shift.cosmocalendar.model.Month;
import com.JH571121692Developer.shift.cosmocalendar.settings.appearance.AppearanceInterface;
import com.JH571121692Developer.shift.cosmocalendar.settings.date.DateInterface;
import com.JH571121692Developer.shift.cosmocalendar.settings.lists.CalendarListsInterface;
import com.JH571121692Developer.shift.cosmocalendar.settings.lists.DisabledDaysCriteria;
import com.JH571121692Developer.shift.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.JH571121692Developer.shift.cosmocalendar.settings.lists.connected_days.ConnectedDaysManager;
import com.JH571121692Developer.shift.cosmocalendar.settings.selection.SelectionInterface;
import com.JH571121692Developer.shift.cosmocalendar.utils.CalendarSyncData;
import com.JH571121692Developer.shift.cosmocalendar.utils.DayContent;
import com.JH571121692Developer.shift.cosmocalendar.utils.SelectionType;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CalendarDialog extends Dialog implements View.OnClickListener,
        AppearanceInterface, DateInterface, CalendarListsInterface, SelectionInterface {

    //Views
    private FrameLayout flNavigationButtonsBar;
    private ImageView ivCancel;
    private ImageView ivDone;
    private TextView tvHelpMention;
    private EditText editText;
    private CalendarView calendarView;
    private LinearLayout colorBunch;
    private List<Pair<Integer, String>> integerStringList = new ArrayList<>();
    String colorkey = "";
    String key = "";
    String settingkey = "";

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
//        Log.e("Shift", "onCreate");
        colorkey = getContext().getString(R.string.colorkey);
        key = getContext().getString(R.string.key);
        settingkey = getContext().getString(R.string.settingkey);
        initViews();
    }

    private void initViews() {
        flNavigationButtonsBar = (FrameLayout) findViewById(R.id.fl_navigation_buttons_bar);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        ivDone = (ImageView) findViewById(R.id.iv_done);
        editText = findViewById(R.id.edit_button);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        colorBunch = findViewById(R.id.colorBunch);
//        integerStringList = new DayContent().getColorStringPref(editText.getContext(), colorkey);
        integerStringList = new SettingHelper(getContext(), settingkey).getColorStringList();
        editText.setText(integerStringList.get(0).second);
        tvHelpMention = findViewById(R.id.tv_help_metion);

//        Drawable background = calendarView.getBackground();
        /*
        if (background instanceof ColorDrawable) {
            flNavigationButtonsBar.setBackgroundColor(((ColorDrawable) background).getColor());
        }
         */
        flNavigationButtonsBar.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.default_calendar_head_background_color));

        ivCancel.setOnClickListener(this);
        ivDone.setOnClickListener(this);
        for(int i=0;i<colorBunch.getChildCount();i++){
            colorBunch.getChildAt(i).setOnClickListener(this);
            colorBunch.getChildAt(i).setBackgroundColor(integerStringList.get(i).first);
        }
    }

    public void setSyncWithCalendarView(List<Month> months, int position){
        calendarView.setMonths(months);
        calendarView.setCurrentPostion(position);
    }

    public void setInvisibleIcon(){
        colorBunch.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
    }

    public void setVisibleIcon(){
        colorBunch.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
    }

    public void setTvHelpMention(String mention){
        tvHelpMention.setText(mention);
        tvHelpMention.setVisibility(View.VISIBLE);
    }


    public void setOnDaysSelectionListener(OnDaysSelectionListener onDaysSelectionListener) {
        this.onDaysSelectionListener = onDaysSelectionListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_cancel) {
            cancel();
        }
        else if (id == R.id.iv_done) {
            doneClick();
        }
        else{
            for(int i=0;i<colorBunch.getChildCount();i++){
                //colorview is colorll (R.id.color1_btn)
                LinearLayout colorView = (LinearLayout) colorBunch.getChildAt(i);
                if(id == colorView.getId()){
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
        List<Day> selectedDays = calendarView.getSelectedDays();
        String written = editText.getText().toString();
        int color = ((ColorDrawable) ((LinearLayout) findViewById(R.id.color1_btn)).getBackground()).getColor();;
        int id = 0;
        for(int i=0;i<colorBunch.getChildCount();i++){
            //colorview is colorll (R.id.color1_btn)
            LinearLayout colorView = (LinearLayout) colorBunch.getChildAt(i);
            if(colorView.getChildAt(0).getVisibility() == View.VISIBLE){
                color = ((ColorDrawable) colorView.getBackground()).getColor();
                id = i;
                //이 구문을 onDaysSelected 앞에 둬야 색깔 정보가 업데이트 되고 이후 onDaysSelected가 실행된다.
                DayContent dayContent = new DayContent();
                List<Pair<Integer, String>> integerStringList = new SettingHelper(getContext(), settingkey).getColorStringList();
                integerStringList.set(i,Pair.create(color,editText.getText().toString()));
                new SettingHelper(getContext(), settingkey).setColorStringList(integerStringList);
                dayContent.updateSelectedDaysPrefByColor(this.getContext(),key,editText.getText().toString(),color,id);
                break;
            }
        }

        if (onDaysSelectionListener != null) {
            onDaysSelectionListener.onDaysSelected(selectedDays, written, color, id);
        }

        dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doneClick();
    }

    @Override
    @SelectionType
    public int getSelectionType() {
        return calendarView.getSelectionType();
    }

    @Override
    public void setSelectionType(@SelectionType int selectionType) {
//        Log.e("Shift","selection");
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
