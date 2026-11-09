package com.ecommerce.pedidos.repositories;

import com.ecommerce.pedidos.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByUsuarioOrderByFechaDesc(String usuario);
}
