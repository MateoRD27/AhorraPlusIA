package com.unimag.ahorroplusia.services;

import com.unimag.ahorroplusia.dto.AuthResponse;
import com.unimag.ahorroplusia.dto.LoginRequest;
import com.unimag.ahorroplusia.dto.RegisterRequest;
import com.unimag.ahorroplusia.dto.UserResponse;
import org.springframework.stereotype.Service;


public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void verifyAccount(String token);
    UserResponse getCurrentUser();
}
