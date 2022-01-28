package com.example.shift;

import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.OrientationHelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.shift.Dialog.OnSettingListener;
import com.example.shift.Dialog.SettingDialog;
import com.example.shift.cosmocalendar.adapter.MonthAdapter;
import com.example.shift.cosmocalendar.dialog.CalendarDialog;
import com.example.shift.cosmocalendar.dialog.OnDaysSelectionListener;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.utils.SelectionType;
import com.example.shift.cosmocalendar.view.CalendarView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private MonthAdapter monthAdapter;
    private DayContent dayContent_saving = new DayContent();
    private String key = "";
    private final int PERMISSION_NUM = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        ImageButton settingButton = findViewById(R.id.setting_button);
        FrameLayout calendarButton = findViewById(R.id.current_calendar);
        TextView calendarTextView = findViewById(R.id.current_calendar_tv);

        String date = new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis()));
        calendarTextView.setText(date);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setFirstDayOfWeek(SUNDAY);
        calendarView.setWeekendDays(new HashSet(){{add(SUNDAY); add(Calendar.SATURDAY);}});
        calendarView.setSelectionType(SelectionType.NONE);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
        calendarView.turnOnSyncCalendar();
        monthAdapter = calendarView.getMonthAdapter();
        List<DayContent> dayContents = dayContent_saving.getSelectedDaysPref(this, key);
        calendarView.setDayContents(dayContents);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingDialog settingDialog = new SettingDialog(view.getContext(), new OnSettingListener() {
                    @Override
                    public void OnSettingListener(DayContent dayContent) {
                        calendarView.setDayContents(dayContent_saving.getSelectedDaysPref(view.getContext(), key));
                    }
                });
                settingDialog.show();
            }
        });

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



}