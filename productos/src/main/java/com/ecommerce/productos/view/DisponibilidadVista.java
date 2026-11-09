package com.ecommerce.productos.view;

import java.math.BigDecimal;

public record DisponibilidadVista(
        Integer productoId,
        String nombre,
        BigDecimal precio,
        Integer existencia,
        Integer cantidadSolicitada,
        boolean disponible
) {
}
