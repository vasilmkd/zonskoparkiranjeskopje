package com.vasilev.zonskoparkiranjeskopje.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.vasilev.zonskoparkiranjeskopje.R;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract;

import static com.vasilev.zonskoparkiranjeskopje.util.LicenceUtil.checkLicence;

public final class RegisterVehicleDialogFragment extends AppCompatDialogFragment {

    public interface Callbacks {
        void onDialogDismissed(@NonNull ContentValues values);
    }

    public static final String TAG = "register_vehicle_dialog_fragment";

    private Callbacks mCallbacks;

    private AppCompatDialog mDialog;

    private TextInputEditText mNameEditText;
    private TextInputLayout mLicenceTextInputLayout;
    private TextInputEditText mLicenceEditText;
    private Button mOkButton;

    @NonNull
    public static RegisterVehicleDialogFragment newInstance() {
        return new RegisterVehicleDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mDialog = new AppCompatDialog(getContext());
        mDialog.setTitle(R.string.dialog_register_vehicle_title);
        mDialog.setContentView(R.layout.dialog_register_vehicle);
        mNameEditText = (TextInputEditText) mDialog.findViewById(R.id.edit_text_name);
        mLicenceTextInputLayout = (TextInputLayout) mDialog.findViewById(R.id.text_input_layout_licence);
        mLicenceEditText = (TextInputEditText) mDialog.findViewById(R.id.edit_text_licence);
        mOkButton = (Button) mDialog.findViewById(R.id.button_ok);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof Callbacks) {
            mCallbacks = (Callbacks) getActivity();
        }
        mOkButton.setOnClickListener(v -> {
            if (checkLicence(mLicenceEditText.getText().toString())) {
                ContentValues values = new ContentValues();
                values.put(DataContract.VehicleEntry.COL_NAME, mNameEditText.getText().toString().trim());
                values.put(DataContract.VehicleEntry.COL_LICENCE, mLicenceEditText.getText().toString().trim().toUpperCase());
                values.put(DataContract.VehicleEntry.COL_LAST_USED, System.currentTimeMillis());
                mCallbacks.onDialogDismissed(values);
                mDialog.dismiss();
            } else {
                mLicenceTextInputLayout.setError(getString(R.string.licence_wrong));
            }
        });

        mDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                if (mNameEditText.getText().length() == 0) {
                    mOkButton.setEnabled(false);
                }
                else {
                    if (checkLicence(mLicenceEditText.getText().toString().trim().toUpperCase())) {
                        mOkButton.setEnabled(true);
                        mLicenceTextInputLayout.setError("");
                    }
                }
            }
        });

        mLicenceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                if (mNameEditText.getText().length() > 0 && checkLicence(mLicenceEditText.getText().toString().trim().toUpperCase())) {
                    mOkButton.setEnabled(true);
                    mLicenceTextInputLayout.setError("");
                } else {
                    mOkButton.setEnabled(false);
                    mLicenceTextInputLayout.setError(getString(R.string.licence_check));
                }
            }
        });
    }
}
