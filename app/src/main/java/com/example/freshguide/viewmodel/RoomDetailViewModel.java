package com.example.freshguide.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.freshguide.model.entity.FacilityEntity;
import com.example.freshguide.model.entity.RoomEntity;
import com.example.freshguide.repository.RoomRepository;

import java.util.List;

public class RoomDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<RoomEntity> room = new MutableLiveData<>();
    private final MutableLiveData<List<FacilityEntity>> facilities = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final RoomRepository repository;

    public RoomDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomRepository(application);
    }

    public LiveData<RoomEntity> getRoom() { return room; }
    public LiveData<List<FacilityEntity>> getFacilities() { return facilities; }
    public LiveData<String> getError() { return error; }

    public void loadRoom(int roomId) {
        repository.getRoomDetail(roomId, new RoomRepository.RoomDetailCallback() {
            @Override
            public void onLoaded(RoomEntity r, List<FacilityEntity> f) {
                room.setValue(r);
                facilities.setValue(f);
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
            }
        });
    }
}
