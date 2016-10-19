package com.vasilev.zonskoparkiranjeskopje.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vasilev.zonskoparkiranjeskopje.R;
import com.vasilev.zonskoparkiranjeskopje.adapter.HomeRecyclerAdapter;
import com.vasilev.zonskoparkiranjeskopje.adapter.HomeRecyclerAdapter.AdapterType;
import com.vasilev.zonskoparkiranjeskopje.async.PrepareViewTask;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ParkingEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.VehicleEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ZoneEntry;

import static com.vasilev.zonskoparkiranjeskopje.adapter.HomeRecyclerAdapter.ADAPTER_TYPE_CURRENT;
import static com.vasilev.zonskoparkiranjeskopje.adapter.HomeRecyclerAdapter.ADAPTER_TYPE_PAST;

public final class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ADAPTER_TYPE_KEY = "adapter_type";

    private @AdapterType int mAdapterType;

    private static final int LOADER_ID = 0;

    private HomeRecyclerAdapter mAdapter;

    @NonNull
    public static HomeFragment newInstance(@AdapterType int adapterType) {
        final HomeFragment fragment = new HomeFragment();
        final Bundle args = new Bundle();
        args.putInt(ADAPTER_TYPE_KEY, adapterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException("Fragment has no arguments");
        }
        if (!args.containsKey(ADAPTER_TYPE_KEY)) {
            throw new IllegalStateException("Args must contain 'adapter_type' key");
        }
        final int adapterType = args.getInt(ADAPTER_TYPE_KEY);
        switch (adapterType) {
            case ADAPTER_TYPE_CURRENT:
                mAdapterType = ADAPTER_TYPE_CURRENT;
                break;
            case ADAPTER_TYPE_PAST:
                mAdapterType = ADAPTER_TYPE_PAST;
                break;
            default:
                throw new IllegalArgumentException("Unknown adapter type: " + adapterType);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new HomeRecyclerAdapter(getContext(), null, mAdapterType);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getLoaderManager().getLoader(LOADER_ID) == null) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        final String[] projection = {
                ParkingEntry.TABLE_NAME + "." + ParkingEntry._ID,
                ParkingEntry.COL_TIME_START,
                VehicleEntry.COL_NAME,
                VehicleEntry.COL_LICENCE,
                ZoneEntry.COL_NAME,
                ZoneEntry.COL_CODE,
                ParkingEntry.COL_TIME_END
        };
        final String selection;
        final String sortOrder;
        switch (mAdapterType) {
            case ADAPTER_TYPE_CURRENT:
                selection = ParkingEntry.TABLE_NAME + "." + ParkingEntry.COL_TIME_END + " > ?";
                sortOrder = ParkingEntry.TABLE_NAME + "." + ParkingEntry.COL_TIME_START + " ASC";
                break;
            case ADAPTER_TYPE_PAST:
                selection = ParkingEntry.TABLE_NAME + "." + ParkingEntry.COL_TIME_END + " <= ?";
                sortOrder = ParkingEntry.TABLE_NAME + "." + ParkingEntry.COL_TIME_START + " DESC";
                break;
            default:
                throw new IllegalArgumentException("Unknown adapter type: " + mAdapterType);
        }
        final String[] selectionArgs = { Long.toString(System.currentTimeMillis()) };
        return new CursorLoader(getContext(), ParkingEntry.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, @Nullable Cursor data) {
        final PrepareViewTask task = new PrepareViewTask(getContext(), mAdapter);
        task.execute(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.setData(null);
    }
}
