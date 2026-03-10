package com.example.freshguide.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.freshguide.database.AppDatabase;
import com.example.freshguide.database.dao.FacilityDao;
import com.example.freshguide.database.dao.RoomDao;
import com.example.freshguide.model.dto.ApiResponse;
import com.example.freshguide.model.dto.RouteDto;
import com.example.freshguide.model.entity.FacilityEntity;
import com.example.freshguide.model.entity.RoomEntity;
import com.example.freshguide.model.entity.RouteEntity;
import com.example.freshguide.model.entity.RouteStepEntity;
import com.example.freshguide.network.ApiClient;
import com.example.freshguide.network.ApiService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomRepository {

    public interface RoomDetailCallback {
        void onLoaded(RoomEntity room, List<FacilityEntity> facilities);
        void onError(String message);
    }

    public interface RouteCallback {
        void onLoaded(RouteDto route);
        void onError(String message);
    }

    private final RoomDao roomDao;
    private final FacilityDao facilityDao;
    private final ApiService apiService;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public RoomRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        roomDao = db.roomDao();
        facilityDao = db.facilityDao();
        apiService = ApiClient.getInstance(context).getApiService();
    }

    public LiveData<List<RoomEntity>> getAllRooms() {
        return roomDao.getAllRooms();
    }

    public LiveData<List<RoomEntity>> searchRooms(String query) {
        return roomDao.search(query);
    }

    public LiveData<List<RoomEntity>> searchRoomsByBuilding(String buildingCode, String query) {
        return roomDao.searchByBuilding(buildingCode, query);
    }

    public void getRoomDetail(int roomId, RoomDetailCallback callback) {
        executor.execute(() -> {
            RoomEntity room = roomDao.getByIdSync(roomId);
            List<FacilityEntity> facilities = facilityDao.getFacilitiesForRoom(roomId);
            mainHandler.post(() -> {
                if (room != null) {
                    callback.onLoaded(room, facilities);
                } else {
                    callback.onError("Room not found");
                }
            });
        });
    }

    public void getRoute(int roomId, int originId, RouteCallback callback) {
        apiService.getRoute(roomId, originId).enqueue(new Callback<ApiResponse<RouteDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<RouteDto>> call,
                                   Response<ApiResponse<RouteDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onLoaded(response.body().getData());
                } else {
                    callback.onError("Route not found");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RouteDto>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
