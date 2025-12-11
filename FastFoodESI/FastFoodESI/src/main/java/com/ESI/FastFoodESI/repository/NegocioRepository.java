package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, UUID> {

    List<Negocio> findByPropietario(Propietario propietario);
}