package com.ecommerce.broker.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorBroker extends RuntimeException {
    private final HttpStatus estado;

    public ErrorBroker(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}
