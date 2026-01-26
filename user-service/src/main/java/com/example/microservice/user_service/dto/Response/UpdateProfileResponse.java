package com.example.microservice.user_service.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Update profile response")
public class UpdateProfileResponse {

    @Schema(description = "User ID")
    private UUID id;

    @Schema(description = "Organization name")
    private String orgName;

    @Schema(description = "User role")
    private String role;

    @Schema(description = "Update date")
    private LocalDate updateDate;

    @Schema(description = "Message")
    private String message;

}
