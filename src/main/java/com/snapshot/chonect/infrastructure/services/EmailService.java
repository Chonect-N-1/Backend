package com.snapshot.chonect.infrastructure.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
// import org.thymeleaf.TemplateEngine;
// import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

// no pretendo reutilizar los metodos de IEmailService asi que mejor ni lo hago jajajaj

@Service
@AllArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    // private final TemplateEngine templateEngine;

    // aqui simplemente cargo toda la info y conecto con el template del email
    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        try {
            logger.info("Intentando enviar correo de verificación a: {}", to);
            logger.info("Configuración de correo - Host: {}, Puerto: {}, Usuario: {}",
                System.getProperty("spring.mail.host", "N/A"),
                System.getProperty("spring.mail.port", "N/A"),
                System.getProperty("spring.mail.username", "N/A"));

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setFrom("onboarding@resend.dev");

            emailSender.send(message);
            logger.info("Correo de verificación enviado exitosamente a: {}", to);

        } catch (MessagingException e) {
            logger.error("Error de mensajería al enviar correo de verificación a: {}", to, e);
            logger.error("Causa raíz: {}", e.getCause() != null ? e.getCause().getMessage() : "No disponible");
            throw new MessagingException("Error al enviar correo de verificación: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al enviar correo de verificación a: {}", to, e);
            throw new MessagingException("Error inesperado al enviar correo: " + e.getMessage(), e);
        }
    }
}
