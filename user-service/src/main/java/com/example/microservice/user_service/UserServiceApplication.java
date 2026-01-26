package com.example.microservice.user_service;

import com.example.microservice.user_service.entity.User;
import com.example.microservice.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdminAccount(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "huhuaa855@gmail.com";
            
            if (!userRepository.existsByContactEmail(adminEmail)) {
                User admin = User.builder()
                        .orgName("System Administrator")
                        .contactEmail(adminEmail)
                        .password(passwordEncoder.encode("123456"))
                        .isActive(true)
                        .role("ADMIN")
                        .createDate(LocalDateTime.now())
                        .isDeleted(false)
                        .build();
                
                userRepository.save(admin);
                log.info("Admin account created successfully - Email: {}, Password: 123456", adminEmail);
            } else {
                log.info("Admin account already exists");
            }
        };
    }

}
