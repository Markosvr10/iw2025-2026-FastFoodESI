package com.ESI.FastFoodESI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cocineros") // "Cocina" es ambiguo, "Cocineros" es más claro
public class Cocina extends Empleado {

    // --- CONSTRUCTORES ---
    public Cocina() {
        super();
    }

}
