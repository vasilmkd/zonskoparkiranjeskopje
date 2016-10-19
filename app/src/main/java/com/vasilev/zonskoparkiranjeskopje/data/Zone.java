package com.vasilev.zonskoparkiranjeskopje.data;

import android.location.Location;
import android.support.annotation.NonNull;

public final class Zone {

    private final String mName;
    private final String mCode;
    private final Location mLocation;
    private final int mCost;
    private final int mHours;

    public Zone(@NonNull String name, @NonNull String code, double latitude, double longitude, int cost, int hours) {
        mName = name;
        mCode = code;
        mLocation = new Location(mName);
        mLocation.setLatitude(latitude);
        mLocation.setLongitude(longitude);
        mCost = cost;
        mHours = hours;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getCode() {
        return mCode;
    }

    @NonNull
    public Location getLocation() {
        return mLocation;
    }

    public int getCost() {
        return mCost;
    }

    public int getHours() {
        return mHours;
    }
}
