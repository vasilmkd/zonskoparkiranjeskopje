package com.vasilev.zonskoparkiranjeskopje.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

public final class DataContract {

    public static final String CONTENT_AUTHORITY = "com.vasilev.zonskoparkiranjeskopje";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ZONES = "zones";
    public static final String PATH_VEHICLES = "vehicles";
    public static final String PATH_PARKING = "parking";

    public static final class ZoneEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ZONES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ZONES;

        public static final String TABLE_NAME = "zones";
        public static final String COL_NAME = "zone_name";
        public static final String COL_CODE = "zone_code";
        public static final String COL_LATITUDE = "zone_latitude";
        public static final String COL_LONGITUDE = "zone_longitude";
        public static final String COL_COST = "zone_cost";
        public static final String COL_HOURS = "zone_hours";

        @NonNull
        public static Uri buildZoneUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class VehicleEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VEHICLES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLES;

        public static final String TABLE_NAME = "vehicles";
        public static final String COL_NAME = "vehicle_name";
        public static final String COL_LICENCE = "vehicle_licence";
        public static final String COL_LAST_USED = "vehicle_last_used";

        @NonNull
        public static Uri buildVehicleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ParkingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PARKING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PARKING;

        public static final String TABLE_NAME = "parking";
        public static final String COL_ZONE_ID = "zone_id";
        public static final String COL_VEHICLE_ID = "vehicle_id";
        public static final String COL_TIME_START = "parking_time_start";
        public static final String COL_TIME_END = "parking_time_end";

        @NonNull
        public static Uri buildParkingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
