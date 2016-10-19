package com.vasilev.zonskoparkiranjeskopje;

import android.animation.ArgbEvaluator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vasilev.zonskoparkiranjeskopje.adapter.IntroPagerAdapter;
import com.vasilev.zonskoparkiranjeskopje.async.VehicleInsertTask;
import com.vasilev.zonskoparkiranjeskopje.async.ZoneParseInsertTask;
import com.vasilev.zonskoparkiranjeskopje.fragment.IntroFragment;
import com.vasilev.zonskoparkiranjeskopje.fragment.RegisterVehicleDialogFragment;

public final class IntroActivity extends AppCompatActivity implements RegisterVehicleDialogFragment.Callbacks {

    private static final String LOG_TAG = IntroActivity.class.getSimpleName();

    private static final String FIRST_RUN_COMPLETE_KEY = "first_run_complete";
    private static final String PARKING_ZONES_PARSED_KEY = "parking_zones_parsed";

    private SharedPreferences mDefaultSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final boolean firstRunComplete = mDefaultSharedPreferences.getBoolean(FIRST_RUN_COMPLETE_KEY, false);

        if (firstRunComplete) {
            saveAndFinish();
            return;
        }

        setContentView(R.layout.activity_intro);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        if (viewPager == null) throw illegalViewState("view_pager");

        final IntroPagerAdapter adapter = new IntroPagerAdapter(getSupportFragmentManager());
        final Fragment registerFragment = IntroFragment.newInstance(IntroFragment.INTRO_POSITION_REGISTER);
        adapter.addFragment(registerFragment);
        final Fragment findFragment = IntroFragment.newInstance(IntroFragment.INTRO_POSITION_FIND);
        adapter.addFragment(findFragment);
        final Fragment payFragment = IntroFragment.newInstance(IntroFragment.INTRO_POSITION_PAY);
        adapter.addFragment(payFragment);
        viewPager.setAdapter(adapter);

        final ImageView[] indicators = {
                (ImageView) findViewById(R.id.indicator_zero),
                (ImageView) findViewById(R.id.indicator_one),
                (ImageView) findViewById(R.id.indicator_two)
        };
        for (ImageView indicator: indicators) {
            if (indicator == null) throw new IllegalStateException("Layout must have exactly three indicators");
        }

        final ImageButton previousButton = (ImageButton) findViewById(R.id.button_previous);
        if (previousButton == null) throw illegalViewState("button_previous");
        previousButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();
                if (position > 0) {
                    viewPager.setCurrentItem(position - 1);
                }
            }
        });

        final Button finishButton = (Button) findViewById(R.id.button_finish);
        if (finishButton == null) throw illegalViewState("button_finish");
        finishButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean parkingZonesParsed = mDefaultSharedPreferences.getBoolean(PARKING_ZONES_PARSED_KEY, false);
                if (!parkingZonesParsed) {
                    final ZoneParseInsertTask task = new ZoneParseInsertTask(IntroActivity.this);
                    task.execute();
                    mDefaultSharedPreferences.edit().putBoolean(PARKING_ZONES_PARSED_KEY, true).apply();
                    Log.d(LOG_TAG, "ZoneParseInsertTask executed");
                }
                final AppCompatDialogFragment dialogFragment = RegisterVehicleDialogFragment.newInstance();
                dialogFragment.show(getSupportFragmentManager(), RegisterVehicleDialogFragment.TAG);
            }
        });

        final ImageButton nextButton = (ImageButton) findViewById(R.id.button_next);
        if (nextButton == null) throw illegalViewState("button_next");
        nextButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();
                if (position < 2) {
                    viewPager.setCurrentItem(position + 1);
                }
            }
        });

        final int[] colorArray = new int[] {
                ContextCompat.getColor(this, R.color.parking_red),
                ContextCompat.getColor(this, R.color.parking_yellow),
                ContextCompat.getColor(this, R.color.parking_green)
        };

        final ArgbEvaluator evaluator = new ArgbEvaluator();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset,
                        colorArray[position], colorArray[position == 2 ? position : position + 1]);
                viewPager.setBackgroundColor(colorUpdate);
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < indicators.length; i++) {
                    indicators[i].setImageResource(i == position ?
                            R.drawable.indicator_selected : R.drawable.indicator_unselected);
                }
                viewPager.setBackgroundColor(colorArray[position]);
                previousButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                nextButton.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                finishButton.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public void onDialogDismissed(@NonNull ContentValues values) {
        VehicleInsertTask task = new VehicleInsertTask(this);
        task.execute(values);
        saveAndFinish();
    }

    @NonNull
    private IllegalStateException illegalViewState(@NonNull String viewId) {
        return new IllegalStateException("Layout must have a view with id '" + viewId + "'");
    }

    private void saveAndFinish() {
        mDefaultSharedPreferences.edit().putBoolean(FIRST_RUN_COMPLETE_KEY, true).apply();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
