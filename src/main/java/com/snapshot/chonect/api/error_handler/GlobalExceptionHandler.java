package com.snapshot.chonect.api.error_handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.snapshot.chonect.api.dto.errors.BaseErrorResponse;
import com.snapshot.chonect.api.dto.errors.ErrorsResponse;
import com.snapshot.chonect.utils.exceptions.BadRequestException;
import com.snapshot.chonect.utils.exceptions.IdNotFoundException;
import com.snapshot.chonect.utils.exceptions.UnauthorizedException;
import com.snapshot.chonect.utils.exceptions.UserExistsException;

/**
 * Controlador de errores global para manejar excepciones personalizadas
 * Proporciona respuestas de error consistentes y amigables para Swagger
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseErrorResponse handleBadRequest(BadRequestException exception) {
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ErrorsResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(IdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseErrorResponse handleNotFound(IdNotFoundException exception) {
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ErrorsResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseErrorResponse handleUnauthorized(UnauthorizedException exception) {
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ErrorsResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public BaseErrorResponse handleUserExists(UserExistsException exception) {
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ErrorsResponse.builder()
                .code(HttpStatus.CONFLICT.value())
                .status(HttpStatus.CONFLICT.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseErrorResponse handleIllegalArgument(IllegalArgumentException exception) {
        List<String> errors = new ArrayList<>();
        errors.add(exception.getMessage());

        return ErrorsResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseErrorResponse handleNullPointer(NullPointerException exception) {
        List<String> errors = new ArrayList<>();
        errors.add("Error interno del servidor: dato requerido no proporcionado");

        return ErrorsResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseErrorResponse handleGeneral(Exception exception) {
        List<String> errors = new ArrayList<>();
        errors.add("Error interno del servidor. Por favor contacte al administrador.");

        return ErrorsResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .errors(errors)
                .build();
    }
}
