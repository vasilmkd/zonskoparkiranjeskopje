package com.vasilev.zonskoparkiranjeskopje;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vasilev.zonskoparkiranjeskopje.adapter.HomePagerAdapter;
import com.vasilev.zonskoparkiranjeskopje.data.DataContract.ParkingEntry;
import com.vasilev.zonskoparkiranjeskopje.fragment.HomeFragment;

import static com.vasilev.zonskoparkiranjeskopje.adapter.HomeRecyclerAdapter.ADAPTER_TYPE_CURRENT;
import static com.vasilev.zonskoparkiranjeskopje.adapter.HomeRecyclerAdapter.ADAPTER_TYPE_PAST;

public final class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout == null) throw illegalViewState("tab_layout");

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        if (viewPager == null) throw illegalViewState("view_pager");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab == null) throw illegalViewState("fab");
        fab.setOnClickListener(v1 -> {
            final Intent intent = new Intent(HomeActivity.this, ParkingActivity.class);
            startActivity(intent);
        });
        fab.postDelayed(fab::show, 2000);

        final HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HomeFragment.newInstance(ADAPTER_TYPE_CURRENT), getString(R.string.current_parking));
        adapter.addFragment(HomeFragment.newInstance(ADAPTER_TYPE_PAST), getString(R.string.past_parking));
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
                if (tab.getPosition() != 0 && fab.isShown()) {
                    fab.hide();
                } else if (tab.getPosition() == 0 && !fab.isShown()) {
                    fab.show();
                }
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_add:
                ContentValues contentValues = new ContentValues();
                contentValues.put(ParkingEntry.COL_ZONE_ID, 1L);
                contentValues.put(ParkingEntry.COL_VEHICLE_ID, 1L);
                contentValues.put(ParkingEntry.COL_TIME_START, 1000L);
                contentValues.put(ParkingEntry.COL_TIME_END, 2000L);
                getContentResolver().insert(ParkingEntry.CONTENT_URI, contentValues);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    private IllegalStateException illegalViewState(@NonNull String viewId) {
        return new IllegalStateException("Layout must have a view with id '" + viewId + "'");
    }
}
