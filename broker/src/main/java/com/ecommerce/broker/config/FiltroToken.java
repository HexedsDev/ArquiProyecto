package com.ecommerce.broker.config;

import com.ecommerce.broker.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Se ejecuta antes que cualquier controlador. Si no hay token valido, la peticion no sigue.
@Component
public class FiltroToken extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = request.getRequestURI();
        return ruta.equals("/")
                || ruta.startsWith("/auth/")
                || ruta.startsWith("/swagger-ui")
                || ruta.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecera = request.getHeader("Authorization");

        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            rechazar(response, "Falta el token. Envie el encabezado Authorization: Bearer <token>");
            return;
        }

        String token = cabecera.substring(7).trim();

        try {
            String usuario = jwtService.obtenerUsuario(token);
            request.setAttribute("usuario", usuario);
        } catch (ExpiredJwtException e) {
            rechazar(response, "El token ya vencio, vuelva a iniciar sesion");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            rechazar(response, "Token invalido");
            return;
        }

        chain.doFilter(request, response);
    }

    private void rechazar(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"estado\":401,\"error\":\"" + mensaje + "\"}");
    }
}
