package com.safanner.ecomm.repositories;

import com.safanner.ecomm.entities.User;
import com.safanner.ecomm.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByEmail(String email);

    // Used to check of ADMIN exists
    Optional<User> findByRole(UserRole userRole);

}