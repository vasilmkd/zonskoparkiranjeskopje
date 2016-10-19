package com.vasilev.zonskoparkiranjeskopje.bind;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class ParkingItem {
    
    private final long mId;
    private final String mZoneString;
    private final String mCodeString;
    private final String mCarString;
    private final @ColorInt int mZoneColor;
    private final @ColorInt int mTextColor;
    private final String mTimeRemainingString;
    private final String mTimeString;

    public ParkingItem(long _id, @NonNull String zoneString, @NonNull String codeString,
                       @NonNull String carString, @ColorInt int zoneColor, @ColorInt int textColor,
                       @Nullable String timeRemainingString, @NonNull String timeString) {
        mId = _id;
        mZoneString = zoneString;
        mCodeString = codeString;
        mCarString = carString;
        mZoneColor = zoneColor;
        mTextColor = textColor;
        mTimeRemainingString = timeRemainingString;
        mTimeString = timeString;
    }

    public long getId() {
        return mId;
    }

    @NonNull
    public String getZoneString() {
        return mZoneString;
    }

    @NonNull
    public String getCodeString() {
        return mCodeString;
    }

    @NonNull
    public String getCarString() {
        return mCarString;
    }

    public int getZoneColor() {
        return mZoneColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    @Nullable
    public String getTimeRemainingString() {
        return mTimeRemainingString;
    }

    @NonNull
    public String getTimeString() {
        return mTimeString;
    }
}
