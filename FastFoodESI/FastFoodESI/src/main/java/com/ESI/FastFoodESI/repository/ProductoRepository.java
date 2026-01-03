package com.ESI.FastFoodESI.repository;

import com.ESI.FastFoodESI.dto.RankingItemDTO;
import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Propietario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import com.ESI.FastFoodESI.dto.RankingItemDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    
    List<Producto> findByStockLessThan(Integer stock);
    
    @Query("SELECT p FROM Carta c JOIN c.productos p WHERE c.negocio = :negocio")
    List<Producto> findByNegocio(@Param("negocio") Negocio negocio);

    @Query("SELECT new com.ESI.FastFoodESI.dto.RankingItemDTO(pr.nombre, CAST(SUM(lp.cantidad) AS bigdecimal)) " +
           "FROM LineaPedido lp JOIN lp.producto pr " +
           "WHERE lp.pedido.fechaHora >= :desde " +
           "AND pr.negocio.propietario = :propietario " +
           "GROUP BY pr " +
           "ORDER BY SUM(lp.cantidad) DESC")
    List<RankingItemDTO> findTopProductosVentas(
            @Param("desde") LocalDateTime desde, 
            @Param("propietario") Propietario propietario, 
            Pageable pageable);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
