package com.ecommerce.productos.controller;

import com.ecommerce.productos.model.Producto;
import com.ecommerce.productos.model.ProductoRepository;
import com.ecommerce.productos.view.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "CRUD de productos y control de existencia")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public List<ProductoVista> listar() {
        return productoRepository.findAll()
                .stream()
                .map(ProductoVista::desde)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un producto por id")
    public ProductoVista buscar(@PathVariable Integer id) {
        return ProductoVista.desde(obtenerProducto(id));
    }

    @PostMapping
    @Operation(summary = "Crear un producto")
    public ResponseEntity<ProductoVista> crear(@RequestBody ProductoSolicitud datos) {
        validar(datos);

        Producto producto = new Producto();
        copiarDatos(datos, producto);

        Producto guardado = productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoVista.desde(guardado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto")
    public ProductoVista actualizar(@PathVariable Integer id, @RequestBody ProductoSolicitud datos) {
        Producto producto = obtenerProducto(id);
        validar(datos);
        copiarDatos(datos, producto);

        return ProductoVista.desde(productoRepository.save(producto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto")
    public MensajeVista eliminar(@PathVariable Integer id) {
        Producto producto = obtenerProducto(id);
        productoRepository.delete(producto);
        return new MensajeVista("Producto " + id + " eliminado");
    }

    @GetMapping("/{id}/disponibilidad")
    @Operation(summary = "Consultar si hay existencia suficiente",
            description = "Lo usa el servicio de pedidos antes de crear un pedido")
    public DisponibilidadVista disponibilidad(@PathVariable Integer id, @RequestParam Integer cantidad) {
        validarCantidad(cantidad);
        Producto producto = obtenerProducto(id);

        return new DisponibilidadVista(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getExistencia(),
                cantidad,
                producto.getExistencia() >= cantidad
        );
    }

    @PutMapping("/{id}/descontar")
    @Transactional
    @Operation(summary = "Descontar existencia",
            description = "Lo usa el servicio de pedidos cuando el pedido se crea")
    public ProductoVista descontar(@PathVariable Integer id, @RequestBody CantidadSolicitud solicitud) {
        validarCantidad(solicitud.cantidad());
        Producto producto = obtenerProducto(id);

        if (producto.getExistencia() < solicitud.cantidad()) {
            throw new ErrorNegocio(HttpStatus.CONFLICT,
                    "Existencia insuficiente. Disponible: " + producto.getExistencia());
        }

        producto.setExistencia(producto.getExistencia() - solicitud.cantidad());
        return ProductoVista.desde(productoRepository.save(producto));
    }

    private Producto obtenerProducto(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ErrorNegocio(HttpStatus.NOT_FOUND, "No existe el producto con id " + id));
    }

    private void validar(ProductoSolicitud datos) {
        if (datos.nombre() == null || datos.nombre().isBlank()) {
            throw new ErrorNegocio(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        if (datos.precio() == null || datos.precio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ErrorNegocio(HttpStatus.BAD_REQUEST, "El precio debe ser mayor a 0");
        }
        if (datos.existencia() == null || datos.existencia() <= 0) {
            throw new ErrorNegocio(HttpStatus.BAD_REQUEST, "La existencia debe ser mayor a 0");
        }
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ErrorNegocio(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
        }
    }

    private void copiarDatos(ProductoSolicitud datos, Producto producto) {
        producto.setNombre(datos.nombre().trim());
        producto.setDescripcion(datos.descripcion());
        producto.setPrecio(datos.precio());
        producto.setExistencia(datos.existencia());
    }
}
