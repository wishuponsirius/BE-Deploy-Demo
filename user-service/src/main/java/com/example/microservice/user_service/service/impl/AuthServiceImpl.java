package com.example.microservice.user_service.service.impl;

import com.example.microservice.user_service.config.JwtUtil;
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
import com.example.microservice.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailServiceImpl emailService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        
        User user = userRepository.findByContactEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        if (!user.getIsActive()) {
            throw new BadCredentialsException("User account is not active. Please check your email to activate your account.");
        }

        if(!user.getIsDeleted().equals(false)) {
            throw new BadCredentialsException("User account has been deleted.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getContactEmail(), user.getId(), user.getRole());

        log.info("Login successful for user: {}", user.getContactEmail());

        return new LoginResponse(token, user.getId(), user.getContactEmail(), user.getOrgName(), user.getRole(), user.getCreateDate().toLocalDate(), user.getUpdateDate() != null ? user.getUpdateDate().toLocalDate() : null);
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        if (userRepository.existsByContactEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        String activationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        User user = User.builder()
                .orgName(request.getOrgName())
                .contactEmail(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(false)
                .role("INVESTOR")
                .createDate(LocalDateTime.now())
                .activationToken(activationToken)
                .activationTokenExpiry(tokenExpiry)
                .isDeleted(false)
                .build();

        user = userRepository.save(user);

        try {
            emailService.sendActivationEmail(user.getContactEmail(), user.getOrgName(), activationToken);
            log.info("Activation email sent to: {}", user.getContactEmail());
        } catch (Exception e) {
            log.error("Failed to send activation email", e);
        }

        log.info("User registered successfully: {}", user.getContactEmail());

        return new RegisterResponse(user.getId(), user.getContactEmail(), user.getOrgName(), user.getRole(), user.getCreateDate().toLocalDate());
    }

    @Transactional
    public void activateAccount(String token) {
        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid activation token"));

        if (user.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Activation token has expired");
        }

        if (user.getIsActive()) {
            throw new IllegalArgumentException("Account is already activated");
        }

        user.setIsActive(true);
        user.setActivationToken(null);
        user.setActivationTokenExpiry(null);
        userRepository.save(user);

        log.info("Account activated successfully for: {}", user.getContactEmail());
    }

    @Transactional
    public void resendActivationEmail(String email) {
        User user = userRepository.findByContactEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.getIsActive()) {
            throw new IllegalArgumentException("Account is already activated");
        }

        String activationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        user.setActivationToken(activationToken);
        user.setActivationTokenExpiry(tokenExpiry);
        userRepository.save(user);

        emailService.sendActivationEmail(user.getContactEmail(), user.getOrgName(), activationToken);
        log.info("Activation email resent to: {}", user.getContactEmail());
    }

    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        return UserResponse.builder()
                .id(user.getId())
                .contactEmail(user.getContactEmail())
                .orgName(user.getOrgName())
                .role(user.getRole())
                .createDate(user.getCreateDate().toLocalDate())
                .updateDate(user.getUpdateDate() != null ? user.getUpdateDate().toLocalDate() : null)
                .isActive(user.getIsActive())
                .build();
    }

    public List<ListUserResponse> getAllUsers() {
        log.info("Getting all users");
        
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> ListUserResponse.builder()
                        .id(user.getId())
                        .contactEmail(user.getContactEmail())
                        .orgName(user.getOrgName())
                        .role(user.getRole())
                        .isActive(user.getIsActive())
                        .createDate(user.getCreateDate().toLocalDate())
                        .updateDate(user.getUpdateDate() != null ? user.getUpdateDate().toLocalDate() : null)
                        .isDeleted(user.getIsDeleted())
                        .build())
                .toList();
    }

    @Transactional
    public UpdateProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        log.info("Profile update attempt for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        boolean emailChanged = false;
        String newToken = null;



        if (request.getOrgName() != null && !request.getOrgName().isEmpty()) {
            user.setOrgName(request.getOrgName());
            log.info("Organization name updated for user ID: {}", userId);
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            log.info("Password updated for user: {}", user.getContactEmail());
        }

        user.setUpdateDate(LocalDateTime.now());
        user = userRepository.save(user);

        String message = emailChanged 
            ? "Profile updated successfully. Please use your new email for future logins." 
            : "Profile updated successfully";

        log.info("Profile updated successfully for user: {}", user.getContactEmail());

        return UpdateProfileResponse.builder()
                .id(user.getId())
                .orgName(user.getOrgName())
                .role(user.getRole())
                .updateDate(user.getUpdateDate().toLocalDate())
                .message(message)
                .build();
    }

    @Transactional
    public void deleteUser(UUID userId) {
        log.info("Delete user attempt for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        if ("ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("Cannot delete admin user");
        }

        user.setIsDeleted(true);
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        log.info("User deleted successfully: {}", user.getContactEmail());
    }

    @Transactional
    public void undeleteUser(UUID userId) {
        log.info("Undelete user attempt for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        if (!user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not deleted");
        }

        user.setIsDeleted(false);
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        log.info("User undeleted successfully: {}", user.getContactEmail());
    }

    @Transactional
    public CreateUserResponse createUserByAdmin(CreateUserRequest request) {
        log.info("Admin creating user for email: {}", request.getEmail());

        if (userRepository.existsByContactEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Generate random 8-character password
        String temporaryPassword = generateRandomPassword(8);

        User user = User.builder()
                .orgName(request.getOrgName())
                .contactEmail(request.getEmail())
                .password(passwordEncoder.encode(temporaryPassword))
                .isActive(true)
                .role("INVESTOR")
                .createDate(LocalDateTime.now())
                .isDeleted(false)
                .build();

        user = userRepository.save(user);

        try {
            emailService.sendAccountCredentialsEmail(user.getContactEmail(), user.getOrgName(), temporaryPassword);
            log.info("Account credentials email sent to: {}", user.getContactEmail());
        } catch (Exception e) {
            log.error("Failed to send account credentials email", e);
        }

        log.info("User created successfully by admin: {}", user.getContactEmail());

        return CreateUserResponse.builder()
                .id(user.getId())
                .contactEmail(user.getContactEmail())
                .orgName(user.getOrgName())
                .role(user.getRole())
                .temporaryPassword(temporaryPassword)
                .createDate(user.getCreateDate().toLocalDate())
                .message("User created successfully. Credentials have been sent to the user's email.")
                .build();
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }

}
