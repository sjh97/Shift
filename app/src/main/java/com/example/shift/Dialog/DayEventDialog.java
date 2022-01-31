package com.example.shift.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.shift.R;
import com.example.shift.cosmocalendar.adapter.SyncDataAdapter;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.utils.CalendarSyncData;
import com.example.shift.cosmocalendar.utils.DayContent;

import java.text.SimpleDateFormat;
import java.util.List;

public class DayEventDialog extends Dialog {
    private Day day;
    private TextView dateTextView;
    private TextView shiftTextview;
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
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getAttributes().gravity = Gravity.TOP;
        initViews();
    }

    private void initViews() {
        dateTextView = findViewById(R.id.eventdialog_date);
        shiftTextview = findViewById(R.id.eventdialog_shift);
        calendarListView = findViewById(R.id.eventdialog_listview);
        dateTextView.setText(new SimpleDateFormat("MM월 dd일").format(day.getCalendar().getTime()));
        if(day.getDayContent() != null){
            shiftTextview.setText(day.getDayContent().getContentString());
            shiftTextview.setBackgroundColor(day.getDayContent().getContentColor());
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
            LinearLayout linearLayout = view.findViewById(R.id.item_dialog_dayEvent_ll);
            TextView textView = view.findViewById(R.id.item_dialog_dayEvent_tv);
            textView.setText(syncDataList.get(i).getTitle());
            linearLayout.setBackgroundColor(syncDataList.get(i).getColor());
            return view;
        }
    }
}
