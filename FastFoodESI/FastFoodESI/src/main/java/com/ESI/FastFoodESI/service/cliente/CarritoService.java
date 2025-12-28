package com.ESI.FastFoodESI.service.cliente;

import com.ESI.FastFoodESI.dto.LineaCarrito;
import com.ESI.FastFoodESI.model.Producto;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@SessionScope
@SpringComponent
public class CarritoService implements Serializable {

    private final Map<UUID, LineaCarrito> items = new HashMap<>();

    public void anadirProducto(Producto p) {
        if (items.containsKey(p.getId())) {
            LineaCarrito linea = items.get(p.getId());
            linea.setCantidad(linea.getCantidad() + 1);
        } else {
            items.put(p.getId(), new LineaCarrito(p, 1));
        }
    }

    public void restarProducto(Producto p) {
        if (items.containsKey(p.getId())) {
            LineaCarrito linea = items.get(p.getId());
            int nuevaCantidad = linea.getCantidad() - 1;

            if (nuevaCantidad > 0) {
                linea.setCantidad(nuevaCantidad);
            } else {
                items.remove(p.getId());
            }
        }
    }

    public void eliminarProductoDelTodo(Producto p) {
        items.remove(p.getId());
    }

    public List<LineaCarrito> getLineas() {
        return new ArrayList<>(items.values());
    }

    public void vaciarCarrito() {
        items.clear();
    }

    public double calcularTotal() {
        return items.values().stream()
                .mapToDouble(LineaCarrito::getTotalLinea)
                .sum();
    }
}