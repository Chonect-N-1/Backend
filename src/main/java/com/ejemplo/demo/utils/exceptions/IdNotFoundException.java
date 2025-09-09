package com.ejemplo.demo.utils.exceptions;

public class IdNotFoundException extends RuntimeException {
    private static final String ERROR_MESSAGE = "aqui no hay registros en la entidad %s con el id suministrado";

    public IdNotFoundException(String nameEntity) {
        super(ERROR_MESSAGE.formatted(nameEntity));
    }
}