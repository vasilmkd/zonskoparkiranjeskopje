package com.vasilev.zonskoparkiranjeskopje.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ParkingEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.VehicleEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ZoneEntry;

public final class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "zonskoparkiranjeskopje.db";

    private static final String SQL_CREATE_ZONES_TABLE = "CREATE TABLE " +
            ZoneEntry.TABLE_NAME + " (" +
            ZoneEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ZoneEntry.COL_NAME + " TEXT NOT NULL, " +
            ZoneEntry.COL_CODE + " TEXT NOT NULL, " +
            ZoneEntry.COL_LATITUDE + " REAL NOT NULL, " +
            ZoneEntry.COL_LONGITUDE + " REAL NOT NULL, " +
            ZoneEntry.COL_COST + " INTEGER NOT NULL, " +
            ZoneEntry.COL_HOURS + " INTEGER NOT NULL" +
            ");";

    private static final String SQL_CREATE_VEHICLE_TABLE = "CREATE TABLE " +
            VehicleEntry.TABLE_NAME + " (" +
            VehicleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            VehicleEntry.COL_NAME + " TEXT NOT NULL, " +
            VehicleEntry.COL_LICENCE + " TEXT NOT NULL, " +
            VehicleEntry.COL_LAST_USED + " INTEGER NOT NULL" +
            ");";

    private static final String SQL_CREATE_PARKING_TABLE = "CREATE TABLE " +
            ParkingEntry.TABLE_NAME + " (" +
            ParkingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ParkingEntry.COL_ZONE_ID + " INTEGER NOT NULL, " +
            ParkingEntry.COL_VEHICLE_ID + " INTEGER NOT NULL, " +
            ParkingEntry.COL_TIME_START + " INTEGER NOT NULL, " +
            ParkingEntry.COL_TIME_END + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + ParkingEntry.COL_ZONE_ID + ") REFERENCES " + ZoneEntry.TABLE_NAME + " (" + ZoneEntry._ID + "), " +
            "FOREIGN KEY (" + ParkingEntry.COL_VEHICLE_ID + ") REFERENCES " + VehicleEntry.TABLE_NAME + " (" + VehicleEntry._ID + ")" +
            ");";

    private static final String SQL_DROP_ZONES_TABLE = "DROP TABLE IF EXISTS " + ZoneEntry.TABLE_NAME;

    private static final String SQL_DROP_VEHICLE_TABLE = "DROP TABLE IF EXISTS " + VehicleEntry.TABLE_NAME;

    private static final String SQL_DROP_PARKING_TABLE = "DROP TABLE IF EXISTS " + ParkingEntry.TABLE_NAME;

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ZONES_TABLE);
        db.execSQL(SQL_CREATE_VEHICLE_TABLE);
        db.execSQL(SQL_CREATE_PARKING_TABLE);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_ZONES_TABLE);
        db.execSQL(SQL_DROP_VEHICLE_TABLE);
        db.execSQL(SQL_DROP_PARKING_TABLE);
        onCreate(db);
    }
}
