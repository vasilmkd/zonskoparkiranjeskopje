package com.vasilev.zonskoparkiranjeskopje.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.vasilev.zonskoparkiranjeskopje.R;
import com.vasilev.zonskoparkiranjeskopje.data.Vehicle;

import java.util.List;

public final class SelectVehicleDialogFragment extends AppCompatDialogFragment {

    public interface Callbacks {
        void onVehicleSelected(int position);
    }

    public static final String TAG = "select_vehicle_dialog_fragment";

    private static final String VEHICLE_LIST_KEY = "vehicle_list";

    private Callbacks mCallbacks;

    private String[] mVehicleArray;
    private int mPosition;

    @NonNull
    public static SelectVehicleDialogFragment newInstance(@NonNull List<Vehicle> vehicleList) {
        final String[] vehicleArray = new String[vehicleList.size()];
        Vehicle vehicle;
        for (int i = 0; i < vehicleList.size(); i++) {
            vehicle = vehicleList.get(i);
            vehicleArray[i] = vehicle.getName() + " (" + vehicle.getLicence() + ")";
        }
        final SelectVehicleDialogFragment fragment = new SelectVehicleDialogFragment();
        final Bundle args = new Bundle();
        args.putStringArray(VEHICLE_LIST_KEY, vehicleArray);
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
        if (!args.containsKey(VEHICLE_LIST_KEY)) {
            throw new IllegalStateException("Args must contain 'vehicle_list' key");
        }
        mVehicleArray = args.getStringArray(VEHICLE_LIST_KEY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.vehicle);
        builder.setSingleChoiceItems(mVehicleArray, 0, (dialog, which) -> {
            mPosition = which;
        });
        builder.setPositiveButton(R.string.ok,
                (dialog, which) -> mCallbacks.onVehicleSelected(mPosition));
        builder.setNegativeButton(R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof Callbacks) {
            mCallbacks = (Callbacks) getActivity();
        }
    }
}
