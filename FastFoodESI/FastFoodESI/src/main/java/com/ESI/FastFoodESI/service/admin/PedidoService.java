package com.ESI.FastFoodESI.service.admin;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.model.EstadoPedido;
import com.ESI.FastFoodESI.model.LineaPedido;
import com.ESI.FastFoodESI.model.Pedido;
import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.repository.LineaPedidoRepository;
import com.ESI.FastFoodESI.repository.PedidoRepository;
import com.ESI.FastFoodESI.repository.ProductoRepository;
import com.ESI.FastFoodESI.dto.LineaPedidoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) 
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final LineaPedidoRepository lineaPedidoRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, 
                         LineaPedidoRepository lineaPedidoRepository,
                         ProductoRepository productoRepository,
                         ProductoService productoService) {
        this.pedidoRepository = pedidoRepository;
        this.lineaPedidoRepository = lineaPedidoRepository;
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    
    public Pedido crearPedido(Cliente cliente, List<LineaPedidoDTO> lineasDTO) {
        if (lineasDTO == null || lineasDTO.isEmpty()) {
            throw new IllegalArgumentException("El pedido no contiene líneas de producto.");
        }
        
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setCliente(cliente);
        
        
        Set<LineaPedido> lineas = lineasDTO.stream()
            .map(dto -> createAndValidateLinea(dto, nuevoPedido))
            .collect(Collectors.toSet());
        
        nuevoPedido.setLineas(lineas);
        return pedidoRepository.save(nuevoPedido); 
    }
    
    
    @Transactional
    public Pedido modificarEstado(UUID pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado."));
        
        
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }
    
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }
    
    public Pedido findById(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

   
    private LineaPedido createAndValidateLinea(LineaPedidoDTO dto, Pedido pedido) {
        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new RuntimeException("Producto ID no válido: " + dto.productoId()));

        productoService.checkStockAndReserve(dto.productoId(), dto.cantidad());

        LineaPedido linea = new LineaPedido();
        linea.setPedido(pedido);
        linea.setProducto(producto);
        linea.setCantidad(dto.cantidad());
    
        linea.setPrecioUnitario(producto.getImporte()); 
        
        return linea;
    }
}