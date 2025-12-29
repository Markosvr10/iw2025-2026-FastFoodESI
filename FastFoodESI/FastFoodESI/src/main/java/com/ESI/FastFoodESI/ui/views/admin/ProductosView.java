package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.service.admin.ProductoService;
import com.ESI.FastFoodESI.ui.layouts.admin.PropietarioMainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Lazy;

@Route(value = "admin/productos", layout = PropietarioMainLayout.class)
@PageTitle("Gestión de Carta | Admin")
@RolesAllowed("PROPIETARIO")
public class ProductosView extends VerticalLayout {

    private final ProductoService service;
    private final ProductoForm form;
    private final Grid<Producto> grid = new Grid<>(Producto.class);
    private final Dialog dialog = new Dialog();

    // Inyección de dependencias
    public ProductosView(ProductoService service, @Lazy ProductoForm form) {
        this.service = service;
        this.form = form;

        setSizeFull();
        configureGrid();
        configureDialog();
        
        add(new H2("Nuestra Carta"), getToolbar(), grid);
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("nombre", "descripcion");
        
        grid.addColumn(p -> p.getImporte() + " €").setHeader("Precio").setSortable(true);
        grid.addColumn(Producto::getStock).setHeader("Stock");
        grid.addColumn(p -> p.getTipo() != null ? p.getTipo().getNombre() : "-").setHeader("Tipo");
        
        grid.addComponentColumn(this::createActions).setHeader("Acciones");
    }
    
    private Component createActions(Producto producto) {
        Button edit = new Button(VaadinIcon.PENCIL.create(), e -> editProducto(producto));
        Button delete = new Button(VaadinIcon.TRASH.create(), e -> deleteProducto(producto));
        delete.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private Component getToolbar() {
        Button addBtn = new Button("Nuevo Producto", VaadinIcon.PLUS.create());
        addBtn.addClickListener(e -> addProducto());
        addBtn.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);
        return new HorizontalLayout(addBtn);
    }

    private void configureDialog() {
        dialog.add(form);
        dialog.setWidth("600px");
    }

    public void updateList() {
        grid.setItems(service.findAll());
    }

    private void addProducto() {
        grid.asSingleSelect().clear();
        form.setProducto(new Producto(), dialog);
        dialog.open();
    }

    private void editProducto(Producto producto) {
        form.setProducto(producto, dialog);
        dialog.open();
    }

    private void deleteProducto(Producto producto) {
        try {
            service.delete(producto.getId());
            updateList();
            Notification.show("Producto eliminado", 3000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
             Notification.show("No se puede eliminar. ¿Tiene pedidos asociados?", 5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}