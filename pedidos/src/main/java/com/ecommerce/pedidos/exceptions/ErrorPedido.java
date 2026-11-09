package com.ecommerce.pedidos.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorPedido extends RuntimeException {
    private final HttpStatus estado;

    public ErrorPedido(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}
