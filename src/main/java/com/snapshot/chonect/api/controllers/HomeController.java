package com.snapshot.chonect.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Hidden;

/**
 * Controlador para manejar la ruta raíz y redirigir a la documentación
 */
@Controller
@Hidden // Ocultar en Swagger
public class HomeController {

    /**
     * Redirige la ruta raíz a GraphiQL
     * 
     * @return RedirectView a /graphiql
     */
    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/graphiql");
    }
}
