package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Camarero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CamareroRepository extends JpaRepository<Camarero, UUID> {
}
