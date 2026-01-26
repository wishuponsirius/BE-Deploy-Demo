package com.example.microservice.user_service.repository;

import com.example.microservice.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByContactEmail(String contactEmail);
    
    boolean existsByContactEmail(String contactEmail);
    
    Optional<User> findByActivationToken(String activationToken);
    
}
