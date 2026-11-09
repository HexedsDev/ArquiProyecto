package com.ecommerce.broker.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey llave;
    private final long minutos;

    public JwtService(@Value("${jwt.secreto}") String secreto, @Value("${jwt.minutos}") long minutos) {
        // HS256 necesita un secreto de al menos 32 caracteres
        this.llave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.minutos = minutos;
    }

    public String generarToken(String usuario) {
        long ahora = System.currentTimeMillis();

        return Jwts.builder()
                .subject(usuario)
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + minutos * 60_000))
                .signWith(llave)
                .compact();
    }

    // Si el token fue alterado, esta mal formado o ya vencio, lanza JwtException
    public String obtenerUsuario(String token) {
        return Jwts.parser()
                .verifyWith(llave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public long getMinutos() {
        return minutos;
    }
}
