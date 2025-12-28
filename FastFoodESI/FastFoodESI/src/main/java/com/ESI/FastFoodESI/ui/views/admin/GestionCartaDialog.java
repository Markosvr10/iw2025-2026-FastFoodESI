package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Carta;
import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.repository.CartaRepository;
import com.ESI.FastFoodESI.service.admin.ProductoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.H3;

public class GestionCartaDialog extends Dialog {

    private final ProductoService productoService;
    private final ProductoForm productoForm;
    private final Negocio negocio;
    private final Grid<Producto> grid = new Grid<>(Producto.class);
    private final CartaRepository cartaRepository;
    private Producto currentProducto;

    public GestionCartaDialog(Negocio negocio, ProductoService productoService, ProductoForm productoForm, CartaRepository cartaRepository) {
        this.negocio = negocio;
        this.productoService = productoService;
        this.productoForm = productoForm;
        this.cartaRepository = cartaRepository;

        setWidth("800px");
        setHeight("600px");
        setHeaderTitle("Gestión de Carta: " + negocio.getNombre());

        grid.setSizeFull();
        grid.setColumns("nombre", "descripcion", "importe", "stock");
        grid.getColumnByKey("importe").setHeader("Precio (€)");
        grid.addComponentColumn(producto -> {
            Button editBtn = new Button(VaadinIcon.PENCIL.create(), e -> editProducto(producto));
            Button delBtn = new Button(VaadinIcon.TRASH.create(), e -> deleteProducto(producto));
            delBtn.getStyle().set("color", "red");
            return new HorizontalLayout(editBtn, delBtn);
        });

        Button addBtn = new Button("Añadir Producto", VaadinIcon.PLUS.create(), e -> addProducto());
        
        this.productoForm.setOnSaveListener(this::refreshGrid);


        VerticalLayout layout = new VerticalLayout(addBtn, grid);
        layout.setSizeFull();
        add(layout);
        
        add(productoForm);
        productoForm.setVisible(false);
        
        this.productoForm.setOnSaveListener(() -> {

            vincularProductoACarta();
            

            refreshGrid();
            
            productoForm.setVisible(false);
            this.currentProducto = null;
        });
        
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(productoService.findAllByNegocio(negocio));
    }

    private void addProducto() {
        grid.asSingleSelect().clear();
        Producto nuevo = new Producto();
        
        this.currentProducto = nuevo;
        
        openForm(nuevo);
    }

    private void editProducto(Producto producto) {
        openForm(producto);
    }
    
    private void deleteProducto(Producto producto) {
        productoService.delete(producto);
        refreshGrid();
        Notification.show("Producto eliminado");
    }

    private void openForm(Producto producto) {
        Dialog formDialog = new Dialog();
        productoForm.setProducto(producto, formDialog); 
        productoForm.setVisible(true);
        formDialog.add(productoForm);
        formDialog.open();
        
        formDialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) refreshGrid();
        });
    }
    
    private void vincularProductoACarta() {
        if (this.currentProducto != null && this.currentProducto.getId() != null) {
            
            Carta carta = cartaRepository.findByNegocio(this.negocio)
                    .orElseGet(() -> {
                        Carta nueva = new Carta();
                        nueva.setNombre("Menú de " + this.negocio.getNombre());
                        nueva.setNegocio(this.negocio);
                        return cartaRepository.save(nueva);
                    });
            
            carta.addProducto(this.currentProducto);
            cartaRepository.save(carta);
        }
    }
}