package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.model.Empleado;
import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {
    @Query("select e from Empleado e where e.negocio.propietario = :propietario")
    List<Empleado> findByPropietario(@Param("propietario") Propietario propietario);

    Optional<Empleado> findByDni(String dni);

    List<Empleado> findByNegocio(Negocio negocio);
}
