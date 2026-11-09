package com.ecommerce.broker.services;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRETO = "secreto-de-pruebas-con-mas-de-32-caracteres";

    @Test
    void elTokenGeneradoSePuedeLeer() {
        JwtService jwtService = new JwtService(SECRETO, 60);

        String token = jwtService.generarToken("admin");

        assertEquals("admin", jwtService.obtenerUsuario(token));
    }

    @Test
    void tokenAlteradoEsRechazado() {
        JwtService jwtService = new JwtService(SECRETO, 60);
        String token = jwtService.generarToken("admin");

        String alterado = token.substring(0, token.length() - 3) + "abc";

        assertThrows(JwtException.class, () -> jwtService.obtenerUsuario(alterado));
    }

    @Test
    void tokenFirmadoConOtroSecretoEsRechazado() {
        String token = new JwtService("otro-secreto-distinto-tambien-de-32-caracteres", 60).generarToken("admin");

        JwtService jwtService = new JwtService(SECRETO, 60);

        assertThrows(JwtException.class, () -> jwtService.obtenerUsuario(token));
    }

    @Test
    void tokenVencidoEsRechazado() {
        JwtService jwtService = new JwtService(SECRETO, -1);
        String token = jwtService.generarToken("admin");

        assertThrows(ExpiredJwtException.class, () -> jwtService.obtenerUsuario(token));
    }
}
