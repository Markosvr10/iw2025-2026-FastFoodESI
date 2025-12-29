package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TipoProductoRepository extends JpaRepository<TipoProducto, UUID> {
    // Está vacío, solo necesitamos los métodos por defecto
}