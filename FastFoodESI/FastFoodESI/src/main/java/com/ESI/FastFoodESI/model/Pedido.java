package com.ESI.FastFoodESI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "La fecha y hora no pueden ser nulas")
    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "tipo_entrega")
    private String tipoEntrega; // "Mesa", "Domicilio", "Recoger"

    // --- NUEVOS CAMPOS PARA EL PAGO ---
    @Column(name = "metodo_pago")
    private String metodoPago; // "TARJETA", "PAYPAL", "EFECTIVO"

    @Column(name = "pagado")
    private boolean pagado = false;

    // --- RELACIONES ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_pedido_id", nullable = false)
    private EstadoPedido estado; // Usando la entidad renombrada

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<LineaPedido> lineas;

    // --- ATRIBUTO CALCULADO ---

    @Transient
    public BigDecimal getImporteTotal() {
        if (this.lineas == null || this.lineas.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return this.lineas.stream()
                .map(LineaPedido::getSubtotal) 
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- CONSTRUCTORES ---

    public Pedido() {
        this.fechaHora = LocalDateTime.now();
    }

    // --- GETTERS Y SETTERS ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }



    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Set<LineaPedido> getLineas() {
        return lineas;
    }

    public void setLineas(Set<LineaPedido> lineas) {
        this.lineas = lineas;
    }

    // No hay setter para 'importeTotal'
}