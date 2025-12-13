package com.ESI.FastFoodESI.service;

import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Tipo;
import com.ESI.FastFoodESI.repository.ProductoRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true) 
public class ProductoService {


    private final ProductoRepository productoRepository;

   
    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

   
    @Transactional
    public Producto save(Producto producto) {
       
        return productoRepository.save(producto);
    }

    
    @Transactional
    public void delete(UUID id) {
        productoRepository.deleteById(id);
    }

    
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    
    public Producto findById(UUID id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }
    
    
    public List<Producto> findByTipo(Tipo tipo) {
        
        return List.of();
    }


    @Transactional
    public void checkStockAndReserve(UUID productoId, int cantidad) {
        Producto producto = findById(productoId);
       
        
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
    
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

}