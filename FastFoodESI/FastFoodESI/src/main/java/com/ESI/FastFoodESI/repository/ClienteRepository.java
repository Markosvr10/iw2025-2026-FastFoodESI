package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findByDni(String dni);
    Optional<Cliente> findByCorreo(String correo);
}
