package com.vasilev.zonskoparkiranjeskopje;

import android.animation.Animator;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vasilev.zonskoparkiranjeskopje.cache.ColumnIndexCache;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ParkingEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.VehicleEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ZoneEntry;
import com.vasilev.zonskoparkiranjeskopje.data.Zone;
import com.vasilev.zonskoparkiranjeskopje.util.ColorUtil;
import com.vasilev.zonskoparkiranjeskopje.util.ImageViewUtil;
import com.vasilev.zonskoparkiranjeskopje.util.TextViewUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.vasilev.zonskoparkiranjeskopje.util.TimePeriod.HOUR;

public final class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    public static final String PARKING_ID_KEY = "parking_id";
    private long mParkingId;

    private boolean mRunAnimations;

    private static final int LOADER_ID = 0;

    private Zone mZone;

    private ColumnIndexCache mCache;

    private CoordinatorLayout mCoordinatorLayout;

    private MapView mMapView;
    private TextView mCodeTextView;

    private ImageView mCarImageView;
    private TextView mCarTextView;
    private TextView mLicenceTextView;

    private ImageView mTimeImageView;
    private TextView mHoursTextView;
    private TextView mTimeTextView;

    private ImageView mCostImageView;
    private TextView mCostTextView;
    private TextView mHourCostTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mRunAnimations = savedInstanceState == null;

        mParkingId = getIntent().getLongExtra(PARKING_ID_KEY, -1L);
        mCache = new ColumnIndexCache();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new IllegalStateException("Layout must have a view with id 'toolbar'");
        }
        toolbar.setNavigationOnClickListener(v ->
            NavUtils.navigateUpFromSameTask(DetailActivity.this));
        setSupportActionBar(toolbar);

        mMapView = (MapView) findViewById(R.id.map_view);
        if (mMapView == null) {
            throw new IllegalStateException("Layout must have a view with id 'map_view'");
        }
        mMapView.onCreate(savedInstanceState);
        mMapView.setClickable(false);

        mMapView.setVisibility(View.INVISIBLE);

        mCodeTextView = (TextView) findViewById(R.id.text_view_code);

        mCarImageView = (ImageView) findViewById(R.id.image_view_car);
        mCarTextView = (TextView) findViewById(R.id.text_view_car);
        mLicenceTextView = (TextView) findViewById(R.id.text_view_licence);

        mTimeImageView = (ImageView) findViewById(R.id.image_view_time);
        mHoursTextView = (TextView) findViewById(R.id.text_view_hours);
        mTimeTextView = (TextView) findViewById(R.id.text_view_time);

        mCostImageView = (ImageView) findViewById(R.id.image_view_cost);
        mCostTextView = (TextView) findViewById(R.id.text_view_cost);
        mHourCostTextView = (TextView) findViewById(R.id.text_view_cost_hour);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                final String latitude = Double.toString(mZone.getLocation().getLatitude());
                final String longitude = Double.toString(mZone.getLocation().getLongitude());
                final Uri uri = Uri.parse("geo:" + latitude + "," + longitude);
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.cannot_open_map, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        final String[] projection = new String[]{
                ZoneEntry.COL_NAME,
                ZoneEntry.COL_CODE,
                ZoneEntry.COL_LATITUDE,
                ZoneEntry.COL_LONGITUDE,
                VehicleEntry.COL_NAME,
                VehicleEntry.COL_LICENCE,
                ParkingEntry.COL_TIME_START,
                ParkingEntry.COL_TIME_END,
                ZoneEntry.COL_COST,
                ZoneEntry.COL_HOURS
        };

        final String selection = ParkingEntry.TABLE_NAME + "." + ParkingEntry._ID + " = ?";

        final String[] selectionArgs = { Long.toString(mParkingId) };

        return new CursorLoader(this, ParkingEntry.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, @Nullable Cursor data) {
        Log.d("DetailActivity", "Called onLoadFinished()");

        if (data == null) {
            throw new IllegalStateException("Cursor is null");
        }
        data.moveToFirst();

        final double latitude = data.getDouble(mCache.get(data, ZoneEntry.COL_LATITUDE));
        final double longitude = data.getDouble(mCache.get(data, ZoneEntry.COL_LONGITUDE));
        final String zoneString = data.getString(mCache.get(data, ZoneEntry.COL_NAME));
        final String codeString = data.getString(mCache.get(data, ZoneEntry.COL_CODE));
        final String carString = data.getString(mCache.get(data, VehicleEntry.COL_NAME));
        final String licenceString = data.getString(mCache.get(data, VehicleEntry.COL_LICENCE));
        final long timeStart = data.getLong(mCache.get(data, ParkingEntry.COL_TIME_START));
        final long timeEnd = data.getLong(mCache.get(data, ParkingEntry.COL_TIME_END));
        final long timeNow = System.currentTimeMillis();
        final int hourCost = data.getInt(mCache.get(data, ZoneEntry.COL_COST));
        final int zoneHours = data.getInt(mCache.get(data, ZoneEntry.COL_HOURS));

        mZone = new Zone(zoneString, codeString, latitude, longitude, hourCost, zoneHours);
        mMapView.getMapAsync(this);

        final long timeDelta = timeEnd > timeNow ? timeNow - timeStart : timeEnd - timeStart;
        final int hours = (int) (timeDelta / HOUR) + 1;
        final @StringRes int timePeriodStringRes;

        if (hours <= 0 || hours > 24) {
            throw new IllegalArgumentException("Invalid hour number: " + hours);
        } else if (hours == 1) {
            timePeriodStringRes = R.string.time_period_hours_single;
        } else if (hours < 6) {
            timePeriodStringRes = R.string.time_period_hours_few;
        } else {
            timePeriodStringRes = R.string.time_period_hours_many;
        }

        final String hoursString = Integer.toString(hours) + " " + getString(timePeriodStringRes);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        final String timeStartString = simpleDateFormat.format(new Date(timeStart));
        final String timeString;
        if (timeEnd > timeNow) {
            timeString = getString(R.string.started) + " " + timeStartString;
        } else {
            final String timeEndString = simpleDateFormat.format(new Date(timeEnd));
            timeString = timeStartString + " - " + timeEndString;
        }

        final int cost = hours * hourCost;
        final String costString = Integer.toString(cost) + " " + getString(R.string.macedonian_denars);

        final String hourCostString = Integer.toString(hourCost) + " " + getString(R.string.denars_per_hour);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("ActionBar is null");
        }
        getSupportActionBar().setTitle(zoneString);
        mCodeTextView.setText(codeString);
        mCarTextView.setText(carString);
        mLicenceTextView.setText(licenceString);
        mHoursTextView.setText(hoursString);
        mTimeTextView.setText(timeString);
        mCostTextView.setText(costString);
        mHourCostTextView.setText(hourCostString);
        setColor(codeString);

        if (timeEnd > timeNow) {
            final long timeRemaining = timeEnd - timeNow;
            final String timeRemainingString = getString(R.string.remaining) + " " + simpleDateFormat.format(new Date(timeRemaining));
            Snackbar.make(mCoordinatorLayout, timeRemainingString, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.end, v -> {})
                    .setActionTextColor(ContextCompat.getColor(this, R.color.accent))
                    .show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCache.clear();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mRunAnimations) {
                final int centerX = (mCodeTextView.getLeft() + mCodeTextView.getRight()) / 2;
                final int centerY = (mCodeTextView.getTop() + mCodeTextView.getBottom()) / 2;
                final float startRadius = 0.0f;
                final float endRadius = (float) Math.hypot(mMapView.getWidth(), mMapView.getHeight());
                final Animator animator = ViewAnimationUtils.createCircularReveal(
                        mMapView, centerX, centerY, startRadius, endRadius);
                mMapView.setVisibility(View.VISIBLE);
                animator.start();
            } else {
                mMapView.setVisibility(View.VISIBLE);
            }
        }
        final Location location = mZone.getLocation();
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void setColor(String code) {
        @ColorRes int zoneColorRes = ColorUtil.getZoneColorRes(code.charAt(0));
        @ColorRes int textColorRes = ColorUtil.getTextColorRes(code.charAt(0));

        final int zoneColor = ContextCompat.getColor(this, zoneColorRes);
        final int textColor = ContextCompat.getColor(this, textColorRes);

        TextViewUtil.setTint(mCodeTextView, zoneColor, textColor);
        ImageViewUtil.setTint(mCarImageView, zoneColor);
        ImageViewUtil.setTint(mTimeImageView, zoneColor);
        ImageViewUtil.setTint(mCostImageView, zoneColor);
    }
}
