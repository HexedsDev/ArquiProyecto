package com.ecommerce.productos.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

// Solo deja pasar las peticiones que traen la llave interna.
// Esa llave la conocen el broker y el servicio de pedidos, nadie mas.
@Component
public class FiltroLlaveInterna extends OncePerRequestFilter {

    @Value("${llave.interna}")
    private String llaveInterna;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = request.getRequestURI();
        return ruta.startsWith("/swagger-ui") || ruta.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String llaveRecibida = request.getHeader("X-Llave-Interna");

        if (llaveRecibida == null || !sonIguales(llaveRecibida, llaveInterna)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"estado\":401,\"error\":\"Acceso denegado: la peticion debe pasar por el broker\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean sonIguales(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
