package com.chonect.backend.api.error_handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ProblemDetail;

import com.chonect.backend.utils.exceptions.IdNotFoundException;
import com.chonect.backend.utils.exceptions.UserAlreadyExistsException;

@RestControllerAdvice
public class BadRequestController {
    @ExceptionHandler(IdNotFoundException.class)
    public ProblemDetail IdNotFound(IdNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Bad Request");
        problem.setType(java.net.URI.create("https://chonect.dev/problems/id-not-found"));
        return problem;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Bad Request");
        problem.setType(java.net.URI.create("https://chonect.dev/problems/user-already-exists"));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleBadRequest(
            MethodArgumentNotValidException exception) {

        List<String> errors = new ArrayList<>();

        exception.getAllErrors()
                .forEach(error -> errors.add(error.getDefaultMessage()));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Se encontraron errores de validación");
        problem.setTitle("Bad Request");
        problem.setType(java.net.URI.create("https://chonect.dev/problems/validation-error"));
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Mensaje genérico y claro para el cliente; evita exponer detalles de la BD
        String message = "Violación de integridad de datos (posible duplicidad o restricción incumplida)";
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message);
        problem.setTitle("Conflict");
        problem.setType(java.net.URI.create("https://chonect.dev/problems/data-integrity"));
        return problem;
    }
}