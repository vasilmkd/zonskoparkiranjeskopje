package com.vasilev.zonskoparkiranjeskopje.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vasilev.zonskoparkiranjeskopje.DetailActivity;
import com.vasilev.zonskoparkiranjeskopje.R;

import static com.vasilev.zonskoparkiranjeskopje.DetailActivity.PARKING_ID_KEY;

public final class ViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    private View mItemLayout;
    private TextView mTimeTextView;
    private TextView mCarTextView;
    private TextView mZoneTextView;
    private TextView mCodeTextView;
    private Button mEndButton;
    private TextView mTimeRemainingTextView;

    public ViewHolder(@NonNull Context context, @NonNull View view) {
        super(view);
        mContext = context;
        mItemLayout = view.findViewById(R.id.item_layout);
        mTimeTextView = (TextView) mItemLayout.findViewById(R.id.text_view_time);
        mCarTextView = (TextView) mItemLayout.findViewById(R.id.text_view_car);
        mZoneTextView = (TextView) mItemLayout.findViewById(R.id.text_view_zone);
        mCodeTextView = (TextView) mItemLayout.findViewById(R.id.text_view_code);
        mEndButton = (Button) view.findViewById(R.id.button_end);
        mTimeRemainingTextView = (TextView) view.findViewById(R.id.text_view_time_remaining);
        mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                final long id = getItemId();
                final Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(PARKING_ID_KEY, id);
                mContext.startActivity(intent);
            }
        });
        if (mEndButton != null) {
            mEndButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {

                }
            });
        }
    }

    public void setTimeText(@NonNull String timeString) {
        mTimeTextView.setText(timeString);
    }

    public void setCarText(@NonNull String carText) {
        mCarTextView.setText(carText);
    }

    public void setZoneText(@NonNull String zoneText) {
        mZoneTextView.setText(zoneText);
    }

    public void setCodeText(@NonNull String codeText) {
        mCodeTextView.setText(codeText);
    }

    public void setTimeRemainingText(@Nullable String timeRemainingText) {
        if (mTimeRemainingTextView != null && timeRemainingText != null) {
            mTimeRemainingTextView.setText(timeRemainingText);
        }
    }

    @NonNull
    public TextView getCodeTextView() {
        return mCodeTextView;
    }
}
