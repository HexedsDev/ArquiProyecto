package com.ecommerce.pedidos.dto;

import java.math.BigDecimal;

// Lo que responde el servicio de productos en /productos/{id}/disponibilidad
public record DisponibilidadProducto(
        Integer productoId,
        String nombre,
        BigDecimal precio,
        Integer existencia,
        Integer cantidadSolicitada,
        boolean disponible
) {
}
