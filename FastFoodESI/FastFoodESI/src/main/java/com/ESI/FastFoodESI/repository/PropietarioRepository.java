package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, UUID> {
    
    Optional<Propietario> findByCorreo(String correo);
}