package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByNegocio(Negocio negocio);
}
