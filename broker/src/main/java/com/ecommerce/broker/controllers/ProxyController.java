package com.ecommerce.broker.controllers;

import com.ecommerce.broker.services.ProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// Cuando la peticion llega aqui, el FiltroToken ya reviso que el token sea valido
@RestController
@Tag(name = "Redireccion", description = "El broker valida el token y reenvia la peticion al servicio que corresponde")
@SecurityRequirement(name = "bearerAuth")
public class ProxyController {

    @Autowired
    private ProxyService proxyService;

    @Value("${servicios.productos-url}")
    private String urlProductos;

    @Value("${servicios.pedidos-url}")
    private String urlPedidos;

    @RequestMapping(value = {"/productos", "/productos/{id}", "/productos/{id}/{accion}"},
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @Operation(summary = "Redirige al servicio de productos")
    public ResponseEntity<byte[]> productos(HttpServletRequest request,
                                            @RequestBody(required = false) byte[] cuerpo) {
        return proxyService.reenviar(urlProductos, "productos", request, cuerpo);
    }

    @RequestMapping(value = {"/pedidos", "/pedidos/{id}"},
            method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Redirige al servicio de pedidos")
    public ResponseEntity<byte[]> pedidos(HttpServletRequest request,
                                          @RequestBody(required = false) byte[] cuerpo) {
        return proxyService.reenviar(urlPedidos, "pedidos", request, cuerpo);
    }
}
