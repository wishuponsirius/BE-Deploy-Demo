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
public class ListUserResponse {

    private UUID id;
    private String contactEmail;
    private String orgName;
    private String role;
    private LocalDate createDate; 
    private LocalDate updateDate;
    private Boolean isActive;
    private Boolean isDeleted;


}
