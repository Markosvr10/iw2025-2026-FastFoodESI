package com.ESI.FastFoodESI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "estados_pedido")
public class EstadoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String nombre; // Ej. "Pendiente", "En preparación", "Completado"

    // --- RELACIONES ---

    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY)
    private Set<Pedido> pedidos;

    // --- CONSTRUCTORES ---

    public EstadoPedido() {
    }

    // --- GETTERS Y SETTERS ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Set<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
