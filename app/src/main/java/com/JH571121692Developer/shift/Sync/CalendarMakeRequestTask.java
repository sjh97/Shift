package com.JH571121692Developer.shift.Sync;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.JH571121692Developer.shift.R;
import com.JH571121692Developer.shift.Utils.SettingHelper;
import com.JH571121692Developer.shift.cosmocalendar.utils.CalendarSyncData;
import com.JH571121692Developer.shift.cosmocalendar.utils.DayContent;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class CalendarMakeRequestTask extends AsyncTask<Void, Void, String> {

    private com.google.api.services.calendar.Calendar mService = null;
    private int mID;
     final int REQUEST_ACCOUNT_PICKER = 1000;
     final int REQUEST_AUTHORIZATION = 1001;
     final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
     final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
     private final String PREF_ACCOUNT_NAME = "accountName";
     private final String[] SCOPES = {CalendarScopes.CALENDAR};
     public AsyncCallback delegate;
     private String settingkey = "";
     public static int GET_EVENT = 4;


    private Exception mLastError = null;
    private Activity mActivity;
    List<String> eventStrings = new ArrayList<>();
    private Context mContext;

    public CalendarMakeRequestTask(Activity activity, GoogleAccountCredential credential, int ID) {
        Log.e("TEST__","MakeRequestTask");
        mActivity = activity;
        mContext = (Context) mActivity;
        settingkey = mContext.getString(R.string.settingkey);
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar
                .Builder(transport, jsonFactory, credential)
                .setApplicationName(mContext.getString(R.string.app_name))
                .build();
        mID = ID;
        // Google Calendar API 호출중에 표시되는 ProgressDialog
    }

    public CalendarMakeRequestTask(Activity activity, GoogleAccountCredential credential, int ID, AsyncCallback asyncCallback) {
        Log.e("TEST__","MakeRequestTask with delegate");
        mActivity = activity;
        mContext = (Context) mActivity;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        settingkey = mContext.getString(R.string.settingkey);
        String accountName = mActivity.getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);
        if(accountName != null)
            credential.setSelectedAccountName(accountName);

        mService = new com.google.api.services.calendar.Calendar
                .Builder(transport, jsonFactory, credential)
                .setApplicationName(mContext.getString(R.string.app_name))
                .build();
        mID = ID;
        this.delegate = asyncCallback;
        // Google Calendar API 호출중에 표시되는 ProgressDialog
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mActivity.getBaseContext(),"데이터 가져오는 중...",Toast.LENGTH_LONG).show();
    }

    /*
     * 백그라운드에서 Google Calendar API 호출 처리
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            if(mID==1){
                Log.d("TEST__", "doInBackground : deleteEvent()");
                return deleteEvent();
            }
            else if (mID == 2) {
//                return addEvent();
                Log.d("TEST__", "doInBackground : updateEvent()");
                return updateEvent();
            }
            else if (mID == 3) {
                Log.d("TEST__", "doInBackground : deleteEventAll()");
                return deleteEventAll();
            }
            else if (mID == GET_EVENT){
                Log.d("TEST__", "doInBackground : getEvent()");
                return getEvent();
            }
        } catch (Exception e) {
            mLastError = e;
            Log.e("TEST__","doInBackground : " + e.toString());
            cancel(true);
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String output) {
//        Toast.makeText(mContext, output, Toast.LENGTH_LONG).show();
        Log.d("TEST__","CalendarMakeRequestTask : onPostExecute : " + mID);
//        if (mID == 3) Toast.makeText(mContext, TextUtils.join("\n\n", eventStrings),Toast.LENGTH_LONG).show();
        if(delegate != null && mID == 4){
            Log.d("TEST__","CalendarMakeRequestTask : onTaskDone");
            delegate.onTaskDone();
        }
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
                Log.e("TEST__","CalendarmakeRequestTask : getCalendarID : " + e.toString());
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
        new SettingHelper(mContext, mContext.getString(R.string.settingkey)).setMyCalendarID(id);
        return id;
    }

    public List<Pair<String, String>> getAllCalendarInfo() {
        Log.d("TEST__","getAllCalendarInfo : 1");
        List<Pair<String, String>> ids = new ArrayList<>();
        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                Log.d("TEST__","getAllCalendarInfo : 2 : ");
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
                Log.d("TEST__","getAllCalendarInfo : 3");
            } catch (UserRecoverableAuthIOException e) {
                Log.e("TEST__","CalendarmakeRequestTask : getAllCalendarInfo : UserRecoverableAuthIOException : " + e.toString());
                mActivity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                Log.e("TEST__","CalendarmakeRequestTask : getAllCalendarInfo : " + e.toString());
                e.printStackTrace();
            }
            List<CalendarListEntry> items = calendarList.getItems();
            for (CalendarListEntry calendarListEntry : items) {
                ids.add(Pair.create(calendarListEntry.getId(),calendarListEntry.getSummary()));
            }
            pageToken = calendarList.getNextPageToken();
            Log.d("TEST__","pageToken is " + pageToken);
        } while (pageToken != null);
        return ids;
    }

    public String getEvent() throws IOException {
        Log.d("TEST__","here1 : " + mContext.getString(R.string.app_name));
        String calendarID = new SettingHelper(mContext,settingkey).getMyCalendarID();
        if(calendarID.isEmpty())
            calendarID = getCalendarID(mContext.getString(R.string.app_name));
        Log.d("TEST__","here2 : " + calendarID);

        List<Pair<String,String>> idTitleList = getAllCalendarInfo();
        Log.d("TEST__","here3");
        List<CalendarSyncData> calendarSyncDataList = new ArrayList<>();
        if(idTitleList.size() != 0){
            for(Pair<String,String> idTitle : idTitleList){
                Log.d("TEST__","getEvent()"+ "\nidTitle.first is " + idTitle.first + "\ncalendarID is " + calendarID);
                if(!idTitle.first.equals(calendarID)){
                    List<CalendarSyncData> syncDataList  = getEvent(idTitle.first);
                    if(syncDataList != null)
                        calendarSyncDataList.addAll(syncDataList);
                }
            }
        }
        new SettingHelper(mContext,mContext.getString(R.string.settingkey)).setCalendarSyncDataList(calendarSyncDataList);
        return eventStrings.size() + "개의 데이터를 가져왔습니다.";
    }

    public List<CalendarSyncData> getEvent(String calendarID) throws IOException {
        Events events = mService.events().list(calendarID)//"primary")
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        ArrayList<String> colorList = new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.colorIndex)));
        CalendarSyncData calendarSyncData = new CalendarSyncData();
        List<CalendarSyncData> calendarSyncDataList = new ArrayList<>();
        for (Event event : items) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            // 모든 이벤트가 시작 시간을 갖고 있지는 않다. 그런 경우 시작 날짜만 사용

            try {
                DateTime start = event.getStart().getDate();
                DateTime end = event.getEnd().getDate();
                if(start == null || end == null)
                    continue;
                String start_string = start.toString();
                String end_string = end.toString();
                Log.d("TEST__",start_string);
                Date startDate = dateFormat.parse(start_string);
                Date endDate = dateFormat.parse(end_string);
                String colorWithSharp = colorList.get(Integer.parseInt((event.getColorId()!=null) ? event.getColorId():"1")-1);
                String title = event.getSummary();
                calendarSyncData.setStartDate(startDate);
                calendarSyncData.setEndDate(endDate);
                calendarSyncData.setTitle(title);
                calendarSyncData.setColor(Color.parseColor(colorWithSharp));
                calendarSyncDataList.add(calendarSyncData);
                Log.d("TEST__","title is " + title);
            } catch (ParseException e) {
                Log.d("TEST__", e.toString());
                e.printStackTrace();
            }
        }
        return calendarSyncDataList;
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
    protected void onCancelled() {
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(
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

    public void updateEvent(DayContent dayContent) throws IOException{
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        ArrayList<String> colorList = new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.colorIndex)));
        DateTime now = new DateTime(dayContent.getContentDate());
        Events events = mService.events().list(calendarID)//"primary")
                .setMaxResults(1)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        String eventId = items.get(0).getId();
        if(eventId != null){
            try {
                Event eventToUpdate = new Event();

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
                eventToUpdate.setStart(startEventDateTime);
                eventToUpdate.setEnd(endEventDateTime);
                eventToUpdate.setSummary(dayContent.getContentString());
//                String color = "#" + String.format("%06X", (0xFFFFFF & dayContent.getContentColor()));
//                int index = colorList.indexOf(color.toLowerCase());
//                eventToUpdate.setColorId(Integer.toString(index+1));
                int index = dayContent.getContentColorId();
                eventToUpdate.setColorId(Integer.toString(index+1));

                eventToUpdate = mService.events().update(calendarID, eventId, eventToUpdate).execute();
                Log.d("TEST__","updated : " + eventToUpdate.getHtmlLink());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TEST__", "updateEvent : " + e.toString());
            }
        }
    }

    public void deleteEvent(DayContent dayContent) throws IOException{
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        DateTime now = new DateTime(dayContent.getContentDate());
        Events events = mService.events().list(calendarID)//"primary")
                .setMaxResults(1)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        String eventId = items.get(0).getId();
        if(eventId != null){
            try {
                mService.events().delete(calendarID, eventId).execute();
                Log.d("TEST__","deleted : ");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TEST__", "deleteEvent : " + e.toString());
            }
        }
    }

    public void deleteEvent(String eventId) throws IOException{
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        if(eventId != null){
            try {
                mService.events().delete(calendarID, eventId).execute();
                Log.d("TEST__","deleted : ");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TEST__", "deleteEvent : " + e.toString());
            }
        }
    }

    public String deleteEventAll() throws IOException{
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        Events events = mService.events().list(calendarID)//"primary")
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if(items != null){
            for(Event item : items)
                deleteEvent(item.getId());
        }

        return "";
    }


    public String addEvent(DayContent dayContent) {
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        ArrayList<String> colorList = new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.colorIndex)));
        if (calendarID == null) {
            Log.e("TEST__","addEvent() : 캘린더를 먼저 생성합니다.");
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
//        String color = "#" + String.format("%06X", (0xFFFFFF & dayContent.getContentColor()));
//        int index = colorList.indexOf(color.toLowerCase());
//        event.setColorId(Integer.toString(index+1));

        int index = dayContent.getContentColorId();
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

    public String deleteEvent() throws IOException{
        String key = mContext.getString(R.string.key);
        List<DayContent> dayContentList = new DayContent().getSelectedDaysPref(mContext,key);
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        if (calendarID == null) {
            //Toast.makeText(mContext, "캘린더를 먼저 생성합니다.", Toast.LENGTH_LONG).show();
            Log.e("TEST__","deleteUpdate() : 캘린더를 먼저 생성합니다.");
            createCalendar();
        }
        for(DayContent dayContent : dayContentList){
            deleteEvent(dayContent);
        }
        return "";
    }

    public String updateEvent() throws IOException{
        String key = mContext.getString(R.string.key);
        String settingKey = mContext.getString(R.string.settingkey);
        SettingHelper mSettingHelper = new SettingHelper(mContext, settingKey);
        List<DayContent> dayContentList = new DayContent().getSelectedDaysPref(mContext,key);
        List<DayContent> beforeSyncDayContentList = mSettingHelper.getBeforeSyncDayContentList();

        Log.d("TEST__","updateEvent() : DayContentList size is : " + dayContentList.size());
        Log.d("TEST__","updateEvent() : beforeSynDayContentList size is : " + dayContentList.size());
        String calendarID = getCalendarID(mContext.getString(R.string.app_name));
        Log.d("TEST__","updateEvent() : " + calendarID);
        if (calendarID == null) {
            //Toast.makeText(mContext, "캘린더를 먼저 생성합니다.", Toast.LENGTH_LONG).show();
            Log.e("TEST__","updateEvent() : 캘린더를 먼저 생성합니다.");
            createCalendar();
            return "캘린더를 먼저 생성하세요.";
        }

        for(DayContent forSaving : dayContentList){
            Pair<Boolean, DayContent> have = mSettingHelper.haveSyncDayContent(forSaving);
            Log.d("TEST__", "have.first : " + have.first + " have.second is null? " + (have.second == null));
            //sync를 하기전에 이미 dayContent를 가지고 있다면 이미 저장이 되어있다는 것. 따라서 업데이트만 진행한다.
            if(have.first){
                //정보 중에 하나라도 바뀐게 있으면 업데이트 한다.
                if(!forSaving.equals(have.second)){
                    Log.d("TEST__","updateEvent() : will updateEvent() : " + forSaving.getContentDate());
                    updateEvent(forSaving);
                }

            }
            //sync를 하기전에 없다면 더해준다.
            else{
                Log.d("TEST__","updateEvent() : will addEvent() : " + forSaving.getContentDate());
                addEvent(forSaving);
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for(DayContent saved : beforeSyncDayContentList){
            boolean have = false;
            for(DayContent forsaving : dayContentList){
                if(simpleDateFormat.format(forsaving.getContentDate()).equals(simpleDateFormat.format(saved.getContentDate())))
                    have = true;
            }
            //sync하기 전에는 있는데 dayContentList에는 없다는 것은 삭제한 데이터라는 의미이다.
            if(!have){
                Log.d("TEST__","updateEvent() : will deleteEvent() : " + saved.getContentDate());
                deleteEvent(saved);
            }
        }
        mSettingHelper.setBeforeSyncDayContentList(dayContentList);
        return "데이터 전송 완료!";
    }
}