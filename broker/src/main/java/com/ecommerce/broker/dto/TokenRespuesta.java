package com.ecommerce.broker.dto;

public record TokenRespuesta(String token, String tipo, long expiraEnMinutos) {
}
