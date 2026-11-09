package com.ecommerce.broker.services;

import com.ecommerce.broker.dto.LoginSolicitud;
import com.ecommerce.broker.dto.TokenRespuesta;
import com.ecommerce.broker.exceptions.ErrorBroker;
import com.ecommerce.broker.models.Usuario;
import com.ecommerce.broker.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioAdmin() {
        Usuario usuario = new Usuario();
        usuario.setUsuario("admin");
        usuario.setPassword("hash-guardado");
        return usuario;
    }

    @Test
    void loginCorrectoDevuelveToken() {
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuarioAdmin()));
        when(passwordEncoder.matches("admin123", "hash-guardado")).thenReturn(true);
        when(jwtService.generarToken("admin")).thenReturn("token-de-prueba");
        when(jwtService.getMinutos()).thenReturn(60L);

        TokenRespuesta respuesta = authService.login(new LoginSolicitud("admin", "admin123"));

        assertEquals("token-de-prueba", respuesta.token());
        assertEquals("Bearer", respuesta.tipo());
    }

    @Test
    void passwordIncorrectaLanza401() {
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuarioAdmin()));
        when(passwordEncoder.matches("otra", "hash-guardado")).thenReturn(false);

        ErrorBroker error = assertThrows(ErrorBroker.class,
                () -> authService.login(new LoginSolicitud("admin", "otra")));

        assertEquals(HttpStatus.UNAUTHORIZED, error.getEstado());
        verify(jwtService, never()).generarToken(anyString());
    }

    @Test
    void usuarioQueNoExisteLanza401() {
        when(usuarioRepository.findByUsuario("nadie")).thenReturn(Optional.empty());

        ErrorBroker error = assertThrows(ErrorBroker.class,
                () -> authService.login(new LoginSolicitud("nadie", "123")));

        assertEquals(HttpStatus.UNAUTHORIZED, error.getEstado());
        verifyNoInteractions(passwordEncoder);
    }
}
