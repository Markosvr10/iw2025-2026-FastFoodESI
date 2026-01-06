package com.ESI.FastFoodESI.service.admin;

import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Tipo;
import com.ESI.FastFoodESI.repository.CartaRepository; // <--- IMPORTANTE: Nuevo import
import com.ESI.FastFoodESI.repository.ProductoRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true) 
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CartaRepository cartaRepository; // <--- 1. Nuevo campo

    @Autowired
    public ProductoService(ProductoRepository productoRepository, CartaRepository cartaRepository) { // <--- 2. Actualizar constructor
        this.productoRepository = productoRepository;
        this.cartaRepository = cartaRepository;
    }

    @Transactional
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    // --- MÉTODOS DE BORRADO ---

    @Transactional
    public void deleteProductoSeguro(UUID id) {
        // 1. Buscamos el producto
        Producto producto = findById(id);

        // 2. Si tiene negocio, buscamos su carta para desvincularlo
        if (producto.getNegocio() != null) {
            var cartaOpt = cartaRepository.findByNegocio(producto.getNegocio());
            
            if (cartaOpt.isPresent()) {
                var carta = cartaOpt.get();
                // Como estamos dentro de una transacción, podemos acceder a getProductos()
                // sin que salte el error "no Session".
                if (carta.getProductos() != null) {
                    // Usamos removeIf para borrar por ID (más seguro)
                    boolean eliminado = carta.getProductos().removeIf(p -> p.getId().equals(id));
                    
                    if (eliminado) {
                        cartaRepository.save(carta);
                    }
                }
            }
        }

        // 3. Ahora que está desvinculado, borramos el producto
        productoRepository.delete(producto);
    }

    @Transactional
    public void vincularProductoACarta(Negocio negocio, Producto producto) {
        // 1. Buscamos o creamos la carta (Dentro de transacción)
        var carta = cartaRepository.findByNegocio(negocio)
                .orElseGet(() -> {
                    com.ESI.FastFoodESI.model.Carta nueva = new com.ESI.FastFoodESI.model.Carta();
                    nueva.setNombre("Menú de " + negocio.getNombre());
                    nueva.setNegocio(negocio);
                    return cartaRepository.save(nueva);
                });

        // 2. Inicializamos la lista si es nula (Evita NullPointer)
        if (carta.getProductos() == null) {
            carta.setProductos(new java.util.HashSet<>());
        }

        // 3. Añadimos el producto
        carta.addProducto(producto);
        
        // 4. Guardamos
        cartaRepository.save(carta);
    }

    @Transactional
    public void delete(Producto producto) {
        if (producto != null) {
            productoRepository.delete(producto);
        }
    }

    @Transactional
    public void delete(UUID id) {
        productoRepository.deleteById(id);
    }

    // --- MÉTODOS DE LECTURA ---

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }
    
    public List<Producto> findAllByNegocio(Negocio negocio) {
        return productoRepository.findByNegocio(negocio);
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