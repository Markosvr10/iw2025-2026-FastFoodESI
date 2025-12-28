package com.ESI.FastFoodESI.dto;

import java.math.BigDecimal;

public class RankingItemDTO {
    private String nombre;
    private BigDecimal valor;

    // --- CONSTRUCTOR 1: Para Dinero (BigDecimal) ---
    public RankingItemDTO(String nombre, BigDecimal valor) {
        this.nombre = nombre;
        this.valor = valor != null ? valor : BigDecimal.ZERO;
    }

    // --- CONSTRUCTOR 2: Para Cantidad (Long) ---
    public RankingItemDTO(String nombre, Long valor) {
        this.nombre = nombre;
        this.valor = (valor != null) ? BigDecimal.valueOf(valor) : BigDecimal.ZERO;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}