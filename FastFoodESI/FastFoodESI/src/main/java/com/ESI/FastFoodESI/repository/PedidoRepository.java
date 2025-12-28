package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    
    long count(); 
    long countByFechaHoraAfter(LocalDateTime fecha);
    List<Pedido> findByClienteId(UUID clienteId);
    List<Pedido> findByEstadoId(UUID estadoId);
    List<Pedido> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);
}
