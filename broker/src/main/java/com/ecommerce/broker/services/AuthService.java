package com.ecommerce.broker.services;

import com.ecommerce.broker.dto.LoginSolicitud;
import com.ecommerce.broker.dto.TokenRespuesta;
import com.ecommerce.broker.exceptions.ErrorBroker;
import com.ecommerce.broker.models.Usuario;
import com.ecommerce.broker.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public TokenRespuesta login(LoginSolicitud solicitud) {
        if (solicitud.usuario() == null || solicitud.password() == null) {
            throw new ErrorBroker(HttpStatus.BAD_REQUEST, "Debe enviar usuario y password");
        }

        Usuario usuario = usuarioRepository.findByUsuario(solicitud.usuario()).orElse(null);

        // Mismo mensaje si no existe el usuario o si la password esta mal
        if (usuario == null || !passwordEncoder.matches(solicitud.password(), usuario.getPassword())) {
            throw new ErrorBroker(HttpStatus.UNAUTHORIZED, "Usuario o password incorrectos");
        }

        String token = jwtService.generarToken(usuario.getUsuario());
        return new TokenRespuesta(token, "Bearer", jwtService.getMinutos());
    }
}
