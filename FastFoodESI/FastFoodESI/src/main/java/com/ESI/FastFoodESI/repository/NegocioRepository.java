package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, UUID> {
    Optional<Negocio> findByNombre(String nombre);
}
