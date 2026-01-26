package com.example.microservice.user_service.service;

import java.util.List;
import java.util.UUID;

import com.example.microservice.user_service.dto.Request.CreateUserRequest;
import com.example.microservice.user_service.dto.Request.LoginRequest;
import com.example.microservice.user_service.dto.Request.RegisterRequest;
import com.example.microservice.user_service.dto.Request.UpdateProfileRequest;
import com.example.microservice.user_service.dto.Response.CreateUserResponse;
import com.example.microservice.user_service.dto.Response.ListUserResponse;
import com.example.microservice.user_service.dto.Response.LoginResponse;
import com.example.microservice.user_service.dto.Response.RegisterResponse;
import com.example.microservice.user_service.dto.Response.UpdateProfileResponse;
import com.example.microservice.user_service.dto.Response.UserResponse;
import com.example.microservice.user_service.entity.User;

public interface AuthService {

    public LoginResponse login(LoginRequest request);

    public RegisterResponse register(RegisterRequest request);

    public void activateAccount(String token);

    public UserResponse getUserById(UUID userId);

    public List<UserResponse> getAllUsers(ListUserResponse request);

    public UpdateProfileResponse updateProfile(UUID userId, UpdateProfileRequest request);

    public void deleteUser(UUID userId);

    public void undeleteUser(UUID userId);

    public CreateUserResponse createUser(CreateUserRequest request);
}
