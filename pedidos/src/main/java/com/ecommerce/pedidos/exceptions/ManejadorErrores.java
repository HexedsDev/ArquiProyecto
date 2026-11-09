package com.ecommerce.pedidos.exceptions;

import com.ecommerce.pedidos.dto.ErrorRespuesta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ErrorPedido.class)
    public ResponseEntity<ErrorRespuesta> errorPedido(ErrorPedido e) {
        return ResponseEntity.status(e.getEstado())
                .body(new ErrorRespuesta(e.getEstado().value(), e.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorRespuesta> datosMalFormados(Exception e) {
        return ResponseEntity.badRequest()
                .body(new ErrorRespuesta(400, "Los datos enviados no tienen el formato correcto"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorRespuesta> rutaNoExiste(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorRespuesta(404, "La ruta solicitada no existe"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorRespuesta> metodoNoPermitido(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorRespuesta(405, "Metodo " + e.getMethod() + " no permitido en esta ruta"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> errorGeneral(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorRespuesta(500, "Error interno en el servicio de pedidos"));
    }
}
