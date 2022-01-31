package com.example.shift.Sync;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.Arrays;
import java.util.List;

public class GooglePlayService{

    final int REQUEST_ACCOUNT_PICKER = 1000;
    final int REQUEST_AUTHORIZATION = 1001;
    final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private int saved_ID;

    private Activity mActivity;
    private Context mContext;
    private GoogleAccountCredential mCredential;
    private PermissionListener permissionListener;

    public GooglePlayService(Activity activity){
        this.mActivity = activity;
        this.mContext = (Context) activity;
        // Google Calendar API 사용하기 위해 필요한 인증 초기화( 자격 증명 credentials, 서비스 객체 )
        // OAuth 2.0를 사용하여 구글 계정 선택 및 인증하기 위한 준비
        mCredential = GoogleAccountCredential.usingOAuth2(
                mContext.getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용
    }

    public GoogleAccountCredential getGoogleAccountCredential(){
        return this.mCredential;
    }

    /**
     * 다음 사전 조건을 모두 만족해야 Google Calendar API를 사용할 수 있다.
     * <p>
     * 사전 조건
     * - Google Play Services 설치
     * - 유효한 구글 계정 선택
     * - 안드로이드 디바이스에서 인터넷 사용 가능
     * <p>
     * 하나라도 만족하지 않으면 해당 사항을 사용자에게 알림.
     */
    public String getResultsFromApi(int ID) {
        this.saved_ID = ID;
        Log.d("TEST__","getResultFromApi : " + ID);
        if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우
            Log.d("TEST__","Google Play Services를 사용할 수 없는 경우");
            acquireGooglePlayServices();
        }
        else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지않은 경우
            Log.d("TEST__","getResultsFromAPi : 유효한 Google 계정이 선택되어 있지않은 경우");
            chooseAccount(ID);
        }
        else if (!isDeviceOnline()) { // 인터넷을 사용할 수 없는 경우
            Log.d("TEST__","인터넷을 사용할 수 없는 경우");
            Toast.makeText(mContext,"No network connection available.", Toast.LENGTH_LONG).show();
        }
        else {// Google Calendar API 호출
            Log.d("TEST__","Google Calendar API 호출");
            new CalendarMakeRequestTask(mActivity, mCredential, ID).execute();
        }
        return null;
    }

    public int getSaved_ID(){
        return saved_ID;
    }


    public boolean isExistCalendarAccount(){
        return mCredential.getSelectedAccountName() != null;
    }

    /*
     * 안드로이드 디바이스가 인터넷 연결되어 있는지 확인한다. 연결되어 있다면 True 리턴, 아니면 False 리턴
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /*
     * Google Calendar API의 자격 증명( credentials ) 에 사용할 구글 계정을 설정한다.
     *
     * 전에 사용자가 구글 계정을 선택한 적이 없다면 다이얼로그에서 사용자를 선택하도록 한다.
     * GET_ACCOUNTS 퍼미션이 필요하다.
     */
    private void chooseAccount(int ID) {
        permissionListener = new PermissionListener() {
            @Override
            //이미 권한이 설정되어 있는 경우와 권한을 승인한 경우 모두 아래 구문이 실행된다.
            public void onPermissionGranted() {
                Log.d("TEST__","GooglePlayService : chooseAccount has Permissions");
                // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
                String accountName = mActivity.getPreferences(Context.MODE_PRIVATE)
                        .getString(PREF_ACCOUNT_NAME, null);
                if (accountName != null) {
                    Log.d("TEST__","chooseAccount : accountName is not null");
                    // 선택된 구글 계정 이름으로 설정한다.
                    mCredential.setSelectedAccountName(accountName);
                    getResultsFromApi(ID);
                } else {
                    // 사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.
                    Log.d("TEST__", "chooseAccount : go to startActivityForResult");
                    mActivity.startActivityForResult(mCredential.newChooseAccountIntent(),
                            REQUEST_ACCOUNT_PICKER);
                }
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };
        TedPermission.with(mContext)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("캘런더 내보내기 기능을 사용하실 수 없어요...ㅠㅜ\n\n" +
                        "[설정]>[권한]에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.GET_ACCOUNTS)
                .check();

    }

    /*
    * 안드로이드 디바이스에 Google Play Services가 설치 안되어 있거나 오래된 버전인 경우 보여주는 대
    화상자
    */
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                mActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }


    /*
    * Google Play Services 업데이트로 해결가능하다면 사용자가 최신 버전으로 업데이트하도록 유도하기
    위해
    * 대화상자를 보여줌.
    */
    public void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * 안드로이드 디바이스에 최신 버전의 Google Play Services가 설치되어 있는지 확인
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
}
