package com.example.freshguide.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.freshguide.model.entity.UserProfileEntity;
import com.example.freshguide.repository.ProfileSyncRepository;

public class ProfileViewModel extends AndroidViewModel {

    public interface SaveCallback {
        void onSuccess(UserProfileEntity profile);
        void onError(String message);
    }

    private final ProfileSyncRepository profileRepository;
    private final LiveData<UserProfileEntity> profile;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profileRepository = new ProfileSyncRepository(application.getApplicationContext());
        profile = profileRepository.observeProfile();
        profileRepository.bootstrap();
    }

    public LiveData<UserProfileEntity> getProfile() {
        return profile;
    }

    public void refreshProfile() {
        profileRepository.syncNow();
    }

    public void saveProfile(String fullName,
                            String courseSection,
                            @Nullable String selectedPhotoRef,
                            @NonNull SaveCallback callback) {
        profileRepository.saveProfile(fullName, courseSection, selectedPhotoRef,
                new ProfileSyncRepository.SaveCallback() {
                    @Override
                    public void onSuccess(UserProfileEntity profile) {
                        callback.onSuccess(profile);
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError(message);
                    }
                });
    }
}
