package com.ecommerce.pedidos.controllers;

import com.ecommerce.pedidos.dto.PedidoSolicitud;
import com.ecommerce.pedidos.models.Pedido;
import com.ecommerce.pedidos.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Creacion y consulta de pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    public List<Pedido> listar() {
        return pedidoService.listar();
    }

    @GetMapping("/mios")
    @Operation(summary = "Listar los pedidos del usuario que hace la peticion")
    public List<Pedido> misPedidos(
            @Parameter(description = "Lo agrega el broker con el usuario del token")
            @RequestHeader(value = "X-Usuario", defaultValue = "anonimo") String usuario) {
        return pedidoService.listarPorUsuario(usuario);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un pedido por id")
    public Pedido buscar(@PathVariable Integer id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear un pedido",
            description = "Verifica la existencia en el servicio de productos y la descuenta si el pedido se crea")
    public ResponseEntity<Pedido> crear(
            @RequestBody PedidoSolicitud solicitud,
            @Parameter(description = "Lo agrega el broker con el usuario del token")
            @RequestHeader(value = "X-Usuario", defaultValue = "anonimo") String usuario) {
        Pedido pedido = pedidoService.crear(solicitud, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }
}
