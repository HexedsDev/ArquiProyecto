package com.ecommerce.broker.config;

import com.ecommerce.broker.models.Usuario;
import com.ecommerce.broker.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Al arrancar crea el usuario administrador si todavia no existe
@Component
public class DatosIniciales implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.usuario}")
    private String adminUsuario;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByUsuario(adminUsuario)) {
            Usuario admin = new Usuario();
            admin.setUsuario(adminUsuario);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            usuarioRepository.save(admin);
        }
    }
}
