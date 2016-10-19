package com.vasilev.zonskoparkiranjeskopje.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vasilev.zonskoparkiranjeskopje.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class IntroFragment extends Fragment {

    private static final String POSITION_KEY = "position";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({INTRO_POSITION_REGISTER, INTRO_POSITION_FIND, INTRO_POSITION_PAY})
    public @interface IntroPosition {}

    public static final int INTRO_POSITION_REGISTER = 0;
    public static final int INTRO_POSITION_FIND = 1;
    public static final int INTRO_POSITION_PAY = 2;

    @NonNull
    public static IntroFragment newInstance(@IntroPosition int position) {
        final IntroFragment fragment = new IntroFragment();
        final Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_intro, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        final TextView textView = (TextView) view.findViewById(R.id.text_view);
        final int position = getArguments().getInt(POSITION_KEY);
        @DrawableRes int imgRes;
        @StringRes int textRes;
        switch (position) {
            case INTRO_POSITION_REGISTER:
                imgRes = R.drawable.ic_register;
                textRes = R.string.register_vehicle;
                break;
            case INTRO_POSITION_FIND:
                imgRes = R.drawable.ic_car;
                textRes = R.string.find_parking;
                break;
            case INTRO_POSITION_PAY:
                imgRes = R.drawable.ic_parking;
                textRes = R.string.pay_parking;
                break;
            default:
                throw new IllegalArgumentException("Unknown position: " + position);
        }
        imageView.setImageResource(imgRes);
        textView.setText(textRes);
        return view;
    }
}
