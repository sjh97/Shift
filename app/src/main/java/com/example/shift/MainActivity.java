package com.example.shift;

import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.OrientationHelper;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shift.cosmocalendar.adapter.MonthAdapter;
import com.example.shift.cosmocalendar.dialog.CalendarDialog;
import com.example.shift.cosmocalendar.dialog.OnDaysSelectionListener;
import com.example.shift.cosmocalendar.listeners.OnMonthChangeListener;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.model.Month;
import com.example.shift.cosmocalendar.settings.SettingsManager;
import com.example.shift.cosmocalendar.utils.CalendarSyncData;
import com.example.shift.cosmocalendar.utils.DateUtils;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.utils.SelectionType;
import com.example.shift.cosmocalendar.utils.WeekDay;
import com.example.shift.cosmocalendar.view.CalendarView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private MonthAdapter monthAdapter;
    private DayContent dayContent_saving;
    private String key = "";
    private final int PERMISSION_NUM = 1000;
    private List<CalendarSyncData> syncDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dayContent_saving = new DayContent();
        key = getString(R.string.key);
        initViews();

        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission1 == PackageManager.PERMISSION_DENIED ||
                permission2 == PackageManager.PERMISSION_DENIED ||
                    permission3 == PackageManager.PERMISSION_DENIED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR, Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_NUM);
            }
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_NUM){
            boolean check_result = true;

            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }
            //권한체크에 동의를 하지 않으면 안드로이드 종료
            if(check_result == true){
                initViews();
            }
            else{
                finish();
            }
        }
    }

    private void initViews(){
        ImageButton doubleButton = findViewById(R.id.double_button);
        ImageButton deleteButton = findViewById(R.id.delete_button);
        ImageButton shareButton = findViewById(R.id.share_button);
        FrameLayout calendarButton = findViewById(R.id.current_calendar);
        TextView calendarTextView = findViewById(R.id.current_calendar_tv);

        calendarSync();

        String date = new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis()));
        calendarTextView.setText(date);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setFirstDayOfWeek(SUNDAY);
        calendarView.setWeekendDays(new HashSet(){{add(SUNDAY); add(Calendar.SATURDAY);}});
        calendarView.setSelectionType(SelectionType.NONE);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
        calendarView.setDaySyncData(syncDataList);
        Log.e("Shift___", "MainActivity : initView : syncDataList.size() : " + syncDataList.size());
        monthAdapter = calendarView.getMonthAdapter();
        List<DayContent> dayContents = dayContent_saving.getSelectedDaysPref(this, key);
        calendarView.setDayContents(dayContents);

        doubleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.setSelectionType(SelectionType.MULTIPLE);
                CalendarDialog calendarDialog= new CalendarDialog(view.getContext(), new OnDaysSelectionListener() {
                    @Override
                    public void onDaysSelected(List<Day> selectedDays, String written, int color) {
                        if(!selectedDays.isEmpty()){
                            for(Day selectedDay : selectedDays){
                                selectedDay.setDayContent(new DayContent(color, written, selectedDay.getCalendar().getTime()));
                            }
                        }
                        dayContent_saving.setSelectedDaysPref(calendarView.getContext(), key, selectedDays, written, color);
                        calendarView.setDayContents(dayContent_saving.getSelectedDaysPref(calendarView.getContext(), key));
                        calendarView.update("hello");

//                        reload();
                    }
                });
                calendarDialog.show();
                calendarDialog.setSelectionType(SelectionType.MULTIPLE);
                calendarDialog.setCalendarOrientation(OrientationHelper.HORIZONTAL);
                calendarDialog.setFirstDayOfWeek(SUNDAY);
                calendarDialog.setVisibleIcon();
                calendarDialog.setWeekendDays(new HashSet(){{add(SUNDAY); add(Calendar.SATURDAY);}});
                calendarDialog.setDayContents(dayContent_saving.getSelectedDaysPref(calendarView.getContext(), key));
                calendarView.setSelectionType(SelectionType.NONE);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarDialog calendarDialog = new CalendarDialog(view.getContext(), new OnDaysSelectionListener() {
                    @Override
                    public void onDaysSelected(List<Day> selectedDays, String written, int color) {
                        if(!selectedDays.isEmpty()){
                            for(Day selectedDay : selectedDays){
                                selectedDay.setDayContent(new DayContent(color, written, selectedDay.getCalendar().getTime()));
                            }
                        }
                        dayContent_saving.deleteSelectedDaysPref(calendarView.getContext(), key, selectedDays, written, color);
                        calendarView.setDayContents(dayContent_saving.getSelectedDaysPref(calendarView.getContext(), key));
                        monthAdapter.updateDayContentListItems(dayContent_saving.getSelectedDaysPref(calendarView.getContext(), key));
//                        calendarView.update("hello");
//                        reload();
                    }
                });
                calendarDialog.show();
                //show()를 먼저 해야 calendarview가 생성이 되어 null 참조가 아니게 되네...
                calendarDialog.setSelectionType(SelectionType.MULTIPLE);
                calendarDialog.setCalendarOrientation(OrientationHelper.HORIZONTAL);
                calendarDialog.setFirstDayOfWeek(SUNDAY);
                calendarDialog.setInvisibleIcon();
                calendarDialog.setWeekendDays(new HashSet(){{add(SUNDAY); add(Calendar.SATURDAY);}});
                calendarDialog.setDayContents(dayContent_saving.getSelectedDaysPref(calendarView.getContext(), key));
                calendarView.setSelectionType(SelectionType.NONE);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //https://gamjatwigim.tistory.com/m/14
                View rootView = calendarView;
                rootView.setDrawingCacheEnabled(true);
                Bitmap captureView = rootView.getDrawingCache();
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), captureView, "title", null);
                Uri bitmapUri = Uri.parse(bitmapPath);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                rootView.setDrawingCacheEnabled(false);
                captureView.recycle();
                captureView = null;
                startActivity(intent);
            }
        });


        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.backToCurrentDay();
            }
        });


    }


    private void calendarSync() {
        //https://zeph1e.tistory.com/42?category=338725
        //https://www.youtube.com/watch?v=GihhIgDYCNo
        syncDataList = new ArrayList<>();
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.DISPLAY_COLOR
        };

        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, null, null, null);
        Log.e("Shift_calendar", "calendarSync");
        // Use the cursor to step through the returned records
        while(cur.moveToNext()){
            int id;
            String title = null;
            Date dtstart = null;
            Date dtend = null;
            boolean allDay;
            int color;

            // Get the field values
            id = cur.getInt(0);
            title = cur.getString(1);
            dtstart = new Date(cur.getLong(2));
            dtend = new Date(cur.getLong(3));
            allDay = (cur.getInt(4)==1) ? true : false;
            color = cur.getInt(5);
            Log.e("Shift___", "color : " + color);

            CalendarSyncData syncData = new CalendarSyncData(id, title, dtstart, dtend, allDay, color);
            syncDataList.add(syncData);
        }

        /*
        Cursor cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            if(cursor!=null){
                int id_1 = cursor.getColumnIndex(CalendarContract.Events._ID);
                int id_2 = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                int id_3 = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
                String idValue = cursor.getColumnName(id_1);
                String titleValue = cursor.getString(id_2);
                String descriptionValue = cursor.getString(id_3);
                Log.e("Shift_calendar", "idValue : " + idValue);
                Log.e("Shift_calendar", "titleValue : " + titleValue);
                Log.e("Shift_calendar", "descriptionValue : " + descriptionValue);
            }
        }
        */
        cur.close();
    }

    private void reload(){
        calendarView.setFirstDayOfWeek(SUNDAY);
        List<DayContent> dayContents = dayContent_saving.getSelectedDaysPref(this, key);
//        calendarView.setDayContents(dayContents);
        calendarView.setDaySyncData(syncDataList);
    }
}