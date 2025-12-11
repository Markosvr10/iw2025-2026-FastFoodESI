package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.LineaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LineaPedidoRepository extends JpaRepository<LineaPedido, UUID> {

}