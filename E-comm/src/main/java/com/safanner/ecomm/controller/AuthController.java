package com.safanner.ecomm.controller;


import com.fasterxml.jackson.databind.util.JSONPObject;
import com.safanner.ecomm.dto.AuthenticationRequest;
import com.safanner.ecomm.dto.SignupRequest;
import com.safanner.ecomm.dto.UserDto;
import com.safanner.ecomm.entities.User;
import com.safanner.ecomm.repositories.UserRepository;
import com.safanner.ecomm.services.auth.AuthService;
import com.safanner.ecomm.services.jwt.UserDetailsServiceImpl;
import com.safanner.ecomm.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final AuthService authService;

    @PostMapping("/authenticate")
    public void createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
                                          HttpServletResponse response) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        } catch (Exception e) {
            throw new BadCredentialsException("incorrect user or pass");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        Optional<User> user = userRepository.findFirstByEmail(userDetails.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        if (user.isPresent()) {
            try {
                response.getWriter().write(new JSONObject().put("userId", user.get().getId())
                        .put("role", user.get().getRole()).toString());
                response.addHeader("Access-Control-Expose-Headers", "Authorization");
                response.addHeader("Access-Control-Allow-Headers", "Authorization, X-PINGOTHER, Origin, "
                        + "X-Requested-With, Content-Type, Accent, X-Custom-header");
            } catch (Exception e) {
            }
            String TOKEN_PREFIX = "Bearer ";
            String HEADER_STRING = "Authorization";
            response.addHeader(HEADER_STRING, TOKEN_PREFIX + jwt);
            log.info("USER TOKEN GENERATED : {} ", jwt);
        }

    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpUser(@RequestBody SignupRequest signupRequest) {
        if (authService.hasUserWithEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>("user already exists", HttpStatus.NOT_ACCEPTABLE);
        }
        UserDto userDto = authService.createUser(signupRequest);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

}


