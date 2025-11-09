package com.snapshot.chonect.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.snapshot.chonect.api.dto.errors.BaseErrorResponse;
import com.snapshot.chonect.api.dto.errors.ErrorsResponse;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import com.snapshot.chonect.utils.exceptions.EntityCreationException;
import com.snapshot.chonect.utils.exceptions.EntityDeletionException;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.exceptions.UnauthorizedException;
import com.snapshot.chonect.utils.exceptions.UserExistsException;
import com.snapshot.chonect.utils.exceptions.VerificationExpiredException;
import com.snapshot.chonect.utils.exceptions.VerificationNotFoundException;
import com.snapshot.chonect.utils.exceptions.InvalidVerificationCodeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Manejador global de excepciones para prevenir errores 500 no controlados
 * Convierte excepciones no manejadas en respuestas HTTP apropiadas
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ========== EXCEPCIONES PERSONALIZADAS ==========

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseErrorResponse> handleBadRequest(BadRequestException exception) {
        logger.warn("BadRequestException: {}", exception.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ResponseEntity.badRequest()
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST.name())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<BaseErrorResponse> handleNotFound(IdNotFoundException exception) {
        logger.warn("IdNotFoundException: {}", exception.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .status(HttpStatus.NOT_FOUND.name())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseErrorResponse> handleUnauthorized(UnauthorizedException exception) {
        logger.warn("UnauthorizedException: {}", exception.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.UNAUTHORIZED.value())
                        .status(HttpStatus.UNAUTHORIZED.name())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<BaseErrorResponse> handleUserExists(UserExistsException exception) {
        logger.warn("UserExistsException: {}", exception.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.CONFLICT.value())
                        .status(HttpStatus.CONFLICT.name())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler({VerificationNotFoundException.class, VerificationExpiredException.class, InvalidVerificationCodeException.class})
    public ResponseEntity<BaseErrorResponse> handleVerificationExceptions(RuntimeException exception) {
        logger.warn("VerificationException: {}", exception.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ResponseEntity.badRequest()
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST.name())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler({EntityCreationException.class, EntityDeletionException.class})
    public ResponseEntity<BaseErrorResponse> handleEntityExceptions(RuntimeException exception) {
        logger.error("EntityException: {}", exception.getMessage(), exception);
        List<String> errors = new ArrayList<>();
        errors.add("Error en operación de base de datos: " + exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .errors(errors)
                        .build());
    }

    // ========== EXCEPCIONES DE VALIDACIÓN ==========

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<BaseErrorResponse> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        logger.warn("ValidationException: {}", ex.getMessage());
        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        return ResponseEntity.badRequest()
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST.name())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseErrorResponse> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        logger.warn("TypeMismatchException: {}", ex.getMessage());
        List<String> errors = new ArrayList<>();

        Class<?> requiredType = ex.getRequiredType();
        String typeName = requiredType != null ? requiredType.getSimpleName() : "desconocido";
        errors.add("Tipo de parámetro inválido: " + ex.getName() + " debe ser de tipo " + typeName);

        return ResponseEntity.badRequest()
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST.name())
                        .errors(errors)
                        .build());
    }

    // ========== EXCEPCIONES DE BASE DE DATOS ==========

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<BaseErrorResponse> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        logger.error("DataIntegrityViolationException: {}", ex.getMessage(), ex);
        List<String> errors = new ArrayList<>();
        errors.add("Error de integridad de datos. Verifique que los datos sean únicos y válidos.");

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.CONFLICT.value())
                        .status(HttpStatus.CONFLICT.name())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<BaseErrorResponse> handleDataAccessException(org.springframework.dao.DataAccessException ex) {
        logger.error("DataAccessException: {}", ex.getMessage(), ex);
        List<String> errors = new ArrayList<>();
        errors.add("Error de acceso a base de datos. Por favor, inténtelo más tarde.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .errors(errors)
                        .build());
    }

    // ========== EXCEPCIONES DE RED Y CONECTIVIDAD ==========

    @ExceptionHandler(java.net.ConnectException.class)
    public ResponseEntity<BaseErrorResponse> handleConnectException(java.net.ConnectException ex) {
        logger.error("ConnectException: {}", ex.getMessage(), ex);
        List<String> errors = new ArrayList<>();
        errors.add("Error de conexión. Verifique su conexión a internet e inténtelo nuevamente.");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .status(HttpStatus.SERVICE_UNAVAILABLE.name())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(java.util.concurrent.TimeoutException.class)
    public ResponseEntity<BaseErrorResponse> handleTimeoutException(java.util.concurrent.TimeoutException ex) {
        logger.error("TimeoutException: {}", ex.getMessage(), ex);
        List<String> errors = new ArrayList<>();
        errors.add("Tiempo de espera agotado. La operación tomó demasiado tiempo en completarse.");

        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.REQUEST_TIMEOUT.value())
                        .status(HttpStatus.REQUEST_TIMEOUT.name())
                        .errors(errors)
                        .build());
    }

    // ========== EXCEPCIÓN GENÉRICA - ÚLTIMA DEFENSA ==========

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseErrorResponse> handleGenericException(Exception exception, WebRequest request) {
        if (logger.isErrorEnabled()) {
            logger.error("Unhandled Exception at {}: {}", request.getDescription(false), exception.getMessage(), exception);
        }

        List<String> errors = new ArrayList<>();
        errors.add("Error interno del servidor. Por favor contacte al administrador del sistema.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorsResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .errors(errors)
                        .build());
    }
}
