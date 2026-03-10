package com.example.freshguide.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.freshguide.database.AppDatabase;
import com.example.freshguide.database.dao.BuildingDao;
import com.example.freshguide.model.dto.ApiResponse;
import com.example.freshguide.model.dto.BuildingDto;
import com.example.freshguide.model.entity.BuildingEntity;
import com.example.freshguide.network.ApiClient;
import com.example.freshguide.network.ApiService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class BuildingRepository {

    public interface RepoCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    private final BuildingDao buildingDao;
    private final ApiService apiService;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public BuildingRepository(Context context) {
        buildingDao = AppDatabase.getInstance(context).buildingDao();
        apiService = ApiClient.getInstance(context).getApiService();
    }

    public LiveData<List<BuildingEntity>> getAllBuildings() {
        return buildingDao.getAllBuildings();
    }

    public void adminGetBuildings(RepoCallback<List<BuildingDto>> cb) {
        apiService.adminGetBuildings().enqueue(new retrofit2.Callback<ApiResponse<List<BuildingDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BuildingDto>>> call,
                                   Response<ApiResponse<List<BuildingDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    cb.onSuccess(response.body().getData());
                } else {
                    cb.onError("Failed to load buildings");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BuildingDto>>> call, Throwable t) {
                cb.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void adminCreateBuilding(String name, String code, String description, RepoCallback<BuildingDto> cb) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("code", code);
        body.put("description", description);

        apiService.adminCreateBuilding(body).enqueue(new retrofit2.Callback<ApiResponse<BuildingDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<BuildingDto>> call,
                                   Response<ApiResponse<BuildingDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    cb.onSuccess(response.body().getData());
                } else {
                    cb.onError("Failed to create building");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BuildingDto>> call, Throwable t) {
                cb.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void adminUpdateBuilding(int id, String name, String code, String description, RepoCallback<BuildingDto> cb) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("code", code);
        body.put("description", description);

        apiService.adminUpdateBuilding(id, body).enqueue(new retrofit2.Callback<ApiResponse<BuildingDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<BuildingDto>> call,
                                   Response<ApiResponse<BuildingDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    cb.onSuccess(response.body().getData());
                } else {
                    cb.onError("Failed to update building");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BuildingDto>> call, Throwable t) {
                cb.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void adminDeleteBuilding(int id, RepoCallback<Void> cb) {
        apiService.adminDeleteBuilding(id).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    cb.onSuccess(null);
                } else {
                    cb.onError("Failed to delete building");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                cb.onError("Network error: " + t.getMessage());
            }
        });
    }
}
