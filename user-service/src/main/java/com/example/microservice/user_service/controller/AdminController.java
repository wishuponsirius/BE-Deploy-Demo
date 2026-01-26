package com.example.microservice.user_service.controller;

import com.example.microservice.user_service.dto.Request.CreateUserRequest;
import com.example.microservice.user_service.dto.Response.ApiResponse;
import com.example.microservice.user_service.dto.Response.CreateUserResponse;
import com.example.microservice.user_service.dto.Response.ListUserResponse;
import com.example.microservice.user_service.dto.Response.UserResponse;
import com.example.microservice.user_service.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Management", description = "Admin management APIs (requires ADMIN role)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final AuthServiceImpl authService;

    @PostMapping
    @Operation(summary = "Create User", description = "Create new user with INVESTOR role (Admin only)")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            CreateUserResponse response = authService.createUserByAdmin(request);

            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message(response.getMessage())
                            .data(response)
                            .build());
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping
    @Operation(summary = "Get All Users", description = "Get all users (Admin only)")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<ListUserResponse> users = authService.getAllUsers();

            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("Users retrieved successfully")
                            .data(users)
                            .build());
        } catch (Exception e) {
            log.error("Failed to get all users: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get User Profile by ID", description = "Get user profile information by user ID")
    public ResponseEntity<ApiResponse> getUserProfileById(@PathVariable UUID userId) {
        try {
            UserResponse user = authService.getUserById(userId);

            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("User profile retrieved successfully")
                            .data(user)
                            .build());
        } catch (Exception e) {
            log.error("Failed to get user profile by ID: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete User", description = "Delete user by ID (Admin only)")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable UUID userId) {
        try {
            authService.deleteUser(userId);

            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("User deleted successfully")
                            .build());
        } catch (Exception e) {
            log.error("Failed to delete user: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        }
    }
    
    @PutMapping("/undelete/{userId}")
    @Operation(summary = "Restore User", description = "Restore deleted user by ID (Admin only)")
    public ResponseEntity<ApiResponse> undeleteUser(@PathVariable UUID userId) {
        try {
            authService.undeleteUser(userId);

            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("User restored successfully")
                            .build());
        } catch (Exception e) {
            log.error("Failed to restore user: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        }
    }

}
