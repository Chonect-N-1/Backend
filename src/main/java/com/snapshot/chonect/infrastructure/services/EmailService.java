package com.snapshot.chonect.infrastructure.services;

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
    
    @Autowired
    private JavaMailSender emailSender;

    // private final TemplateEngine templateEngine;

    // aqui simplemente cargo toda la info y conecto con el template del email
    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {

        // String htmlContent = templateEngine.process(templateName, context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        emailSender.send(message);
    }
}
