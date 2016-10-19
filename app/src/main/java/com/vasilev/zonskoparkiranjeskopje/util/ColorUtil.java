package com.vasilev.zonskoparkiranjeskopje.util;

import android.support.annotation.ColorRes;

import com.vasilev.zonskoparkiranjeskopje.R;

public final class ColorUtil {

    public static @ColorRes int getZoneColorRes(char c) {
        switch (c) {
            case 'A':
                return R.color.parking_red;
            case 'B':
                return R.color.parking_yellow;
            case 'C':
                return R.color.parking_green;
            case 'D':
                return R.color.parking_white;
            default:
                throw new IllegalArgumentException("Unknown zone type: " + c);
        }
    }

    public static @ColorRes int getTextColorRes(char c) {
        switch (c) {
            case 'A':
            case 'C':
                return R.color.text_light;
            case 'B':
            case 'D':
                return R.color.text_dark;
            default:
                throw new IllegalArgumentException("Unknown zone type: " + c);
        }
    }
}
