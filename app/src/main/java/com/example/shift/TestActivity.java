package com.example.shift;


import com.example.shift.Sync.GooglePlayService;
import com.example.shift.Utils.SettingHelper;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


//https://webnautes.tistory.com/1217
public class TestActivity extends AppCompatActivity {

    private com.google.api.services.calendar.Calendar mService = null;
    /**
     * Google Calendar API 호출 관련 메커니즘 및 AsyncTask을 재사용하기 위해 사용
     */
    private int mID = 0;

    private TextView mStatusText;
    private TextView mResultText;
    private Button mGetEventButton;
    private Button mAddEventButton;
    private Button mAddCalendarButton;
    ProgressDialog mProgress;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private GooglePlayService googlePlayService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mAddCalendarButton = (Button) findViewById(R.id.button_main_add_calendar);
        mAddEventButton = (Button) findViewById(R.id.button_main_add_event);
        mGetEventButton = (Button) findViewById(R.id.button_main_get_event);
        mStatusText = (TextView) findViewById(R.id.textview_main_status);
        mResultText = (TextView) findViewById(R.id.textview_main_result);
        googlePlayService = new GooglePlayService((Activity) this);
        /**
         * 버튼 클릭으로 동작 테스트
         */
        mAddCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddCalendarButton.setEnabled(false);
                mStatusText.setText("");
                mID = 1; //캘린더 생성
                googlePlayService.getResultsFromApi(mID);
                mAddCalendarButton.setEnabled(true);
            }
        });
        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddEventButton.setEnabled(false);
                mStatusText.setText("");
                mID = 2; //이벤트 생성
                googlePlayService.getResultsFromApi(mID);
                mAddEventButton.setEnabled(true);
            }
        });
        mGetEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetEventButton.setEnabled(false);
                mStatusText.setText("");
                mID = 3; //이벤트 가져오기
                googlePlayService.getResultsFromApi(mID);
                mGetEventButton.setEnabled(true);
            }
        });
        // Google Calendar API의 호출 결과를 표시하는 TextView를 준비
        mResultText.setVerticalScrollBarEnabled(true);
        mResultText.setMovementMethod(new ScrollingMovementMethod());
        mStatusText.setVerticalScrollBarEnabled(true);
        mStatusText.setMovementMethod(new ScrollingMovementMethod());
        mStatusText.setText("버튼을 눌러 테스트를 진행하세요.");
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
                    googlePlayService.getResultsFromApi(mID);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                Log.e("TEST__","onActivityResult : REQUEST_ACCOUNT_PICKER");
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        googlePlayService.getGoogleAccountCredential().setSelectedAccountName(accountName);
                        googlePlayService.getResultsFromApi(mID);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                Log.e("TEST__","onActivityResult : REQUEST_AUTHORIZATION");
                if (resultCode == RESULT_OK) {
                    googlePlayService.getResultsFromApi(mID);
                }
                break;
        }
    }
}