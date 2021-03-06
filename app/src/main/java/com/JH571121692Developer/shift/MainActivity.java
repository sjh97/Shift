package com.JH571121692Developer.shift;

import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.getInstance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.OrientationHelper;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.JH571121692Developer.shift.Dialog.OnSettingListener;
import com.JH571121692Developer.shift.Dialog.SettingDialog;
import com.JH571121692Developer.shift.Sync.GooglePlayService;
import com.JH571121692Developer.shift.Utils.SettingHelper;
import com.JH571121692Developer.shift.cosmocalendar.adapter.MonthAdapter;
import com.JH571121692Developer.shift.cosmocalendar.dialog.CalendarDialog;
import com.JH571121692Developer.shift.cosmocalendar.dialog.OnDaysSelectionListener;
import com.JH571121692Developer.shift.cosmocalendar.model.Day;
import com.JH571121692Developer.shift.cosmocalendar.utils.DayContent;
import com.JH571121692Developer.shift.cosmocalendar.utils.SelectionType;
import com.JH571121692Developer.shift.cosmocalendar.view.CalendarView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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
        LinearLayout toolbar = findViewById(R.id.toolbar);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            toolbar.setBackgroundColor(getColor(R.color.dark_calendar_background_color));

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
                settingHelper.update();
                SettingDialog settingDialog = new SettingDialog(view.getContext(), MainActivity.this,
                        calendarView, googlePlayService, settingHelper, new OnSettingListener() {
                    @Override
                    public void OnSettingListener(List<Pair<Integer, String>> beforeintegerStringList, List<Pair<Integer, String>> integerStringList) {
                        dayContent_saving.updateSelectedDaysPrefByColor(view.getContext(), key, integerStringList);
                        calendarView.setDayContents(dayContent_saving.getSelectedDaysPref(view.getContext(),key));
                        settingHelper = new SettingHelper(view.getContext(), settingkey);
//                        Log.d("TEST__","" + "settingHelper.isExport() : " + settingHelper.isExport());
                        boolean isDiff = false;
                        for(int i = 0;i<beforeintegerStringList.size();i++){
                            Pair<Integer,String> before = beforeintegerStringList.get(i);
                            Pair<Integer,String> after = integerStringList.get(i);
//                            Log.d("TEST__","" + "settingButton : " + before.first + " : " + after.first + " : " + (before.first - after.first != 0));
//                            Log.d("TEST__","" + "settingButton : " + before.second + " : " + after.second + " : " + (!before.second.replace(after.second,"").equals("")));
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
                settingHelper.update();
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
                settingHelper.update();
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
                //show()??? ?????? ?????? calendarview??? ????????? ?????? null ????????? ????????? ??????...
                calendarDialog.setSelectionType(SelectionType.MULTIPLE);
                calendarDialog.setCalendarOrientation(OrientationHelper.HORIZONTAL);
                calendarDialog.setFirstDayOfWeek(SUNDAY);
                calendarDialog.setTvHelpMention("???????????? ?????? ????????? ??????!");
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
                        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), captureView, getString(R.string.app_name), null);
                        Uri bitmapUri = Uri.parse(bitmapPath);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                        rootView.setDrawingCacheEnabled(false);
                        captureView.recycle();
                        captureView = null;
                        startActivity(Intent.createChooser(intent,"????????? ???????????????!"));

//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        Gson gson = new Gson();
//                        String save = gson.toJson(new DayContent().getSelectedDaysPref(view.getContext(),key),
//                                new TypeToken<List<DayContent>>(){}.getType());
//                        intent.setType("text/plain");
//                        intent.putExtra(Intent.EXTRA_TEXT,save);
//                        startActivity(intent);


                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                };
                TedPermission.with(view.getContext())
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("?????? ????????? ???????????? ??? ?????????...??????\n\n" +
                                "[??????]>[??????]?????? ????????? ????????? ??? ?????????.")
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
    * ?????? ????????? ????????? ???????????? ???????????????, ?????? ?????? ?????? ???????????????, ?????? ????????????????????? ?????????
    ?????? ????????????.
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.e("TEST__","onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
//                Log.e("TEST__","onActivityResult : REQUEST_GOOGLE_PLAY_SERVICES");
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, " ?????? ?????????????????? ?????? ????????? ???????????? ???????????????."
                            + "?????? ????????? ???????????? ?????? ??? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                } else {
                    googlePlayService.getResultsFromApi(googlePlayService.getSaved_ID());
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
//                Log.e("TEST__","onActivityResult : REQUEST_ACCOUNT_PICKER");
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
//                        Log.e("TEST__","onActivityResult : REQUEST_ACCOUNT_PICKER : accountName is not null");
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
//                Log.e("TEST__","onActivityResult : REQUEST_AUTHORIZATION");
                if (resultCode == RESULT_OK) {
                    googlePlayService.getResultsFromApi(googlePlayService.getSaved_ID());
                }
                break;
        }
    }
}