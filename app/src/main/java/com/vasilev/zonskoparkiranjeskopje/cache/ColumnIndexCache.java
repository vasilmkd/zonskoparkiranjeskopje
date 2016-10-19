package com.vasilev.zonskoparkiranjeskopje.cache;

import android.database.Cursor;
import android.support.v4.util.ArrayMap;

public final class ColumnIndexCache {

    private ArrayMap<String, Integer> mMap = new ArrayMap<>();

    public int get(Cursor cursor, String columnName) {
        if (!mMap.containsKey(columnName)) {
            mMap.put(columnName, cursor.getColumnIndex(columnName));
        }
        return mMap.get(columnName);
    }

    public void clear() {
        mMap.clear();
    }
}
