package com.ESI.FastFoodESI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "negocios")
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(length = 1024)
    private String descripcion;

    @NotBlank(message = "La dirección no puede estar vacía")
    @Column(nullable = false)
    private String direccion;

    private String telefono;

    @Email(message = "El formato del correo no es válido")
    @Column(unique = true)
    private String correo;

    @Transient // nEmpleados es un valor calculado, no una columna de BBDD
    public Integer getnEmpleados() {
        // La lógica para calcular esto iría en la capa de servicio
        return 0; 
    }

    // --- RELACIONES ---

    @OneToOne
    @JoinColumn(name = "propietario_id", referencedColumnName = "id")
    private Propietario propietario; // Asumiendo que crearás una @Entity "Propietario"

    @OneToMany(mappedBy = "negocio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Carta> cartas;

    // --- CONSTRUCTORES ---

    public Negocio() {
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public Set<Carta> getCartas() {
        return cartas;
    }

    public void setCartas(Set<Carta> cartas) {
        this.cartas = cartas;
    }
}