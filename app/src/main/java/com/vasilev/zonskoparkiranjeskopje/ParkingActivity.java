package com.vasilev.zonskoparkiranjeskopje;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.vasilev.zonskoparkiranjeskopje.cache.ColumnIndexCache;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.VehicleEntry;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ZoneEntry;
import com.vasilev.zonskoparkiranjeskopje.data.Vehicle;
import com.vasilev.zonskoparkiranjeskopje.data.Zone;
import com.vasilev.zonskoparkiranjeskopje.fragment.SelectVehicleDialogFragment;
import com.vasilev.zonskoparkiranjeskopje.fragment.SelectZoneDialogFragment;
import com.vasilev.zonskoparkiranjeskopje.util.ColorUtil;
import com.vasilev.zonskoparkiranjeskopje.util.ImageViewUtil;

import java.util.ArrayList;
import java.util.List;

// TODO: СРЕДИ ГИ ПРОКЛЕТИТЕ ДОЗВОЛИ!!!

public final class ParkingActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener, LoaderManager.LoaderCallbacks<Cursor>,
        SelectZoneDialogFragment.Callbacks, SelectVehicleDialogFragment.Callbacks {

    private static final String LOG_TAG = ParkingActivity.class.getSimpleName();

    private static final String REQUEST_LOCATION_UPDATES_KEY = "request_location_updates";
    private boolean mRequestLocationUpdates;

    private static final int LOCATION_PERMISSION_REQUEST = 0;
    private static final int SMS_PERMISSION_REQUEST = 1;

    private static final int ZONE_LOADER_ID = 0;
    private static final int VEHICLE_LOADER_ID = 1;

    private static final float MAX_DISTANCE = 150f;

    private ColumnIndexCache mCache;

    private List<Zone> mZoneList;
    private List<Vehicle> mVehicleList;

    private Zone mZone = null;
    private Vehicle mVehicle = null;

    private GoogleApiClient mGoogleApiClient;
    private final LocationRequest mLocationRequest = new LocationRequest();

    private CoordinatorLayout mCoordinatorLayout;

    private View mLocationPermissionTile;

    private View mSmsPermissionTile;

    private ImageView mZoneImageView;
    private TextView mCodeTextView;
    private TextView mZoneTextView;

    private ImageView mCarImageView;
    private TextView mCarTextView;
    private TextView mLicenceTextView;

    private ImageView mTimeImageView;
    private TextView mTimeTextView;
    private TextView mHourCostTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        mRequestLocationUpdates = savedInstanceState == null || savedInstanceState.getBoolean(REQUEST_LOCATION_UPDATES_KEY, true);
        mCache = new ColumnIndexCache();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        mLocationPermissionTile = findViewById(R.id.tile_location_permission);
        if (mLocationPermissionTile == null) {
            throw illegalViewState("tile_location_permission");
        }

        final Button locationPermissionButton = (Button) mLocationPermissionTile.findViewById(R.id.button_location_permission);
        locationPermissionButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(ParkingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION };
                ActivityCompat.requestPermissions(ParkingActivity.this, permissions, LOCATION_PERMISSION_REQUEST);
                Log.d(LOG_TAG, "Requesting permissions");
            } else {
                Log.d(LOG_TAG, "Permission granted, updating location");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, ParkingActivity.this);
            }
        });

        mSmsPermissionTile = findViewById(R.id.tile_sms_permission);
        if (mSmsPermissionTile == null) {
            throw illegalViewState("tile_sms_permission");
        }

        final Button smsPermissionButton = (Button) mSmsPermissionTile.findViewById(R.id.button_sms_permission);
        smsPermissionButton.setOnClickListener(v -> {
            final String[] permissions = { Manifest.permission.SEND_SMS };
            ActivityCompat.requestPermissions(ParkingActivity.this, permissions, SMS_PERMISSION_REQUEST);
            Log.d(LOG_TAG, "Requesting permissions");
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw illegalViewState("toolbar");
        }
        toolbar.setNavigationOnClickListener(v ->
            NavUtils.navigateUpFromSameTask(ParkingActivity.this));
        setSupportActionBar(toolbar);

        final View zoneTile = findViewById(R.id.tile_zone);
        if (zoneTile == null) {
            throw illegalViewState("tile_zone");
        }
        zoneTile.setOnClickListener(v -> {
            final AppCompatDialogFragment dialogFragment = SelectZoneDialogFragment.newInstance(mZoneList);
            dialogFragment.show(getSupportFragmentManager(), SelectZoneDialogFragment.TAG);
        });

        mZoneImageView = (ImageView) zoneTile.findViewById(R.id.image_view_zone);
        mCodeTextView = (TextView) zoneTile.findViewById(R.id.text_view_code);
        mZoneTextView = (TextView) zoneTile.findViewById(R.id.text_view_zone);

        final View carTile = findViewById(R.id.tile_car);
        if (carTile == null) {
            throw illegalViewState("tile_car");
        }
        carTile.setOnClickListener(v -> {
            final AppCompatDialogFragment dialogFragment = SelectVehicleDialogFragment.newInstance(mVehicleList);
            dialogFragment.show(getSupportFragmentManager(), SelectVehicleDialogFragment.TAG);
        });

        mCarImageView = (ImageView) carTile.findViewById(R.id.image_view_car);
        mCarTextView = (TextView) carTile.findViewById(R.id.text_view_car);
        mLicenceTextView = (TextView) carTile.findViewById(R.id.text_view_licence);

        mTimeImageView = (ImageView) findViewById(R.id.image_view_time);
        mTimeTextView = (TextView) findViewById(R.id.text_view_time);
        mHourCostTextView = (TextView) findViewById(R.id.text_view_cost_hour);

        setColor(R.color.accent);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(6000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getSupportLoaderManager().initLoader(ZONE_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(VEHICLE_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_parking, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        final MenuItem itemSend = menu.findItem(R.id.action_send);
        boolean sendAvailable = mZone != null && mVehicle != null;
        if (sendAvailable) {
            itemSend.setEnabled(true);
            itemSend.setIcon(R.drawable.ic_check);
            final String smsMessage = getString(R.string.sms_message) + ": " + mZone.getCode() + " " + mVehicle.getLicence();
            Snackbar.make(mCoordinatorLayout, smsMessage, Snackbar.LENGTH_INDEFINITE).show();
        }
        else {
            itemSend.setEnabled(false);
            itemSend.setIcon(R.drawable.ic_check_disabled);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                if (mSmsPermissionTile.getVisibility() != View.VISIBLE) {
                    sendSms();
                } else {
                    Toast.makeText(this, R.string.please_allow_sms_permission, Toast.LENGTH_LONG).show();
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        stopLocationUpdates();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(REQUEST_LOCATION_UPDATES_KEY, mRequestLocationUpdates);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hideLocationPermissionRationale();
                    startLocationUpdates();
                } else {
                    Log.d(LOG_TAG, "Smotan");
                    //setLocationUnavailable();
                    //showLocationPermissionRationale();
                }
                break;
            case SMS_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hideSmsPermissionRationale();
                    sendSms();
                } else {
                    showSmsPermissionRationale();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        setLocationUnavailable();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "GoogleApiClient connection failed");
        setLocationUnavailable();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(LOG_TAG, "Location updated");
        float minDistance = Float.MAX_VALUE;
        float distance;
        Zone zone = null;
        Zone tempZone;
        if (mZoneList != null && mZoneList.size() > 0) {
            for (int i = 0; i < mZoneList.size(); i++) {
                tempZone = mZoneList.get(i);
                distance = location.distanceTo(tempZone.getLocation());
                if (Float.compare(distance, minDistance) < 0) {
                    minDistance = distance;
                    zone = tempZone;
                    Log.d(LOG_TAG, "Latitude: " + zone.getLocation().getLatitude());
                    Log.d(LOG_TAG, "Longitude: " + zone.getLocation().getLongitude());
                }
            }
            if (zone == null) {
                throw new IllegalStateException("Zone is null");
            }
            Log.d(LOG_TAG, "Minimum distance: " + minDistance);
            Log.d(LOG_TAG, "Maximum distance: " + MAX_DISTANCE);
            if (Float.compare(minDistance, MAX_DISTANCE) > 0) {
                mCodeTextView.setText(R.string.zone_not_found);
                mZoneTextView.setText(R.string.zone_not_found_message);
                mTimeTextView.setText("");
                mHourCostTextView.setText("");
                Log.d(LOG_TAG, "executed this...");
            } else {
                setZone(zone);
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        final Uri uri;
        final String[] projection;
        final String sortOrder;
        switch (id) {
            case ZONE_LOADER_ID:
                uri = ZoneEntry.CONTENT_URI;
                projection = new String[] {
                        ZoneEntry.COL_CODE,
                        ZoneEntry.COL_NAME,
                        ZoneEntry.COL_LATITUDE,
                        ZoneEntry.COL_LONGITUDE,
                        ZoneEntry.COL_COST,
                        ZoneEntry.COL_HOURS
                };
                sortOrder = null;
                break;
            case VEHICLE_LOADER_ID:
                uri = VehicleEntry.CONTENT_URI;
                projection = new String[] {
                        VehicleEntry.COL_NAME,
                        VehicleEntry.COL_LICENCE
                };
                sortOrder = VehicleEntry.TABLE_NAME + "." + VehicleEntry.COL_LAST_USED + " DESC";
                break;
            default:
                throw new IllegalArgumentException("Unknown loader id: " + id);
        }
        return new CursorLoader(this, uri, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, @Nullable Cursor data) {
        if (data == null) {
            throw new IllegalStateException("Cursor is null");
        }
        data.moveToFirst();
        final int loaderId = loader.getId();
        switch (loaderId) {
            case ZONE_LOADER_ID:
                mZoneList = new ArrayList<>(data.getCount());
                String code;
                String zoneName;
                double latitude;
                double longitude;
                int cost;
                int hours;
                for (int i = 0; i < data.getCount(); i++) {
                    if (!data.moveToPosition(i)) throw new IllegalStateException("Could not move cursor to position: " + i);
                    code = data.getString(mCache.get(data, ZoneEntry.COL_CODE));
                    zoneName = data.getString(mCache.get(data, ZoneEntry.COL_NAME));
                    latitude = data.getDouble(mCache.get(data, ZoneEntry.COL_LATITUDE));
                    longitude = data.getDouble(mCache.get(data, ZoneEntry.COL_LONGITUDE));
                    cost = data.getInt(mCache.get(data, ZoneEntry.COL_COST));
                    hours = data.getInt(mCache.get(data, ZoneEntry.COL_HOURS));
                    mZoneList.add(new Zone(zoneName, code, latitude, longitude, cost, hours));
                }
                break;
            case VEHICLE_LOADER_ID:
                mVehicleList = new ArrayList<>(data.getCount());
                String carName;
                String licence;
                for (int i = 0; i < data.getCount(); i++) {
                    if (!data.moveToPosition(i)) throw new IllegalStateException("Could not move cursor to position: " + i);
                    carName = data.getString(mCache.get(data, VehicleEntry.COL_NAME));
                    licence = data.getString(mCache.get(data, VehicleEntry.COL_LICENCE));
                    mVehicleList.add(new Vehicle(carName, licence));
                }
                setVehicle(mVehicleList.get(0));
                break;
            default:
                throw new IllegalArgumentException("Unknown loader id: " + loaderId);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCache.clear();
    }

    @Override
    public void onZoneSelected(int position) {
        setZone(mZoneList.get(position));
        mRequestLocationUpdates = false;
        stopLocationUpdates();
        hideLocationPermissionRationale();
    }

    @Override
    public void onVehicleSelected(int position) {
        setVehicle(mVehicleList.get(position));
    }

    @NonNull
    private IllegalStateException illegalViewState(@NonNull String viewId) {
        return new IllegalStateException("Layout must have a view with id '" + viewId + "'");
    }

    private void setLocationUnavailable() {
        mRequestLocationUpdates = false;
        stopLocationUpdates();
        setColor(R.color.accent);
        mCodeTextView.setText(R.string.zone_not_found);
        mZoneTextView.setText(R.string.zone_not_found_message);
        Snackbar.make(mCoordinatorLayout, R.string.location_unavailable, Snackbar.LENGTH_SHORT).show();
    }

    private void setColor(@ColorRes int colorRes) {
        final int color = ContextCompat.getColor(this, colorRes);
        ImageViewUtil.setTint(mZoneImageView, color);
        ImageViewUtil.setTint(mCarImageView, color);
        ImageViewUtil.setTint(mTimeImageView, color);
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showLocationPermissionRationale();
                Log.d(LOG_TAG, "Showing location permission rationale");
            } else {
                final String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION };
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST);
                Log.d(LOG_TAG, "Requesting permissions");
            }
        } else {
            if (mGoogleApiClient.isConnected() && mRequestLocationUpdates) {
                Log.d(LOG_TAG, "Permission granted, updating location");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d(LOG_TAG, "Stopping location updates");
        }
    }

    private void sendSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                showSmsPermissionRationale();
                Log.d(LOG_TAG, "Showing sms permission rationale");
            } else {
                final String[] permissions = { Manifest.permission.SEND_SMS };
                ActivityCompat.requestPermissions(this, permissions, SMS_PERMISSION_REQUEST);
                Log.d(LOG_TAG, "Requesting permissions");
            }
        } else {
            final SmsManager smsManager = SmsManager.getDefault();
            final String smsMessage = mZone.getCode() + " " + mVehicle.getLicence();
            smsManager.sendTextMessage("+38641805514", null, smsMessage, null, null);
        }
    }

    private void showLocationPermissionRationale() {
        if (mLocationPermissionTile.getVisibility() != View.VISIBLE) {
            mLocationPermissionTile.setVisibility(View.VISIBLE);
        }
    }

    private void hideLocationPermissionRationale() {
        if (mLocationPermissionTile.getVisibility() != View.GONE) {
            mLocationPermissionTile.setVisibility(View.GONE);
        }
    }

    private void showSmsPermissionRationale() {
        if (mSmsPermissionTile.getVisibility() != View.VISIBLE) {
            mSmsPermissionTile.setVisibility(View.VISIBLE);
        }
    }

    private void hideSmsPermissionRationale() {
        if (mSmsPermissionTile.getVisibility() != View.GONE) {
            mSmsPermissionTile.setVisibility(View.GONE);
        }
    }

    private void setZone(@NonNull Zone zone) {
        mZone = zone;
        mCodeTextView.setText(mZone.getCode());
        mZoneTextView.setText(mZone.getName());
        final int maxHours = mZone.getHours();
        final String timeString;
        if (maxHours == -1) timeString = getString(R.string.unlimited);
        else timeString = getString(R.string.maximum) + " " + Integer.toString(maxHours) + " " + getString(R.string.time_period_hours_few);
        mTimeTextView.setText(timeString);
        final String hourCostString = Integer.toString(mZone.getCost()) + " " + getString(R.string.denars_per_hour);
        mHourCostTextView.setText(hourCostString);
        final @ColorRes int colorRes = ColorUtil.getZoneColorRes(mZone.getCode().charAt(0));
        setColor(colorRes);
        supportInvalidateOptionsMenu();
    }

    private void setVehicle(@NonNull Vehicle vehicle) {
        mVehicle = vehicle;
        mCarTextView.setText(mVehicle.getName());
        mLicenceTextView.setText(mVehicle.getLicence());
        supportInvalidateOptionsMenu();
    }
}
