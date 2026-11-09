package com.ecommerce.broker.services;

import com.ecommerce.broker.exceptions.ErrorBroker;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.Duration;

// No tiene logica de negocio: solo arma la peticion hacia el servicio y devuelve lo que responda
@Service
public class ProxyService {

    private final RestClient restClient;
    private final String llaveInterna;

    public ProxyService(@Value("${llave.interna}") String llaveInterna,
                        @Value("${servicios.timeout-ms}") long timeoutMs) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofMillis(timeoutMs));
        fabrica.setReadTimeout(Duration.ofMillis(timeoutMs));

        this.restClient = RestClient.builder().requestFactory(fabrica).build();
        this.llaveInterna = llaveInterna;
    }

    public ResponseEntity<byte[]> reenviar(String urlServicio, String nombreServicio,
                                           HttpServletRequest request, byte[] cuerpo) {
        String destino = urlServicio + request.getRequestURI();
        if (request.getQueryString() != null) {
            destino += "?" + request.getQueryString();
        }

        String usuario = (String) request.getAttribute("usuario");

        RestClient.RequestBodySpec peticion = restClient
                .method(HttpMethod.valueOf(request.getMethod()))
                .uri(URI.create(destino))
                .header("X-Llave-Interna", llaveInterna)
                .header("X-Usuario", usuario);

        if (cuerpo != null && cuerpo.length > 0) {
            String tipo = request.getContentType() != null ? request.getContentType() : MediaType.APPLICATION_JSON_VALUE;
            peticion.contentType(MediaType.parseMediaType(tipo));
            peticion.body(cuerpo);
        }

        try {
            return peticion.exchange((pet, respuesta) -> {
                byte[] datos = respuesta.getBody().readAllBytes();

                HttpHeaders cabeceras = new HttpHeaders();
                if (respuesta.getHeaders().getContentType() != null) {
                    cabeceras.setContentType(respuesta.getHeaders().getContentType());
                }
                return new ResponseEntity<>(datos, cabeceras, respuesta.getStatusCode());
            });
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new ErrorBroker(HttpStatus.GATEWAY_TIMEOUT,
                        "El servicio de " + nombreServicio + " no respondio a tiempo");
            }
            throw new ErrorBroker(HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de " + nombreServicio + " no esta disponible");
        }
    }
}
