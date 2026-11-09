package com.ecommerce.broker.controllers;

import com.ecommerce.broker.dto.LoginSolicitud;
import com.ecommerce.broker.dto.TokenRespuesta;
import com.ecommerce.broker.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Autenticacion", description = "Rutas publicas del broker")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    @Operation(summary = "Estado del broker")
    public Map<String, String> inicio() {
        return Map.of("servicio", "broker", "estado", "activo");
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Iniciar sesion y obtener el token")
    public TokenRespuesta login(@RequestBody LoginSolicitud solicitud) {
        return authService.login(solicitud);
    }
}
