package com.JH571121692Developer.shift.cosmocalendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.cosmocalendar.utils.CalendarSyncData;

import java.util.ArrayList;
import java.util.List;

public class SyncDataAdapter extends BaseAdapter {
    //https://lktprogrammer.tistory.com/163
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    List<CalendarSyncData> syncDataList = new ArrayList<>();

    public SyncDataAdapter(Context context, List<CalendarSyncData> syncDataList) {
        this.mContext = context;
        this.syncDataList = syncDataList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return syncDataList.size();
    }

    @Override
    public CalendarSyncData getItem(int position) {
        return syncDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.view_day_list, null);

        TextView title = (TextView)view.findViewById(R.id.list_item_text);
        title.setText(syncDataList.get(position).getTitle());
        title.setBackgroundColor(syncDataList.get(position).getColor());

        return view;
    }
}
