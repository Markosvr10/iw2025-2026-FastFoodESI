package com.ESI.FastFoodESI.dto;
import com.ESI.FastFoodESI.model.Producto;


public class LineaCarrito {
    private Producto producto;
    private int cantidad;

    public LineaCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    // Calcula el precio total de esta línea (Precio x Cantidad)
    public double getTotalLinea() {
        return producto.getImporte().doubleValue() * cantidad;
    }
}