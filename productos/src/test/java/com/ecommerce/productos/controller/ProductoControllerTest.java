package com.ecommerce.productos.controller;

import com.ecommerce.productos.model.Producto;
import com.ecommerce.productos.model.ProductoRepository;
import com.ecommerce.productos.view.CantidadSolicitud;
import com.ecommerce.productos.view.DisponibilidadVista;
import com.ecommerce.productos.view.ProductoSolicitud;
import com.ecommerce.productos.view.ProductoVista;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoController productoController;

    private Producto crearProducto(Integer id, String precio, Integer existencia) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre("Teclado");
        producto.setPrecio(new BigDecimal(precio));
        producto.setExistencia(existencia);
        return producto;
    }

    @Test
    void crearProductoValidoLoGuarda() {
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocacion -> {
            Producto p = invocacion.getArgument(0);
            p.setId(1);
            return p;
        });

        ResponseEntity<ProductoVista> respuesta = productoController.crear(
                new ProductoSolicitud("Teclado", "Mecanico", new BigDecimal("450.00"), 10));

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().id());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void crearConPrecioCeroLanzaError() {
        ErrorNegocio error = assertThrows(ErrorNegocio.class, () -> productoController.crear(
                new ProductoSolicitud("Teclado", null, BigDecimal.ZERO, 10)));

        assertEquals(HttpStatus.BAD_REQUEST, error.getEstado());
        assertEquals("El precio debe ser mayor a 0", error.getMessage());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void crearConExistenciaCeroLanzaError() {
        ErrorNegocio error = assertThrows(ErrorNegocio.class, () -> productoController.crear(
                new ProductoSolicitud("Teclado", null, new BigDecimal("10"), 0)));

        assertEquals("La existencia debe ser mayor a 0", error.getMessage());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void buscarProductoQueNoExisteDevuelve404() {
        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        ErrorNegocio error = assertThrows(ErrorNegocio.class, () -> productoController.buscar(99));

        assertEquals(HttpStatus.NOT_FOUND, error.getEstado());
    }

    @Test
    void disponibilidadIndicaFalseSiNoAlcanza() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(crearProducto(1, "450.00", 3)));

        DisponibilidadVista vista = productoController.disponibilidad(1, 5);

        assertFalse(vista.disponible());
        assertEquals(3, vista.existencia());
    }

    @Test
    void descontarRestaLaExistencia() {
        Producto producto = crearProducto(1, "450.00", 10);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);

        ProductoVista vista = productoController.descontar(1, new CantidadSolicitud(4));

        assertEquals(6, vista.existencia());
        verify(productoRepository).save(producto);
    }

    @Test
    void descontarMasDeLoQueHayLanzaConflicto() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(crearProducto(1, "450.00", 2)));

        ErrorNegocio error = assertThrows(ErrorNegocio.class,
                () -> productoController.descontar(1, new CantidadSolicitud(5)));

        assertEquals(HttpStatus.CONFLICT, error.getEstado());
        verify(productoRepository, never()).save(any());
    }
}
