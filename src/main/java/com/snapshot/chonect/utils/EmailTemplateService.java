package com.snapshot.chonect.utils;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    public EmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Genera el contenido HTML para el email de verificación usando Thymeleaf.
     * @param firstName Nombre del usuario.
     * @param verificationCode Código de verificación.
     * @return Contenido HTML del email.
     */
    public String generateVerificationEmailHtml(String firstName, String verificationCode) {
        Context context = new Context();
        context.setVariable("name", firstName);
        context.setVariable("verificationCode", verificationCode);
        return templateEngine.process("email-verification", context);
    }

    /**
     * Genera el contenido de texto plano para el email de verificación.
     * @param firstName Nombre del usuario.
     * @param verificationCode Código de verificación.
     * @return Contenido de texto plano del email.
     */
    public String generateVerificationEmailText(String firstName, String verificationCode) {
        return "¡Hola " + firstName + "!\n\n"
                + "Gracias por registrarte en Chonect.\n\n"
                + "Tu código de verificación es: " + verificationCode + "\n\n"
                + "Por favor ingresa este código en la aplicación para verificar tu cuenta.\n\n"
                + "Este código expirará en 15 minutos.\n\n"
                + "Si no solicitaste este registro, puedes ignorar este mensaje.\n\n"
                + "Saludos,\n"
                + "El equipo de Chonect";
    }

    /**
     * Genera el asunto del email de verificación.
     * @return Asunto del email.
     */
    public String getVerificationEmailSubject() {
        return "Verificación de cuenta - Chonect";
    }
}
