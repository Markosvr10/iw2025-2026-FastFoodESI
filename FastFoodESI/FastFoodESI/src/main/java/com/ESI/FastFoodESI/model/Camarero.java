package com.ESI.FastFoodESI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "camareros")
public class Camarero extends Empleado {

    // --- CONSTRUCTORES ---
    public Camarero() {
        super();
    }

}
