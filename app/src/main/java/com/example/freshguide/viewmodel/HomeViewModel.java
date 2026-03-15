package com.example.freshguide.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.freshguide.database.AppDatabase;
import com.example.freshguide.model.entity.BuildingEntity;
import com.example.freshguide.model.entity.FloorEntity;
import com.example.freshguide.model.entity.RoomEntity;
import com.example.freshguide.repository.SyncRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeViewModel extends AndroidViewModel {

    public enum SyncState { IDLE, SYNCING, DONE, ERROR, SKIPPED }

    private final MutableLiveData<SyncState> syncState = new MutableLiveData<>(SyncState.IDLE);
    private final MutableLiveData<String> syncError = new MutableLiveData<>();
    private final MutableLiveData<Integer> roomCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> buildingCount = new MutableLiveData<>(0);
    /** Distinct floor entries for chip labels: floor number → floor name */
    private final MutableLiveData<Map<Integer, String>> floorNumbers = new MutableLiveData<>();

    private final SyncRepository syncRepository;
    private final AppDatabase db;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface RoomLookupCallback {
        void onResult(@NonNull Integer roomId);
    }

    public HomeViewModel(@NonNull Application application) {
        super(application);
        syncRepository = new SyncRepository(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<SyncState> getSyncState() { return syncState; }
    public LiveData<String> getSyncError() { return syncError; }
    public LiveData<Integer> getRoomCount() { return roomCount; }
    public LiveData<Integer> getBuildingCount() { return buildingCount; }
    public LiveData<Map<Integer, String>> getFloorNumbers() { return floorNumbers; }

    public void sync() {
        syncState.setValue(SyncState.SYNCING);
        syncRepository.syncIfNeeded(new SyncRepository.SyncCallback() {
            @Override
            public void onSyncComplete() {
                syncState.setValue(SyncState.DONE);
                loadCounts();
            }

            @Override
            public void onSyncSkipped() {
                syncState.setValue(SyncState.SKIPPED);
                loadCounts();
            }

            @Override
            public void onSyncError(String message) {
                syncError.setValue(message);
                syncState.setValue(SyncState.ERROR);
                loadCounts(); // load whatever is in local DB
            }
        });
    }

    private void loadCounts() {
        executor.execute(() -> {
            int rooms = db.roomDao().count();
            int buildings = db.buildingDao().count();

            BuildingEntity mainBuilding = db.buildingDao().getByCodeSync("MAIN");
            List<FloorEntity> floors = mainBuilding == null
                    ? new ArrayList<>()
                    : db.floorDao().getByBuildingSync(mainBuilding.id);
            Map<Integer, String> distinctFloors = new LinkedHashMap<>();
            for (FloorEntity f : floors) {
                if (!distinctFloors.containsKey(f.number)) {
                    distinctFloors.put(f.number, f.name);
                }
            }

            mainHandler.post(() -> {
                roomCount.setValue(rooms);
                buildingCount.setValue(buildings);
                floorNumbers.setValue(distinctFloors);
            });
        });
    }

    public void findRoomIdByCode(@NonNull String code, @NonNull RoomLookupCallback callback) {
        executor.execute(() -> {
            RoomEntity room = db.roomDao().getByCodeSync(code);
            int roomId = room != null ? room.id : -1;
            mainHandler.post(() -> callback.onResult(roomId));
        });
    }
}
