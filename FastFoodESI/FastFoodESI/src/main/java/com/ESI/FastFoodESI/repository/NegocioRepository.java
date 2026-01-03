package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import com.ESI.FastFoodESI.dto.RankingItemDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface NegocioRepository extends JpaRepository<Negocio, UUID> {

    @Query("SELECT DISTINCT n FROM Negocio n LEFT JOIN FETCH n.empleados WHERE n.propietario = :propietario")
    List<Negocio> findByPropietario(@Param("propietario") Propietario propietario);

    @Query("SELECT new com.ESI.FastFoodESI.dto.RankingItemDTO(n.nombre, SUM(p.total)) " +
           "FROM Pedido p JOIN p.negocio n " +
           "WHERE p.fechaHora >= :desde " +
           "AND n.propietario = :propietario " +
           "GROUP BY n " +
           "ORDER BY SUM(p.total) DESC")
    List<RankingItemDTO> findTopNegociosVentas (
            @Param("desde") LocalDateTime desde, 
            @Param("propietario") Propietario propietario, 
            Pageable pageable);

    Optional<Negocio> findByNombre(String nombre);
}
