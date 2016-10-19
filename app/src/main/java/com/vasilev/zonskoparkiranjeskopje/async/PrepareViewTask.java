package com.vasilev.zonskoparkiranjeskopje.async;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.vasilev.zonskoparkiranjeskopje.R;
import com.vasilev.zonskoparkiranjeskopje.adapter.HomeRecyclerAdapter;
import com.vasilev.zonskoparkiranjeskopje.bind.ParkingItem;
import com.vasilev.zonskoparkiranjeskopje.cache.ColumnIndexCache;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ParkingEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.VehicleEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ZoneEntry;
import com.vasilev.zonskoparkiranjeskopje.util.ColorUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.vasilev.zonskoparkiranjeskopje.util.TimePeriod.DAY;
import static com.vasilev.zonskoparkiranjeskopje.util.TimePeriod.HOUR;
import static com.vasilev.zonskoparkiranjeskopje.util.TimePeriod.MINUTE;
import static com.vasilev.zonskoparkiranjeskopje.util.TimePeriod.MONTH;
import static com.vasilev.zonskoparkiranjeskopje.util.TimePeriod.YEAR;

public final class PrepareViewTask extends AsyncTask<Cursor, Void, List<ParkingItem>> {

    private static final String LOG_TAG = PrepareViewTask.class.getSimpleName();

    private final Context mContext;
    private final ColumnIndexCache mCache;
    private final HomeRecyclerAdapter mAdapter;

    public PrepareViewTask(@NonNull Context context, @NonNull HomeRecyclerAdapter adapter) {
        Log.d(LOG_TAG, "Task created");
        mContext = context;
        mAdapter = adapter;
        mCache = new ColumnIndexCache();
    }

    @Override
    protected List<ParkingItem> doInBackground(@NonNull Cursor... params) {
        Log.d(LOG_TAG, "Task started");
        final Cursor cursor = params[0];
        final List<ParkingItem> parkingItemList = new ArrayList<>(cursor.getCount());

        long _id;
        long timeStart;
        long timeEnd;
        String carName;
        String licencePlate;
        String zoneString;
        String codeString;

        String carString;
        @ColorRes int zoneColorRes;
        @ColorRes int textColorRes;
        @ColorInt int zoneColor;
        @ColorInt int textColor;

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        String timeStartString;
        long timeRemaining;
        String timeRemainingString;

        long timeDelta;
        String timeString;

        int minutes;
        int hours;
        int days;
        int months;
        int years;

        for (int position = 0; position < cursor.getCount(); position++) {
            if (!cursor.moveToPosition(position)) {
                throw new IllegalStateException("Could not move cursor to position: " + position);
            }

            _id = cursor.getLong(mCache.get(cursor, ParkingEntry._ID));
            timeStart = cursor.getLong(mCache.get(cursor, ParkingEntry.COL_TIME_START));
            timeEnd = cursor.getLong(mCache.get(cursor, ParkingEntry.COL_TIME_END));
            carName = cursor.getString(mCache.get(cursor, VehicleEntry.COL_NAME));
            licencePlate = cursor.getString(mCache.get(cursor, VehicleEntry.COL_LICENCE));
            zoneString = cursor.getString(mCache.get(cursor, ZoneEntry.COL_NAME));
            codeString = cursor.getString(mCache.get(cursor, ZoneEntry.COL_CODE));

            carString = carName + " (" + licencePlate + ")";

            zoneColorRes = ColorUtil.getZoneColorRes(codeString.charAt(0));
            textColorRes = ColorUtil.getTextColorRes(codeString.charAt(0));
            zoneColor = ContextCompat.getColor(mContext, zoneColorRes);
            textColor = ContextCompat.getColor(mContext, textColorRes);

            timeStartString = simpleDateFormat.format(new Date(timeStart));
            timeRemaining = timeEnd - System.currentTimeMillis();
            if (timeRemaining > 0) {
                timeRemainingString = mContext.getString(R.string.remaining) + " " + simpleDateFormat.format(new Date(timeRemaining));
            } else {
                timeRemainingString = null;
            }
            timeString = timeStartString;

            timeDelta = System.currentTimeMillis() - timeEnd;

            if (timeDelta > 0) {
                minutes = (int) (timeDelta / MINUTE);
                if (minutes >= 0 && minutes < 60) {
                    timeString = Integer.toString(minutes) + " " + mContext.getString(R.string.time_period_minutes);
                }
                hours = (int) (timeDelta / HOUR);
                if (hours > 0 && hours < 24) {
                    timeString = Integer.toString(hours) + " " + mContext.getString(R.string.time_period_hours);
                }
                days = (int) (timeDelta / DAY);
                if (days > 0 && days < 30) {
                    timeString = Integer.toString(days) + " " + mContext.getString(R.string.time_period_days);
                }
                months = (int) (timeDelta / MONTH);
                if (months > 0 && months < 12) {
                    timeString = Integer.toString(months) + " " + mContext.getString(R.string.time_period_months);
                }
                years = (int) (timeDelta / YEAR);
                if (years > 0) {
                    timeString = Integer.toString(years) + " " + mContext.getString(R.string.time_period_years);
                }
            }

            parkingItemList.add(new ParkingItem(_id, zoneString, codeString, carString,
                    zoneColor, textColor, timeRemainingString, timeString));
        }

        Log.d(LOG_TAG, "Task ended");
        return parkingItemList;
    }

    @Override
    protected void onPostExecute(List<ParkingItem> parkingItemList) {
        super.onPostExecute(parkingItemList);
        mAdapter.setData(parkingItemList);
        Log.d(LOG_TAG, "Data set");
    }
}
