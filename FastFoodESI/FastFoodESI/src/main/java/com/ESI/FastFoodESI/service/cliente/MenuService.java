package com.ESI.FastFoodESI.service.cliente;

import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Tipo; // Importante
import com.ESI.FastFoodESI.repository.ProductoRepository;
import com.ESI.FastFoodESI.repository.TipoRepository; // Necesitas crear este import si no sale solo
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final ProductoRepository productoRepository;
    private final TipoRepository tipoRepository;

    // Inyectamos ambos repositorios
    public MenuService(ProductoRepository productoRepository, TipoRepository tipoRepository) {
        this.productoRepository = productoRepository;
        this.tipoRepository = tipoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    // Pestañas Categoría
    @Transactional(readOnly = true)
    public List<Tipo> obtenerTodosLosTipos() {
        return tipoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarProductos(String texto) {
        if (texto == null || texto.isEmpty()) {
            return obtenerTodosLosProductos();
        }
        return productoRepository.findAll().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(texto.toLowerCase()))
                .collect(Collectors.toList());
    }
}