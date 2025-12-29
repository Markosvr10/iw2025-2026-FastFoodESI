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

    @Column(precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_pedido_id", nullable = false)
    private EstadoPedido estado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<LineaPedido> lineas;

    @ManyToOne
    @JoinColumn(name = "NEGOCIO_ID") // Esto lo vincula con la columna que pusimos en data.sql
    private Negocio negocio;

    // --- ATRIBUTO CALCULADO ---

    @Transient
    public BigDecimal getImporteTotalCalculado() {
        if (this.lineas == null || this.lineas.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return this.lineas.stream()
                .map(LineaPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Pedido() {
        this.fechaHora = LocalDateTime.now();
    }

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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
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

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }

    // No hay setter para 'importeTotal'
}