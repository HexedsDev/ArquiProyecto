package com.ecommerce.productos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI documentacion() {
        SecurityScheme llave = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-Llave-Interna");

        return new OpenAPI()
                .info(new Info()
                        .title("Servicio de productos")
                        .description("Servicio MVC encargado de los productos del ecommerce")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes("llaveInterna", llave))
                .addSecurityItem(new SecurityRequirement().addList("llaveInterna"));
    }
}
