package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Carta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartaRepository extends JpaRepository<Carta, UUID> {
    Optional<Carta> findByNombre(String nombre);
}
