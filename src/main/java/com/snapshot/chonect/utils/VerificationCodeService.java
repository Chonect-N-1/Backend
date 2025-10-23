package com.snapshot.chonect.utils;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class VerificationCodeService {

    /**
     * Genera un código de verificación de 6 dígitos.
     * @return Código de verificación como String.
     */
    public String generateVerificationCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 999999);
        return String.valueOf(code);
    }
}
