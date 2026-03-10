package com.example.freshguide.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.freshguide.model.dto.LoginResponse;
import com.example.freshguide.repository.AuthRepository;

public class LoginViewModel extends AndroidViewModel {

    public enum State { IDLE, LOADING, SUCCESS_STUDENT, SUCCESS_ADMIN, ERROR }

    private final MutableLiveData<State> state = new MutableLiveData<>(State.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final AuthRepository authRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public LiveData<State> getState() { return state; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loginStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            errorMessage.setValue("Student ID is required");
            return;
        }
        state.setValue(State.LOADING);
        authRepository.loginStudent(studentId.trim(), new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse response) {
                state.setValue(State.SUCCESS_STUDENT);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
                state.setValue(State.ERROR);
            }
        });
    }

    public void registerStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            errorMessage.setValue("Student ID is required");
            return;
        }
        state.setValue(State.LOADING);
        authRepository.registerStudent(studentId.trim(), new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse response) {
                state.setValue(State.SUCCESS_STUDENT);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
                state.setValue(State.ERROR);
            }
        });
    }

    public void loginAdmin(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue("Email is required");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            errorMessage.setValue("Password is required");
            return;
        }
        state.setValue(State.LOADING);
        authRepository.loginAdmin(email.trim(), password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse response) {
                state.setValue(State.SUCCESS_ADMIN);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
                state.setValue(State.ERROR);
            }
        });
    }

    public void resetState() {
        state.setValue(State.IDLE);
    }
}
