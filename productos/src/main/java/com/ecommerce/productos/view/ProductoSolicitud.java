package com.ecommerce.productos.view;

import java.math.BigDecimal;

public record ProductoSolicitud(
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer existencia
) {
}
