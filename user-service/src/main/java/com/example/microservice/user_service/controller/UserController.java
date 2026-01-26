package com.example.microservice.user_service.controller;

import com.example.microservice.user_service.config.JwtUtil;
import com.example.microservice.user_service.dto.Request.UpdateProfileRequest;
import com.example.microservice.user_service.dto.Response.ApiResponse;
import com.example.microservice.user_service.dto.Response.UpdateProfileResponse;
import com.example.microservice.user_service.dto.Response.UserResponse;
import com.example.microservice.user_service.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User management APIs (requires authentication)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

        private final AuthServiceImpl authService;
        private final JwtUtil jwtUtil;

        @GetMapping("/me")
        @Operation(summary = "Get Current User", description = "Get currently authenticated user information")
        public ResponseEntity<ApiResponse> getCurrentUser() {
                try {
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        String userId = authentication.getName();

                        UserResponse user = authService.getUserById(UUID.fromString(userId));

                        return ResponseEntity.ok(
                                        ApiResponse.builder()
                                                        .success(true)
                                                        .message("User information retrieved successfully")
                                                        .data(user)
                                                        .build());
                } catch (Exception e) {
                        log.error("Failed to get current user: {}", e.getMessage());
                        return ResponseEntity
                                        .badRequest()
                                        .body(ApiResponse.builder()
                                                        .success(false)
                                                        .message(e.getMessage())
                                                        .build());
                }
        }

        @Hidden
        @GetMapping("/validate-token")
        @Operation(summary = "Validate Token", description = "Validate JWT token")
        public ResponseEntity<ApiResponse> validateToken() {
                return ResponseEntity.ok(
                                ApiResponse.builder()
                                                .success(true)
                                                .message("Token is valid")
                                                .build());
        }

        @PutMapping("/profile")
        @Operation(summary = "Update User Profile", description = "Update current user's profile information")
        public ResponseEntity<ApiResponse> updateProfile(
                        @Valid @RequestBody UpdateProfileRequest request) {
                try {
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        UUID userId = UUID.fromString(authentication.getName());

                        UpdateProfileResponse response = authService.updateProfile(userId, request);

                        return ResponseEntity.ok(
                                        ApiResponse.builder()
                                                        .success(true)
                                                        .message(response.getMessage())
                                                        .data(response)
                                                        .build());
                } catch (Exception e) {
                        log.error("Failed to update profile: {}", e.getMessage());
                        return ResponseEntity
                                        .badRequest()
                                        .body(ApiResponse.builder()
                                                        .success(false)
                                                        .message(e.getMessage())
                                                        .build());
                }
        }

}
