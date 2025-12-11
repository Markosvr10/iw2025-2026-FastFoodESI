package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    
    Producto findByNombre(String nombre);
}