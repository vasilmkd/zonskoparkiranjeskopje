package com.vasilev.zonskoparkiranjeskopje.util;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.TextView;

public final class TextViewUtil {

    public static void setTint(@NonNull TextView textView, int zoneColor, int textColor) {
        final Drawable drawable = DrawableCompat.wrap(DrawableCompat.unwrap(textView.getBackground()));
        DrawableCompat.setTint(drawable, zoneColor);
        setDrawable(textView, drawable);
        textView.setTextColor(textColor);
    }

    @SuppressWarnings("deprecation")
    private static void setDrawable(@NonNull TextView textView, @NonNull Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            textView.setBackgroundDrawable(drawable);
        else textView.setBackground(drawable);
    }
}
