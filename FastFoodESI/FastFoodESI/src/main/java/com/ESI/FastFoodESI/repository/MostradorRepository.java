package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Mostrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MostradorRepository extends JpaRepository<Mostrador, UUID> {
}
