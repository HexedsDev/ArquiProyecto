package com.ecommerce.pedidos.services;

import com.ecommerce.pedidos.clients.ProductoCliente;
import com.ecommerce.pedidos.dto.DisponibilidadProducto;
import com.ecommerce.pedidos.dto.PedidoSolicitud;
import com.ecommerce.pedidos.exceptions.ErrorPedido;
import com.ecommerce.pedidos.models.Pedido;
import com.ecommerce.pedidos.repositories.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoCliente productoCliente;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private DisponibilidadProducto disponibilidad(int existencia, int cantidad) {
        return new DisponibilidadProducto(1, "Teclado", new BigDecimal("450.00"),
                existencia, cantidad, existencia >= cantidad);
    }

    @Test
    void crearPedidoConExistenciaSuficiente() {
        when(productoCliente.consultarDisponibilidad(1, 2)).thenReturn(disponibilidad(10, 2));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Pedido pedido = pedidoService.crear(new PedidoSolicitud(1, 2), "admin");

        assertEquals(new BigDecimal("900.00"), pedido.getTotal());
        assertEquals("Teclado", pedido.getNombreProducto());
        assertEquals("admin", pedido.getUsuario());
        verify(productoCliente).descontarExistencia(1, 2);
    }

    @Test
    void crearPedidoSinExistenciaLanzaConflicto() {
        when(productoCliente.consultarDisponibilidad(1, 20)).thenReturn(disponibilidad(5, 20));

        ErrorPedido error = assertThrows(ErrorPedido.class,
                () -> pedidoService.crear(new PedidoSolicitud(1, 20), "admin"));

        assertEquals(HttpStatus.CONFLICT, error.getEstado());
        verify(pedidoRepository, never()).save(any());
        verify(productoCliente, never()).descontarExistencia(anyInt(), anyInt());
    }

    @Test
    void crearPedidoDeProductoQueNoExiste() {
        when(productoCliente.consultarDisponibilidad(99, 1))
                .thenThrow(new ErrorPedido(HttpStatus.NOT_FOUND, "El producto 99 no existe"));

        ErrorPedido error = assertThrows(ErrorPedido.class,
                () -> pedidoService.crear(new PedidoSolicitud(99, 1), "admin"));

        assertEquals(HttpStatus.NOT_FOUND, error.getEstado());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void servicioDeProductosNoRespondeATiempo() {
        when(productoCliente.consultarDisponibilidad(1, 1))
                .thenThrow(new ErrorPedido(HttpStatus.GATEWAY_TIMEOUT, "El servicio de productos no respondio a tiempo"));

        ErrorPedido error = assertThrows(ErrorPedido.class,
                () -> pedidoService.crear(new PedidoSolicitud(1, 1), "admin"));

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, error.getEstado());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void servicioDeProductosRechazaLaAutenticacion() {
        when(productoCliente.consultarDisponibilidad(1, 1))
                .thenThrow(new ErrorPedido(HttpStatus.BAD_GATEWAY, "El servicio de productos rechazo la autenticacion"));

        ErrorPedido error = assertThrows(ErrorPedido.class,
                () -> pedidoService.crear(new PedidoSolicitud(1, 1), "admin"));

        assertEquals(HttpStatus.BAD_GATEWAY, error.getEstado());
    }

    @Test
    void cantidadCeroNoConsultaProductos() {
        ErrorPedido error = assertThrows(ErrorPedido.class,
                () -> pedidoService.crear(new PedidoSolicitud(1, 0), "admin"));

        assertEquals(HttpStatus.BAD_REQUEST, error.getEstado());
        verifyNoInteractions(productoCliente);
    }

    @Test
    void buscarPedidoQueNoExiste() {
        when(pedidoRepository.findById(5)).thenReturn(Optional.empty());

        ErrorPedido error = assertThrows(ErrorPedido.class, () -> pedidoService.buscarPorId(5));

        assertEquals(HttpStatus.NOT_FOUND, error.getEstado());
    }
}
