package com.example.shift.cosmocalendar.view;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.icu.lang.UCharacter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shift.R;
import com.example.shift.cosmocalendar.FetchMonthsAsyncTask;
import com.example.shift.cosmocalendar.adapter.MonthAdapter;
import com.example.shift.cosmocalendar.listeners.OnMonthChangeListener;
import com.example.shift.cosmocalendar.model.Day;
import com.example.shift.cosmocalendar.model.Month;
import com.example.shift.cosmocalendar.selection.BaseSelectionManager;
import com.example.shift.cosmocalendar.selection.JustShowSelectionManager;
import com.example.shift.cosmocalendar.selection.MultipleSelectionManager;
import com.example.shift.cosmocalendar.selection.NoneSelectionManager;
import com.example.shift.cosmocalendar.selection.OnDaySelectedListener;
import com.example.shift.cosmocalendar.selection.RangeSelectionManager;
import com.example.shift.cosmocalendar.selection.SingleSelectionManager;
import com.example.shift.cosmocalendar.selection.selectionbar.MultipleSelectionBarAdapter;
import com.example.shift.cosmocalendar.selection.selectionbar.SelectionBarItem;
import com.example.shift.cosmocalendar.settings.SettingsManager;
import com.example.shift.cosmocalendar.settings.appearance.AppearanceInterface;
import com.example.shift.cosmocalendar.settings.date.DateInterface;
import com.example.shift.cosmocalendar.settings.lists.CalendarListsInterface;
import com.example.shift.cosmocalendar.settings.lists.DisabledDaysCriteria;
import com.example.shift.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.example.shift.cosmocalendar.settings.lists.connected_days.ConnectedDaysManager;
import com.example.shift.cosmocalendar.settings.selection.SelectionInterface;
import com.example.shift.cosmocalendar.utils.CalendarSyncData;
import com.example.shift.cosmocalendar.utils.CalendarUtils;
import com.example.shift.cosmocalendar.utils.DayContent;
import com.example.shift.cosmocalendar.utils.SelectionType;
import com.example.shift.cosmocalendar.utils.WeekDay;
import com.example.shift.cosmocalendar.utils.snap.GravityPagerSnapHelper;
import com.example.shift.cosmocalendar.utils.snap.GravitySnapHelper;
import com.example.shift.cosmocalendar.view.customviews.CircleAnimationTextView;
import com.example.shift.cosmocalendar.view.customviews.SquareTextView;
import com.example.shift.cosmocalendar.view.delegate.MonthDelegate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class CalendarView extends RelativeLayout implements OnDaySelectedListener,
        AppearanceInterface, DateInterface, CalendarListsInterface, SelectionInterface,
        MultipleSelectionBarAdapter.ListItemClickListener {


    private List<Day> selectedDays;

    //Recycler
//    private SlowdownRecyclerView rvMonths;
    private ViewPager2 rvMonths;
    private MonthAdapter monthAdapter;

    //Bottom selection bar
    private FrameLayout flBottomSelectionBar;
    //Multiple mode
    private RecyclerView rvMultipleSelectedList;
    public MultipleSelectionBarAdapter multipleSelectionBarAdapter;
    //Range mode
    private LinearLayout llRangeSelection;

    //Views
    private LinearLayout llDaysOfWeekTitles;
    private FrameLayout flNavigationButtons;
    public ImageView ivPrevious;
    public ImageView ivNext;

    //Helpers
    private SettingsManager settingsManager;
    private BaseSelectionManager selectionManager;
    private GravitySnapHelper snapHelper;

    //Listeners
    private OnMonthChangeListener onMonthChangeListener;
    private Month previousSelectedMonth;

    //Contents
    private List<CalendarSyncData> syncDataList = new ArrayList<>();
    private int calendar_ID = -100;

    public int lastVisibleMonthPosition = SettingsManager.DEFAULT_MONTH_COUNT / 2;

    private FetchMonthsAsyncTask asyncTask;

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(attrs, 0, 0);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        handleAttributes(attrs, defStyle, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        handleAttributes(attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (asyncTask != null && !asyncTask.isCancelled()) {
            asyncTask.cancel(false);
        }
    }

    public MonthAdapter getMonthAdapter() {
        return this.monthAdapter;
    }

    private void handleAttributes(AttributeSet attrs, int defStyle, int defStyleRes) {
        settingsManager = new SettingsManager();
        final TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyle, defStyleRes);
        try {
            handleAttributes(typedArray);
            handleWeekendDaysAttributes(typedArray);
        } finally {
            typedArray.recycle();
        }
        init();
    }

    /**
     * Handles custom attributes and sets them to settings manager
     *
     * @param typedArray
     */
    private void handleAttributes(TypedArray typedArray) {
        int orientation = typedArray.getInteger(R.styleable.CalendarView_orientation, SettingsManager.DEFAULT_ORIENTATION);
        int firstDayOfWeek = typedArray.getInteger(R.styleable.CalendarView_firstDayOfTheWeek, SettingsManager.DEFAULT_FIRST_DAY_OF_WEEK);
        int selectionType = typedArray.getInteger(R.styleable.CalendarView_selectionType, SettingsManager.DEFAULT_SELECTION_TYPE);
        boolean showDaysOfWeekTitle = orientation != LinearLayoutManager.HORIZONTAL;
        boolean showDaysOfWeek = orientation == LinearLayoutManager.HORIZONTAL;
        int calendarBackgroundColor = typedArray.getColor(R.styleable.CalendarView_calendarBackgroundColor, ContextCompat.getColor(getContext(), R.color.default_calendar_background_color));
        int monthTextColor = typedArray.getColor(R.styleable.CalendarView_monthTextColor, ContextCompat.getColor(getContext(), R.color.default_month_text_color));
        int otherDayTextColor = typedArray.getColor(R.styleable.CalendarView_otherDayTextColor, ContextCompat.getColor(getContext(), R.color.default_other_day_text_color));
        int dayTextColor = typedArray.getColor(R.styleable.CalendarView_dayTextColor, ContextCompat.getColor(getContext(), R.color.default_day_text_color));
        int weekendDayTextColor = typedArray.getColor(R.styleable.CalendarView_weekendDayTextColor, ContextCompat.getColor(getContext(), R.color.default_weekend_day_text_color));
        int weekDayTitleTextColor = typedArray.getColor(R.styleable.CalendarView_weekDayTitleTextColor, ContextCompat.getColor(getContext(), R.color.default_week_day_title_text_color));
        int selectedDayTextColor = typedArray.getColor(R.styleable.CalendarView_selectedDayTextColor, ContextCompat.getColor(getContext(), R.color.default_selected_day_text_color));
        int selectedDayBackgroundColor = typedArray.getColor(R.styleable.CalendarView_selectedDayBackgroundColor, ContextCompat.getColor(getContext(), R.color.default_selected_day_background_color));
        int selectedDayBackgroundStartColor = typedArray.getColor(R.styleable.CalendarView_selectedDayBackgroundStartColor, ContextCompat.getColor(getContext(), R.color.default_selected_day_background_start_color));
        int selectedDayBackgroundEndColor = typedArray.getColor(R.styleable.CalendarView_selectedDayBackgroundEndColor, ContextCompat.getColor(getContext(), R.color.default_selected_day_background_end_color));
        int currentDayTextColor = typedArray.getColor(R.styleable.CalendarView_currentDayTextColor, ContextCompat.getColor(getContext(), R.color.default_day_text_color));
        int currentDayIconRes = typedArray.getResourceId(R.styleable.CalendarView_currentDayIconRes, R.drawable.ic_triangle_green);
        int currentDaySelectedIconRes = typedArray.getResourceId(R.styleable.CalendarView_currentDaySelectedIconRes, R.drawable.ic_triangle_white);
        int connectedDayIconRes = typedArray.getResourceId(R.styleable.CalendarView_connectedDayIconRes, 0);
        int connectedDaySelectedIconRes = typedArray.getResourceId(R.styleable.CalendarView_connectedDaySelectedIconRes, 0);
        int connectedDayIconPosition = typedArray.getInteger(R.styleable.CalendarView_connectedDayIconPosition, SettingsManager.DEFAULT_CONNECTED_DAY_ICON_POSITION);
        int disabledDayTextColor = typedArray.getColor(R.styleable.CalendarView_disabledDayTextColor, ContextCompat.getColor(getContext(), R.color.default_disabled_day_text_color));
        int selectionBarMonthTextColor = typedArray.getColor(R.styleable.CalendarView_selectionBarMonthTextColor, ContextCompat.getColor(getContext(), R.color.default_selection_bar_month_title_text_color));
        int previousMonthIconRes = typedArray.getResourceId(R.styleable.CalendarView_previousMonthIconRes, R.drawable.ic_chevron_left_gray);
        int nextMonthIconRes = typedArray.getResourceId(R.styleable.CalendarView_nextMonthIconRes, R.drawable.ic_chevron_right_gray);

        setBackgroundColor(calendarBackgroundColor);
        settingsManager.setCalendarBackgroundColor(calendarBackgroundColor);
        settingsManager.setMonthTextColor(monthTextColor);
        settingsManager.setOtherDayTextColor(otherDayTextColor);
        settingsManager.setDayTextColor(dayTextColor);
        settingsManager.setWeekendDayTextColor(weekendDayTextColor);
        settingsManager.setWeekDayTitleTextColor(weekDayTitleTextColor);
        settingsManager.setSelectedDayTextColor(selectedDayTextColor);
        settingsManager.setSelectedDayBackgroundColor(selectedDayBackgroundColor);
        settingsManager.setSelectedDayBackgroundStartColor(selectedDayBackgroundStartColor);
        settingsManager.setSelectedDayBackgroundEndColor(selectedDayBackgroundEndColor);
        settingsManager.setConnectedDayIconRes(connectedDayIconRes);
        settingsManager.setConnectedDaySelectedIconRes(connectedDaySelectedIconRes);
        settingsManager.setConnectedDayIconPosition(connectedDayIconPosition);
        settingsManager.setDisabledDayTextColor(disabledDayTextColor);
        settingsManager.setSelectionBarMonthTextColor(selectionBarMonthTextColor);
        settingsManager.setCurrentDayTextColor(currentDayTextColor);
        settingsManager.setCurrentDayIconRes(currentDayIconRes);
        settingsManager.setCurrentDaySelectedIconRes(currentDaySelectedIconRes);
        settingsManager.setCalendarOrientation(orientation);
        settingsManager.setFirstDayOfWeek(firstDayOfWeek);
        settingsManager.setShowDaysOfWeek(showDaysOfWeek);
        settingsManager.setShowDaysOfWeekTitle(showDaysOfWeekTitle);
        settingsManager.setSelectionType(selectionType);
        settingsManager.setPreviousMonthIconRes(previousMonthIconRes);
        settingsManager.setNextMonthIconRes(nextMonthIconRes);
    }

    private void handleWeekendDaysAttributes(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.CalendarView_weekendDays)) {
            Set<Long> weekendDays = new TreeSet<>();

            int weekdaysAttr = typedArray.getInteger(R.styleable.CalendarView_weekendDays, WeekDay.SUNDAY);
            if (containsFlag(weekdaysAttr, WeekDay.MONDAY))
                weekendDays.add((long) Calendar.MONDAY);
            if (containsFlag(weekdaysAttr, WeekDay.TUESDAY))
                weekendDays.add((long) Calendar.TUESDAY);
            if (containsFlag(weekdaysAttr, WeekDay.WEDNESDAY))
                weekendDays.add((long) Calendar.WEDNESDAY);
            if (containsFlag(weekdaysAttr, WeekDay.THURSDAY))
                weekendDays.add((long) Calendar.THURSDAY);
            if (containsFlag(weekdaysAttr, WeekDay.FRIDAY))
                weekendDays.add((long) Calendar.FRIDAY);
            if (containsFlag(weekdaysAttr, WeekDay.SATURDAY))
                weekendDays.add((long) Calendar.SATURDAY);
            if (containsFlag(weekdaysAttr, WeekDay.SUNDAY))
                weekendDays.add((long) Calendar.SUNDAY);

            settingsManager.setWeekendDays(weekendDays);
        }
    }

    private boolean containsFlag(int attr, int flag) {
        return (attr | flag) == attr;
    }

    public void init() {
        setDaysOfWeekTitles();

        setSelectionManager();
        createRecyclerView();
        createBottomSelectionBar();

        if (settingsManager.getCalendarOrientation() == LinearLayoutManager.HORIZONTAL) {
            createNavigationButtons();
        }
    }

    /**
     * Defines days of week displaying according to calendar orientation
     * HORIZONTAL - displaying below month name and above dates
     * VERTICAL - displaying above whole calendar
     */
    private void setDaysOfWeekTitles() {
        settingsManager.setShowDaysOfWeekTitle(settingsManager.getCalendarOrientation() != LinearLayoutManager.HORIZONTAL);
        settingsManager.setShowDaysOfWeek(settingsManager.getCalendarOrientation() == LinearLayoutManager.HORIZONTAL);

        if (llDaysOfWeekTitles == null) {
            createDaysOfWeekTitle();
        }
        if (settingsManager.isShowDaysOfWeekTitle()) {
            showDaysOfWeekTitle();
        } else {
            hideDaysOfWeekTitle();
        }
    }

    /**
     * Creates days of week title above calendar
     */
    private void createDaysOfWeekTitle() {
        boolean isTitleAlreadyAdded = llDaysOfWeekTitles != null;
        if (!isTitleAlreadyAdded) {
            llDaysOfWeekTitles = new LinearLayout(getContext());
            llDaysOfWeekTitles.setId(View.generateViewId());
            llDaysOfWeekTitles.setOrientation(LinearLayout.HORIZONTAL);
            llDaysOfWeekTitles.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        } else {
            llDaysOfWeekTitles.removeAllViews();
        }

        //creating days of week views
        LinearLayout.LayoutParams textViewParam = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textViewParam.weight = 1;
        for (String title : CalendarUtils.createWeekDayTitles(settingsManager.getFirstDayOfWeek())) {
            SquareTextView tvDayTitle = new SquareTextView(getContext());
            tvDayTitle.setText(title);
            tvDayTitle.setLayoutParams(textViewParam);
            tvDayTitle.setGravity(Gravity.CENTER);
            llDaysOfWeekTitles.addView(tvDayTitle);
        }

        //adding borders
        llDaysOfWeekTitles.setBackgroundResource(R.drawable.border_top_bottom);

        if (!isTitleAlreadyAdded) {
            addView(llDaysOfWeekTitles);
        }
    }

    /**
     * Creates bottom selection bar to show selected days
     */
    private void createBottomSelectionBar() {
        flBottomSelectionBar = new FrameLayout(getContext());
//        flBottomSelectionBar.setLayoutTransition(new LayoutTransition());
        flBottomSelectionBar.setId(View.generateViewId());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, rvMonths.getId());
        flBottomSelectionBar.setLayoutParams(params);
        flBottomSelectionBar.setBackgroundResource(R.drawable.border_top_bottom);
        flBottomSelectionBar.setVisibility(settingsManager.getCalendarOrientation() == OrientationHelper.HORIZONTAL ? View.VISIBLE : View.GONE);
        addView(flBottomSelectionBar);

        createMultipleSelectionBarRecycler();
        createRangeSelectionLayout();
    }

    /**
     * Creates recycler view to display selected days in bottom selection bar
     * Visible only for Multiple selection mode
     */
    private void createMultipleSelectionBarRecycler() {
        rvMultipleSelectedList = new RecyclerView(getContext());
        rvMultipleSelectedList.setId(View.generateViewId());
        rvMultipleSelectedList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rvMultipleSelectedList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        multipleSelectionBarAdapter = new MultipleSelectionBarAdapter(this, this);
        rvMultipleSelectedList.setAdapter(multipleSelectionBarAdapter);
        flBottomSelectionBar.addView(rvMultipleSelectedList);
    }

    private void createRangeSelectionLayout() {
        llRangeSelection = (LinearLayout) ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_selection_bar_range, null);
        llRangeSelection.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llRangeSelection.setVisibility(GONE);
        flBottomSelectionBar.addView(llRangeSelection);
    }

    private void showDaysOfWeekTitle() {
        llDaysOfWeekTitles.setVisibility(View.VISIBLE);
    }

    private void hideDaysOfWeekTitle() {
        llDaysOfWeekTitles.setVisibility(View.GONE);
    }

    private void setSelectionManager() {
        switch (getSelectionType()) {
            case SelectionType.SINGLE:
                selectionManager = new SingleSelectionManager(this);
                break;

            case SelectionType.MULTIPLE:
                selectionManager = new MultipleSelectionManager(this);
                break;

            case SelectionType.RANGE:
                selectionManager = new RangeSelectionManager(this);
                break;

            case SelectionType.JUST_SHOW_INFO:
                selectionManager = new JustShowSelectionManager(this);
                break;

            case SelectionType.NONE:
                selectionManager = new NoneSelectionManager();
                break;
        }
    }

    public void setSelectionManager(BaseSelectionManager selectionManager) {
        this.selectionManager = selectionManager;
        monthAdapter.setSelectionManager(selectionManager);
        update();
    }

    public BaseSelectionManager getSelectionManager() {
        return selectionManager;
    }

    public void update() {
        if (monthAdapter != null) {
            monthAdapter.notifyDataSetChanged();
            rvMonths.setCurrentItem(lastVisibleMonthPosition);
            multipleSelectionBarAdapter.notifyDataSetChanged();
        }
    }

    //calendar view 만드는 recyclerview 전체적인 달력 부분을 만든다.
    private void createRecyclerView() {
        rvMonths = new ViewPager2(getContext());
        rvMonths.setId(View.generateViewId());
        rvMonths.setNestedScrollingEnabled(false);
//        rvMonths.setOrientation(settingsManager.getCalendarOrientation());
        rvMonths.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        rvMonths.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int totalItemCount = SettingsManager.DEFAULT_MONTH_COUNT;
                lastVisibleMonthPosition = position;

                if (position < 2) {
                    loadAsyncMonths(false);
                } else if (position >= totalItemCount - 2) {
                    loadAsyncMonths(true);
                }
                Log.d("test___", "totalItemCount" + rvMonths.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (getCalendarOrientation() == OrientationHelper.HORIZONTAL) {
                multipleSelectionBarAdapter.notifyDataSetChanged();

                //Hide navigation buttons
                boolean show = state != RecyclerView.SCROLL_STATE_DRAGGING;
                ivPrevious.setVisibility(show ? View.VISIBLE : View.GONE);
                ivNext.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            }
        });


        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, llDaysOfWeekTitles.getId());
        rvMonths.setLayoutParams(params);

        monthAdapter = createAdapter();

        rvMonths.setAdapter(monthAdapter);
        rvMonths.setCurrentItem(SettingsManager.DEFAULT_MONTH_COUNT / 2);
        addView(rvMonths);
    }

    public int getCurrentMonth(){
        String current_Month  = new SimpleDateFormat("yyyy MMMM", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
        int position = 0;
        for(int i=0;i<monthAdapter.getData().size();i++){
            final Month month = monthAdapter.getData().get(i);
            if(month.getMonthName().equals(current_Month))
                position = i;
        }
        return position;
    }

    public void setMonths(List<Month> months){
        List<Month> monthList = new ArrayList<>();
        monthList.addAll(months);
        monthAdapter.setMonths(monthList);
    }

    public List<Month> getMonths(){
        return monthAdapter.getData();
    }

    public int getCurrentPosition() {
        return lastVisibleMonthPosition;
    }

    public void setCurrentPostion(int position){
        rvMonths.setCurrentItem(position);
        lastVisibleMonthPosition = position;
    }


    //현재 날짜로 돌아가기
    //https://comoi.io/247
//    public void backToCurrentDay() {
//
//    }

    public void backToCurrentDay(){
        rvMonths.setCurrentItem(getCurrentMonth());
    }


    public int getrvMonthsHeight() {
        return rvMonths.getHeight();
    }

    public ViewPager2 getRvMonths() {
        return this.rvMonths;
    }

    /**
     * Creates Next/Previous buttons to navigate months.
     * Visible only in HORIZONTAL mode
     */
    private void createNavigationButtons() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        flNavigationButtons = (FrameLayout) inflater.inflate(R.layout.calendar_navigation_buttons, this, false);

        setPreviousNavigationButton();
        setNextNavigationButton();

        addView(flNavigationButtons);
    }

    private void setPreviousNavigationButton() {
        ivPrevious = (ImageView) flNavigationButtons.findViewById(R.id.iv_previous_month);
        ivPrevious.setImageResource(settingsManager.getPreviousMonthIconRes());
        ivPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPreviousMonth();
            }
        });
    }

    private void setNextNavigationButton() {
        ivNext = (ImageView) flNavigationButtons.findViewById(R.id.iv_next_month);
        ivNext.setImageResource(settingsManager.getNextMonthIconRes());
        ivNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextMonth();
            }
        });
    }

    private MonthAdapter createAdapter() {
        return new MonthAdapter.MonthAdapterBuilder()
                .setMonths(CalendarUtils.createInitialMonths(settingsManager))
                .setMonthDelegate(new MonthDelegate(settingsManager))
                .setCalendarView(this)
                .setSelectionManager(selectionManager)
                .setDayContent(settingsManager.getDayContents())
                .setDaySynData(settingsManager.getDaySyncData())
                .createMonthAdapter();
    }

    public int getFirstVisiblePosition(RecyclerView.LayoutManager manager) {
        if (manager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
        } else {
            throw new IllegalArgumentException("Unsupported Layout Manager");
        }
    }

    public void loadAsyncMonths(final boolean future) {
        if (asyncTask != null && (asyncTask.getStatus() == AsyncTask.Status.PENDING || asyncTask.getStatus() == AsyncTask.Status.RUNNING))
            return;

        asyncTask = new FetchMonthsAsyncTask();
        Month month;

        if (future) {
            month = monthAdapter.getData().get(monthAdapter.getData().size() - 1);
        } else {
            month = monthAdapter.getData().get(0);
        }

        asyncTask.execute(new FetchMonthsAsyncTask.FetchParams(future, month, settingsManager, monthAdapter, SettingsManager.DEFAULT_MONTH_COUNT));
    }

    @Override
    public Set<Long> getDisabledDays() {
        return settingsManager.getDisabledDays();
    }

    @Override
    public List<DayContent> getDayContents() {
        return settingsManager.getDayContents();
    }

    @Override
    public List<CalendarSyncData> getDaySyncData() {
        return settingsManager.getDaySyncData();
    }

    @Override
    public ConnectedDaysManager getConnectedDaysManager() {
        return settingsManager.getConnectedDaysManager();
    }

    @Override
    public Set<Long> getWeekendDays() {
        return settingsManager.getWeekendDays();
    }

    @Override
    public DisabledDaysCriteria getDisabledDaysCriteria() {
        return settingsManager.getDisabledDaysCriteria();
    }

    public void setDisabledDays(Set<Long> disabledDays) {
        settingsManager.setDisabledDays(disabledDays);
        monthAdapter.setDisabledDays(disabledDays);
    }

    @Override
    public void setDayContents(List<DayContent> dayContents) {
        Log.d("Shift__", "3 : " + new SimpleDateFormat("mm:ss").format(System.currentTimeMillis()));
        settingsManager.setDayContents(dayContents);
        Log.d("Shift__", "4 : " + new SimpleDateFormat("mm:ss").format(System.currentTimeMillis()));
        monthAdapter.setDayContents(dayContents);
        Log.d("Shift__", "5 : " + new SimpleDateFormat("mm:ss").format(System.currentTimeMillis()));
        update();
    }

    @Override
    public void setDaySyncData(List<CalendarSyncData> syncDataList) {
        settingsManager.setDaySyncData(syncDataList);
        monthAdapter.setDaySyncData(syncDataList);
        update();
    }

    //set whether default calendar is synced or not.
    public void turnOnSyncCalendar() {
        calendarSync();
        setDaySyncData(this.syncDataList);
    }

    public void turnOffSyncCalendar() {
        this.syncDataList = null;
        setDaySyncData(null);
    }

    //synchronize default calendar.
    private void calendarSync() {
        //https://zeph1e.tistory.com/42?category=338725
        //https://www.youtube.com/watch?v=GihhIgDYCNo
        this.syncDataList = new ArrayList<>();
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
        ContentResolver cr = this.getContext().getContentResolver();
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

            this.calendar_ID = cur.getInt(9);

            duration = (prev_duration != null) ? Integer.parseInt(prev_duration.split("\\D+")[1]) : 0;
            Log.d("Shift__", "Calendar ID : " + this.calendar_ID);
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
    }


    public void setWeekendDays(Set<Long> weekendDays) {
        settingsManager.setWeekendDays(weekendDays);
        monthAdapter.setWeekendDays(weekendDays);
    }

    @Override
    public void setDisabledDaysCriteria(DisabledDaysCriteria criteria) {
        settingsManager.setDisabledDaysCriteria(criteria);
        monthAdapter.setDisabledDaysCriteria(criteria);
    }

    @Override
    public void addConnectedDays(ConnectedDays connectedDays) {
        settingsManager.getConnectedDaysManager().addConnectedDays(connectedDays);
        recreateInitialMonth();
    }

    /**
     * Removes all selections (manual and by criteria)
     */
    public void clearSelections() {
        selectionManager.clearSelections();
        if (selectionManager instanceof MultipleSelectionManager) {
            ((MultipleSelectionManager) selectionManager).clearCriteriaList();
        }
        multipleSelectionBarAdapter.setData(new ArrayList<SelectionBarItem>());
        setSelectionBarVisibility();
        update();
    }

    /**
     * Returns all selected days
     *
     * @return
     */
    public List<Day> getSelectedDays() {
        List<Day> selectedDays = new ArrayList<>();
        for (Iterator<Month> monthIterator = monthAdapter.getData().iterator(); monthIterator.hasNext(); ) {
            Month month = monthIterator.next();
            for (Iterator<Day> dayIterator = month.getDaysWithoutTitlesAndOnlyCurrent().iterator(); dayIterator.hasNext(); ) {
                Day day = dayIterator.next();
                if (selectionManager.isDaySelected(day)) {
                    selectedDays.add(day);
                }
            }
        }
        return selectedDays;
    }

    /**
     * Returns all selected dates
     *
     * @return
     */
    public List<Calendar> getSelectedDates() {
        List<Calendar> selectedDays = new ArrayList<>();
        for (Day day : getSelectedDays()) {
            selectedDays.add(day.getCalendar());
        }
        return selectedDays;
    }

    /**
     * Scroll calendar to previous month
     */
    public void goToPreviousMonth() {
        int currentVisibleItemPosition = rvMonths.getCurrentItem();
        if (currentVisibleItemPosition != 0) {
            rvMonths.setCurrentItem(currentVisibleItemPosition - 1);
        }
    }

    /**
     * Scroll calendar to next month
     */
    public void goToNextMonth() {
        int currentVisibleItemPosition = rvMonths.getCurrentItem();
        if (currentVisibleItemPosition != monthAdapter.getData().size() - 1) {
            rvMonths.setCurrentItem(currentVisibleItemPosition + 1);
        }
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public void recreateInitialMonth() {
        monthAdapter.getData().clear();
        monthAdapter.getData().addAll(CalendarUtils.createInitialMonths(settingsManager));
        lastVisibleMonthPosition = SettingsManager.DEFAULT_MONTH_COUNT / 2;
    }

    @Override
    public void onDaySelected() {
        selectedDays = getSelectedDays();
        //multiple selection 할 때 밑에 표시할 거냐 말 거냐
        displaySelectedDays();
    }

    /**
     * Displays selected days
     */
    private void displaySelectedDays() {
        switch (settingsManager.getSelectionType()) {
            case SelectionType.MULTIPLE:
                displaySelectedDaysMultiple();
                break;

            case SelectionType.RANGE:
                displaySelectedDaysRange();
                break;

            default:
                llRangeSelection.setVisibility(GONE);
                break;
        }
    }

    /**
     * Display selected days for MULTIPLE mode in bottom bar
     */
    private void displaySelectedDaysMultiple() {
        multipleSelectionBarAdapter.setData(CalendarUtils.getSelectedDayListForMultipleMode(selectedDays));
    }

    /**
     * Display selected days for RANGE mode in bottom bar
     */
    private void displaySelectedDaysRange() {
        if (selectionManager instanceof RangeSelectionManager) {
            Pair<Day, Day> days = ((RangeSelectionManager) selectionManager).getDays();
            if (days != null) {
                llRangeSelection.setVisibility(VISIBLE);
                TextView tvStartRangeTitle = (TextView) llRangeSelection.findViewById(R.id.tv_range_start_date);
                tvStartRangeTitle.setText(CalendarUtils.getYearNameTitle(days.first));
                tvStartRangeTitle.setTextColor(getSelectionBarMonthTextColor());

                TextView tvEndRangeTitle = (TextView) llRangeSelection.findViewById(R.id.tv_range_end_date);
                tvEndRangeTitle.setText(CalendarUtils.getYearNameTitle(days.second));
                tvEndRangeTitle.setTextColor(getSelectionBarMonthTextColor());

                CircleAnimationTextView catvStart = (CircleAnimationTextView) llRangeSelection.findViewById(R.id.catv_start);
                catvStart.setText(String.valueOf(days.first.getDayNumber()));
                catvStart.setTextColor(getSelectedDayTextColor());
                catvStart.showAsStartCircle(this, true);

                CircleAnimationTextView catvEnd = (CircleAnimationTextView) llRangeSelection.findViewById(R.id.catv_end);
                catvEnd.setText(String.valueOf(days.second.getDayNumber()));
                catvEnd.setTextColor(getSelectedDayTextColor());
                catvEnd.showAsEndCircle(this, true);

                CircleAnimationTextView catvMiddle = (CircleAnimationTextView) llRangeSelection.findViewById(R.id.catv_middle);
                catvMiddle.showAsRange(this);
            } else {
                llRangeSelection.setVisibility(GONE);
            }
        }
    }

    /**
     * Defines do we need to show range of selected days in bottom selection bar
     *
     * @return
     */
    private boolean needToShowSelectedDaysRange() {
        if (getCalendarOrientation() == OrientationHelper.HORIZONTAL && getSelectionType() == SelectionType.RANGE) {
            if (selectionManager instanceof RangeSelectionManager) {
                if (((RangeSelectionManager) selectionManager).getDays() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets selection bar layout visibility
     */
    private void setSelectionBarVisibility() {
        flBottomSelectionBar.setVisibility(getCalendarOrientation() == OrientationHelper.HORIZONTAL ? View.VISIBLE : View.GONE);
        rvMultipleSelectedList.setVisibility(getCalendarOrientation() == OrientationHelper.HORIZONTAL && getSelectionType() == SelectionType.MULTIPLE ? View.VISIBLE : View.GONE);
        llRangeSelection.setVisibility(needToShowSelectedDaysRange() ? View.VISIBLE : View.GONE);
    }

    @Override
    @SelectionType
    public int getSelectionType() {
        return settingsManager.getSelectionType();
    }

    @Override
    public void setSelectionType(@SelectionType int selectionType) {
        settingsManager.setSelectionType(selectionType);
        setSelectionManager();
        monthAdapter.setSelectionManager(selectionManager);
        setSelectionBarVisibility();

        //Clear selections and selection bar
        multipleSelectionBarAdapter.setData(new ArrayList<SelectionBarItem>());
        selectionManager.clearSelections();
        if (selectionManager instanceof MultipleSelectionManager) {
            ((MultipleSelectionManager) selectionManager).clearCriteriaList();
        }

        update();
    }

    @Override
    public int getCalendarBackgroundColor() {
        return settingsManager.getCalendarBackgroundColor();
    }

    @Override
    public int getMonthTextColor() {
        return settingsManager.getMonthTextColor();
    }

    @Override
    public int getOtherDayTextColor() {
        return settingsManager.getOtherDayTextColor();
    }

    @Override
    public int getDayTextColor() {
        return settingsManager.getDayTextColor();
    }

    @Override
    public int getWeekendDayTextColor() {
        return settingsManager.getWeekendDayTextColor();
    }

    @Override
    public int getWeekDayTitleTextColor() {
        return settingsManager.getWeekDayTitleTextColor();
    }

    @Override
    public int getSelectedDayTextColor() {
        return settingsManager.getSelectedDayTextColor();
    }

    @Override
    public int getSelectedDayBackgroundColor() {
        return settingsManager.getSelectedDayBackgroundColor();
    }

    @Override
    public int getSelectedDayBackgroundStartColor() {
        return settingsManager.getSelectedDayBackgroundStartColor();
    }

    @Override
    public int getSelectedDayBackgroundEndColor() {
        return settingsManager.getSelectedDayBackgroundEndColor();
    }

    @Override
    public int getCurrentDayTextColor() {
        return settingsManager.getCurrentDayTextColor();
    }

    @Override
    public int getCurrentDayIconRes() {
        return settingsManager.getCurrentDayIconRes();
    }

    @Override
    public int getCurrentDaySelectedIconRes() {
        return settingsManager.getCurrentDaySelectedIconRes();
    }

    @Override
    public int getCalendarOrientation() {
        return settingsManager.getCalendarOrientation();
    }

    @Override
    public int getConnectedDayIconRes() {
        return settingsManager.getConnectedDayIconRes();
    }

    @Override
    public int getConnectedDaySelectedIconRes() {
        return settingsManager.getConnectedDaySelectedIconRes();
    }

    @Override
    public int getConnectedDayIconPosition() {
        return settingsManager.getConnectedDayIconPosition();
    }

    @Override
    public int getDisabledDayTextColor() {
        return settingsManager.getDisabledDayTextColor();
    }

    @Override
    public int getSelectionBarMonthTextColor() {
        return settingsManager.getSelectionBarMonthTextColor();
    }

    @Override
    public int getPreviousMonthIconRes() {
        return settingsManager.getPreviousMonthIconRes();
    }

    @Override
    public int getNextMonthIconRes() {
        return settingsManager.getNextMonthIconRes();
    }

    @Override
    public boolean isShowDaysOfWeek() {
        return settingsManager.isShowDaysOfWeek();
    }

    @Override
    public boolean isShowDaysOfWeekTitle() {
        return settingsManager.isShowDaysOfWeekTitle();
    }

    @Override
    public void setCalendarBackgroundColor(int calendarBackgroundColor) {
        settingsManager.setCalendarBackgroundColor(calendarBackgroundColor);
        setBackgroundColor(calendarBackgroundColor);
    }

    @Override
    public void setMonthTextColor(int monthTextColor) {
        settingsManager.setMonthTextColor(monthTextColor);
        update();
    }

    @Override
    public void setOtherDayTextColor(int otherDayTextColor) {
        settingsManager.setOtherDayTextColor(otherDayTextColor);
        update();
    }

    @Override
    public void setDayTextColor(int dayTextColor) {
        settingsManager.setDayTextColor(dayTextColor);
        update();
    }

    @Override
    public void setWeekendDayTextColor(int weekendDayTextColor) {
        settingsManager.setWeekendDayTextColor(weekendDayTextColor);
        update();
    }

    @Override
    public void setWeekDayTitleTextColor(int weekDayTitleTextColor) {
        settingsManager.setWeekDayTitleTextColor(weekDayTitleTextColor);
        for (int i = 0; i < llDaysOfWeekTitles.getChildCount(); i++) {
            ((SquareTextView) llDaysOfWeekTitles.getChildAt(i)).setTextColor(weekDayTitleTextColor);
        }
        update();
    }

    @Override
    public void setSelectedDayTextColor(int selectedDayTextColor) {
        settingsManager.setSelectedDayTextColor(selectedDayTextColor);
        update();
    }

    @Override
    public void setSelectedDayBackgroundColor(int selectedDayBackgroundColor) {
        settingsManager.setSelectedDayBackgroundColor(selectedDayBackgroundColor);
        update();
    }

    @Override
    public void setSelectedDayBackgroundStartColor(int selectedDayBackgroundStartColor) {
        settingsManager.setSelectedDayBackgroundStartColor(selectedDayBackgroundStartColor);
        update();
    }

    @Override
    public void setSelectedDayBackgroundEndColor(int selectedDayBackgroundEndColor) {
        settingsManager.setSelectedDayBackgroundEndColor(selectedDayBackgroundEndColor);
        update();
    }

    @Override
    public void setCurrentDayTextColor(int currentDayTextColor) {
        settingsManager.setCurrentDayTextColor(currentDayTextColor);
        update();
    }

    @Override
    public void setCurrentDayIconRes(int currentDayIconRes) {
        settingsManager.setCurrentDayIconRes(currentDayIconRes);
        update();
    }

    @Override
    public void setCurrentDaySelectedIconRes(int currentDaySelectedIconRes) {
        settingsManager.setCurrentDaySelectedIconRes(currentDaySelectedIconRes);
        update();
    }

    @Override
    public void setCalendarOrientation(int calendarOrientation) {
        clearSelections();
        settingsManager.setCalendarOrientation(calendarOrientation);
        setDaysOfWeekTitles();
        recreateInitialMonth();

        if (getCalendarOrientation() == LinearLayout.HORIZONTAL) {
            if (flNavigationButtons != null) {
                flNavigationButtons.setVisibility(VISIBLE);
            } else {
                createNavigationButtons();
            }
        } else {
            if (flNavigationButtons != null) {
                flNavigationButtons.setVisibility(GONE);
            }
        }

        setSelectionBarVisibility();
        update();
    }

    @Override
    public void setConnectedDayIconRes(int connectedDayIconRes) {
        settingsManager.setConnectedDayIconRes(connectedDayIconRes);
        update();
    }

    @Override
    public void setConnectedDaySelectedIconRes(int connectedDaySelectedIconRes) {
        settingsManager.setConnectedDaySelectedIconRes(connectedDaySelectedIconRes);
        update();
    }

    @Override
    public void setConnectedDayIconPosition(int connectedDayIconPosition) {
        settingsManager.setConnectedDayIconPosition(connectedDayIconPosition);
        update();
    }

    @Override
    public void setDisabledDayTextColor(int disabledDayTextColor) {
        settingsManager.setDisabledDayTextColor(disabledDayTextColor);
        update();
    }

    @Override
    public void setSelectionBarMonthTextColor(int selectionBarMonthTextColor) {
        settingsManager.setSelectionBarMonthTextColor(selectionBarMonthTextColor);
        update();
    }

    @Override
    public void setPreviousMonthIconRes(int previousMonthIconRes) {
        settingsManager.setPreviousMonthIconRes(previousMonthIconRes);
        setPreviousNavigationButton();
    }

    @Override
    public void setNextMonthIconRes(int nextMonthIconRes) {
        settingsManager.setNextMonthIconRes(nextMonthIconRes);
        setNextNavigationButton();
    }

    @Override
    public void setShowDaysOfWeek(boolean showDaysOfWeek) {
        settingsManager.setShowDaysOfWeek(showDaysOfWeek);
        recreateInitialMonth();
    }

    @Override
    public void setShowDaysOfWeekTitle(boolean showDaysOfWeekTitle) {
        settingsManager.setShowDaysOfWeekTitle(showDaysOfWeekTitle);
        if (showDaysOfWeekTitle) {
            showDaysOfWeekTitle();
        } else {
            hideDaysOfWeekTitle();
        }
    }

    @Override
    public int getFirstDayOfWeek() {
        return settingsManager.getFirstDayOfWeek();
    }

    @Override
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (firstDayOfWeek > 0 && firstDayOfWeek < 8) {
            settingsManager.setFirstDayOfWeek(firstDayOfWeek);
            recreateInitialMonth();
            createDaysOfWeekTitle();
        } else {
            throw new IllegalArgumentException("First day of week must be 1 - 7");
        }
    }


    /*
     * Removes selected day by click in bottom selection bar
     */
    @Override
    public void onMultipleSelectionListItemClick(final Day day) {
        if (getSelectionManager() instanceof MultipleSelectionManager) {
            ((MultipleSelectionManager) getSelectionManager()).removeDay(day);
            monthAdapter.notifyDataSetChanged();
        }
    }

    public void setOnMonthChangeListener(OnMonthChangeListener onMonthChangeListener) {
        this.onMonthChangeListener = onMonthChangeListener;
    }


}