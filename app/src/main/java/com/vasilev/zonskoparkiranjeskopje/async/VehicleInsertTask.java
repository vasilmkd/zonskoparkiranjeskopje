package com.vasilev.zonskoparkiranjeskopje.async;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vasilev.zonskoparkiranjeskopje.data.DataContract.VehicleEntry;

public final class VehicleInsertTask extends AsyncTask<ContentValues, Void, Void> {

    private Context mContext;

    public VehicleInsertTask(@NonNull Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    protected Void doInBackground(@NonNull ContentValues... params) {
        mContext.getContentResolver().bulkInsert(VehicleEntry.CONTENT_URI, params);
        return null;
    }
}
