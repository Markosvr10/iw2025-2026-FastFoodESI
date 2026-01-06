package com.ESI.FastFoodESI.service.pedido;

import com.ESI.FastFoodESI.model.*;
import com.ESI.FastFoodESI.repository.EstadoPedidoRepository;
import com.ESI.FastFoodESI.repository.LineaPedidoRepository;
import com.ESI.FastFoodESI.repository.PedidoRepository;
import com.ESI.FastFoodESI.service.cliente.CarritoService;
import com.ESI.FastFoodESI.dto.LineaCarrito;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class PedidoClienteService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoClienteService.class);

    private final PedidoRepository pedidoRepository;
    private final LineaPedidoRepository lineaPedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    public PedidoClienteService(PedidoRepository pedidoRepository,
            LineaPedidoRepository lineaPedidoRepository,
            EstadoPedidoRepository estadoPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.lineaPedidoRepository = lineaPedidoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
    }

    // metodo de pago
    @Transactional
    public Pedido confirmarPedido(CarritoService carrito, Cliente cliente, String tipoEntrega, String metodoPago,
            String direccion) {

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFechaHora(LocalDateTime.now());

        EstadoPedido estadoInicial = estadoPedidoRepository.findByNombre("RECIBIDO")
                .orElseThrow(() -> new RuntimeException("Estado 'RECIBIDO' no encontrado"));
        pedido.setEstado(estadoInicial);

        pedido.setTotal(BigDecimal.valueOf(carrito.calcularTotal()));

        // Guardamos los datos nuevos
        pedido.setTipoEntrega(tipoEntrega);
        pedido.setMetodoPago(metodoPago);

        // Si es tarjeta o paypal simulado, lo marcamos como pagado.
        // Si es efectivo, lo dejamos como NO pagado (se paga al repartidor).
        if ("Efectivo".equals(metodoPago)) {
            pedido.setPagado(false);
        } else {
            pedido.setPagado(true);
        }

        pedido = pedidoRepository.save(pedido);

        for (LineaCarrito item : carrito.getLineas()) {
            LineaPedido lineaBD = new LineaPedido();
            lineaBD.setPedido(pedido);
            lineaBD.setProducto(item.getProducto());
            lineaBD.setCantidad(item.getCantidad());
            lineaBD.setPrecioUnitario(item.getProducto().getImporte());

            lineaPedidoRepository.save(lineaBD);
        }

        logger.info("AUDITORIA - NUEVO PEDIDO CLIENTE: ID: {} | Cliente: {} | Total: {} € | Pago: {}",
                pedido.getId(),
                cliente.getCorreo(),
                pedido.getTotal(),
                metodoPago);

        return pedido;
    }

    public List<Pedido> obtenerPedidosDeCliente(Cliente cliente) {
        return pedidoRepository.findByClienteIdOrderByFechaHoraDesc(cliente.getId());
    }
}