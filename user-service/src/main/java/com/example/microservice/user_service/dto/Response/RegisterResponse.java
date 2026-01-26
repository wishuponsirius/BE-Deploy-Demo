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
public class RegisterResponse {

    private String type = "Bearer";
    private UUID id;
    private String email;
    private String orgName;
    private String role;
    private LocalDate createDate;


    public RegisterResponse(UUID id, String email, String orgName, String role, LocalDate createDate) {
        this.id = id;
        this.email = email;
        this.orgName = orgName;
        this.role = role;
        this.createDate = createDate;
    }

}
