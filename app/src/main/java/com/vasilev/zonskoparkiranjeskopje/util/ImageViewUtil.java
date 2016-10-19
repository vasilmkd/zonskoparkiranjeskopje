package com.vasilev.zonskoparkiranjeskopje.util;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

public final class ImageViewUtil {

    public static void setTint(@NonNull ImageView imageView, int color) {
        final Drawable drawable = DrawableCompat.wrap(DrawableCompat.unwrap(imageView.getDrawable()));
        DrawableCompat.setTint(drawable, color);
        imageView.setImageDrawable(drawable);
    }
}
