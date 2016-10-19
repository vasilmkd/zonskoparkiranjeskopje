package com.vasilev.zonskoparkiranjeskopje.util;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ParkingEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.VehicleEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ZoneEntry;

import static com.vasilev.zonskoparkiranjeskopje.data.DataProvider.PARKING;
import static com.vasilev.zonskoparkiranjeskopje.data.DataProvider.VEHICLES;
import static com.vasilev.zonskoparkiranjeskopje.data.DataProvider.ZONES;

public final class UriMatcherUtil {

    @NonNull
    public static String getTableName(int match) {
        switch (match) {
            case ZONES:
                return ZoneEntry.TABLE_NAME;
            case VEHICLES:
                return VehicleEntry.TABLE_NAME;
            case PARKING:
                return ParkingEntry.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown match: " + match);
        }
    }

    @NonNull
    public static Uri getReturnUri(int match, long _id) {
        switch (match) {
            case ZONES:
                return ZoneEntry.buildZoneUri(_id);
            case VEHICLES:
                return VehicleEntry.buildVehicleUri(_id);
            case PARKING:
                return ParkingEntry.buildParkingUri(_id);
            default:
                throw new IllegalArgumentException("Unknown match: " + match);
        }
    }
}
