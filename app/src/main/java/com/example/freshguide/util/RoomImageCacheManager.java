package com.example.freshguide.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class RoomImageCacheManager {

    private static final String CACHE_DIR_NAME = "room_images";

    private RoomImageCacheManager() {
    }

    @Nullable
    public static String cacheRoomImage(@NonNull Context context, int roomId, @Nullable String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();

            if (connection.getResponseCode() < 200 || connection.getResponseCode() >= 300) {
                return null;
            }

            input = connection.getInputStream();
            return saveToCacheFile(context, roomId, input);
        } catch (Exception ignored) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Nullable
    public static String cacheRoomImageFromFile(@NonNull Context context, int roomId, @Nullable File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) {
            return null;
        }

        InputStream input = null;
        try {
            input = new java.io.FileInputStream(sourceFile);
            return saveToCacheFile(context, roomId, input);
        } catch (Exception ignored) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void deleteCachedRoomImage(@Nullable String cachedImagePath) {
        if (cachedImagePath == null || cachedImagePath.trim().isEmpty()) {
            return;
        }
        File file = new File(cachedImagePath);
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    public static void clearAllCachedRoomImages(@NonNull Context context) {
        File dir = getCacheDir(context);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file != null && file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

    @NonNull
    private static String saveToCacheFile(@NonNull Context context, int roomId, @NonNull InputStream input) throws IOException {
        File dir = getCacheDir(context);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Unable to create image cache directory");
        }

        File targetFile = new File(dir, "room_" + roomId + ".jpg");
        FileOutputStream output = new FileOutputStream(targetFile, false);
        try {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } finally {
            output.close();
        }
        return targetFile.getAbsolutePath();
    }

    @NonNull
    private static File getCacheDir(@NonNull Context context) {
        return new File(context.getFilesDir(), CACHE_DIR_NAME);
    }
}
