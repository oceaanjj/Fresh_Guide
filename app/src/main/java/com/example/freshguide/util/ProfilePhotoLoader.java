package com.example.freshguide.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public final class ProfilePhotoLoader {

    private ProfilePhotoLoader() {
    }

    public static boolean loadInto(@NonNull Context context,
                                   @NonNull ImageView imageView,
                                   @Nullable String photoRef) {
        String normalized = normalizeRef(photoRef);
        if (normalized == null) {
            return false;
        }

        Bitmap bitmap = decodeBitmap(context, normalized);
        if (bitmap == null) {
            return false;
        }

        imageView.setImageDrawable(null);
        imageView.setImageBitmap(bitmap);
        return true;
    }

    @Nullable
    private static Bitmap decodeBitmap(@NonNull Context context, @NonNull String photoRef) {
        try {
            if (photoRef.startsWith("content://") || photoRef.startsWith("file://")) {
                Uri uri = Uri.parse(photoRef);
                try (InputStream input = context.getContentResolver().openInputStream(uri)) {
                    if (input == null) {
                        return null;
                    }
                    return BitmapFactory.decodeStream(input);
                }
            }

            File file = new File(photoRef);
            if (!file.exists() || !file.isFile()) {
                return null;
            }

            try (InputStream input = new FileInputStream(file)) {
                return BitmapFactory.decodeStream(input);
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    public static String normalizeRef(@Nullable String photoRef) {
        if (TextUtils.isEmpty(photoRef)) {
            return null;
        }

        String trimmed = photoRef.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return null;
        }

        return trimmed;
    }
}
