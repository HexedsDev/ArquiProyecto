package com.ecommerce.pedidos.services;

import com.ecommerce.pedidos.clients.ProductoCliente;
import com.ecommerce.pedidos.dto.DisponibilidadProducto;
import com.ecommerce.pedidos.dto.PedidoSolicitud;
import com.ecommerce.pedidos.exceptions.ErrorPedido;
import com.ecommerce.pedidos.models.Pedido;
import com.ecommerce.pedidos.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoCliente productoCliente;

    @Override
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Override
    public List<Pedido> listarPorUsuario(String usuario) {
        return pedidoRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    @Override
    public Pedido buscarPorId(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ErrorPedido(HttpStatus.NOT_FOUND, "No existe el pedido con id " + id));
    }

    @Override
    @Transactional
    public Pedido crear(PedidoSolicitud solicitud, String usuario) {
        if (solicitud.productoId() == null) {
            throw new ErrorPedido(HttpStatus.BAD_REQUEST, "Debe indicar el producto");
        }
        if (solicitud.cantidad() == null || solicitud.cantidad() <= 0) {
            throw new ErrorPedido(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
        }

        // 1. Preguntar al servicio de productos si el producto existe y si alcanza
        DisponibilidadProducto producto = productoCliente.consultarDisponibilidad(
                solicitud.productoId(), solicitud.cantidad());

        if (!producto.disponible()) {
            throw new ErrorPedido(HttpStatus.CONFLICT,
                    "Existencia insuficiente de " + producto.nombre()
                            + ". Disponible: " + producto.existencia()
                            + ", solicitado: " + solicitud.cantidad());
        }

        // 2. Guardar el pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setProductoId(producto.productoId());
        pedido.setNombreProducto(producto.nombre());
        pedido.setCantidad(solicitud.cantidad());
        pedido.setPrecioUnitario(producto.precio());
        pedido.setTotal(producto.precio().multiply(BigDecimal.valueOf(solicitud.cantidad())));
        pedido.setFecha(LocalDateTime.now());

        Pedido guardado = pedidoRepository.save(pedido);

        // 3. Descontar la existencia. Si falla, la excepcion hace rollback del pedido guardado
        productoCliente.descontarExistencia(producto.productoId(), solicitud.cantidad());

        return guardado;
    }
}
