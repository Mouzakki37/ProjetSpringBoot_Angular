package com.safanner.ecomm.services.auth;

import com.safanner.ecomm.dto.SignupRequest;
import com.safanner.ecomm.dto.UserDto;


public interface AuthService {
    UserDto createUser(SignupRequest signupRequest);

    Boolean hasUserWithEmail(String email);

    void createAdminAccount();
}
