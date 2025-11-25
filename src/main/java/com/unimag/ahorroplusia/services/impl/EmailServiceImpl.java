package com.unimag.ahorroplusia.services.impl;

import com.unimag.ahorroplusia.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.verification.url}")
    private String verificationUrl;

    @Override
    public void sendVerificationEmail(String to, String token) {
        String link = verificationUrl + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirma tu correo - AhorroPlus IA");
        message.setText(
                "Gracias por registrarte.\n\n" +
                        "Por favor confirma tu correo haciendo clic en el siguiente enlace:\n" +
                        link + "\n\n" +
                        "Este enlace expirará en 24 horas."
        );

        mailSender.send(message);
    }
}
