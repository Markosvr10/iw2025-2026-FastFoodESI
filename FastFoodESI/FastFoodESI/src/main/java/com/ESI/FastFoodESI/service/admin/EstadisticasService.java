package com.ESI.FastFoodESI.service.admin;

import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.dto.EstadisticaDTO;
import com.ESI.FastFoodESI.dto.RankingItemDTO;
import com.ESI.FastFoodESI.repository.EmpleadoRepository;
import com.ESI.FastFoodESI.repository.NegocioRepository;
import com.ESI.FastFoodESI.repository.PedidoRepository; 
import com.ESI.FastFoodESI.repository.ProductoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;     
import java.time.LocalDateTime; 
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    private final NegocioRepository negocioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository; 
    
    public EstadisticasService(NegocioRepository negocioRepository,
                               EmpleadoRepository empleadoRepository,
                               ProductoRepository productoRepository,
                               PedidoRepository pedidoRepository) {
        this.negocioRepository = negocioRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public EstadisticaDTO obtenerEstadisticasPedidos() {
        EstadisticaDTO dto = new EstadisticaDTO();

        dto.setnPedidosTotal(pedidoRepository.count());

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        dto.setnPedidosDia(pedidoRepository.countByFechaHoraAfter(inicioDia));

        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        dto.setnPedidosMes(pedidoRepository.countByFechaHoraAfter(inicioMes));

        LocalDateTime inicioAnno = LocalDate.now().withDayOfYear(1).atStartOfDay();
        dto.setnPedidosAnno(pedidoRepository.countByFechaHoraAfter(inicioAnno));

        return dto;
    }

    private LocalDateTime calcularFechaInicio(String periodo) {
        if (periodo == null) return LocalDateTime.of(1970, 1, 1, 0, 0); // Histórico por defecto

        switch (periodo) {
            case "DIA": return LocalDate.now().atStartOfDay();
            case "MES": return LocalDate.now().withDayOfMonth(1).atStartOfDay();
            case "ANNO": return LocalDate.now().withDayOfYear(1).atStartOfDay();
            case "HISTORICO": default: return LocalDateTime.of(1970, 1, 1, 0, 0);
        }
    }

    public List<RankingItemDTO> getRankingEmpleados(String periodo) {
        return empleadoRepository.findTopEmpleadosVentas(
            calcularFechaInicio(periodo), 
            PageRequest.of(0, 5) 
        );
    }

    public List<RankingItemDTO> getRankingNegocios(String periodo) {
        return negocioRepository.findTopNegociosVentas(
            calcularFechaInicio(periodo), 
            PageRequest.of(0, 5)
        );
    }

    public List<RankingItemDTO> getRankingProductos(String periodo) {
        return productoRepository.findTopProductosVentas(
            calcularFechaInicio(periodo), 
            PageRequest.of(0, 5)
        );
    }


    public long countNegocios() {
        return negocioRepository.count();
    }

    public long countEmpleados() {
        return empleadoRepository.count();
    }

    public long countProductos() {
        return productoRepository.count();
    }

    public BigDecimal calcularMasaSalarial() {
        return empleadoRepository.findAll().stream()
                .map(e -> e.getSalario() != null ? e.getSalario() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Producto> obtenerProductosBajoStock(int limite) {

        return productoRepository.findByStockLessThan(limite);
    }
}