package com.JH571121692Developer.shift.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.JH571121692Developer.shift.R;
import java.util.ArrayList;
import java.util.List;

public class InfoDialog extends Dialog {
    private ListView listView;
    public InfoDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_info);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.6);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        getWindow().setBackgroundDrawableResource(R.drawable.round_border);
        getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;
        initViews();
    }

    private void initViews() {
        listView = findViewById(R.id.info_listview);
        List<String> contentList = new ArrayList<>();
        contentList.add("App : made by JH571121692Developer");
        contentList.add("App Logo : designed by petaine");
        contentList.add("Design Idea : provided by jiwon");
        ContentAdapter contentAdapter = new ContentAdapter(getContext(), contentList);
        listView.setAdapter(contentAdapter);

    }

    private class ContentAdapter extends BaseAdapter {

        private List<String> contentList;
        private Context mContext = null;
        private LayoutInflater mLayoutInflater = null;

        public ContentAdapter( Context mContext, List<String> contentList) {
            this.contentList = contentList;
            this.mContext = mContext;
            this.mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return contentList.size();
        }

        @Override
        public Object getItem(int i) {
            return contentList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            View view = mLayoutInflater.inflate(R.layout.item_dialog_info, viewGroup,false);
            TextView textView = view.findViewById(R.id.item_dialog_info_tv);
            textView.setText(contentList.get(i));

            return view;
        }
    }
}
