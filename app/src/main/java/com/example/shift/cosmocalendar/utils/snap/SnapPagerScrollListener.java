package com.example.shift.cosmocalendar.utils.snap;

import android.view.Gravity;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shift.cosmocalendar.settings.SettingsManager;
import com.example.shift.cosmocalendar.view.CalendarView;
import com.example.shift.cosmocalendar.view.SlowdownRecyclerView;

public class SnapPagerScrollListener extends RecyclerView.OnScrollListener {

    // Constants
    public static final int ON_SCROLL = 0;
    public static final int ON_SETTLED = 1;

    @IntDef({ON_SCROLL, ON_SETTLED})
    public @interface Type {
    }

    public interface OnChangeListener {
        void onSnapped(int position);
    }

    // Properties
    private final PagerSnapHelper snapHelper;
    private final int type;
    private final boolean notifyOnInit;
    private final OnChangeListener listener;
    private int snapPosition;
    private SlowdownRecyclerView rvMonths;
    private CalendarView calendarView;
    private SettingsManager settingsManager;

    // Constructor
    public SnapPagerScrollListener(CalendarView calendarView, SettingsManager settingsmanager, SlowdownRecyclerView rvMonths ,
                                   @Type int type, boolean notifyOnInit, OnChangeListener listener) {
        this.settingsManager = settingsmanager;
        this.type = type;
        this.notifyOnInit = notifyOnInit;
        this.listener = listener;
        this.snapPosition = RecyclerView.NO_POSITION;
        this.rvMonths = rvMonths;
        this.calendarView = calendarView;
        this.snapHelper = new GravityPagerSnapHelper(settingsManager.getCalendarOrientation()
                == LinearLayoutManager.VERTICAL ? Gravity.TOP : Gravity.START,
                true, calendarView);
    }

    // Methods
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if ((type == ON_SCROLL) || !hasItemPosition()) {
            notifyListenerIfNeeded(getSnapPosition(recyclerView));
        }

        final RecyclerView.LayoutManager manager = rvMonths.getLayoutManager();

        int totalItemCount = manager.getItemCount();
        int firstVisibleItemPosition = calendarView.getFirstVisiblePosition(manager);
        calendarView.lastVisibleMonthPosition = firstVisibleItemPosition;

        if (firstVisibleItemPosition < 2) {
            calendarView.loadAsyncMonths(false);
        } else if (firstVisibleItemPosition >= totalItemCount - 2) {
            calendarView.loadAsyncMonths(true);
        }
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        if (type == ON_SETTLED && newState == RecyclerView.SCROLL_STATE_IDLE) {
            notifyListenerIfNeeded(getSnapPosition(recyclerView));
        }

        //Fix for bug with bottom selection bar and different month item height in horizontal mode (different count of weeks)
        View view = rvMonths.getLayoutManager().findViewByPosition(calendarView.getFirstVisiblePosition(rvMonths.getLayoutManager()));
        if (view != null) {
            view.requestLayout();
        }

        if (calendarView.getCalendarOrientation() == OrientationHelper.HORIZONTAL) {
            calendarView.multipleSelectionBarAdapter.notifyDataSetChanged();

            //Hide navigation buttons
            boolean show = newState != RecyclerView.SCROLL_STATE_DRAGGING;
            calendarView.ivPrevious.setVisibility(show ? View.VISIBLE : View.GONE);
            calendarView.ivNext.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        super.onScrollStateChanged(recyclerView, newState);
    }

    private int getSnapPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return RecyclerView.NO_POSITION;
        }

        View snapView = snapHelper.findSnapView(layoutManager);
        if (snapView == null) {
            return RecyclerView.NO_POSITION;
        }

        return layoutManager.getPosition(snapView);
    }

    private void notifyListenerIfNeeded(int newSnapPosition) {
        if (snapPosition != newSnapPosition) {
            if (notifyOnInit && !hasItemPosition()) {
                listener.onSnapped(newSnapPosition);
            } else if (hasItemPosition()) {
                listener.onSnapped(newSnapPosition);
            }

            snapPosition = newSnapPosition;
        }
    }

    private boolean hasItemPosition() {
        return snapPosition != RecyclerView.NO_POSITION;
    }
}
