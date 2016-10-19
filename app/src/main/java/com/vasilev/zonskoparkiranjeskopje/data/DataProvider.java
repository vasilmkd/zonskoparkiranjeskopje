package com.vasilev.zonskoparkiranjeskopje.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ParkingEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.VehicleEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ZoneEntry;
import com.vasilev.zonskoparkiranjeskopje.util.UriMatcherUtil;

public final class DataProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;

    public static final int ZONES = 100;
    public static final int VEHICLES = 200;
    public static final int PARKING = 300;

    private static final SQLiteQueryBuilder sQueryBuilder;

    static {
        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(ParkingEntry.TABLE_NAME + " INNER JOIN " + ZoneEntry.TABLE_NAME + " ON " +
                ParkingEntry.TABLE_NAME + "." + ParkingEntry.COL_ZONE_ID + " = " + ZoneEntry.TABLE_NAME + "." + ZoneEntry._ID +
                " INNER JOIN " + VehicleEntry.TABLE_NAME + " ON " +
                ParkingEntry.TABLE_NAME + "." + ParkingEntry.COL_VEHICLE_ID + " = " + VehicleEntry.TABLE_NAME + "." + VehicleEntry._ID);
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DataContract.PATH_ZONES, ZONES);
        matcher.addURI(authority, DataContract.PATH_VEHICLES, VEHICLES);
        matcher.addURI(authority, DataContract.PATH_PARKING, PARKING);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @NonNull
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ZONES:
                return ZoneEntry.CONTENT_TYPE;
            case VEHICLES:
                return VehicleEntry.CONTENT_TYPE;
            case PARKING:
                return ParkingEntry.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final Cursor cursor;
        final int match = sUriMatcher.match(uri);
        final String tableName = UriMatcherUtil.getTableName(match);
        switch (sUriMatcher.match(uri)) {
            case ZONES:
            case VEHICLES:
                cursor = mDbHelper.getReadableDatabase().query(tableName,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PARKING:
                cursor = sQueryBuilder.query(mDbHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        if (getContext() == null) {
            throw new IllegalStateException("Context is null");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        final String tableName = UriMatcherUtil.getTableName(match);
        final long _id = db.insert(tableName, null, values);
        if (_id <= 0) {
            throw new SQLException("Failed to insert row into: " + uri);
        }
        final Uri returnUri = UriMatcherUtil.getReturnUri(match, _id);
        if (getContext() == null) {
            throw new IllegalStateException("Context is null");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        final String tableName = UriMatcherUtil.getTableName(match);
        final int rowsUpdated = db.update(tableName, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            if (getContext() == null) {
                throw new IllegalStateException("Context is null");
            }
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        final String tableName = UriMatcherUtil.getTableName(match);
        final int rowsDeleted = db.delete(tableName, selection, selectionArgs);
        if (rowsDeleted != 0) {
            if (getContext() == null) {
                throw new IllegalStateException("Context is null");
            }
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        final String tableName = UriMatcherUtil.getTableName(match);
        int returnCount;
        switch (match) {
            case ZONES:
            case VEHICLES:
            case PARKING:
                db.beginTransaction();
                returnCount = 0;
                try {
                    long _id;
                    for (ContentValues contentValues: values) {
                        _id = db.insert(tableName, null, contentValues);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (getContext() == null) {
                    throw new IllegalStateException("Context is null");
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
