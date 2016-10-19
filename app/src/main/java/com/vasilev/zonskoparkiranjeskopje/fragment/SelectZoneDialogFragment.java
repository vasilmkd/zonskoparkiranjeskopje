package com.vasilev.zonskoparkiranjeskopje.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.vasilev.zonskoparkiranjeskopje.R;
import com.vasilev.zonskoparkiranjeskopje.data.Zone;

import java.util.List;

public final class SelectZoneDialogFragment extends AppCompatDialogFragment {

    public interface Callbacks {
        void onZoneSelected(int position);
    }

    public static final String TAG = "select_zone_dialog_fragment";

    private static final String ZONE_LIST_KEY = "zone_list";

    private Callbacks mCallbacks;

    private String[] mZoneArray;
    private int mPosition;

    @NonNull
    public static SelectZoneDialogFragment newInstance(@NonNull List<Zone> zoneList) {
        final String[] zoneArray = new String[zoneList.size()];
        Zone zone;
        for (int i = 0; i < zoneList.size(); i++) {
            zone = zoneList.get(i);
            zoneArray[i] = zone.getCode() + " (" + zone.getName() + ")";
        }
        final SelectZoneDialogFragment fragment = new SelectZoneDialogFragment();
        final Bundle args = new Bundle();
        args.putStringArray(ZONE_LIST_KEY, zoneArray);
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
        if (!args.containsKey(ZONE_LIST_KEY)) {
            throw new IllegalStateException("Args must contain 'zone_list' key");
        }
        mZoneArray = args.getStringArray(ZONE_LIST_KEY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.zone);
        builder.setSingleChoiceItems(mZoneArray, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                mPosition = which;
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCallbacks.onZoneSelected(mPosition);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof Callbacks) {
            mCallbacks = (Callbacks) getActivity();
        }
    }
}
