package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, UUID> {
}
