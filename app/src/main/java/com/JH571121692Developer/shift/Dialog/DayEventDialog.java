package com.JH571121692Developer.shift.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.model.Day;
import com.JH571121692Developer.shift.cosmocalendar.utils.CalendarSyncData;

import java.text.SimpleDateFormat;
import java.util.List;

public class DayEventDialog extends Dialog {
    private Day day;
    private TextView dateTextView;
    private TextView shiftTextview;
    private ImageView shiftCircleview;
    private ListView calendarListView;
    public DayEventDialog(@NonNull Context context, Day day) {
        super(context);
        this.day = day;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_dayevent);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.6);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        getWindow().setBackgroundDrawableResource(R.drawable.round_border);
        getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;
        initViews();
    }

    private void initViews() {
        dateTextView = findViewById(R.id.eventdialog_date);
        shiftTextview = findViewById(R.id.eventdialog_shift);
        shiftCircleview = findViewById(R.id.eventdialog_shift_circle);
        calendarListView = findViewById(R.id.eventdialog_listview);
        dateTextView.setText(new SimpleDateFormat("MM월 dd일").format(day.getCalendar().getTime()));
        if(day.getDayContent() != null){

            Drawable roundDrawable = getContext().getResources().getDrawable(R.drawable.circle_border);
            roundDrawable.setColorFilter(day.getDayContent().getContentColor(), PorterDuff.Mode.SRC_ATOP);
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                shiftCircleview.setBackgroundDrawable(roundDrawable);
            } else {
                shiftCircleview.setBackground(roundDrawable);
            }
            shiftTextview.setText(day.getDayContent().getContentString());
        }
        else{
            shiftCircleview.setVisibility(View.INVISIBLE);
        }
        if(day.getSyncDataList() != null){
            DayEventAdapter dayEventAdapter = new DayEventAdapter(getContext(), day.getSyncDataList());
            calendarListView.setAdapter(dayEventAdapter);
        }

    }

    private class DayEventAdapter extends BaseAdapter {

        private List<CalendarSyncData> syncDataList;
        private Context mContext = null;
        private LayoutInflater mLayoutInflater = null;

        public DayEventAdapter( Context mContext, List<CalendarSyncData> syncDataAdapterList) {
            this.syncDataList = syncDataAdapterList;
            this.mContext = mContext;
            this.mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return syncDataList.size();
        }

        @Override
        public Object getItem(int i) {
            return syncDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            View view = mLayoutInflater.inflate(R.layout.item_dialog_dayevent, viewGroup,false);
            TextView textView = view.findViewById(R.id.item_dialog_dayEvent_tv);
            ImageView imageView = view.findViewById(R.id.item_dialog_dayEvent_circle);
            textView.setText(syncDataList.get(i).getTitle());

            Drawable roundDrawable = getContext().getResources().getDrawable(R.drawable.circle_border);
            roundDrawable.setColorFilter(syncDataList.get(i).getColor(), PorterDuff.Mode.SRC_ATOP);
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackgroundDrawable(roundDrawable);
            } else {
                imageView.setBackground(roundDrawable);
            }

            return view;
        }
    }
}
