package com.example.freshguide.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManager {

    private static final String PREFS_FILE = "fresh_guide_session";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_SYNC_VERSION = "sync_version";

    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_ADMIN = "admin";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        SharedPreferences temp;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            temp = EncryptedSharedPreferences.create(
                    context,
                    PREFS_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback to plain prefs if encryption fails (should not happen on normal devices)
            temp = context.getSharedPreferences(PREFS_FILE + "_plain", Context.MODE_PRIVATE);
        }
        prefs = temp;
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveSession(String token, String role, String studentId, String userName) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_ROLE, role)
                .putString(KEY_STUDENT_ID, studentId)
                .putString(KEY_USER_NAME, userName)
                .apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public String getStudentId() {
        return prefs.getString(KEY_STUDENT_ID, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(getRole());
    }

    public void saveSyncVersion(int version) {
        prefs.edit().putInt(KEY_SYNC_VERSION, version).apply();
    }

    public int getSyncVersion() {
        return prefs.getInt(KEY_SYNC_VERSION, -1);
    }

    public void clearSession() {
        prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_ROLE)
                .remove(KEY_STUDENT_ID)
                .remove(KEY_USER_NAME)
                .apply();
    }
}
