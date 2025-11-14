package com.ESI.FastFoodESI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "repartidores")
public class Repartidor extends Empleado {

    // --- CONSTRUCTORES ---
    public Repartidor() {
        super();
    }
    
}