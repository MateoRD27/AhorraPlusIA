package com.unimag.ahorroplusia.services;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
}

