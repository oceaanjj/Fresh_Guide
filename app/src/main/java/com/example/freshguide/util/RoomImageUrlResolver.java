package com.example.freshguide.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.freshguide.network.ApiClient;

import java.net.URI;

public final class RoomImageUrlResolver {

    private RoomImageUrlResolver() {
    }

    @Nullable
    public static String resolveFromDto(@NonNull Context context,
                                        @Nullable String imageFullUrl,
                                        @Nullable String imageUrl) {
        String resolvedFull = resolvePath(context, imageFullUrl);
        if (resolvedFull != null && !isClearlyMalformedAbsoluteUrl(resolvedFull)) {
            return resolvedFull;
        }
        return resolvePath(context, imageUrl);
    }

    @Nullable
    public static String resolvePath(@NonNull Context context, @Nullable String rawPath) {
        if (rawPath == null) {
            return null;
        }

        String path = rawPath.trim();
        if (path.isEmpty()) {
            return null;
        }

        if (path.startsWith("//")) {
            path = "https:" + path;
        }

        String baseHost = toBaseHost(ApiClient.getInstance(context.getApplicationContext()).getBaseUrl());

        if (path.startsWith("http://") || path.startsWith("https://")) {
            if (isClearlyMalformedAbsoluteUrl(path)) {
                String repaired = repairMalformedAbsolute(path, baseHost);
                if (repaired != null) {
                    return repaired;
                }
            }

            path = enforceHttps(path);
            String rewritten = rewriteIfLocalhost(path, baseHost);
            return rewritten != null ? rewritten : path;
        }

        if (baseHost == null || baseHost.isEmpty()) {
            return path;
        }

        if (path.startsWith("/storage/")) {
            return baseHost + path;
        }

        if (path.startsWith("storage/")) {
            return baseHost + "/" + path;
        }

        if (path.startsWith("/")) {
            return baseHost + path;
        }

        return baseHost + "/storage/" + path;
    }

    @Nullable
    private static String rewriteIfLocalhost(@NonNull String absoluteUrl, @Nullable String baseHost) {
        if (baseHost == null || baseHost.isEmpty()) {
            return null;
        }

        try {
            URI uri = URI.create(absoluteUrl);
            String host = uri.getHost();
            if (host == null) {
                return null;
            }
            if (!isLocalHost(host)) {
                return null;
            }

            String path = uri.getRawPath();
            if (path == null || path.isEmpty()) {
                return null;
            }

            String query = uri.getRawQuery();
            String suffix = (query != null && !query.isEmpty()) ? ("?" + query) : "";
            return baseHost + path + suffix;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean isLocalHost(@NonNull String host) {
        String h = host.trim().toLowerCase();
        return h.equals("localhost") || h.equals("127.0.0.1") || h.equals("::1");
    }

    private static boolean isClearlyMalformedAbsoluteUrl(@NonNull String url) {
        int protocolCount = countOccurrences(url, "http://") + countOccurrences(url, "https://");
        if (protocolCount > 1) {
            return true;
        }
        return url.contains("http://localhost/")
                || url.contains("https://localhost/")
                || url.contains("http://127.0.0.1/")
                || url.contains("https://127.0.0.1/");
    }

    private static int countOccurrences(@NonNull String source, @NonNull String token) {
        int count = 0;
        int from = 0;
        while (true) {
            int idx = source.indexOf(token, from);
            if (idx < 0) {
                return count;
            }
            count++;
            from = idx + token.length();
        }
    }

    @Nullable
    private static String repairMalformedAbsolute(@NonNull String url, @Nullable String baseHost) {
        if (baseHost == null || baseHost.isEmpty()) {
            return null;
        }

        int storageIdx = url.indexOf("/storage/");
        if (storageIdx >= 0) {
            return baseHost + url.substring(storageIdx);
        }

        int roomsIdx = url.indexOf("rooms/");
        if (roomsIdx >= 0) {
            return baseHost + "/storage/" + url.substring(roomsIdx);
        }

        return null;
    }

    @NonNull
    private static String enforceHttps(@NonNull String url) {
        if (url.startsWith("http://")) {
            return "https://" + url.substring("http://".length());
        }
        return url;
    }

    @Nullable
    private static String firstNonBlank(@Nullable String first, @Nullable String second) {
        if (first != null && !first.trim().isEmpty()) {
            return first;
        }
        if (second != null && !second.trim().isEmpty()) {
            return second;
        }
        return null;
    }

    @Nullable
    private static String toBaseHost(@Nullable String baseUrl) {
        if (baseUrl == null) {
            return null;
        }

        String url = baseUrl.trim();
        if (url.isEmpty()) {
            return null;
        }

        if (url.endsWith("/api/")) {
            url = url.substring(0, url.length() - 5);
        } else if (url.endsWith("/api")) {
            url = url.substring(0, url.length() - 4);
        }

        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }
}
