package com.snapshot.chonect.infrastructure.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.snapshot.chonect.utils.EmailTemplateService;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

// no pretendo reutilizar los metodos de IEmailService asi que mejor ni lo hago jajajaj

@Service
@AllArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000; // 1 segundo

    @Autowired
    private final JavaMailSender emailSender;

    @Autowired
    private final EmailTemplateService emailTemplateService;

    // private final TemplateEngine templateEngine;

    // aqui simplemente cargo toda la info y conecto con el template del email
    public void sendVerificationEmail(String to, String subject, String textContent, String htmlContent) throws MessagingException {
        sendEmailWithRetry(to, subject, textContent, htmlContent, true);
    }

    // Método centralizado para enviar emails de verificación
    public void sendVerificationEmail(String email, String firstName, String verificationCode) throws BadRequestException {
        String subject = emailTemplateService.getVerificationEmailSubject();
        String htmlMessage = emailTemplateService.generateVerificationEmailHtml(firstName, verificationCode);
        String textMessage = emailTemplateService.generateVerificationEmailText(firstName, verificationCode);
        try {
            sendVerificationEmail(email, subject, textMessage, htmlMessage);
        } catch (MessagingException e) {
            logger.error("Error de mensajería al enviar correo de verificación a {}: {}", email, e.getMessage(), e);
            String errorMessage = analyzeEmailError(e);
            if (errorMessage.contains("SMTP") || errorMessage.contains("conexión")) {
                throw new BadRequestException("Error de conexión con el servidor de correo. El correo podría enviarse en unos minutos. Si el problema persiste, contacte al soporte.");
            } else if (errorMessage.contains("crítico") || errorMessage.contains("intentos")) {
                throw new BadRequestException("Error crítico al enviar correo de verificación. Por favor, contacte al administrador del sistema.");
            } else {
                throw new BadRequestException("Error al enviar correo de verificación. Por favor, inténtelo de nuevo más tarde.");
            }
        }
    }

    // Método genérico para envío de correos con reintentos
    public void sendEmailWithRetry(String to, String subject, String textContent, String htmlContent, boolean isCritical) throws MessagingException {
        MessagingException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.info("Intento {}/{} de envío de correo a: {}", attempt, MAX_RETRIES, to);

                if (attempt > 1) {
                    // Esperar antes de reintentar (solo si no es el primer intento)
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                }

                logger.debug("Configuración de correo - Host: {}, Puerto: {}, Usuario: {}",
                    System.getProperty("spring.mail.host", "N/A"),
                    System.getProperty("spring.mail.port", "N/A"),
                    System.getProperty("spring.mail.username", "N/A"));

                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(textContent, htmlContent);
                helper.setFrom("onboarding@resend.dev");

                emailSender.send(message);
                logger.info("Correo enviado exitosamente a: {} en el intento {}", to, attempt);
                return; // Éxito, salir del método

            } catch (MailException e) {
                lastException = new MessagingException("Error de correo en intento " + attempt + ": " + e.getMessage(), e);
                logger.warn("Error de correo temporal en intento {}/{} para {}: {}", attempt, MAX_RETRIES, to, e.getMessage());

                if (attempt == MAX_RETRIES) {
                    // Último intento fallido, notificar si es crítico
                    if (isCritical) {
                        notifyCriticalEmailFailure(to, subject, e);
                    }
                }

            } catch (MessagingException e) {
                lastException = e;
                logger.error("Error de mensajería SMTP en intento {}/{} para {}: {}", attempt, MAX_RETRIES, to, e.getMessage());
                logger.error("Causa raíz: {}", e.getCause() != null ? e.getCause().getMessage() : "No disponible");

                // Para errores de SMTP, no reintentar (son errores permanentes)
                break;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                lastException = new MessagingException("Interrupción durante reintento de correo: " + e.getMessage(), e);
                break;

            } catch (Exception e) {
                lastException = new MessagingException("Error inesperado en intento " + attempt + ": " + e.getMessage(), e);
                logger.error("Error inesperado en intento {}/{} para {}: {}", attempt, MAX_RETRIES, to, e.getMessage());

                // Para errores inesperados, intentar una vez más pero no todas las veces
                if (attempt == MAX_RETRIES) {
                    break;
                }
            }
        }

        // Si llegamos aquí, todos los intentos fallaron
        logger.error("Todos los intentos de envío fallaron para: {}", to);
        throw lastException != null ? lastException : new MessagingException("Error desconocido al enviar correo");
    }

    // Método para notificar fallos críticos (puede ser extendido para enviar notificaciones)
    private void notifyCriticalEmailFailure(String to, String subject, Exception cause) {
        logger.error("🚨 FALLO CRÍTICO: No se pudo enviar correo crítico a {} con asunto '{}'", to, subject);
        logger.error("Causa: {}", cause.getMessage());

        // Aquí podrías agregar:
        // - Envío de notificación a administradores
        // - Almacenamiento en base de datos para reintento manual
        // - Métricas de monitoreo
        // - Alertas a sistemas externos

        // Por ahora solo logueamos, pero esta estructura permite extensión futura
    }

    // Método para analizar errores de correo y proporcionar información útil
    private String analyzeEmailError(MessagingException e) {
        String message = e.getMessage().toLowerCase();
        String cause = e.getCause() != null ? e.getCause().getMessage().toLowerCase() : "";

        if (message.contains("smtp") || cause.contains("smtp")) {
            return "Error SMTP";
        } else if (message.contains("conexión") || message.contains("connection") || cause.contains("connection")) {
            return "Error de conexión";
        } else if (message.contains("timeout") || cause.contains("timeout")) {
            return "Error de tiempo de espera";
        } else if (message.contains("autenticación") || message.contains("authentication") || cause.contains("authentication")) {
            return "Error de autenticación";
        } else if (message.contains("crítico") || message.contains("intentos") || message.contains("todos los intentos")) {
            return "Error crítico de correo";
        } else {
            return "Error general de correo";
        }
    }
}
