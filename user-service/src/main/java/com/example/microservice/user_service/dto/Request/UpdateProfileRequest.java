package com.example.microservice.user_service.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Update profile request")
public class UpdateProfileRequest {

    @Schema(description = "Organization name", example = "ABC Investment Corp")
    @Size(min = 2, max = 255, message = "Organization name must be between 2 and 255 characters")
    private String orgName;

    @Schema(description = "Current password (required if changing email or password)")
    private String currentPassword;

    @Schema(description = "New password (optional, only if changing password)")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}
