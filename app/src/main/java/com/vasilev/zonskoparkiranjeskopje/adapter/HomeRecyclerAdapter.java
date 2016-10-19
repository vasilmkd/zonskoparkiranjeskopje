package com.vasilev.zonskoparkiranjeskopje.adapter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vasilev.zonskoparkiranjeskopje.R;
import com.vasilev.zonskoparkiranjeskopje.bind.ParkingItem;
import com.vasilev.zonskoparkiranjeskopje.util.TextViewUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public final class HomeRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ADAPTER_TYPE_CURRENT, ADAPTER_TYPE_PAST})
    public @interface AdapterType {}

    public static final int ADAPTER_TYPE_CURRENT = 0;
    public static final int ADAPTER_TYPE_PAST = 1;

    private Context mContext;
    private @AdapterType int mAdapterType;
    private List<ParkingItem> mParkingItemList;

    public HomeRecyclerAdapter(@NonNull Context context, @Nullable List<ParkingItem> parkingItemList, @AdapterType int adapterType) {
        mContext = context;
        mAdapterType = adapterType;
        mParkingItemList = parkingItemList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        switch (mAdapterType) {
            case ADAPTER_TYPE_CURRENT:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_parking_current, parent, false);
                break;
            case ADAPTER_TYPE_PAST:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_parking_past, parent, false);
                break;
            default:
                throw new IllegalArgumentException("Unknown adapter type: " + mAdapterType);
        }
        return new ViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mParkingItemList == null) {
            throw new IllegalStateException("onBindViewHolder() called with invalid data");
        }
        final ParkingItem parkingItem = mParkingItemList.get(position);
        holder.setCarText(parkingItem.getCarString());
        holder.setZoneText(parkingItem.getZoneString());
        holder.setCodeText(parkingItem.getCodeString());
        holder.setTimeText(parkingItem.getTimeString());
        holder.setTimeRemainingText(parkingItem.getTimeRemainingString());
        TextViewUtil.setTint(holder.getCodeTextView(), parkingItem.getZoneColor(), parkingItem.getTextColor());
    }

    @Override
    public int getItemCount() {
        if (mParkingItemList != null) {
            return mParkingItemList.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (hasStableIds() && mParkingItemList != null) {
            return mParkingItemList.get(position).getId();
        }
        return RecyclerView.NO_ID;
    }

    public void setData(@Nullable List<ParkingItem> data) {
        if (mParkingItemList == data) {
            return;
        }
        if (data != null) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, mParkingItemList.size());
        }
        mParkingItemList = data;
    }
}
