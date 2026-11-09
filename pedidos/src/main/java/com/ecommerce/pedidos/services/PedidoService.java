package com.ecommerce.pedidos.services;

import com.ecommerce.pedidos.dto.PedidoSolicitud;
import com.ecommerce.pedidos.models.Pedido;

import java.util.List;

public interface PedidoService {
    List<Pedido> listar();

    List<Pedido> listarPorUsuario(String usuario);

    Pedido buscarPorId(Integer id);

    Pedido crear(PedidoSolicitud solicitud, String usuario);
}
