package com.ESI.FastFoodESI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cartas")
public class Carta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "La carta debe tener un nombre (ej. 'Menú Principal')")
    @Column(nullable = false)
    private String nombre;
    
    // --- RELACIONES ---


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "carta_productos",
        joinColumns = @JoinColumn(name = "carta_id"), 
        inverseJoinColumns = @JoinColumn(name = "producto_id") 
    )
    private Set<Producto> productos;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio; 

    // --- CONSTRUCTORES ---

    public Carta() {
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

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }
}