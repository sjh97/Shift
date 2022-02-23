package com.JH571121692Developer.shift.cosmocalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.Sync.GooglePlayService;
import com.JH571121692Developer.shift.Utils.SettingHelper;
import com.JH571121692Developer.shift.cosmocalendar.utils.CalendarSyncData;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by leonardo on 08/10/17.
 */

public class CalendarSyncAsyncTask extends AsyncTask<CalendarSyncAsyncTask.FetchParams, Void, List<CalendarSyncData>> {

    private CalendarView calendarView;
    private Activity activity;

    @Override
    protected List<CalendarSyncData> doInBackground(FetchParams... fetchParams) {
        FetchParams params = fetchParams[0];
        calendarView = params.calendarView;
        activity = params.activity;
        List<CalendarSyncData> syncDataList = calendarSync(activity);

        return syncDataList;
    }

    //synchronize default calendar.
    private List<CalendarSyncData> calendarSync2() {
        List<CalendarSyncData> syncDataList = new ArrayList<>();
        String myCalendarID =  new SettingHelper((Context) activity,((Context) activity).getString(R.string.settingkey)).getMyCalendarID();
        //https://zeph1e.tistory.com/42?category=338725
        //https://www.youtube.com/watch?v=GihhIgDYCNo
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.DISPLAY_COLOR,
                CalendarContract.Events.DURATION,
                CalendarContract.Events.RDATE,
                CalendarContract.Events.RRULE,
                CalendarContract.Events.CALENDAR_ID
        };

        Cursor cur = null;
        ContentResolver cr = ((Context) activity).getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, null, null, null);
        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            int id;
            String title = null;
            Date dtstart = null;
            Date dtend = null;
            boolean allDay;
            int color;
            int duration = 0;
            String rrule = null;
            String currentCalendarID = null;

            // Get the field values
            id = cur.getInt(0);
            title = cur.getString(1);
            dtstart = new Date(cur.getLong(2));
            dtend = new Date(cur.getLong(3));
            allDay = (cur.getInt(4) == 1) ? true : false;
            color = cur.getInt(5);
            //duration : 반복된 이벤트에서 그 이벤트가 며칠간인지
            String prev_duration = cur.getString(6);
            //rrule은 duration > 0이 아니면 null 임
            rrule = cur.getString(8);

            currentCalendarID = cur.getString(9);
            Log.d("Shift__", " currentCalendarID : " + currentCalendarID);
            if(currentCalendarID != null && myCalendarID != "")
                if(currentCalendarID.equals(myCalendarID))
                    continue;

            duration = (prev_duration != null) ? Integer.parseInt(prev_duration.split("\\D+")[1]) : 0;
            Log.d("Shift__", " _ID : " + id);
//            Log.d("Shift__", new SimpleDateFormat("yyyy-MM-dd").format(dtstart)
//                    + " Duration : " + prev_duration + " : " + duration);
            if (duration > 0) {
                Calendar calendar = Calendar.getInstance();
                int current_year = calendar.get(Calendar.YEAR);
                calendar.setTime(dtstart);
                //calendar.set(current_year, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dtstart = calendar.getTime();
                String[] rules = rrule.split(";|=");
                Log.d("Shift__", "After setting : " + new SimpleDateFormat("yyyy-MM-dd").format(dtstart));
                Log.d("Shift__", "rrule : " + rrule);
            }


            CalendarSyncData syncData = new CalendarSyncData(id, title, dtstart, dtend, allDay, color);
            syncDataList.add(syncData);
        }
        cur.close();
        return syncDataList;
    }

    private List<CalendarSyncData> calendarSync(Activity activity){
        new GooglePlayService(activity).getResultsFromApi(GooglePlayService.GET_EVENT);
        List<CalendarSyncData> syncDataList = new ArrayList<>();
        List<CalendarSyncData> calendarSyncDataList = new SettingHelper((Context) activity, ((Context) activity).getString(R.string.settingkey)).getCalendarSyncDataList();
        if(calendarSyncDataList == null){
            Log.d("TEST__","CalendarView : calendarSync2()");
             syncDataList = calendarSync2();
        }
        else{
            Log.d("TEST__","CalendarView : calendarSync()");
            syncDataList.addAll(calendarSyncDataList);
        }
        return syncDataList;
    }

    @Override
    protected void onPostExecute(List<CalendarSyncData> syncDataList) {
        if (!syncDataList.isEmpty()) {
            Log.d("TEST__","CalendarSyncAsyncTask : onPostExecute()");
            calendarView.setDaySyncData(syncDataList);
            calendarView.update();
        }
    }

    public static class FetchParams {
        private final CalendarView calendarView;
        private final Activity activity;

        public FetchParams(CalendarView calendarView, Activity activity) {
            this.calendarView = calendarView;
            this.activity = activity;
        }
    }
}
