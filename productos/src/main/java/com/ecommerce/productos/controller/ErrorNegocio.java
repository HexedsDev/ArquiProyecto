package com.ecommerce.productos.controller;

import org.springframework.http.HttpStatus;

public class ErrorNegocio extends RuntimeException {
    private final HttpStatus estado;

    public ErrorNegocio(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}
