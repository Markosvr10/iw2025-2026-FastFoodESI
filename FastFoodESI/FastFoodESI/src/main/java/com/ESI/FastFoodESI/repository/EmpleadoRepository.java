package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.dto.RankingItemDTO;
import com.ESI.FastFoodESI.model.Empleado;
import com.ESI.FastFoodESI.model.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {

    @Query("select e from Empleado e where e.negocio.propietario = :propietario")
    List<Empleado> findByPropietario(@Param("propietario") Propietario propietario);

    @Query("SELECT new com.ESI.FastFoodESI.dto.RankingItemDTO(e.nombre || ' ' || e.apellido, SUM(p.total)) " +
           "FROM Pedido p JOIN p.empleado e " +
           "WHERE p.fechaHora >= :desde " +
           "AND e.negocio.propietario = :propietario " + 
           "GROUP BY e " +
           "ORDER BY SUM(p.total) DESC")
    List<RankingItemDTO> findTopEmpleadosVentas(
            @Param("desde") LocalDateTime desde, 
            @Param("propietario") Propietario propietario, 
            Pageable pageable);

    Optional<Empleado> findByDni(String dni);
}