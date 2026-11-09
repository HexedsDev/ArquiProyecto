package com.ecommerce.pedidos.clients;

import com.ecommerce.pedidos.dto.DisponibilidadProducto;
import com.ecommerce.pedidos.exceptions.ErrorPedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Map;

// Capa de acceso al servicio de productos (igual que el repositorio accede a la base de datos)
@Component
public class ProductoCliente {

    private final RestClient restClient;

    public ProductoCliente(@Value("${productos.url}") String urlProductos,
                           @Value("${llave.interna}") String llaveInterna,
                           @Value("${productos.timeout-ms}") long timeoutMs) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofMillis(timeoutMs));
        fabrica.setReadTimeout(Duration.ofMillis(timeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(urlProductos)
                .defaultHeader("X-Llave-Interna", llaveInterna)
                .requestFactory(fabrica)
                .build();
    }

    public DisponibilidadProducto consultarDisponibilidad(Integer productoId, Integer cantidad) {
        try {
            return restClient.get()
                    .uri("/productos/{id}/disponibilidad?cantidad={cantidad}", productoId, cantidad)
                    .retrieve()
                    .body(DisponibilidadProducto.class);
        } catch (RestClientException e) {
            throw traducirError(e, productoId);
        }
    }

    public void descontarExistencia(Integer productoId, Integer cantidad) {
        try {
            restClient.put()
                    .uri("/productos/{id}/descontar", productoId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("cantidad", cantidad))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw traducirError(e, productoId);
        }
    }

    private ErrorPedido traducirError(RestClientException e, Integer productoId) {
        if (e instanceof HttpClientErrorException.NotFound) {
            return new ErrorPedido(HttpStatus.NOT_FOUND, "El producto " + productoId + " no existe");
        }
        if (e instanceof HttpClientErrorException.Unauthorized || e instanceof HttpClientErrorException.Forbidden) {
            return new ErrorPedido(HttpStatus.BAD_GATEWAY,
                    "El servicio de productos rechazo la autenticacion del servicio de pedidos");
        }
        if (e instanceof HttpClientErrorException.Conflict) {
            return new ErrorPedido(HttpStatus.CONFLICT, "No hay existencia suficiente del producto " + productoId);
        }
        if (e instanceof ResourceAccessException) {
            if (e.getCause() instanceof SocketTimeoutException) {
                return new ErrorPedido(HttpStatus.GATEWAY_TIMEOUT, "El servicio de productos no respondio a tiempo");
            }
            return new ErrorPedido(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de productos no esta disponible");
        }
        return new ErrorPedido(HttpStatus.BAD_GATEWAY, "Error al comunicarse con el servicio de productos");
    }
}
