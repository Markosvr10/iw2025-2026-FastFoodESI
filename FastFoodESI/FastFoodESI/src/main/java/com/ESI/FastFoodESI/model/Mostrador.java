package com.ESI.FastFoodESI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "mostradores")
public class Mostrador extends Empleado {

    // --- CONSTRUCTORES ---
    public Mostrador() {
        super();
    }
    
}