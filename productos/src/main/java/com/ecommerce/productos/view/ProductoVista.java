package com.ecommerce.productos.view;

import com.ecommerce.productos.model.Producto;

import java.math.BigDecimal;

public record ProductoVista(
        Integer id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer existencia,
        String estado
) {
    public static ProductoVista desde(Producto producto) {
        String estado = producto.getExistencia() > 0 ? "DISPONIBLE" : "AGOTADO";
        return new ProductoVista(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getExistencia(),
                estado
        );
    }
}
