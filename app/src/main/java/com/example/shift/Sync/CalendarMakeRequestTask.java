package com.example.shift.Sync;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shift.R;
import com.example.shift.TestActivity;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarMakeRequestTask extends AsyncTask<Void, Void, String> {

    private com.google.api.services.calendar.Calendar mService = null;
    private int mID;
    ProgressDialog mProgress;
     final int REQUEST_ACCOUNT_PICKER = 1000;
     final int REQUEST_AUTHORIZATION = 1001;
     final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
     final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
     private final String PREF_ACCOUNT_NAME = "accountName";
     private final String[] SCOPES = {CalendarScopes.CALENDAR};


    private Exception mLastError = null;
    private Activity mActivity;
    List<String> eventStrings = new ArrayList<>();
    private Context mContext;
    private GooglePlayService googlePlayService;

    public CalendarMakeRequestTask(Activity activity, GoogleAccountCredential credential, int ID) {
        Log.e("TEST__","MakeRequestTask");
        mActivity = activity;
        mContext = (Context) mActivity;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar
                .Builder(transport, jsonFactory, credential)
                .setApplicationName(mContext.getString(R.string.app_name))
                .build();
        googlePlayService = new GooglePlayService(mActivity);
        mID = ID;
        // Google Calendar API 호출중에 표시되는 ProgressDialog
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Google Calendar API 호출 중입니다.");
    }

    @Override
    protected void onPreExecute() {
        mProgress.show();
        Toast.makeText(mActivity.getBaseContext(),"데이터 가져오는 중...",Toast.LENGTH_LONG).show();
    }

    /*
     * 백그라운드에서 Google Calendar API 호출 처리
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            if (mID == 1) {
                return createCalendar();
            }
            else if (mID == 2) {
                return addEvent();
            }
            else if (mID == 3) {
                return getEvent();
            }
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
        return null;
    }
    /*
     * CalendarTitle 이름의 캘린더에서 10개의 이벤트를 가져와 리턴
     */

    public String getCalendarID(String calendarTitle) {
        String id = null;
// Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                mActivity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<CalendarListEntry> items = calendarList.getItems();
            for (CalendarListEntry calendarListEntry : items) {
                if (calendarListEntry.getSummary().toString().equals(calendarTitle)) {
                    id = calendarListEntry.getId().toString();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);
        return id;
    }

    public String getEvent() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        if (calendarID == null) {
            return "캘린더를 먼저 생성하세요.";
        }
        Events events = mService.events().list(calendarID)//"primary")
                //.setMaxResults(10)
                //.setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // 모든 이벤트가 시작 시간을 갖고 있지는 않다. 그런 경우 시작 날짜만 사용
                start = event.getStart().getDate();
            }
            eventStrings.add(String.format("%s \n (%s)", event.getSummary(), start));
        }
        return eventStrings.size() + "개의 데이터를 가져왔습니다.";
    }

    /*
     * 선택되어 있는 Google 계정에 새 캘린더를 추가한다.
     */
    public String createCalendar() throws IOException {
        Log.e("TEST__","Task : createCalendar()");
        String ids = getCalendarID(mContext.getString(R.string.app_name));
        if (ids != null) {
            return "이미 캘린더가 생성되어 있습니다. ";
        }
        // 새로운 캘린더 생성
        com.google.api.services.calendar.model.Calendar calendar = new Calendar();
        // 캘린더의 제목 설정
        calendar.setSummary(mContext.getString(R.string.app_name));
//         캘린더의 시간대 설정
        //calendar.setTimeZone("Asia/Seoul");
        //Log.e("TEST__",);
        calendar.setTimeZone(java.util.Calendar.getInstance().getTimeZone().getID());
        // 구글 캘린더에 새로 만든 캘린더를 추가
        Calendar createdCalendar = mService.calendars().insert(calendar).execute();
        // 추가한 캘린더의 ID를 가져옴.
        String calendarId = createdCalendar.getId();
        // 구글 캘린더의 캘린더 목록에서 새로 만든 캘린더를 검색
        CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();
        // 캘린더의 배경색을 파란색으로 표시 RGB
        calendarListEntry.setBackgroundColor("#0000ff");
        // 변경한 내용을 구글 캘린더에 반영
        CalendarListEntry updatedCalendarListEntry = mService.calendarList()
                .update(calendarListEntry.getId(), calendarListEntry)
                .setColorRgbFormat(true)
                .execute();
        // 새로 추가한 캘린더의 ID를 리턴
        return "캘린더가 생성되었습니다.";
    }

    @Override
    protected void onPostExecute(String output) {
        mProgress.hide();
        Toast.makeText(mContext, output, Toast.LENGTH_LONG).show();
        if (mID == 3) Toast.makeText(mContext, TextUtils.join("\n\n", eventStrings),Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCancelled() {
        mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                googlePlayService.showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                mActivity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(), REQUEST_AUTHORIZATION);
            } else {
                Toast.makeText(mContext,"MakeRequestTask The following error occurred:\n" + mLastError.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mContext,"요청 취소됨.", Toast.LENGTH_LONG).show();
        }
    }

    private String addEvent(String dayContent) {
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        if (calendarID == null) {
            return "캘린더를 먼저 생성하세요.";
        }
        Event event = new Event()
                .setSummary("구글 캘린더 테스트")
                .setLocation("서울시")
                .setDescription("캘린더에 이벤트 추가하는 것을 테스트합니다.");
        java.util.Calendar calander;
        calander = java.util.Calendar.getInstance();
        SimpleDateFormat simpledateformat;
        //simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA);
        // Z에 대응하여 +0900이 입력되어 문제 생겨 수작업으로 입력
        simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);
        String datetime = simpledateformat.format(calander.getTime());
        DateTime startDateTime = new DateTime(datetime);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Seoul");
        event.setStart(start);
        Log.d("@@@", datetime);
        DateTime endDateTime = new DateTime(datetime);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Seoul");
        event.setEnd(end);

        try {
            event = mService.events().insert(calendarID, event).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "Exception : " + e.toString());
        }
        System.out.printf("Event created: %s\n", event.getHtmlLink());
        Log.e("Event", "created : " + event.getHtmlLink());
        String eventStrings = "created : " + event.getHtmlLink();
        return eventStrings;
    }


    public String addEvent(DayContent dayContent) {
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        ArrayList<String> colorList = new ArrayList<String>(Arrays.asList(mContext.getResources().getStringArray(R.array.colorIndex)));
        if (calendarID == null) {
            return "캘린더를 먼저 생성하세요.";
        }
        Event event = new Event();

        Date startDate = dayContent.getContentDate();
        Date endDate = new Date(startDate.getTime() + 86400000); // An all-day event is 1 day (or 86400000 ms) long

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);

        // Out of the 6 methods for creating a DateTime object with no time element, only the String version works
        DateTime startDateTime = new DateTime(startDateStr);
        DateTime endDateTime = new DateTime(endDateStr);

        // Must use the setDate() method for an all-day event (setDateTime() is used for timed events)
        EventDateTime startEventDateTime = new EventDateTime().setDate(startDateTime);
        EventDateTime endEventDateTime = new EventDateTime().setDate(endDateTime);

        event.setStart(startEventDateTime);
        event.setEnd(endEventDateTime);
        event.setSummary(dayContent.getContentString());
        String color = "#" + String.format("%06X", (0xFFFFFF & dayContent.getContentColor()));
        int index = colorList.indexOf(color.toLowerCase());
        event.setColorId(Integer.toString(index+1));

        try {
            event = mService.events().insert(calendarID, event).execute();
            Log.d("TEST__","created : " + event.getHtmlLink());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TEST__", "Exception : " + e.toString());
        }

        String eventStrings = "created : " + event.getHtmlLink();
        return eventStrings;
    }


    public String addEvent() {
        String key = mContext.getString(R.string.key);
        List<DayContent> dayContentList = new DayContent().getSelectedDaysPref(mContext,key);
        Log.d("TEST__","addEvent() : DayContentList size is : " + dayContentList.size());
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        if (calendarID == null) {
            return "캘린더를 먼저 생성하세요.";
        }
        Event event = new Event();

        java.util.Calendar calender;
        calender = java.util.Calendar.getInstance();

        for(DayContent dayContent : dayContentList){
            addEvent(dayContent);
        }

        return "";
    }
}