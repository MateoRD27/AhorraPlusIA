package com.unimag.ahorroplusia.services.impl;


import com.unimag.ahorroplusia.dto.AuthResponse;
import com.unimag.ahorroplusia.dto.LoginRequest;
import com.unimag.ahorroplusia.dto.RegisterRequest;
import com.unimag.ahorroplusia.dto.UserResponse;
import com.unimag.ahorroplusia.entity.entities.Role;
import com.unimag.ahorroplusia.entity.entities.User;
import com.unimag.ahorroplusia.entity.enums.ERole;
import com.unimag.ahorroplusia.exception.*;
import com.unimag.ahorroplusia.repository.RoleRepository;
import com.unimag.ahorroplusia.repository.UserRepository;
import com.unimag.ahorroplusia.security.jwt.JwtUtil;
import com.unimag.ahorroplusia.services.AuthService;
import com.unimag.ahorroplusia.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        Role roleUsuario = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER"));

        // Generar token de verificación
        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fixedSalary(request.getFixedSalary())
                .currentAvailableMoney(request.getCurrentAvailableMoney())
                .roles(Set.of(roleUsuario))
                .verificationToken(verificationToken)
                .verificationTokenExpiration(LocalDateTime.now().plusHours(24))
                .build();


        userRepository.save(user);

        // Enviar correo
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return AuthResponse.builder()
                .message("User registered successfully. Please check your email to verify your account.")
                .build();
    }


    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Bloquear si no ha verificado su correo
        if (!user.getVerifiedAccount()) {
            throw new EmailNotVerifiedException("Debe verificar su correo antes de iniciar sesión.");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .fixedSalary(user.getFixedSalary())
                .currentAvailableMoney(user.getCurrentAvailableMoney())
                .build();

        return AuthResponse.builder()
                .token(token)
                .message("Login successful")
                .user(userResponse)
                .build();
    }

    public void verifyAccount(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (user.getVerificationTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        user.setVerifiedAccount(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiration(null);

        userRepository.save(user);
    }

    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .fixedSalary(user.getFixedSalary())
                .currentAvailableMoney(user.getCurrentAvailableMoney())
                .build();
    }


}