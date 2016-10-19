package com.vasilev.zonskoparkiranjeskopje.data;

import android.support.annotation.NonNull;

public final class Vehicle {

    private final String mName;
    private final String mLicence;

    public Vehicle(@NonNull String name, @NonNull String licence) {
        mName = name;
        mLicence = licence;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getLicence() {
        return mLicence;
    }
}
