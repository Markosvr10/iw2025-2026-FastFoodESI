package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.repository.CartaRepository;
import com.ESI.FastFoodESI.service.admin.ProductoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

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

        setWidth("900px");
        setHeight("700px");
        setHeaderTitle("Gestión de Carta: " + negocio.getNombre());

        configureGrid();

        Button addBtn = new Button("Añadir Producto", VaadinIcon.PLUS.create(), e -> addProducto());
        
        // --- LISTENER DE GUARDADO ---
        this.productoForm.setOnSaveListener(() -> {
            // USAMOS EL SERVICIO TRANSACCIONAL
            // Esto evita el error "failed to lazily initialize... no Session"
            if (this.currentProducto != null && this.currentProducto.getId() != null) {
                productoService.vincularProductoACarta(this.negocio, this.currentProducto);
            }
            
            refreshGrid();
            this.currentProducto = null;
        });

        VerticalLayout layout = new VerticalLayout(addBtn, grid);
        layout.setSizeFull();
        add(layout);
        
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("nombre", "stock"); 
        grid.addColumn(p -> p.getDescripcion()).setHeader("Descripción");
        grid.addColumn(p -> p.getImporte() + " €").setHeader("Precio").setSortable(true);
        
        grid.addComponentColumn(producto -> {
            Button editBtn = new Button(VaadinIcon.PENCIL.create(), e -> editProducto(producto));
            Button delBtn = new Button(VaadinIcon.TRASH.create(), e -> deleteProducto(producto));
            delBtn.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
            return new HorizontalLayout(editBtn, delBtn);
        }).setHeader("Acciones");
    }

    private void refreshGrid() {
        if (negocio != null) {
            grid.setItems(productoService.findAllByNegocio(negocio));
        }
    }

    private void addProducto() {
        grid.asSingleSelect().clear();
        Producto nuevo = new Producto();
        nuevo.setNegocio(this.negocio);
        this.currentProducto = nuevo;
        openForm(nuevo);
    }

    private void editProducto(Producto producto) {
        this.currentProducto = producto;
        openForm(producto);
    }
    
    private void deleteProducto(Producto producto) {
        try {
            // Usamos el método seguro que creamos antes
            productoService.deleteProductoSeguro(producto.getId());
            
            refreshGrid();
            Notification.show("Producto eliminado correctamente")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        } catch (Exception e) {
            Notification.show("Error al eliminar: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void openForm(Producto producto) {
        Dialog formDialog = new Dialog();
        productoForm.setProducto(producto, formDialog); 
        productoForm.setVisible(true);
        formDialog.add(productoForm);
        formDialog.open();
        
        formDialog.addOpenedChangeListener(e -> {
            if (!e.isOpened()) {
                refreshGrid();
                formDialog.remove(productoForm);
            }
        });
    }

}