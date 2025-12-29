package com.ESI.FastFoodESI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "TIPOS_PRODUCTO")
public class TipoProducto {
    @Id
    private UUID id;
    private String nombre;

    public TipoProducto() {
    } // Constructor vacío necesario

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
}