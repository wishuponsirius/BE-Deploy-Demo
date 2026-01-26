package com.example.microservice.user_service.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.cglib.core.Local;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private UUID id;
    private String email;
    private String orgName;
    private String role;
    private LocalDate createDate;
    private LocalDate updateDate;

}
