package com.example.freshguide.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.freshguide.model.entity.RoomEntity;
import com.example.freshguide.model.entity.ScheduleEntryEntity;
import com.example.freshguide.repository.ScheduleSyncRepository;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {

    public interface RoomsCallback {
        void onResult(List<RoomEntity> rooms);
    }

    public interface OperationCallback {
        void onSuccess(ScheduleEntryEntity savedEntry);
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    private final ScheduleSyncRepository repository;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        repository = new ScheduleSyncRepository(application);
    }

    public LiveData<List<ScheduleEntryEntity>> getSchedulesByDay(int dayOfWeek) {
        return repository.observeSchedulesByDay(dayOfWeek);
    }

    public LiveData<List<ScheduleEntryEntity>> getAllSchedules() {
        return repository.observeAllSchedules();
    }

    public LiveData<Integer> getTotalScheduleCount() {
        return repository.observeScheduleCount();
    }

    public void loadRooms(RoomsCallback callback) {
        repository.loadRooms(callback::onResult);
    }

    public void saveSchedule(ScheduleEntryEntity entry, OperationCallback callback) {
        repository.saveSchedule(entry, new ScheduleSyncRepository.OperationCallback() {
            @Override
            public void onSuccess(ScheduleEntryEntity savedEntry) {
                callback.onSuccess(savedEntry);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void deleteSchedule(ScheduleEntryEntity entry, SimpleCallback callback) {
        repository.deleteSchedule(entry, new ScheduleSyncRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void syncSchedules() {
        repository.syncNow();
    }
}
