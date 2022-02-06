package com.example.shift;

import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.getInstance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.OrientationHelper;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shift.Dialog.OnSettingListener;
import com.example.shift.Dialog.SettingDialog;
import com.example.shift.Sync.GooglePlayService;
import com.example.shift.Utils.SettingHelper;
import com.example.shift.cosmocalendar.adapter.MonthAdapter;
import com.example.shift.cosmocalendar.dialog.CalendarDialog;
import com.example.shift.cosmocalendar.dialog.OnDaysSelectionListener;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.utils.SelectionType;
import com.example.shift.cosmocalendar.view.CalendarView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private MonthAdapter monthAdapter;
    private DayContent dayContent_saving = new DayContent();
    private String key = "";
    private String settingkey = "";
    private SettingHelper settingHelper;

    private com.google.api.services.calendar.Calendar mService = null;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private GooglePlayService googlePlayService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        key = getString(R.string.key);
        settingkey = getString(R.string.settingkey);
        settingHelper = new SettingHelper(this, settingkey);
        googlePlayService = new GooglePlayService((Activity) this);

        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
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
//        calendarView.setSelectionType(SelectionType.JUST_SHOW_INFO);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);

        if(settingHelper.isImport()){
            calendarView.turnOnSyncCalendar();
        }
        monthAdapter = calendarView.getMonthAdapter();
        List<DayContent> dayContents = dayContent_saving.getSelectedDaysPref(this, key);
        calendarView.setDayContents(dayContents);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingDialog settingDialog = new SettingDialog(view.getContext(), MainActivity.this,
                        calendarView, googlePlayService, settingHelper, new OnSettingListener() {
                    @Override
                    public void OnSettingListener(List<Pair<Integer, String>> beforeintegerStringList, List<Pair<Integer, String>> integerStringList) {
                        dayContent_saving.updateSelectedDaysPrefByColor(view.getContext(), key, integerStringList);
                        calendarView.setDayContents(dayContent_saving.getSelectedDaysPref(view.getContext(),key));
                        settingHelper = new SettingHelper(view.getContext(), settingkey);
                        Log.d("TEST__","" + "settingHelper.isExport() : " + settingHelper.isExport());
                        boolean isDiff = false;
                        for(int i = 0;i<beforeintegerStringList.size();i++){
                            Pair<Integer,String> before = beforeintegerStringList.get(i);
                            Pair<Integer,String> after = integerStringList.get(i);
                            Log.d("TEST__","" + "settingButton : " + before.first + " : " + after.first + " : " + (before.first - after.first != 0));
                            Log.d("TEST__","" + "settingButton : " + before.second + " : " + after.second + " : " + (!before.second.replace(after.second,"").equals("")));
                            if(!before.second.replace(after.second,"").equals("")){
                                isDiff = true;
                                break;
                            }
                        }
                        boolean restart = (settingHelper.getBeforeSyncDayContentList().size() == 0) && settingHelper.isExport();
                        if((settingHelper.isExport() && isDiff) || restart)
                            googlePlayService.getResultsFromApi(2);
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
                    public void onDaysSelected(List<Day> selectedDays, String written, int color, int id) {
                        if(!selectedDays.isEmpty()){
                            for(Day selectedDay : selectedDays){
                                selectedDay.setDayContent(new DayContent(color, written, selectedDay.getCalendar().getTime(), id));
                            }
                        }
//                        dayContent_saving.setSelectedDaysPref(calendarView.getContext(), key, selectedDays, written, color);
                        dayContent_saving.setSelectedDaysPref(calendarView.getContext(), key, selectedDays, written, color, id);
                        calendarView.setDayContents(dayContent_saving.getSelectedDaysPref(calendarView.getContext(), key));
                        if(settingHelper.isExport())
                            googlePlayService.getResultsFromApi(2);
                    }
                });
                calendarDialog.show();
                calendarDialog.setSelectionType(SelectionType.MULTIPLE);
                calendarDialog.setCalendarOrientation(OrientationHelper.HORIZONTAL);
                calendarDialog.setFirstDayOfWeek(SUNDAY);
                calendarDialog.setVisibleIcon();
                calendarDialog.setWeekendDays(new HashSet(){{add(SUNDAY); add(Calendar.SATURDAY);}});
                calendarDialog.setDayContents(dayContent_saving.getSelectedDaysPref(calendarView.getContext(), key));
                //calendarDialog.setSyncWithCalendarView(calendarView.getMonths(), calendarView.getCurrentPosition());
                calendarView.setSelectionType(SelectionType.NONE);
//                calendarView.setSelectionType(SelectionType.JUST_SHOW_INFO);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarDialog calendarDialog = new CalendarDialog(view.getContext(), new OnDaysSelectionListener() {
                    @Override
                    public void onDaysSelected(List<Day> selectedDays, String written, int color, int id) {
                        if(!selectedDays.isEmpty()){
                            for(Day selectedDay : selectedDays){
                                selectedDay.setDayContent(new DayContent(color, written, selectedDay.getCalendar().getTime(),id));
                            }
                        }
                        dayContent_saving.deleteSelectedDaysPref(calendarView.getContext(), key, selectedDays, written, color);
                        calendarView.setDayContents(dayContent_saving.getSelectedDaysPref(calendarView.getContext(), key));
                        if(settingHelper.isExport())
                            googlePlayService.getResultsFromApi(2);
                    }
                });
                calendarDialog.show();
                //show()를 먼저 해야 calendarview가 생성이 되어 null 참조가 아니게 되네...
                calendarDialog.setSelectionType(SelectionType.MULTIPLE);
                calendarDialog.setCalendarOrientation(OrientationHelper.HORIZONTAL);
                calendarDialog.setFirstDayOfWeek(SUNDAY);
                calendarDialog.setTvHelpMention("삭제하고 싶은 날짜를 선택!");
                calendarDialog.setInvisibleIcon();
                calendarDialog.setWeekendDays(new HashSet(){{add(SUNDAY); add(Calendar.SATURDAY);}});
                calendarDialog.setDayContents(dayContent_saving.getSelectedDaysPref(calendarView.getContext(), key));
                //calendarDialog.setSyncWithCalendarView(calendarView.getMonths(), calendarView.getCurrentPosition());
                calendarView.setSelectionType(SelectionType.NONE);
//                calendarView.setSelectionType(SelectionType.JUST_SHOW_INFO);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
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
                        startActivity(Intent.createChooser(intent,"일정을 공유하세요!"));
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                };
                TedPermission.with(view.getContext())
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("공유 기능을 사용하실 수 없어요...ㅠㅜ\n\n" +
                                "[설정]>[권한]에서 권한을 허용할 수 있어요.")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });


        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.backToCurrentDay();
            }
        });
    }

    /*
    * 구글 플레이 서비스 업데이트 다이얼로그, 구글 계정 선택 다이얼로그, 인증 다이얼로그에서 되돌아
    올때 호출된다.
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TEST__","onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                Log.e("TEST__","onActivityResult : REQUEST_GOOGLE_PLAY_SERVICES");
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, " 앱을 실행시키려면 구글 플레이 서비스가 필요합니다."
                            + "구글 플레이 서비스를 설치 후 다시 실행하세요.", Toast.LENGTH_LONG).show();
                } else {
                    googlePlayService.getResultsFromApi(googlePlayService.getSaved_ID());
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                Log.e("TEST__","onActivityResult : REQUEST_ACCOUNT_PICKER");
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        Log.e("TEST__","onActivityResult : REQUEST_ACCOUNT_PICKER : accountName is not null");
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        googlePlayService.getGoogleAccountCredential().setSelectedAccountName(accountName);
                        googlePlayService.getResultsFromApi(googlePlayService.getSaved_ID());
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                Log.e("TEST__","onActivityResult : REQUEST_AUTHORIZATION");
                if (resultCode == RESULT_OK) {
                    googlePlayService.getResultsFromApi(googlePlayService.getSaved_ID());
                }
                break;
        }
    }
}