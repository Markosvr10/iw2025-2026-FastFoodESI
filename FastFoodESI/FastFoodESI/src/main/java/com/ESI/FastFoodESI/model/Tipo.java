package com.ESI.FastFoodESI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tipos_producto") // Lo llamo "tipos_producto" para ser más específico
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String nombre; // Ej. "Hamburguesa", "Bebida", "Postre"

    // --- RELACIONES ---

    @OneToMany(mappedBy = "tipo", fetch = FetchType.LAZY)
    private Set<Producto> productos;

    // --- CONSTRUCTORES ---

    public Tipo() {
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

    public Set<Producto> getProductos() {
        return productos;
    }

    public void setProductos(Set<Producto> productos) {
        this.productos = productos;
    }
}
