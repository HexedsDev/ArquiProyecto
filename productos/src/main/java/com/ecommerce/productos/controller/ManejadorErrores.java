package com.ecommerce.productos.controller;

import com.ecommerce.productos.view.ErrorVista;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ErrorNegocio.class)
    public ResponseEntity<ErrorVista> errorNegocio(ErrorNegocio e) {
        return ResponseEntity.status(e.getEstado())
                .body(new ErrorVista(e.getEstado().value(), e.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorVista> datosMalFormados(Exception e) {
        return ResponseEntity.badRequest()
                .body(new ErrorVista(400, "Los datos enviados no tienen el formato correcto"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorVista> rutaNoExiste(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorVista(404, "La ruta solicitada no existe"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorVista> metodoNoPermitido(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorVista(405, "Metodo " + e.getMethod() + " no permitido en esta ruta"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorVista> errorGeneral(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorVista(500, "Error interno en el servicio de productos"));
    }
}
