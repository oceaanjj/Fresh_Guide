package com.example.freshguide.network;

import android.content.Context;

import com.example.freshguide.BuildConfig;
import com.example.freshguide.util.SessionManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = ensureApiBaseUrl(BuildConfig.API_BASE_URL);

    private static ApiClient instance;
    private final ApiService apiService;

    private ApiClient(Context context) {
        SessionManager session = SessionManager.getInstance(context);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(session))
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context.getApplicationContext());
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    public String getBaseUrl() {
        return BASE_URL;
    }

    private static String ensureApiBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("API base URL is empty. Set api.base.url in local.properties.");
        }

        String normalized = url.trim();
        if (normalized.startsWith("http://")) {
            normalized = "https://" + normalized.substring("http://".length());
        }
        if (!normalized.startsWith("https://")) {
            throw new IllegalStateException("API base URL must start with https://");
        }
        if (!normalized.endsWith("/")) {
            normalized = normalized + "/";
        }

        if (normalized.endsWith("/api/")) {
            return normalized;
        }
        if (normalized.endsWith("/api")) {
            return normalized + "/";
        }

        return normalized + "api/";
    }
}
