package com.JH571121692Developer.shift.cosmocalendar.adapter.viewholder;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.JH571121692Developer.shift.Dialog.DayEventDialog;
import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.adapter.SyncDataAdapter;
import com.JH571121692Developer.shift.cosmocalendar.model.Day;
import com.JH571121692Developer.shift.cosmocalendar.selection.BaseSelectionManager;
import com.JH571121692Developer.shift.cosmocalendar.selection.JustShowSelectionManager;
import com.JH571121692Developer.shift.cosmocalendar.settings.appearance.ConnectedDayIconPosition;
import com.JH571121692Developer.shift.cosmocalendar.utils.CalendarSyncData;
import com.JH571121692Developer.shift.cosmocalendar.utils.CalendarUtils;
import com.JH571121692Developer.shift.cosmocalendar.utils.DayContent;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class DayHolder extends BaseDayHolder {
    //private CircleAnimationTextView ctvDay;
    public static final int IMPOSSIBLE_COLOR = -100;
    private TextView ctvDay;
    private TextView ctvText;
    private LinearLayout llDay;
    private ListView listView;
    private BaseSelectionManager selectionManager;
    private int backColor;
    private DayContent dayContent_saving;
    SimpleDateFormat simpleDateFormat;

    public DayHolder(View itemView, CalendarView calendarView) {
        super(itemView, calendarView);
        ctvDay = itemView.findViewById(R.id.tv_day_number);
        ctvText = itemView.findViewById(R.id.tv_day_text);
        llDay = itemView.findViewById(R.id.ll_day);
        listView = itemView.findViewById(R.id.listview_day_list);
        backColor = ((ColorDrawable) llDay.getBackground()).getColor();
        String key = "selectedDays";
        dayContent_saving = new DayContent();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    }

    public void setColorWithDrawable(View view, int color, int state){
        Drawable roundDrawable;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        int margin = Math.round(view.getContext().getResources().getDimension(R.dimen.day_text_padding));
        switch (state){
            case DayContent.ALONE :
                roundDrawable = view.getContext().getResources().getDrawable(R.drawable.round_border_for_daytext);
                layoutParams.setMargins(margin,0,margin,margin);
                break;
            case DayContent.START :
                roundDrawable = view.getContext().getResources().getDrawable(R.drawable.round_border_for_daytext_start);
                layoutParams.setMargins(margin,0,0,margin);
                break;
            case DayContent.KEEP :
                roundDrawable = view.getContext().getResources().getDrawable(R.drawable.round_border_for_daytext);
                layoutParams.setMargins(0,0,0,margin);
                break;
            case DayContent.END :
                roundDrawable = view.getContext().getResources().getDrawable(R.drawable.round_border_for_daytext_end);
                layoutParams.setMargins(0,0,margin,margin);
                break;
            default:
                roundDrawable = view.getContext().getResources().getDrawable(R.drawable.round_border_for_daytext);
                layoutParams.setMargins(margin,0,margin,margin);
                break;
        }
        if(state != DayContent.KEEP){
            roundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(roundDrawable);
            } else {
                view.setBackground(roundDrawable);
            }
        }
        else{
            view.setBackgroundColor(color);
        }
        if(state == DayContent.ALONE || state == DayContent.START)
            ((TextView) view).setTextColor(view.getContext().getResources().getColor(R.color.default_calendar_content_color));
        else
            ((TextView) view).setTextColor(color);
        view.setLayoutParams(layoutParams);
    }

    public void bind(Day day, BaseSelectionManager selectionManager) {
        this.selectionManager = selectionManager;
        ctvDay.setText(String.valueOf(day.getDayNumber()));
        ctvDay.setBackground(null);
        List<CalendarSyncData> syncDataList = day.getSyncDataList();
        if(syncDataList != null){
            final SyncDataAdapter syncDataAdapter = new SyncDataAdapter(listView.getContext(), syncDataList);
            listView.setAdapter(syncDataAdapter);
        }
        DayContent dayContent = day.getDayContent();
        if(dayContent != null){
            setColorWithDrawable(ctvText, dayContent.getContentColor(),day.getDayContent().getContentState());
            ctvText.setText(dayContent.getContentString());
        }

        boolean isSelected = selectionManager.isDaySelected(day);
        if (isSelected && !day.isDisabled()) {
            if(selectionManager instanceof JustShowSelectionManager)
                showInfo(day);
            else
                select(day);
        } else {
            unselect(day);
        }
        //선택하는 모드에도 listView가 보이면 listView에 해당하는 부분에 터치가 안 먹혀서 불편함
        if(!(selectionManager instanceof JustShowSelectionManager)){
            listView.setVisibility(View.GONE);
        }

        if (day.isCurrent()) {
            ctvDay.setBackground(ContextCompat.getDrawable(ctvDay.getContext(), R.drawable.round_button));
            ctvDay.setTextColor(ContextCompat.getColor(ctvDay.getContext(), R.color.default_border_color));
            //addCurrentDayIcon(isSelected);
        }

        if(day.isDisabled()){
            ctvDay.setTextColor(calendarView.getDisabledDayTextColor());
        }
    }

    private void addCurrentDayIcon(boolean isSelected){
        ctvDay.setCompoundDrawablePadding(getPadding(getCurrentDayIconHeight(isSelected)) * -1);
        ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, isSelected
                ? calendarView.getCurrentDaySelectedIconRes()
                : calendarView.getCurrentDayIconRes(), 0, 0);
    }

    private int getCurrentDayIconHeight(boolean isSelected){
        if (isSelected) {
            return CalendarUtils.getIconHeight(calendarView.getContext().getResources(), calendarView.getCurrentDaySelectedIconRes());
        } else {
            return CalendarUtils.getIconHeight(calendarView.getContext().getResources(), calendarView.getCurrentDayIconRes());
        }
    }

    private int getConnectedDayIconHeight(boolean isSelected){
        if (isSelected) {
            return CalendarUtils.getIconHeight(calendarView.getContext().getResources(), calendarView.getConnectedDaySelectedIconRes());
        } else {
            return CalendarUtils.getIconHeight(calendarView.getContext().getResources(), calendarView.getConnectedDayIconRes());
        }
    }

    private void showInfo(Day day){
        DayEventDialog dayEventDialog = new DayEventDialog(itemView.getContext(), day);
        dayEventDialog.show();
    }

    private void select(Day day) {
//        Log.d("click_test", "disabled : " + day.isDisabled() + " selected : " + day.isSelected());
        if (day.isFromConnectedCalendar()) {
            if(day.isDisabled()){
                ctvDay.setTextColor(day.getConnectedDaysDisabledTextColor());
            } else {
                ctvDay.setTextColor(day.getConnectedDaysSelectedTextColor());
            }
            addConnectedDayIcon(true);
        }
        else {
//            Log.d("click_test", "here");
            llDay.setBackgroundColor(calendarView.getSelectedDayBackgroundColor());
            ctvText.setBackgroundColor(calendarView.getSelectedDayBackgroundColor());
            ctvText.setText("");
            ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        if(day.isCurrent()){
//            Log.d("click_test", "isCUrrent");
            llDay.setBackgroundColor(calendarView.getSelectedDayBackgroundColor());
            ctvText.setBackgroundColor(calendarView.getSelectedDayBackgroundColor());
            ctvText.setText("");
            ctvDay.setBackground(null);
            ctvDay.setTextColor(ContextCompat.getColor(ctvDay.getContext(), R.color.default_day_text_color));
        }
        ctvDay.setTextColor(ContextCompat.getColor(ctvDay.getContext(), R.color.default_day_text_color));
    }

    private void unselect(Day day) {
        int textColor;
        if (day.isFromConnectedCalendar()) {
            if(day.isDisabled()){
                textColor = day.getConnectedDaysDisabledTextColor();
            } else {
                textColor = day.getConnectedDaysTextColor();
            }
            addConnectedDayIcon(false);
        }
        else if (day.isWeekend()) {
            if(day.getDayName() == Calendar.SATURDAY)
                textColor = ContextCompat.getColor(calendarView.getContext(), R.color.default_saturday_text_color);
            else
                textColor = calendarView.getWeekendDayTextColor();
            ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        else if (day.isCurrent()){
            ctvDay.setBackground(ContextCompat.getDrawable(ctvDay.getContext(), R.drawable.round_button));
            textColor = ContextCompat.getColor(ctvDay.getContext(), R.color.default_border_color);
        }
        else {
            textColor = calendarView.getDayTextColor();
            ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        day.setSelectionCircleDrawed(false);
        ctvDay.setTextColor(textColor);

        DayContent dayContent = day.getDayContent();
        int color = (dayContent != null) ? dayContent.getContentColor() : backColor;
        String content_string = (dayContent != null) ? dayContent.getContentString() : "";
        llDay.setBackgroundColor(backColor);
        int state = (dayContent != null) ? dayContent.getContentState() : DayContent.ALONE;
        setColorWithDrawable(ctvText, color, state);
//        ctvText.setBackgroundColor(color);
        ctvText.setText(content_string);

    }

    private void addConnectedDayIcon(boolean isSelected){
        ctvDay.setCompoundDrawablePadding(getPadding(getConnectedDayIconHeight(isSelected)) * -1);

        switch (calendarView.getConnectedDayIconPosition()){
            case ConnectedDayIconPosition.TOP:
                ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, isSelected
                        ? calendarView.getConnectedDaySelectedIconRes()
                        : calendarView.getConnectedDayIconRes(), 0, 0);
                break;

            case ConnectedDayIconPosition.BOTTOM:
                ctvDay.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, isSelected
                        ? calendarView.getConnectedDaySelectedIconRes()
                        : calendarView.getConnectedDayIconRes());
                break;
        }
    }


    private int getPadding(int iconHeight){
        return (int) (iconHeight * Resources.getSystem().getDisplayMetrics().density);
    }
}
