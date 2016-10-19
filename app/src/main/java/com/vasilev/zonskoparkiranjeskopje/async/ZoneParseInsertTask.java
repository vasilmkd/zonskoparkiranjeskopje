package com.vasilev.zonskoparkiranjeskopje.async;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vasilev.zonskoparkiranjeskopje.R;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ZoneEntry;
import com.vasilev.zonskoparkiranjeskopje.parser.XmlParser;
import com.vasilev.zonskoparkiranjeskopje.data.Zone;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public final class ZoneParseInsertTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public ZoneParseInsertTask(@NonNull Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    protected Void doInBackground(@NonNull Void... params) {
        try {
            mContext.getContentResolver().delete(ZoneEntry.CONTENT_URI, null, null);
            final List<Zone> zoneList = XmlParser.parse(mContext.getResources().openRawResource(R.raw.zones));
            final ContentValues[] values = new ContentValues[zoneList.size()];
            for (int i = 0; i < zoneList.size(); i++) {
                Zone zone = zoneList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(ZoneEntry.COL_NAME, zone.getName());
                contentValues.put(ZoneEntry.COL_CODE, zone.getCode());
                contentValues.put(ZoneEntry.COL_LATITUDE, zone.getLocation().getLatitude());
                contentValues.put(ZoneEntry.COL_LONGITUDE, zone.getLocation().getLongitude());
                contentValues.put(ZoneEntry.COL_COST, zone.getCost());
                contentValues.put(ZoneEntry.COL_HOURS, zone.getHours());
                values[i] = contentValues;
            }
            mContext.getContentResolver().bulkInsert(ZoneEntry.CONTENT_URI, values);
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Zone parsing exception thrown");
        } catch (IOException e) {
            throw new IllegalStateException("Zone io exception thrown");
        }
        return null;
    }
}
