package com.example.microservice.user_service.dto.Response;

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
public class CreateUserResponse {

    private UUID id;
    private String contactEmail;
    private String orgName;
    private String role;
    private String temporaryPassword;
    private LocalDate createDate;
    private String message;
}
