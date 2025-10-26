package com.snapshot.chonect.infrastructure.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.snapshot.chonect.utils.EmailTemplateService;
import com.snapshot.chonect.utils.exceptions.BadRequestException;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000; // 1 segundo

    @Autowired
    private final WebClient webClient;

    @Autowired
    private final EmailTemplateService emailTemplateService;

    // Método centralizado para enviar emails de verificación
    public void sendVerificationEmail(String email, String firstName, String verificationCode) throws BadRequestException {
        String subject = emailTemplateService.getVerificationEmailSubject();
        String htmlMessage = emailTemplateService.generateVerificationEmailHtml(firstName, verificationCode);
        String textMessage = emailTemplateService.generateVerificationEmailText(firstName, verificationCode);

        try {
            sendEmailWithRetry(email, subject, textMessage, htmlMessage);
        } catch (Exception e) {
            logger.error("Error al enviar correo de verificación a {}: {}", email, e.getMessage(), e);
            String errorMessage = analyzeEmailError(e);
            if (errorMessage.contains("API") || errorMessage.contains("conexión")) {
                throw new BadRequestException("Error de conexión con el servidor de correo. El correo podría enviarse en unos minutos. Si el problema persiste, contacte al soporte.");
            } else if (errorMessage.contains("crítico") || errorMessage.contains("intentos")) {
                throw new BadRequestException("Error crítico al enviar correo de verificación. Por favor, contacte al administrador del sistema.");
            } else {
                throw new BadRequestException("Error al enviar correo de verificación. Por favor, inténtelo de nuevo más tarde.");
            }
        }
    }

    // Método genérico para envío de correos con reintentos usando la API de Resend
    public void sendEmailWithRetry(String to, String subject, String textContent, String htmlContent) throws Exception {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.info("Intento {}/{} de envío de correo a: {}", attempt, MAX_RETRIES, to);

                if (attempt > 1) {
                    // Esperar antes de reintentar (solo si no es el primer intento)
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                }

                // Preparar el payload para la API de Resend
                Map<String, Object> payload = Map.of(
                    "from", "onboarding@resend.dev",
                    "to", List.of(to),
                    "subject", subject,
                    "html", htmlContent,
                    "text", textContent
                );

                // Enviar usando WebClient
                final int currentAttempt = attempt;
                Map<String, Object> response = webClient.post()
                    .uri("/emails")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .doOnSuccess(res -> logger.info("Email enviado exitosamente a: {} en el intento {}", to, currentAttempt))
                    .doOnError(error -> logger.warn("Error en intento {}/{} para {}: {}", currentAttempt, MAX_RETRIES, to, error.getMessage()))
                    .block(); // Bloquear para mantener la API síncrona

                if (response != null && response.containsKey("id")) {
                    logger.info("Correo enviado exitosamente a: {} con ID: {}", to, response.get("id"));
                    return; // Éxito, salir del método
                }

            } catch (WebClientResponseException e) {
                lastException = e;
                logger.error("Error de respuesta HTTP en intento {}/{} para {}: {} - {}",
                    attempt, MAX_RETRIES, to, e.getStatusCode(), e.getResponseBodyAsString());

                // Para errores 4xx (cliente), no reintentar
                if (e.getStatusCode().is4xxClientError()) {
                    break;
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                lastException = e;
                break;

            } catch (Exception e) {
                lastException = e;
                logger.error("Error inesperado en intento {}/{} para {}: {}", attempt, MAX_RETRIES, to, e.getMessage());

                // Para errores inesperados, intentar una vez más pero no todas las veces
                if (attempt == MAX_RETRIES) {
                    break;
                }
            }
        }

        // Si llegamos aquí, todos los intentos fallaron
        logger.error("Todos los intentos de envío fallaron para: {}", to);
        throw lastException != null ? lastException : new Exception("Error desconocido al enviar correo");
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
    private String analyzeEmailError(Exception e) {
        String message = e.getMessage().toLowerCase();

        if (message.contains("api") || message.contains("http")) {
            return "Error API";
        } else if (message.contains("conexión") || message.contains("connection") || message.contains("timeout")) {
            return "Error de conexión";
        } else if (message.contains("autenticación") || message.contains("authentication") || message.contains("unauthorized")) {
            return "Error de autenticación";
        } else if (message.contains("crítico") || message.contains("intentos") || message.contains("todos los intentos")) {
            return "Error crítico de correo";
        } else {
            return "Error general de correo";
        }
    }
}
