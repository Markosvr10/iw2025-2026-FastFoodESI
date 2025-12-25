package com.ESI.FastFoodESI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    //-----------------------------------------------------------------------------------------------
    @Column(name = "imagen_url")
    private String imagenUrl;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String nombre;

    @NotNull(message = "El importe no puede ser nulo")
    @PositiveOrZero(message = "El importe no puede ser negativo")
    @Column(nullable = false)
    private BigDecimal importe;

    @Column(length = 1024) // Damos más espacio para la descripción
    private String descripcion;

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    // --- RELACIONES ---

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private Set<LineaPedido> lineasPedido; // Pedidos que contienen este producto

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER) // Es útil saber el tipo de producto al cargarlo
    @JoinColumn(name = "tipo_id", nullable = false)
    private Tipo tipo; // Asumiendo que crearás una @Entity "Tipo"

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "producto_alergenos",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "alergeno_id")
    )
    private Set<Alergeno> alergenos; // Asumiendo que crearás una @Entity "Alergeno"

    @ManyToMany(mappedBy = "productos", fetch = FetchType.LAZY)
    private Set<Carta> cartas; // Menús en los que aparece este producto

    // -----------------------------------------------------------------------------------------------
    @NotNull(message = "El producto debe pertenecer a un negocio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    // --- CONSTRUCTORES ---

    public Producto() {
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

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Set<LineaPedido> getLineasPedido() {
        return lineasPedido;
    }

    public void setLineasPedido(Set<LineaPedido> lineasPedido) {
        this.lineasPedido = lineasPedido;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Set<Alergeno> getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(Set<Alergeno> alergenos) {
        this.alergenos = alergenos;
    }

    public Set<Carta> getCartas() {
        return cartas;
    }

    public void setCartas(Set<Carta> cartas) {
        this.cartas = cartas;
    }


//-----------------------------------------------------------------------------------------------
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }
}