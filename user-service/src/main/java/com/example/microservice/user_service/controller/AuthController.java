package com.example.microservice.user_service.controller;

import com.example.microservice.user_service.dto.Request.LoginRequest;
import com.example.microservice.user_service.dto.Request.RegisterRequest;
import com.example.microservice.user_service.dto.Response.ApiResponse;
import com.example.microservice.user_service.dto.Response.LoginResponse;
import com.example.microservice.user_service.dto.Response.RegisterResponse;
import com.example.microservice.user_service.entity.User;
import com.example.microservice.user_service.service.impl.AuthServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and Registration APIs")
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and get JWT token")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("Login successful")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
                    );
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user account")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.register(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.builder()
                            .success(true)
                            .message("User registered successfully. Please check your email to activate your account.")
                            .data(response)
                            .build()
                    );
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
                    );
        }
    }

    @GetMapping("/activate")
    @Operation(summary = "Activate Account", description = "Activate user account using activation token")
    public ResponseEntity<ApiResponse> activateAccount(@RequestParam("token") String token) {
        try {
            authService.activateAccount(token);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("Account activated successfully. You can now login.")
                            .build()
            );
        } catch (Exception e) {
            log.error("Account activation failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
                    );
        }
    }

    @PostMapping("/resend-activation")
    @Operation(summary = "Resend Activation Email", description = "Resend activation email to user")
    public ResponseEntity<ApiResponse> resendActivationEmail(@RequestParam("email") String email) {
        try {
            authService.resendActivationEmail(email);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("Activation email has been resent. Please check your email.")
                            .build()
            );
        } catch (Exception e) {
            log.error("Resend activation email failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
                    );
        }
    }

    // @GetMapping("/validate")
    // public ResponseEntity<ApiResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
    //     try {
    //         // Token validation will be handled by JWT filter
    //         return ResponseEntity.ok(
    //                 ApiResponse.builder()
    //                         .success(true)
    //                         .message("Token is valid")
    //                         .build()
    //         );
    //     } catch (Exception e) {
    //         return ResponseEntity
    //                 .status(HttpStatus.UNAUTHORIZED)
    //                 .body(ApiResponse.builder()
    //                         .success(false)
    //                         .message("Invalid token")
    //                         .build()
    //                 );
    //     }
    // }

}
